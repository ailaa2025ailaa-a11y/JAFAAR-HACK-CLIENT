package dev.jafaar.client.recorder;

import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.AL10;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Optional microphone capture through OpenAL's capture extension.
 * Some Android OpenAL backends do not expose ALC_EXT_CAPTURE; in that case
 * start() fails cleanly and the caller can show the returned status.
 */
public final class JafaarMicrophoneCapture {
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNELS = 1;
    private static final int BYTES_PER_SAMPLE = 2;
    private static final int CHUNK_SAMPLES = 1024;

    private static volatile long captureDevice;
    private static volatile Thread captureThread;
    private static final AtomicBoolean running = new AtomicBoolean(false);
    private static volatile String status = "MICROPHONE OFF";
    private static volatile Path wavFile;

    private JafaarMicrophoneCapture() {}

    public static synchronized boolean start(Path directory) {
        if (running.get()) return true;
        long device = 0L;
        try {
            Files.createDirectories(directory);
            String stamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            wavFile = directory.resolve("JAFAAR_MIC_" + stamp + ".wav");
            device = ALC11.alcCaptureOpenDevice((ByteBuffer) null, SAMPLE_RATE,
                    AL10.AL_FORMAT_MONO16, SAMPLE_RATE);
            if (device == 0L) {
                status = "MIC UNAVAILABLE: OpenAL capture unsupported";
                return false;
            }
            captureDevice = device;
            try (RandomAccessFile file = new RandomAccessFile(wavFile.toFile(), "rw")) {
                writeWavHeader(file, 0);
            }
            ALC11.alcCaptureStart(device);
            running.set(true);
            status = "MICROPHONE RECORDING";
            final long openedDevice = device;\n            final Path openedFile = wavFile;\n            captureThread = new Thread(() -> captureLoop(openedDevice, openedFile), "JAFAAR-Microphone-Capture");
            captureThread.setDaemon(true);
            captureThread.start();
            return true;
        } catch (Throwable failure) {
            running.set(false);
            if (device != 0L) {
                try { ALC11.alcCaptureCloseDevice(device); } catch (Throwable ignored) {}
            }
            captureDevice = 0L;
            status = "MIC UNAVAILABLE: " + failure.getClass().getSimpleName();
            return false;
        }
    }

    public static synchronized void stop() {
        if (!running.getAndSet(false)) return;
        long device = captureDevice;
        if (device != 0L) {
            try { ALC11.alcCaptureStop(device); } catch (Throwable ignored) {}
        }
        Thread thread = captureThread;
        if (thread != null) {
            try { thread.join(1200L); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (device != 0L) {
            try { ALC11.alcCaptureCloseDevice(device); } catch (Throwable ignored) {}
        }
        captureDevice = 0L;
        captureThread = null;
        status = "MIC SAVED: " + (wavFile == null ? "unknown" : wavFile.getFileName());
    }

    public static boolean isRecording() { return running.get(); }
    public static String status() { return status; }
    public static Path outputFile() { return wavFile; }

    private static void captureLoop(long device, Path target) {
        int dataBytes = 0;
        ByteBuffer pcm = ByteBuffer.allocateDirect(CHUNK_SAMPLES * BYTES_PER_SAMPLE)
                .order(ByteOrder.LITTLE_ENDIAN);
        try (RandomAccessFile file = new RandomAccessFile(target.toFile(), "rw")) {
            file.seek(44);
            while (running.get()) {
                int available = ALC11.alcGetInteger(device, ALC11.ALC_CAPTURE_SAMPLES);
                if (available <= 0) {
                    Thread.sleep(8L);
                    continue;
                }
                int samples = Math.min(available, CHUNK_SAMPLES);
                pcm.clear();
                ALC11.alcCaptureSamples(device, pcm, samples);
                int bytes = samples * BYTES_PER_SAMPLE;
                pcm.position(0);
                byte[] chunk = new byte[bytes];
                pcm.get(chunk);
                file.write(chunk);
                dataBytes += bytes;
            }
            file.seek(0);
            writeWavHeader(file, dataBytes);
            status = "MIC SAVED: " + target.getFileName();
        } catch (Throwable failure) {
            status = "MIC CAPTURE ERROR: " + failure.getClass().getSimpleName();
        }
    }

    private static void writeWavHeader(RandomAccessFile file, int dataLength) throws java.io.IOException {
        int byteRate = SAMPLE_RATE * CHANNELS * BYTES_PER_SAMPLE;
        file.writeBytes("RIFF");
        writeIntLE(file, 36 + dataLength);
        file.writeBytes("WAVE");
        file.writeBytes("fmt ");
        writeIntLE(file, 16);
        writeShortLE(file, 1);
        writeShortLE(file, CHANNELS);
        writeIntLE(file, SAMPLE_RATE);
        writeIntLE(file, byteRate);
        writeShortLE(file, CHANNELS * BYTES_PER_SAMPLE);
        writeShortLE(file, BYTES_PER_SAMPLE * 8);
        file.writeBytes("data");
        writeIntLE(file, dataLength);
    }

    private static void writeIntLE(RandomAccessFile file, int value) throws java.io.IOException {
        file.write(value & 255);
        file.write((value >>> 8) & 255);
        file.write((value >>> 16) & 255);
        file.write((value >>> 24) & 255);
    }

    private static void writeShortLE(RandomAccessFile file, int value) throws java.io.IOException {
        file.write(value & 255);
        file.write((value >>> 8) & 255);
    }
}

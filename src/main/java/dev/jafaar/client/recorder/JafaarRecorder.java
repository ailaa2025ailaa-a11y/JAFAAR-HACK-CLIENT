package dev.jafaar.client.recorder;

import net.minecraft.client.Minecraft;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Video-only capture backend. Frames are read from the current OpenGL framebuffer
 * on the client thread and encoded away from the render thread.
 */
public final class JafaarRecorder {
    private static final ArrayBlockingQueue<Picture> FRAMES = new ArrayBlockingQueue<>(8);
    private static volatile boolean recording;
    private static volatile String status = "IDLE";
    private static volatile Path outputFile;
    private static volatile int outputFps = 20;
    private static volatile int outputHeight = 720;
    private static long lastCaptureNanos;
    private static final AtomicLong droppedFrames = new AtomicLong();

    private JafaarRecorder() {}

    public static synchronized boolean start(int height) {
        if (recording) return false;
        try {
            Path directory = Minecraft.getInstance().gameDirectory.toPath().resolve("recordings");
            Files.createDirectories(directory);
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            outputFile = directory.resolve("JAFAAR_" + stamp + ".mov");
            outputHeight = height;
            outputFps = 20;
            FRAMES.clear();
            droppedFrames.set(0);
            lastCaptureNanos = 0;
            recording = true;
            status = "STARTING";
            Thread encoderThread = new Thread(JafaarRecorder::encodeLoop, "JAFAAR-Video-Encoder");
            encoderThread.setDaemon(true);
            encoderThread.start();
            status = "RECORDING";
            return true;
        } catch (Exception exception) {
            recording = false;
            status = "ERROR: " + exception.getMessage();
            return false;
        }
    }

    public static synchronized void stop() {
        if (!recording) return;
        recording = false;
        status = "FINALIZING";
    }

    public static boolean isRecording() { return recording; }
    public static String status() { return status; }
    public static Path outputFile() { return outputFile; }
    public static long droppedFrames() { return droppedFrames.get(); }

    /** Called from END_CLIENT_TICK while Minecraft's OpenGL context is current. */
    public static void captureFrame(Minecraft client) {
        if (!recording || client.player == null || client.getWindow() == null) return;
        long now = System.nanoTime();
        long interval = 1_000_000_000L / outputFps;
        if (lastCaptureNanos != 0 && now - lastCaptureNanos < interval) return;
        lastCaptureNanos = now;

        int sourceWidth = client.getWindow().getWidth();
        int sourceHeight = client.getWindow().getHeight();
        if (sourceWidth < 2 || sourceHeight < 2) return;

        try {
            ByteBuffer pixels = BufferUtils.createByteBuffer(sourceWidth * sourceHeight * 3);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glReadPixels(0, 0, sourceWidth, sourceHeight, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);
            pixels.rewind();

            int targetHeight = Math.min(outputHeight, sourceHeight);
            int targetWidth = Math.max(2, (int) Math.round((double) sourceWidth * targetHeight / sourceHeight));
            targetWidth &= ~1;
            targetHeight &= ~1;
            Picture picture = Picture.create(targetWidth, targetHeight, ColorSpace.RGB);
            byte[] red = picture.getPlaneData(0);
            byte[] green = picture.getPlaneData(1);
            byte[] blue = picture.getPlaneData(2);

            for (int y = 0; y < targetHeight; y++) {
                int sourceY = sourceHeight - 1 - (int) ((long) y * sourceHeight / targetHeight);
                for (int x = 0; x < targetWidth; x++) {
                    int sourceX = (int) ((long) x * sourceWidth / targetWidth);
                    int sourceIndex = (sourceY * sourceWidth + sourceX) * 3;
                    int targetIndex = y * targetWidth + x;
                    red[targetIndex] = (byte) ((pixels.get(sourceIndex) & 255) - 128);
                    green[targetIndex] = (byte) ((pixels.get(sourceIndex + 1) & 255) - 128);
                    blue[targetIndex] = (byte) ((pixels.get(sourceIndex + 2) & 255) - 128);
                }
            }

            if (!FRAMES.offer(picture)) droppedFrames.incrementAndGet();
        } catch (Throwable throwable) {
            status = "CAPTURE ERROR: " + throwable.getClass().getSimpleName();
            recording = false;
        }
    }

    private static void encodeLoop() {
        SequenceEncoder encoder = null;
        try {
            encoder = SequenceEncoder.createSequenceEncoder(outputFile.toFile(), outputFps);
            while (recording || !FRAMES.isEmpty()) {
                Picture frame = FRAMES.poll(250, TimeUnit.MILLISECONDS);
                if (frame != null) encoder.encodeNativeFrame(frame);
            }
            encoder.finish();
            status = "SAVED: " + outputFile.getFileName();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            status = "ENCODER INTERRUPTED";
        } catch (Throwable throwable) {
            status = "ENCODE ERROR: " + throwable.getClass().getSimpleName();
            recording = false;
            if (encoder != null) {
                try { encoder.finish(); } catch (IOException ignored) {}
            }
        }
    }
}

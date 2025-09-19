package com.genymobile.scrcpy.device;

import com.genymobile.scrcpy.control.ControlChannel;
import com.genymobile.scrcpy.util.Ln;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public final class DesktopConnection implements Closeable {

    private static final int DEVICE_NAME_FIELD_LENGTH = 64;

    private final Socket videoSocket;
    private final OutputStream videoStream;

    private final Socket audioSocket;
    private final OutputStream audioStream;

    private final Socket controlSocket;
    private final ControlChannel controlChannel;

    private DesktopConnection(Socket videoSocket, Socket audioSocket, Socket controlSocket) throws IOException {
        this.videoSocket = videoSocket;
        this.audioSocket = audioSocket;
        this.controlSocket = controlSocket;

        videoStream = videoSocket != null ? videoSocket.getOutputStream() : null;
        audioStream = audioSocket != null ? audioSocket.getOutputStream() : null;
        controlChannel = controlSocket != null ? new ControlChannel(controlSocket) : null;
    }

    private static Socket connect(int port) throws IOException {
        Ln.i("connectin to " + port);

        return new Socket("127.0.0.1", port);
    }

    public static DesktopConnection open(int controlerPort, int mediaPort, boolean video, boolean audio, boolean control, boolean sendDummyByte)
            throws IOException {
        Socket videoSocket = null;
        Socket audioSocket = null;
        Socket controlSocket = null;

        try {
            if (video) {
                videoSocket = connect(mediaPort);
            }

            if (audio) {
                audioSocket = connect(mediaPort);
            }

            if (control) {
                controlSocket = connect(controlerPort);
            }
        } catch (IOException | RuntimeException e) {
            if (videoSocket != null) {
                videoSocket.close();
            }

            if (audioSocket != null) {
                audioSocket.close();
            }

            if (controlSocket != null) {
                controlSocket.close();
            }

            throw e;
        }

        return new DesktopConnection(videoSocket, audioSocket, controlSocket);
    }

    private Socket getFirstSocket() {
        if (controlSocket != null) {
            return controlSocket;
        }

        if (videoSocket != null) {
            return videoSocket;
        }

        if (audioSocket != null) {
            return audioSocket;
        }

        return null;
    }

    public void shutdown() throws IOException {
        if (videoSocket != null) {
            videoSocket.shutdownInput();
            videoSocket.shutdownOutput();
        }
        if (audioSocket != null) {
            audioSocket.shutdownInput();
            audioSocket.shutdownOutput();
        }
        if (controlSocket != null) {
            controlSocket.shutdownInput();
            controlSocket.shutdownOutput();
        }
    }

    public void close() throws IOException {
        if (videoSocket != null) {
            videoSocket.close();
        }
        if (audioSocket != null) {
            audioSocket.close();
        }
        if (controlSocket != null) {
            controlSocket.close();
        }
    }

    public OutputStream getVideoStream() {
        return videoStream;
    }

    public OutputStream getAudioStream() {
        return audioStream;
    }

    public ControlChannel getControlChannel() {
        return controlChannel;
    }
}

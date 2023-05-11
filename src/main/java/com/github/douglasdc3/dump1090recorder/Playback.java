package com.github.douglasdc3.dump1090recorder;

import org.apache.commons.cli.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;

public class Playback implements Command {
    private final InetSocketAddress address;
    private final String file;
    private final int speed;
    private final long max;
    private final ServerSocket socket;

    public Playback(InetSocketAddress address, String file, int speed, int max) {
        this.address = address;
        this.file = file;
        this.speed = speed;
        this.max = max * 1000;

        try {
            this.socket = new ServerSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        try (InputStream filestream = new FileInputStream(file)) {
            socket.bind(address);

            long lastMessageTime = 0;
            long currentMessageTime = 0;
            String message;
            String[] messageFields;

            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(filestream, StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.accept().getOutputStream(), StandardCharsets.UTF_8));
            ) {

                while (true) {
                    message = in.readLine();
                    messageFields = message.split(";");
                    currentMessageTime = Long.parseLong(messageFields[0]);
                    if (!(lastMessageTime == 0)) {
                        if ((currentMessageTime - lastMessageTime) > max) {
                            System.out.println("Fast-forwarding: " + (currentMessageTime - lastMessageTime) / 1000 + " seconds");
                            lastMessageTime = currentMessageTime - max;
                        }
                        Thread.sleep((currentMessageTime - lastMessageTime) / speed);
                    }
                    lastMessageTime = currentMessageTime;
                    out.write(messageFields[1] + "\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.out.println("Got an interrupt signal, the program will now shutdown.");
        }
    }

    @Override
    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
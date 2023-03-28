package com.github.douglasdc3.dump1090recorder;

import com.vlkan.rfos.RotatingFileOutputStream;
import com.vlkan.rfos.RotationConfig;
import com.vlkan.rfos.policy.DailyRotationPolicy;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class Record implements Command {
    private final Socket socket;
    private final InetSocketAddress address;

    public Record(InetSocketAddress address) {
        this.socket = new Socket();
        this.address = address;
    }

    @Override
    public void start() {
        RotationConfig config = RotationConfig
                .builder()
                .file("./dump1090-adsb.log")
                .filePattern("./dump1090-adsb-%d{yyyyMMdd}.log")
                .append(true)
                .policy(DailyRotationPolicy.getInstance())
                .build();

        try {
            socket.connect(address);

            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    RotatingFileOutputStream os = new RotatingFileOutputStream(config)
            ) {
                String message;

                while (true) {
                    message = in.readLine();

                    os.write((Instant.now().toEpochMilli() + ";" + message + "\n").getBytes());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
package com.github.douglasdc3.dump1090recorder;

import com.vlkan.rfos.RotatingFileOutputStream;
import com.vlkan.rfos.RotationConfig;
import com.vlkan.rfos.policy.DailyRotationPolicy;
import org.apache.commons.cli.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;

public class Record {
    public static void main(String[] args) throws ParseException, IOException, InterruptedException {
        Options options = new Options();
        options.addOption(Option.builder("help").desc("Print this message").build());
        options.addOption(Option.builder("h").longOpt("host").hasArg().argName("ip").desc("IP Address").build());
        options.addOption(Option.builder("p").longOpt("port").hasArg().argName("port").desc("Port").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            System.out.println("Dump1090 Recorder");
            System.out.println("-----------------");
            System.out.println();
            System.out.println("Usage: <executable> -h 127.0.0.1 -p 30002");
            System.out.println();
            System.out.println("Starts recording messages to a rolling log inside the current directory.");
            System.out.println();

            System.exit(0);
        }

        String host = cmd.getOptionValue("h", "127.0.0.1");
        int port = Integer.parseInt(cmd.getOptionValue("p", "30002"));

        Socket socket = (new Socket());
        RotationConfig config = RotationConfig
                .builder()
                .file("./dump1090-adsb.log")
                .filePattern("./dump1090-adsb-%d{yyyyMMdd}.log")
                .append(true)
                .policy(DailyRotationPolicy.getInstance())
                .build();


        Runtime.getRuntime().addShutdownHook(new Thread() {
                 public void run() {
                     try {
                         socket.close();
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                 }
             }
        );


        System.out.println("Press CTRL-C to stop recording");
        socket.connect(new InetSocketAddress(host, port));
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                RotatingFileOutputStream os = new RotatingFileOutputStream(config)
        ) {
            String message;

            while (true) {
                message = in.readLine();

                os.write((Instant.now().toEpochMilli() + ";" + message + "\n").getBytes());
            }
        }
    }
}
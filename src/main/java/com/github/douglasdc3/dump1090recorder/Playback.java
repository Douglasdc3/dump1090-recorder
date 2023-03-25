package com.github.douglasdc3.dump1090recorder;

import org.apache.commons.cli.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class Playback {
    public static void main(String[] args) throws ParseException, IOException, InterruptedException {
        Options options = new Options();
        options.addOption(Option.builder("help").desc("Print this message").build());
        options.addOption(Option.builder("f").longOpt("file").hasArg().argName("file").desc("Filename").build());
        options.addOption(Option.builder("s").longOpt("speed").hasArg().argName("speed").desc("Speed Multiplier").build());
        options.addOption(Option.builder("h").longOpt("host").hasArg().argName("ip").desc("IP Address").build());
        options.addOption(Option.builder("p").longOpt("port").hasArg().argName("port").desc("Port").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            System.out.println("Dump1090 Player");
            System.out.println("-----------------");
            System.out.println();
            System.out.println("Usage: <executable> -f filename.log -s 1 -h 127.0.0.1 -p 30002");
            System.out.println();
            System.out.println("Starts playing messages from a recorded log inside the current directory.");
            System.out.println();

            System.exit(0);
        }

        String filename = cmd.getOptionValue("f", "dump1090-adsb.log");
        int speed = Integer.parseInt(cmd.getOptionValue("s", "1"));
        String host = cmd.getOptionValue("h", "127.0.0.1");
        int port = Integer.parseInt(cmd.getOptionValue("p", "30002"));

        ServerSocket socket = (new ServerSocket());

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

        System.out.println("Press CTRL-C to stop playback");

        InputStream filestream = new FileInputStream(filename);
        socket.bind(new InetSocketAddress(host, port));

        long lastMessageTime = 0;
        long currentMessageTime = 0;
        String message;
        String[] messageFields;

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(filestream, "UTF-8"));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.accept().getOutputStream(), "UTF-8"));
        ) {

            while (true) {
                message = in.readLine();
                messageFields = message.split(";");
                currentMessageTime = Long.parseLong(messageFields[0]);

                if (!(lastMessageTime == 0)) {
//                    System.out.println("Waiting for " + (currentMessageTime - lastMessageTime) / speed + " milliseconds");
                    Thread.sleep((currentMessageTime - lastMessageTime)/speed);
                }
                lastMessageTime = currentMessageTime;
//                System.out.println(messageFields[1]);
                out.write(messageFields[1] + "\n");
            }
        }
    }
}
package com.github.douglasdc3.dump1090recorder;

import org.apache.commons.cli.*;

import java.net.InetSocketAddress;

public class Main {
    private final Command command;

    public Main(Command command) {
        this.command = command;
    }

    public static void main(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption(Option.builder("help").desc("Print this message").build());
        options.addOption(Option.builder("playback").desc("Start in playback mode").build());
        options.addOption(Option.builder("record").desc("Start in playback mode").build());
        options.addOption(Option.builder("f").longOpt("file").hasArg().argName("file").desc("Filename").build());
        options.addOption(Option.builder("s").longOpt("speed").hasArg().argName("speed").desc("Speed Multiplier").build());
        options.addOption(Option.builder("h").longOpt("host").hasArg().argName("ip").desc("IP Address").build());
        options.addOption(Option.builder("p").longOpt("port").hasArg().argName("port").desc("Port").build());

        CommandLine input = DefaultParser.builder().build().parse(options, args);
        Main program = null;


        if (input.getArgList().contains("record")) {
            program = new Main(new Record(
                new InetSocketAddress(input.getOptionValue("host", "127.0.0.1"), Integer.parseInt(input.getOptionValue("port", "30002")))
            ));
        } else if (input.getArgList().contains("playback")) {
            program = new Main(new Playback(
                new InetSocketAddress(input.getOptionValue("h", "127.0.0.1"), Integer.parseInt(input.getOptionValue("p", "30002"))),
                input.getOptionValue("f", "dump1090-adsb.log"),
                Integer.parseInt(input.getOptionValue("s", "1"))
            ));
        } else {
           if (!input.getArgList().contains("help"))  {
               System.out.println("Illegal argument provided.");
           }

            System.out.println("Usage:");
            System.out.println("\t<executable> record");
            System.out.println("\t<executable> record -h 127.0.01 -p 30002");
            System.out.println();
            System.out.println("\t<executable> playback -f ./dump1090-adsb.log");
            System.out.println("\t<executable> playback -f ./dump1090-adsb.log -h 127.0.01 -p 30002");
            System.out.println();
            System.out.println("Arguments:");
            System.out.println("----------");
            System.out.println("help <command>: Print this help message");
            System.out.println("      playback: Playback a log file by broadcasting it in the same format as dump1090 raw dump.");
            System.out.println("        record: Record a dump1090 stream with timing information into a rotating log file.");

            System.exit(0);
        }

        program.run();

        System.exit(0);
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(command::shutdown));

        command.start();
    }
}

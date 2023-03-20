# Dump1090 Recorder

A simple utility which records dump1090 raw messages to log file for later processing and analysis.
Works with the output of [dump1090](https://github.com/antirez/dump1090) 30002 port. 

## Usage

Download the executable from the releases page. 

Inside a terminal / Console window run the following command

```bash
<executable> -h <your-dump-1090-ip> -p 30002
```

For example on windows

```bash
dump1090-recorder.exe -h 127.0.0.1 -p 30002
```

On mac there is an issue with the build you can run the app using

```bash
./dump1090-recorder/Contents/MacOS/dump1090-recorder -h 127.0.0.1 -p 30002
```

## Contributing

You can contribute to this project by reporting/fixing bugs or implementing new features.
We are always looking for help on this project.

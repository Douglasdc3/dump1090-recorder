# Dump1090 Recorder

A simple utility which records dump1090 raw messages to log file for later processing and analysis.
Works with the output of [dump1090](https://github.com/antirez/dump1090) 30002 port. 

## Usage

Download the executable from the releases page. 

Inside a terminal / Console window run the following command

```bash
<executable> record -h <your-dump-1090-ip> -p 30002
```

For example on windows

```bash
dump1090-recorder.exe record -h 127.0.0.1 -p 30002
```

On mac there is an issue with the build you can run the app using

```bash
./dump1090-recorder/Contents/MacOS/dump1090-recorder record -h 127.0.0.1 -p 30002
```

## Playback

```bash
<executable> playback -f ./dump1090-adsb.log -h <your-dump-1090-ip> -p 30002
```

For example on windows

```bash
dump1090-recorder.exe playback -f ./dump1090-adsb.log -h 127.0.0.1 -p 30002
```

On mac there is an issue with the build you can run the app using

```bash
./dump1090-recorder/Contents/MacOS/dump1090-recorder playback -f ./dump1090-adsb.log -h 127.0.0.1 -p 30002
```

## Contributing

You can contribute to this project by reporting/fixing bugs or implementing new features.
We are always looking for help on this project.

# SubtitleThing
A small java app for renaming and converting subtitle files. Uses robelix' excellent [sub2srt](http://github.com/robelix/sub2srt) perl script to convert sub files to srt. It is best used with a link in context menus of file managers.

## Requirements
JRE 8 or newer and perl must be installed. Perl must be available in PATH.

## Usage
```SubtitleThing movie.mp4```

## Building
```./gradlew assembleDist```
After running the build command, binaries can be found in `build/distributions/SubtitleThing.zip` and `build/distributions/SubtitleThing.jar`

## IntelliJ IDEA
The GUI is built with IDEA's GUI tool. IDEA project files can be generated with `./gradlew idea`.

# SubtitleThing
A small java app for renaming and converting subtitle files. Uses robelix' excellent [sub2srt](http://github.com/robelix/sub2srt) perl script to convert sub files to srt. It is best used with a link in context menus of file managers.

## Requirements
JRE 8 or newer and perl must be installed. Perl must be available in PATH.

## Usage
```SubtitleThing movie.mp4```

## Building
```./gradlew assembleDist```
After running the build command, binaries can be found in `build/distributions/SubtitleThing.zip` and `build/distributions/SubtitleThing.tar`

## Adding SubtitleThing to context menu
### Windows
You must have the app unpacked and installed somewhere.

1. Open regedit
2. Find `HKEY_CLASSES_ROOT -> * -> shell`
3. Add new key `SubtitleThing` under `shell`
4. Add new key `command` under `SubtitleThing`
5. Edit the `(Default)` value of `command` and enter: `"C:\Program Files\SubtitleThing\bin\SubtitleThing.bat" "%1"`. Replace the program path with the path you installed the app to.

## IntelliJ IDEA
The GUI is built with IDEA's GUI tool. IDEA project files can be generated with `./gradlew idea`.

# README

[Logisim](http://www.cburch.com/logisim/index.html) components for implementing a HACK-like computer as described in the [From NAND to Tetris](http://nand2tetris.org/) course.

## Dependencies
The only external dependency is `logisim.jar`. Since it's not available in the maven repos we need to add it manually before the first build:

    mvn install:install-file -Dfile=${PATH_TO_LOGISIM}/logisim.jar -DgroupId=com.cbrunch -DartifactId=logisim -Dversion=2.7.1 -Dpackaging=jar

This needs to be executed just once.

## Build
This project uses [maven](https://maven.apache.org/).

To build it, use the following command:

    mvn package

The jar file is in the `target` directory.

## Usage
Open Logisim and load the jar file from the Project -> Load Library -> JAR Library... menu.

## Components

### Bitmap display
The display follows the specification from nand2tetris. It is a 512 by 256 bitmap (that is, black and white only). It consist of 8192 16-pixel "segments".

Here are the pins from left to right:

* `RST` - Resets the display. Redraws it all in white. Works regardless of the state of `CLK` and `LOAD`.
* `CLK` - The clock. The display is updated only when the clock changes state from 0 to 1.
* `LOAD` - Idicates whether to update display (1) or not (0).
* `ADDR` - 13-bit address of the 16-bit segment to be updated. Address 0 is the top-left segment, 1 is next to it and so on.
* `IN` - 16-bit data in. The bits are copied over the specified segment. Bit 1 means foreground color (black), while 0 means background (white).
* `OUT` - 16-bit data out. The data that is currently stored (and displayed) in the specified segment.

If the `LOAD` is 1 when the `CLK` goes up, all the 16 pixels in the specified segment are updated at once.

`RST` works with priority. If it's 1, the display is cleared (all white) regardless of the state of the other pins.

Here is how it looks: ![HACK display](https://github.com/itoshkov/logisimn2t/blob/master/HACK-display.png "HACK display")

## Author

Ivan Toshkov

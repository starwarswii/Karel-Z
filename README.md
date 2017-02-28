# Karel-Z

A vast improvement on [Karel J Robot](https://csis.pace.edu/~bergin/KarelJava2ed/karelexperimental.html), rewritten from scratch.

![Karel The Robot Icon](https://raw.githubusercontent.com/starwarswii/Karel-Z/master/src/resources/icon.png)

### Table of Contents
* [Karel-Z](#karel-z)
* [Table of Contents](#table-of-contents)
* [Improvements and Changes from Karel J Robot](#improvements-and-changes-from-karel-j-robot)
  * [Big Improvements](#big-improvements)
  * [New World File Format (.kzw)](#new-world-file-format-kzw)
  * [Programming Related Changes](#programming-related-changes)
  * [Miscellaneous Changes and Improvements](#miscellaneous-changes-and-improvements)
* [Examples](#examples)

When first working with Karel J Robot, the Java Implementation of the [Karel programming language](https://en.wikipedia.org/wiki/Karel_\(programming_language\)), It left a lot to be desired. At its core, it was not very flexible, didn’t follow many Object Oriented Programming conventions, and had poor editor support. Additionally, it just wasn’t capable of running fast enough for more complicated programs. Initially, I considered tweaking the code here and there to achieve what I wanted out of it, but I found too many changes I wanted to make. In the end, I started from scratch and rewrote everything.

# Improvements and Changes from Karel J Robot

#### Big Improvements
* Everything has been 'OOPified' and is much more object oriented, with less static calls underneath
* The simulation can be run much faster, and includes an 'Overdrive' setting for long-executing programs
* Improved Player and Editor
  * The simulation player and world builder have been combined
  * World editing while the simulation is running
  * Click and drag for pan and scroll for vector zoom
  * Various options to show some, all, or none of the editor buttons when running the simulation
  * Player
    * Play and pause with precise speed control
    * Step one frame forward at a time
  * Editor
    * Simple loading and saving world files
    * Color chooser for world colors
    * Reactive editor buttons based on world color
    * Beeper pile numbers scale to fill beeper
    * Beeper pile represents infinity with ∞
* Very thorough JavaDoc documentation In all areas for easy understandability
* Support for running multiple worlds/tests in sequence, including generated worlds

#### New World File Format (.kzw)
* Designed to be much more simpler, processable, and human readable
* World colors are now stored in the world file itself
* The editor will load and convert files with the old world format (.kwld) to the new one for easy transition

#### Programming Related Changes
* New `SuperRobot` class, a more advanced robot
  * Can turn right and turn around
  * Can teleport to an arbitrary spot
  * Has access to the current world object it is in, allowing for things such as getting the width and height
  * Can cause a crash, to allow for custom user crashes with error messages
* New `iterate` method, that functions as a more pure wrapper for a for loop, to mimic the `ITERATE n TIMES` structure from Karel
* The play, pause, and step buttons can be programmatically pushed
* Worlds are easily editable with code
* Most method names are identical or very similar to the old format, making transferring Karel J Robot code to Karel-Z very easy with only minimal tweaking

#### Miscellaneous Changes and Improvements
* Street/Avenue System is changed to x, y
* Indexing starts at 0 instead of 1
* Positions are now marked in the boxes instead of on the lines
* Karel Robot image cleaned up
* Loaded Karel images are now pre-rotated by the program, so only 3 images need be provided, instead of 12 (3 states times 4 directions)
* Added a 'block' wall type to serve as a wall on all sides of a cell

# Examples

For robot examples, see the Robot files under [src/test](https://github.com/starwarswii/Karel-Z/tree/master/src/test)

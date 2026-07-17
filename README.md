# Wind Tunnel

## How it works
The Wind Tunnel uses the Lattice Boltzmann method to simulate a fluid flowing around an object based on variable flow speed and viscosity. \
I have added a few optimisation algorithms to speed up the simulation, such as Esoteric Pull and Back-Face Culling. \
There is a `Levels` mode where the aim is to create the most aerodynamic shape around a fixed shape. \
There is a `Freeplay` mode where you can draw 2D or 3D shapes, import 3D Blender files and change settings to simulate a fluid flowing over an object.

## Limitations
The Lattice Boltzmann method has a few limitations such as:
- Can only model the fluid at a constant temperature
- Flow velocities are a few times less than the speed of sound
- Uses an arbitrary units system and so cannot easily be compared to the real world
- LBM is very memory intensive as it stores 9 values per cell in D2Q9 and 19 in D3Q19, so for a `1024x576x576` grid in 3D it stores 6,455,033,856 values

## How to run it
This project uses LibGDX in Java. Launch the project by running `Lwjgl3Launcher.java` located at `lwjgl3/src/main/java/io/github/wind_tunnel/lwjgl3/Lwjgl3Launcher.java`

- Left-click to draw
- Right-click to erase

When in 3D mode, use:
- WASD to rotate the simulation area
- UP arrow to zoom in
- DOWN arrow to zoom out
- Scroll to select a drawing plane

## About
This is an incompressible 2D and 3D wind tunnel made by Nathan Becker as an A-Level Computer Science NEA project.

## Statistics
xxx lines of code \
~ xxx hours of coding \
xxx words of documentation \
xx / xx for documentation \
xx / xx for coding

package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.wind_tunnel.ENums.Solver;

public class LatticeBoltzmannCFDSolver {

    private Settings settings;
    private ThreeDimensionalRenderer renderer;
    private Keybinds keybinds;

    private float cellDimensions;

    // array of densities named by their relative offset to the cell (in 3D)
    private float[] densities;
    private int[][] relativeDirections = {
        {0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {1, -1, 0}, {-1, 0, 0}, {-1, 1, 0}, {-1, -1, 0}, {0, 1, 0}, {0, -1, 0},
        {0, 0, -1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, -1}, {0, -1, -1}, {0, 0, 1}, {1, 0, 1}, {-1, 0, 1}, {0, 1, 1}, {0, -1, 1}
    };

    private int simulationX;
    private int simulationY;
    private int simulationZ;
    private int oldSimulationX;
    private int oldSimulationY;
    private int oldSimulationZ;
    private int index;

    private boolean[] barriers;
    private int neighbours;
    private int oldNeighbours;
    private boolean[] storedBarriers;

    private Vector3 rotatedPoint = new Vector3();
    private Vector2 screenPos;
    private Vector2 mouse = new Vector2();
    private Vector3 cellPosition = new Vector3();

    private float four9ths = 4/9f;
    private float one9th = 1/9f;
    private float one36th = 1/36f;
    private float one3rd = 1/3f;
    private float one18th = 1/18f;
    // D3Q19 weight of each relative direction: 1/3 at origin, 1/18 along an axis, 1/36 along a diagonal
    private float[] weights3D = {
        one3rd, one18th, one36th, one36th, one18th, one36th, one36th, one18th, one18th,
        one18th, one36th, one36th, one36th, one36th, one18th, one36th, one36th, one36th, one36th
    };
    private float v;
    private float one15vv;
    private float one3v3vv;
    private float one_3v3vv;

    private float cellDensity;
    private Vector3 cellVelocity = new Vector3();
    private float omega;
    private float vx3;
    private float vy3;
    private float vxvx;
    private float vyvy;
    private float twovxvy;
    private float vxvxvyvy;
    private float one5vxvxvyvy;
    private float one9thDensity;
    private float one36thDensity;
    private Vector3 flowVector = new Vector3();
    private float disturbanceThreshold = 0.1f;

    private int numOfColours = 600;
    private Color[] colours = new Color[numOfColours];
    float contrast;

    private float[] cellAverageVelocities;
    private float[] cellDensities;

    private Vector2 tail = new Vector2();
    private Vector2 head = new Vector2();
    private Vector2 tip = new Vector2();
    private Vector3 centre = new Vector3();

    private static LatticeBoltzmannCFDSolver instance;

    public static LatticeBoltzmannCFDSolver getInstance() {
        if (instance == null) {
            instance = new LatticeBoltzmannCFDSolver();
        }
        return instance;
    }

    private LatticeBoltzmannCFDSolver() {
        this.settings = Settings.getInstance();
        this.renderer = new ThreeDimensionalRenderer();
        initialiseFluid();
        this.colours = calculateColours();
    }

    public void initialiseFluid() {
        oldSimulationX = simulationX;
        oldSimulationY = simulationY;
        oldSimulationZ = simulationZ;
        oldNeighbours = neighbours;

        simulationX = (int) settings.getResolution().x;
        simulationY = (int) settings.getResolution().y;
        simulationZ = (int) settings.getResolution().z;

        if (settings.getSolver().equals(Solver.TWO_D)) {
            neighbours = 9;
        } else {
            neighbours = 19;
        }

        this.densities = new float[simulationX*simulationY*simulationZ*neighbours];
        this.cellAverageVelocities = new float[simulationX*simulationY*simulationZ*3];
        this.cellDensities = new float[simulationX*simulationY*simulationZ];
        this.barriers = scaleBarriersUsingResolution();

        v = settings.getFlowSpeed();
        one15vv = 1-1.5f*v*v;
        one3v3vv = 1+3*v+3*v*v;
        one_3v3vv = 1-3*v+3*v*v;

        for (int x=0; x<simulationX; x++) {
            for (int y=0; y<simulationY; y++) {
                for (int z=0; z<simulationZ; z++) {
                    initialiseCell(x, y, z);
                }
            }
        }
        zeroBarriers();
    }

    public void initialiseCell(int x, int y, int z) {
        // this moves the fluid towards an equilibruim/resting state based on the Maxwell-Boltzmann Distribution curve
        index = densitiesCellIndexAt(x, y, z);
        if (neighbours == 9) {
            densities[index] = four9ths * one15vv;
            densities[index+1] = one9th * one3v3vv;
            densities[index+4] = one9th * one_3v3vv;
            densities[index+7] = one9th * one15vv;
            densities[index+8] = one9th * one15vv;
            densities[index+2] = one36th * one3v3vv;
            densities[index+3] = one36th * one3v3vv;
            densities[index+5] = one36th * one_3v3vv;
            densities[index+6] = one36th * one_3v3vv;
        } else {
            densities[index] = one3rd * one15vv;
            densities[index+1] = one18th * one3v3vv;
            densities[index+4] = one18th * one_3v3vv;
            densities[index+7] = one18th * one15vv;
            densities[index+8] = one18th * one15vv;
            densities[index+9] = one18th * one15vv;
            densities[index+14] = one18th * one15vv;
            densities[index+2] = one36th * one3v3vv;
            densities[index+3] = one36th * one3v3vv;
            densities[index+5] = one36th * one_3v3vv;
            densities[index+6] = one36th * one_3v3vv;
            densities[index+10] = one36th * one3v3vv;
            densities[index+11] = one36th * one_3v3vv;
            densities[index+12] = one36th * one15vv;
            densities[index+13] = one36th * one15vv;
            densities[index+15] = one36th * one3v3vv;
            densities[index+16] = one36th * one_3v3vv;
            densities[index+17] = one36th * one15vv;
            densities[index+18] = one36th * one15vv;
        }
    }

    public void doStep() {
        collide();
        stream();
        bounce();
    }

    public void collide() {
        omega = 1/(3*settings.getViscosity()+0.5f);
        for (int x=0; x<simulationX; x++) {
            for (int y=0; y<simulationY; y++) {
                for (int z=0; z<simulationZ; z++) {
                    if (isBarrier(x, y, z)) {continue;}
                    index = densitiesCellIndexAt(x, y, z);

                    cellDensity = 0;
                    for (int count=0; count<neighbours; count++) {
                        cellDensity += densities[index+count];
                    }
                    // calculate the cell's x and y average velocity for calculating colours and relaxation time
                    cellVelocity.x = densities[index+1]+densities[index+2]+densities[index+3]-densities[index+4]-densities[index+5]-densities[index+6];
                    cellVelocity.y = densities[index+2]-densities[index+3]+densities[index+5]-densities[index+6]+densities[index+7]-densities[index+8];


                    if (neighbours == 19) {
                        cellVelocity.x += densities[index+10]+densities[index+15]-densities[index+11]-densities[index+16];
                        cellVelocity.y += densities[index+12]+densities[index+17]-densities[index+13]-densities[index+18];
                        cellVelocity.z = densities[index+14]-densities[index+9]+densities[index+15]+densities[index+16]+densities[index+17]+densities[index+18]-densities[index+10]-densities[index+11]-densities[index+12]-densities[index+13];
                        cellVelocity.z /= cellDensity;
                    }

                    cellVelocity.x /= cellDensity;
                    cellVelocity.y /= cellDensity;

                    // store the x and y velocities to draw colours later
                    cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)] = cellVelocity.x;
                    cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+1] = cellVelocity.y;
                    cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+2] = cellVelocity.z;
                    cellDensities[cellIndexAt(x, y, z)] = cellDensity;

                    // pre-calculate re-used values
                    vx3 = 3*cellVelocity.x;
                    vy3 = 3*cellVelocity.y;
                    vxvx = cellVelocity.x*cellVelocity.x;
                    vyvy = cellVelocity.y*cellVelocity.y;
                    twovxvy = 2*cellVelocity.x*cellVelocity.y;
                    vxvxvyvy = vxvx+vyvy;
                    one5vxvxvyvy = 1.5f*vxvxvyvy;
                    one9thDensity = one9th*cellDensity;
                    one36thDensity = one36th*cellDensity;

                    // relaxation equations based on the Maxwell-Boltzmann Distribution curve
                    if (neighbours == 9){
                        densities[index] += omega*(four9ths*cellDensity * (1-one5vxvxvyvy)-densities[index]);
                        densities[index+1] += omega*(one9thDensity*(1+vx3 + 4.5f*vxvx - one5vxvxvyvy)-densities[index+1]);
                        densities[index+4] += omega*(one9thDensity*(1-vx3 + 4.5f*vxvx - one5vxvxvyvy)-densities[index+4]);
                        densities[index+7] += omega*(one9thDensity*(1+vy3 + 4.5f*vyvy - one5vxvxvyvy)-densities[index+7]);
                        densities[index+8] += omega*(one9thDensity*(1-vy3 + 4.5f*vyvy - one5vxvxvyvy)-densities[index+8]);
                        densities[index+2] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[index+2]);
                        densities[index+5] += omega*(one36thDensity*(1-vx3+vy3 + 4.5f*(vxvxvyvy-twovxvy) - one5vxvxvyvy)-densities[index+5]);
                        densities[index+3] += omega*(one36thDensity*(1+vx3-vy3 + 4.5f*(vxvxvyvy-twovxvy) - one5vxvxvyvy)-densities[index+3]);
                        densities[index+6] += omega*(one36thDensity*(1-vx3-vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[index+6]);
                    } else {
                        // equilibrium for each direction: weight*density*(1 + 3(e*u) + 4.5(e*u)^2 - 1.5(u*u))
                        for (int count=0; count<19; count++) {
                            float eu = relativeDirections[count][0]*cellVelocity.x + relativeDirections[count][1]*cellVelocity.y + relativeDirections[count][2]*cellVelocity.z;
                            float equilibrium = weights3D[count]*cellDensity*(1 + 3*eu + 4.5f*eu*eu - 1.5f*((cellVelocity.x*cellVelocity.x)+(cellVelocity.y*cellVelocity.y)+(cellVelocity.z*cellVelocity.z)));
                            densities[index+count] += omega*(equilibrium-densities[index+count]);
                        }
                    }
                }
            }
        }
    }

    public void stream() {
        // move fluid using the LBM optimization Esoteric Pull
        if (neighbours == 9) {
            for (int x=0; x<simulationX-1; x++) {
                for (int y = simulationY-1; y>0; y--) {
                    index = densitiesCellIndexAt(x, y, 0);
                    densities[index+7] = densities[densitiesCellIndexAt(x, y-1, 0)+7]; // 010
                    densities[index+5] = densities[densitiesCellIndexAt(x+1, y-1, 0)+5]; // -110
                }
            }
            for (int x=simulationX-1; x>0; x--) {
                for (int y=simulationY-1; y>0; y--) {
                    index = densitiesCellIndexAt(x, y, 0);
                    densities[index+1] = densities[densitiesCellIndexAt(x-1, y, 0)+1]; // 100
                    densities[index+2] = densities[densitiesCellIndexAt(x-1, y-1, 0)+2]; // 110
                }
            }
            for (int x=simulationX-1; x>0; x--) {
                for (int y=0; y<simulationY-1; y++) {
                    index = densitiesCellIndexAt(x, y, 0);
                    densities[index+8] = densities[densitiesCellIndexAt(x, y+1, 0)+8]; // 0-10
                    densities[index+3] = densities[densitiesCellIndexAt(x-1, y+1, 0)+3]; // 1-10
                }
            }
            for (int x=0; x<simulationX-1; x++) {
                for (int y=0; y<simulationY-1; y++) {
                    index = densitiesCellIndexAt(x, y, 0);
                    densities[index+4] = densities[densitiesCellIndexAt(x+1, y, 0)+4]; // -100
                    densities[index+6] = densities[densitiesCellIndexAt(x+1, y+1, 0)+6]; // -1-10
                }
            }

            for (int y=0; y<simulationY-1; y++) {
                densities[densitiesCellIndexAt(0, y, 0)+8] = densities[densitiesCellIndexAt(0, y+1, 0)+8]; // 0-10
            }
            for (int y=simulationY-1; y>0; y--) {
                densities[densitiesCellIndexAt(simulationX-1, y, 0)+7] = densities[densitiesCellIndexAt(simulationX-1, y-1, 0)+7]; // 010
            }

            // set the left, right, top and bottom cells to be in equilibrium (aka inject new fluid)
            v = settings.getFlowSpeed();

            for (int y=0; y<simulationY; y++) {
                index = densitiesCellIndexAt(0, y, 0);
                if (!isBarrier(0, y, 0)) { // left
                    densities[index+1] = one9th*(1 + 3*v + 3*v*v); // 100
                    densities[index+2] = one36th*(1 + 3*v + 3*v*v); // 110
                    densities[index+3] = one36th*(1 + 3*v + 3*v*v); // 1-10
                }
                if (!isBarrier(simulationX-1, y, 0)) { // right
                    densities[densitiesCellIndexAt(simulationX-1, y, 0)+4] = one9th*(1 - 3*v + 3*v*v); // -100
                    densities[densitiesCellIndexAt(simulationX-1, y, 0)+5] = one36th*(1 - 3*v + 3*v*v); // -110
                    densities[densitiesCellIndexAt(simulationX-1, y, 0)+6] = one36th*(1 - 3*v + 3*v*v); // -1-10
                }
            }

            for (int x=0; x<simulationX; x++) {
                // top
                index = densitiesCellIndexAt(x, 0, 0);
                densities[index] = four9ths*(1 - 1.5f*v*v); // 000
                densities[index+1] = one9th*(1 + 3*v + 3*v*v); // 100
                densities[index+4] = one9th*(1 - 3*v + 3*v*v); // -100
                densities[index+7] = one9th*(1 - 1.5f*v*v); // 010
                densities[index+8] = one9th*(1 - 1.5f*v*v); // 0-10
                densities[index+2] = one36th*(1 + 3*v + 3*v*v); // 110
                densities[index+3] = one36th*(1 + 3*v + 3*v*v); // 1-10
                densities[index+5] = one36th*(1 - 3*v + 3*v*v); // -110
                densities[index+6] = one36th*(1 - 3*v + 3*v*v); // -1-10
                // bottom
                index = densitiesCellIndexAt(x, simulationY-1, 0);
                densities[index] = four9ths*(1 - 1.5f*v*v); // 000
                densities[index+1] = one9th*(1 + 3*v + 3*v*v); // 100
                densities[index+4] = one9th*(1 - 3*v + 3*v*v); // -100
                densities[index+7] = one9th*(1 - 1.5f*v*v); // 010
                densities[index+8] = one9th*(1 - 1.5f*v*v); // 0-10
                densities[index+2] = one36th*(1 + 3*v + 3*v*v); // 110
                densities[index+3] = one36th*(1 + 3*v + 3*v*v); // 1-10
                densities[index+5] = one36th*(1 - 3*v + 3*v*v); // -110
                densities[index+6] = one36th*(1 - 3*v + 3*v*v); // -1-10
            }
        } else { // 3D streaming

            for (int d=1; d<19; d++) {
                int cx = relativeDirections[d][0];
                int cy = relativeDirections[d][1];
                int cz = relativeDirections[d][2];

                for (int i=0; i<simulationX-Math.abs(cx); i++) {
                    int x;
                    if (cx > 0) {x = simulationX-1-i;}
                    else {x = i;}

                    for (int j=0; j<simulationY-Math.abs(cy); j++) {
                        int y;
                        if (cy > 0) {y = simulationY-1-j;}
                        else {y = j;}

                        for (int k=0; k<simulationZ-Math.abs(cz); k++) {
                            int z;
                            if (cz > 0) {z = simulationZ-1-k;}
                            else {z = k;}
                            densities[densitiesCellIndexAt(x, y, z)+d] = densities[densitiesCellIndexAt(x-cx, y-cy, z-cz)+d];
                        }
                    }
                }
            }

            // set the inlet (left) and outlet (right) (directions pointing into the grid), top, bottom, front and back faces, to equilibrium (aka inject new fluid)
            v = settings.getFlowSpeed();
            one15vv = 1-1.5f*v*v;
            one3v3vv = 1+3*v+3*v*v;
            one_3v3vv = 1-3*v+3*v*v;

            for (int x=0; x<simulationX; x++) {
                for (int y=0; y<simulationY; y++) {
                    for (int z=0; z<simulationZ; z++) {
                        if (isBarrier(x, y, z)) {continue;} // skip all barriers
                        index = densitiesCellIndexAt(x, y, z);
                        if (x == 0) { // inlet (left) (directions pointing into the grid)
                            densities[index+1] = one18th * one3v3vv;
                            densities[index+2] = one36th * one3v3vv;
                            densities[index+3] = one36th * one3v3vv;
                            densities[index+10] = one36th * one3v3vv;
                            densities[index+15] = one36th * one3v3vv;
                        } else if (x == simulationX-1) { // outlet (right) (directions pointing into the grid)
                            densities[index+4] = one18th * one_3v3vv;
                            densities[index+5] = one36th * one_3v3vv;
                            densities[index+6] = one36th * one_3v3vv;
                            densities[index+11] = one36th * one_3v3vv;
                            densities[index+16] = one36th * one_3v3vv;
                        } else if (y == 0 || y == simulationY-1 || z == 0 || z == simulationZ-1) { // top, bottom, front and back
                            densities[index] = one3rd * one15vv;
                            densities[index+1] = one18th * one3v3vv;
                            densities[index+4] = one18th * one_3v3vv;
                            densities[index+7] = one18th * one15vv;
                            densities[index+8] = one18th * one15vv;
                            densities[index+9] = one18th * one15vv;
                            densities[index+14] = one18th * one15vv;
                            densities[index+2] = one36th * one3v3vv;
                            densities[index+3] = one36th * one3v3vv;
                            densities[index+5] = one36th * one_3v3vv;
                            densities[index+6] = one36th * one_3v3vv;
                            densities[index+10] = one36th * one3v3vv;
                            densities[index+11] = one36th * one_3v3vv;
                            densities[index+12] = one36th * one15vv;
                            densities[index+13] = one36th * one15vv;
                            densities[index+15] = one36th * one3v3vv;
                            densities[index+16] = one36th * one_3v3vv;
                            densities[index+17] = one36th * one15vv;
                            densities[index+18] = one36th * one15vv;
                        }
                    }
                }
            }
        }
    }

    public void bounce() {
        // "bounce" the fluid off barriers

        for (int x=0; x<simulationX; x++) {
            for (int y=0; y<simulationY; y++) {
                for (int z=0; z<simulationZ; z++) {
                    if (isBarrier(x, y, z)) {
                        index = densitiesCellIndexAt(x, y, z);
                        if (densities[index+7] > 0) {densities[densitiesCellIndexAt(x, y-1, z)+8] += densities[index+7];} // 010
                        if (densities[index+8] > 0) {densities[densitiesCellIndexAt(x, y+1, z)+7] += densities[index+8];} // 0-10
                        if (densities[index+1] > 0) {densities[densitiesCellIndexAt(x-1, y, z)+4] += densities[index+1];} // 100
                        if (densities[index+4] > 0) {densities[densitiesCellIndexAt(x+1, y, z)+1] += densities[index+4];} // -100

                        if (densities[index+2] > 0) {densities[densitiesCellIndexAt(x-1, y-1, z)+6] += densities[index+2];} // 110
                        if (densities[index+5] > 0) {densities[densitiesCellIndexAt(x+1, y-1, z)+3] += densities[index+5];} // -110
                        if (densities[index+6] > 0) {densities[densitiesCellIndexAt(x+1, y+1, z)+2] += densities[index+6];} // -1-10
                        if (densities[index+3] > 0) {densities[densitiesCellIndexAt(x-1, y+1, z)+5] += densities[index+3];} // 1-10

                        if (neighbours == 19) {
                            if (densities[index+9] > 0) {densities[densitiesCellIndexAt(x, y, z+1)+14] += densities[index+9];} // 00-1
                            if (densities[index+10] > 0) {densities[densitiesCellIndexAt(x-1, y, z+1)+16] += densities[index+10];} // 10-1
                            if (densities[index+11] > 0) {densities[densitiesCellIndexAt(x+1, y, z+1)+15] += densities[index+11];} // -10-1
                            if (densities[index+12] > 0) {densities[densitiesCellIndexAt(x, y-1, z+1)+18] += densities[index+12];} // 01-1
                            if (densities[index+13] > 0) {densities[densitiesCellIndexAt(x, y+1, z+1)+17] += densities[index+13];} // 0-1-1
                            if (densities[index+14] > 0) {densities[densitiesCellIndexAt(x, y, z-1)+9] += densities[index+14];} // 001
                            if (densities[index+15] > 0) {densities[densitiesCellIndexAt(x-1, y, z-1)+11] += densities[index+15];} // 101
                            if (densities[index+16] > 0) {densities[densitiesCellIndexAt(x+1, y, z-1)+10] += densities[index+16];} // -101
                            if (densities[index+17] > 0) {densities[densitiesCellIndexAt(x, y-1, z-1)+13] += densities[index+17];} // 011
                            if (densities[index+18] > 0) {densities[densitiesCellIndexAt(x, y+1, z-1)+12] += densities[index+18];} // 0-11
                        }
                    }
                }
            }
        }
        zeroBarriers();
    }

    public void render(ShapeRenderer sr) {
        if (neighbours == 9) {
            cellDimensions = (float) Gdx.graphics.getWidth()/simulationX;
        }

        drawBarriers();
        contrast = 1/(3*settings.getFlowSpeed());
        renderer.preCalculateRotationMatrices();

        for (int x=0; x<simulationX; x++) {
            for (int y=0; y<simulationY; y++) {
                for (int z=0; z<simulationZ; z++) {

                    if (settings.getRenderOnlySelectedZPlane() && z != settings.getSelectedZPlane()) {
                        continue;
                    }/* else if (neighbours == 19 && !settings.getSimulationRunning()) {
                        if (!isBarrier(x, y, z)) {
                            continue;
                        }
                    } else if (!(isBarrier(x, y, z) || z == settings.getSelectedZPlane() || x == 0 || y == 0 || z == 0 || x == simulationX-1 || y == simulationY-1 || z == simulationZ-1)) {
                        continue;
                    }*/ else if (neighbours == 19) {
                        flowVector.set(cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)]-settings.getFlowSpeed(), cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+1], cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+2]);
                        if (!isBarrier(x, y, z) && flowVector.len2() < disturbanceThreshold*disturbanceThreshold*settings.getFlowSpeed()*settings.getFlowSpeed()) {
                            continue; // if fluid is close to undisturbed, don't draw it
                        }
                    }

                    if (settings.getLoadedLevel() && settings.getFixedBarrierAt(x, y, z)) {
                        if (neighbours == 9) {
                            sr.setColor(Color.BLACK);
                        } else {
                            sr.setColor(Color.GRAY);
                        }
                    } else if (isBarrier(x, y, z)) {
                        sr.setColor(Color.WHITE);
                    } else {
                        sr.setColor(colours[calculateColourIndex(x, y, z)]); // calculates colour based on plotMode
                    }
                    if (neighbours == 9) {
                        sr.rect(x*cellDimensions, y*cellDimensions, cellDimensions, cellDimensions);
                    } else {
                        rotatedPoint = renderer.rotate(x-(simulationX/2f), y-(simulationY/2f), z-(simulationZ/2f));
                        screenPos = renderer.pointProjection(rotatedPoint);
                        if (screenPos == null) {continue;}
                        sr.rect(screenPos.x-1, screenPos.y-1, 2, 2); // .rect uses fewer triangles than .circle, therefore is more efficient
                    }
                }
            }
        }
        if (settings.getShowFlowLines()) {
            drawFlowLines(sr);
        }
    }

    public void drawBarriers() {
        keybinds = Keybinds.getInstance();

        mouse.x = Gdx.input.getX();
        mouse.y = Gdx.graphics.getHeight()-Gdx.input.getY();
        if (!settings.getPlaceBarriers()) {
            if (!Gdx.input.isButtonPressed(keybinds.getBindAt(9)) && !Gdx.input.isButtonPressed(keybinds.getBindAt(10))) {
                settings.setPlaceBarriers(true);
            }
        }
        if (neighbours == 9) {
            cellPosition.x = Math.round(((mouse.x-(cellDimensions/2f))/1920)*simulationX);
            cellPosition.y = Math.round(((mouse.y-(cellDimensions/2f))/1080)*simulationY);
            cellPosition.z = 0;
            settings.setIntersectionPoint(cellPosition);

            if (settings.getPlaceBarriers() && !(cellPosition.x <= 0 || cellPosition.x >= simulationX-1 || cellPosition.y <= 0 || cellPosition.y >= simulationY-1)) {
                if (Gdx.input.isButtonPressed(keybinds.getBindAt(9))) {
                    drawOrEraseBarriersUsingBrushSize((int) cellPosition.x, (int) cellPosition.y, 0, true);
                }
                if (Gdx.input.isButtonPressed(keybinds.getBindAt(10))) {
                    drawOrEraseBarriersUsingBrushSize((int) cellPosition.x, (int) cellPosition.y, 0, false);
                }
            }
        } else { // 3D
            cellPosition = renderer.getIntersectionBetweenPlaneAnd(mouse); // gets intersection point between selected plane and ray from mouse
            if (cellPosition == null) { // ray (almost) parallel to the plane
                return;
            }
            cellPosition.x = Math.round(cellPosition.x+simulationX/2f);
            cellPosition.y = Math.round(cellPosition.y+simulationY/2f);
            cellPosition.z = Math.round(cellPosition.z+simulationZ/2f);
            settings.setIntersectionPoint(cellPosition);

            if (settings.getPlaceBarriers() && !(cellPosition.x <= 0 || cellPosition.x >= simulationX-1 || cellPosition.y <= 0 || cellPosition.y >= simulationY-1 || cellPosition.z <= 0 || cellPosition.z >= simulationZ-1)) {
                if (Gdx.input.isButtonPressed(keybinds.getBindAt(9))) {
                    drawOrEraseBarriersUsingBrushSize((int) cellPosition.x, (int) cellPosition.y, (int) cellPosition.z, true);
                }
                if (Gdx.input.isButtonPressed(keybinds.getBindAt(10))) {
                    drawOrEraseBarriersUsingBrushSize((int) cellPosition.x, (int) cellPosition.y, (int) cellPosition.z, false);
                }
            }
        }
    }

    public void drawOrEraseBarriersUsingBrushSize(int x, int y, int z, boolean draw) {
        int radius = settings.getBrushSize()-1;

        for (int dx=-radius; dx<=radius; dx++) {
            for (int dy=-radius; dy<=radius; dy++) {
                if (dx*dx + dy*dy > radius*radius) { // if point is outside circle
                    continue;
                }
                int cellX = x+dx;
                int cellY = y+dy;

                // point outside simulation area
                if (cellX <= 0 || cellX >= simulationX-1 || cellY <= 0 || cellY >= simulationY-1) {
                    continue;
                }
                if (draw) {
                    addBarrier(cellX, cellY, z, false);
                } else if (isBarrier(cellX, cellY, z)) {
                    removeBarrier(cellX, cellY, z);
                }
            }
        }
    }

    public void renderBrushSizePreview(ShapeRenderer sr) {
        if (neighbours == 19) {return;}

        int radius = settings.getBrushSize()-1;
        int x = (int) (Gdx.input.getX()/cellDimensions);
        int y = (int) ((Gdx.graphics.getHeight()-Gdx.input.getY())/cellDimensions);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(1, 1, 1, 0.4f);

        for (int dx=-radius; dx<=radius; dx++) {
            for (int dy=-radius; dy<=radius; dy++) {
                if (dx*dx + dy*dy > radius*radius) { // if point is outside circle
                    continue;
                }

                int cellX = x+dx;
                int cellY = y+dy;

                // point outside simulation area
                if (cellX <= 0 || cellX >= simulationX-1 || cellY <= 0 || cellY >= simulationY-1) {
                    continue;
                }
                sr.rect(cellX*cellDimensions, cellY*cellDimensions, cellDimensions, cellDimensions);
            }
        }
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public boolean[] scaleBarriersUsingResolution() {
        boolean[] newBarriers = new boolean[simulationX*simulationY*simulationZ];

        if (barriers == null) {
            return newBarriers;
        }
        if (neighbours != oldNeighbours) {
            return newBarriers;
        }
        if (oldSimulationX == simulationX && oldSimulationY == simulationY && oldSimulationZ == simulationZ) {
            return barriers; // no change in resolution
        }

        storedBarriers = barriers;

        if (simulationX > oldSimulationX) { // upscaling
            for (int x=0; x<simulationX; x++) {
                for (int y=0; y<simulationY; y++) {
                    for (int z=0; z<simulationZ; z++) {
                        newBarriers[cellIndexAt(x, y, z)] = storedBarriers[(x*oldSimulationX/simulationX) + oldSimulationX*((y*oldSimulationY/simulationY) + oldSimulationY*(z*oldSimulationZ/simulationZ))];
                    }
                }
            }
        } else { // downscaling
            int scaleX = oldSimulationX/simulationX;
            int scaleY = oldSimulationY/simulationY;
            int scaleZ = oldSimulationZ/simulationZ;
            for (int x=0; x<simulationX; x++) {
                for (int y=0; y<simulationY; y++) {
                    for (int z=0; z<simulationZ; z++) {
                        int count = 0;
                        for (int dx=0; dx<scaleX; dx++) {
                            for (int dy=0; dy<scaleY; dy++) {
                                for (int dz=0; dz<scaleZ; dz++) {
                                    if (storedBarriers[(x*scaleX+dx) + oldSimulationX*((y*scaleY+dy) + oldSimulationY*(z*scaleZ+dz))]) {
                                        count += 1;
                                    }
                                }
                            }
                        }
                        if (count*2 >= scaleX*scaleY*scaleZ) {
                            newBarriers[cellIndexAt(x, y, z)] = true;
                        }
                    }
                }
            }
        }
        return newBarriers;
    }

    public void drawFlowLines(ShapeRenderer sr) {
        int z;
        if (neighbours == 9) {
            z = 0;
            sr.setColor(Color.BLACK);
        } else {
            z = settings.getSelectedZPlane();
            sr.setColor(Color.PINK);
        }

        float scale = 0.9f/settings.getFlowSpeed();

        for (int x=0; x<simulationX; x++) {
            for (int y=0; y<simulationY; y++) {
                if (isBarrier(x, y, z)) {continue;}

                // vector in cell units
                flowVector.set(cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)]*scale, cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+1]*scale, cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+2]*scale);
                if (neighbours == 9) {flowVector.z = 0;}

                if (flowVector.len() < 0.05) {continue;} // if flowspeed of cell is too slow

                if (neighbours == 9) {
                    // centre the arrow on the cell
                    tail.set((x+0.5f)*cellDimensions, (y+0.5f)*cellDimensions);
                    head.set(flowVector.x*cellDimensions/2, flowVector.y*cellDimensions/2);
                    drawArrow(sr, new Vector2(tail.x-head.x, tail.y-head.y), new Vector2(tail.x+head.x, tail.y+head.y));
                } else {
                    centre.set(x-simulationX/2f, y-simulationY/2f, z-simulationZ/2f);

                    rotatedPoint = renderer.rotate(centre.x-flowVector.x/2, centre.y-flowVector.y/2, centre.z-flowVector.z/2);
                    tail.set(renderer.pointProjection(rotatedPoint));
                    if (tail == null) {continue;}

                    rotatedPoint = renderer.rotate(centre.x+flowVector.x/2, centre.y+flowVector.y/2, centre.z+flowVector.z/2);
                    tip.set(renderer.pointProjection(rotatedPoint));
                    if (tip == null) {continue;}

                    drawArrow(sr, tail, tip);
                }
            }
        }
    }

    private void drawArrow(ShapeRenderer sr, Vector2 tail, Vector2 head) {
        float dx = head.x-tail.x;
        float dy = head.y-tail.y;
        float length = (float) Math.sqrt(dx*dx + dy*dy);
        if (length < 1) {return;} // too small

        sr.rectLine(tail.x, tail.y, head.x, head.y, 2);

        // arrow heads
        float headLength = Math.min(length/4, cellDimensions);

        double arcAngle = Math.toDegrees(Math.atan2(tail.y-head.y, tail.x-head.x));

        double xChange = Math.cos(Math.toRadians(arcAngle));
        double yChange = Math.sin(Math.toRadians(arcAngle));

        double[] angles = {arcAngle+45, arcAngle-45};
        for (double angle : angles) {
            double xOffset = headLength*Math.cos(Math.toRadians(angle));
            double yOffset = headLength*Math.sin(Math.toRadians(angle));
            sr.rectLine((float) (head.x+xChange), (float) (head.y+yChange), (float) (head.x+xOffset+xChange), (float) (head.y+yOffset+yChange), 2);
        }
    }

    public void fillEnclosedShapes(boolean fixedBarrier) {
        // fill enclosed spaces using Breadth-First Search (BFS)

        int numOfCells = simulationX*simulationY*simulationZ;
        boolean[] visited = new boolean[numOfCells];
        int[] queue = new int[numOfCells];
        int head = 0;
        int tail = 0;

        // start from every fluid cell on the edge of the grid
        for (int index=0; index<numOfCells; index++) {
            int x = getXFromIndex(index);
            int y = getYFromIndex(index);
            int z = getZFromIndex(index);
            if (x <= 0 || y <= 0 || x >= simulationX-1 || y >= simulationY-1 || (simulationZ > 1 && (z <= 0 || z >= simulationZ-1)) && !barriers[index]) {
                visited[index] = true;
                queue[tail] = index;
                tail += 1;
            }
        }

        int[] adjacent = new int[6];
        while (head < tail) {
            int index = queue[head];
            head += 1;
            int x = getXFromIndex(index);
            int y = getYFromIndex(index);
            int z = getZFromIndex(index);

            if (x > 0) {adjacent[0] = index-1;}
            else {adjacent[0] = -1;}
            if (x < simulationX-1) {adjacent[1] = index+1;}
            else {adjacent[1] = -1;}

            if (y > 0) {adjacent[2] = index-simulationX;}
            else {adjacent[2] = -1;}
            if (y < simulationY-1) {adjacent[3] = index+simulationX;}
            else {adjacent[3] = -1;}

            if (z > 0) {adjacent[4] = index-(simulationX*simulationY);}
            else {adjacent[4] = -1;}
            if (z < simulationZ-1) {adjacent[5] = index+(simulationX*simulationY);}
            else {adjacent[5] = -1;}

            for (int neighbourIndex : adjacent) {
                if (neighbourIndex >= 0 && !barriers[neighbourIndex] && !visited[neighbourIndex]) {
                    visited[neighbourIndex] = true;
                    queue[tail] = neighbourIndex;
                    tail += 1;
                }
            }
        }

        // anything the BFS couldn't reach is sealed off by barriers, therefore, fill it
        for (int index=0; index<numOfCells; index++) {
            if (!barriers[index] && !visited[index]) {
                addBarrier(getXFromIndex(index), getYFromIndex(index), getZFromIndex(index), fixedBarrier);
            }
        }
    }

    public Color[] calculateColours() {
        float[][] keyColours = {
            {0, 0, 1}, // blue (slowest)
            {0, 1, 1}, // cyan
            {0, 1, 0}, // green
            {1, 1, 0}, // yellow
            {1, 0, 0}, // red (fastest)
        };
        int pointer = 0;

        for (int count=0; count<numOfColours; count++) {
            double position = (double) count/numOfColours + 0.045*Math.sin(4*Math.PI*count/numOfColours);
            position = Math.max(0, Math.min(1, position));

            int pair = Math.min((int) (position*4), 3); // which two key colours this position is between
            double blend = position*4 - pair; // 0 at the first of the two, 1 at the second
            float[] from = keyColours[pair];
            float[] to = keyColours[pair+1];

            float r = toEightBit(from[0] + blend*(to[0]-from[0]));
            float g = toEightBit(from[1] + blend*(to[1]-from[1]));
            float b = toEightBit(from[2] + blend*(to[2]-from[2]));
            colours[pointer] = new Color(r, g, b, 1f);
            pointer += 1;
        }
        return colours;
    }

    private float toEightBit(double amount) {
        return Math.round(amount*255)/255f;
    }


    public int calculateColourIndex(int x, int y, int z) {
        double index = 0;
        if (settings.getPlot().equals("speed")) {
            index = numOfColours*(1.5*Math.sqrt(cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)]*cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)] + cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+1]*cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+1] + cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+2]*cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+2])*contrast);
        } else if (settings.getPlot().equals("x velocity")) {
            index = numOfColours*(0.5+(cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)]*contrast));
        } else if (settings.getPlot().equals("y velocity")) {
            index = numOfColours*(0.5+(cellAverageVelocities[averageVelocitiesCellIndexAt(x, y, z)+1]*contrast));
        } else if (settings.getPlot().equals("density")) {
            index = numOfColours*(0.5+(cellDensities[cellIndexAt(x, y, z)]-1)*0.6*contrast*contrast);
        } else if (settings.getPlot().equals("curl")) {
            index = numOfColours*(0.5+calculateCurl(x, y, z)*1.5*contrast);;
        }
        if (index < 0) {index = 0;}
        if (index >= numOfColours) {index = numOfColours-1;}
        return (int) index;
    }

    public float calculateCurl(int x, int y, int z) {
        if (x <= 0 || y <= 0 || x >= simulationX-1 || y >= simulationY-1) {return 0;}

        float curl = 0;
        if (!isBarrier(x, y, z)) {
            curl = cellAverageVelocities[averageVelocitiesCellIndexAt(x+1, y, z)+1]-cellAverageVelocities[averageVelocitiesCellIndexAt(x-1, y, z)+1]-cellAverageVelocities[averageVelocitiesCellIndexAt(x, y+1, z)]+cellAverageVelocities[averageVelocitiesCellIndexAt(x, y-1, z)];
        }
        return curl;
    }


    public void addBarrier(int x, int y, int z, boolean fixedBarrier) {
        if (x > 0 && x < simulationX-1 && y > 0 && y < simulationY-1) { // in simulation area
            if (neighbours == 19 && (z <= 0 || z >= simulationZ-1)) { // 3D and outside
                return;
            }
            if (!isBarrier(x, y, z)) {
                if (settings.getLoadedLevel()) {
                    if (settings.getAvailableBarriers() > 0) {
                        settings.setAvailableBarriers(settings.getAvailableBarriers()-1);
                        barriers[cellIndexAt(x, y, z)] = true;
                    }
                } else {
                    barriers[cellIndexAt(x, y, z)] = true;
                }
                if (fixedBarrier) {settings.addFixedBarrier(x, y, z);}
            }
        }
    }

    public void removeBarrier(int x, int y, int z) {
        if (settings.getLoadedLevel()) {
            if (!settings.getFixedBarrierAt(x, y, z)) {
                settings.setAvailableBarriers(settings.getAvailableBarriers()+1);
                barriers[cellIndexAt(x, y, z)] = false;
                initialiseCell(x, y, z);
            }
        } else {
            barriers[cellIndexAt(x, y, z)] = false;
            initialiseCell(x, y, z);
        }
    }

    public void removeAllBarriers() {
        for (int x=0; x<simulationX; x++) {
            for (int y=0; y<simulationY; y++) {
                for (int z=0; z<simulationZ; z++) {
                    if (isBarrier(x, y, z)) {
                        removeBarrier(x, y, z);
                    }
                }
            }
        }
    }

    public void zeroBarriers() {
        for (int x=0; x<simulationX; x++) {
            for (int y=0; y<simulationY; y++) {
                for (int z=0; z<simulationZ; z++) {
                    if (barriers[cellIndexAt(x, y, z)]) {
                        for (int count=0; count<neighbours; count++) {
                            densities[densitiesCellIndexAt(x, y, z)+count] = 0;
                        }
                    }
                }
            }
        }
    }

    public boolean isBarrier(int x, int y, int z) {
        return barriers[cellIndexAt(x, y, z)];
    }

    private int cellIndexAt(int x, int y, int z) {
        return x+simulationX*(y+simulationY*z);
    }

    private int densitiesCellIndexAt(int x, int y, int z) {
        return cellIndexAt(x, y, z)*neighbours;
    }

    private int averageVelocitiesCellIndexAt(int x, int y, int z) {
        return cellIndexAt(x, y, z)*3;
    }

    private int getXFromIndex(int index) {
        return index%simulationX;
    }

    private int getYFromIndex(int index) {
        return (index/simulationX)%simulationY;
    }

    private int getZFromIndex(int index) {
        return index/(simulationX*simulationY);
    }

    public int getNeighbours() {return this.neighbours;}
    public void setNeighbours(int neighbours) {this.neighbours = neighbours;}
}

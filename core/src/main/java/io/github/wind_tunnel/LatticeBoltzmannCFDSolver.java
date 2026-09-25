package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.Arrays;

public class LatticeBoltzmannCFDSolver {

    /*
    TO DO:
    - research how to initialise a cell with the correct starting densities - done
    - research why each relative internal cell direction gets a specific weight - done
    - create a collide function - 100% - done
    - create a stream function - 50% - need 3D
    - create a bounce function - 50% - need 3D
     */

    private Settings settings;
    private ThreeDimensionalRenderer renderer;

    private float cellDimensions;

    // array of densities named by their relative offset to the cell (in 3D)
    private float[][][][] densities;
    /*relativeDirections = {
        {0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {1, -1, 0}, {-1, 0, 0}, {-1, 1, 0}, {-1, -1, 0}, {0, 1, 0}, {0, -1, 0},
        {0, 0, -1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, -1}, {0, -1, -1}, {0, 0, 1}, {1, 0, 1}, {-1, 0, 1}, {0, 1, 1}, {0, -1, 1}
    };*/
    private ArrayList<String> barriers = new ArrayList<>(); // the xyz coordinates are stored as a String, separated by spaces, for example, 32 2 54
    private int neighbours;

    private Vector3 rotatedPoint = new Vector3();
    private Vector2 screenPos;
    private Vector2 mouse = new Vector2();
    private Vector2 cellPosition = new Vector2();

    private float four9ths = 4/9f;
    private float one9th = 1/9f;
    private float one36th = 1/36f;
    private float one3rd = 1/3f;
    private float one18th = 1/18f;
    private float v;
    private float one15vv;
    private float one3v3vv;
    private float one_3v3vv;

    private float cellDensity;
    private float cellXVelocity;
    private float cellYVelocity;
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
    private float one18thDensity;

    private int numOfColors = 600;
    private ArrayList<Color> colours = new ArrayList<>();

    private float[][][][] cellAverageVelocities;

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
        this.colours = calculateColours(numOfColors);
    }

    public void initialiseFluid() {
        if (settings.getSolver().equals("2D LBM")) {
            neighbours = 9;
        } else {
            neighbours = 19;
        }
        this.densities = new float[(int) settings.getResolution().x][(int) settings.getResolution().y][(int) settings.getResolution().z][neighbours];
        this.cellAverageVelocities = new float[(int) settings.getResolution().x][(int) settings.getResolution().y][(int) settings.getResolution().z][2];

        v = settings.getFlowSpeed();
        one15vv = 1-1.5f*v*v;
        one3v3vv = 1+3*v+3*v*v;
        one_3v3vv = 1-3*v+3*v*v;

        for (int x=0; x<settings.getResolution().x; x++) {
            for (int y=0; y<settings.getResolution().y; y++) {
                for (int z=0; z<settings.getResolution().z; z++) {
                    initialiseCell(x, y, z);
                }
            }
        }
        zeroBarriers();
    }

    public void initialiseCell(int x, int y, int z) {
        // this moves the fluid towards an equilibruim/resting state based on the Maxwell-Boltzmann Distribution curve
        if (settings.getSolver().equals("2D LBM")) {
            densities[x][y][z][0] = four9ths * one15vv;
            densities[x][y][z][1] = one9th * one3v3vv;
            densities[x][y][z][4] = one9th * one_3v3vv;
            densities[x][y][z][7] = one9th * one15vv;
            densities[x][y][z][8] = one9th * one15vv;
            densities[x][y][z][2] = one36th * one3v3vv;
            densities[x][y][z][3] = one36th * one3v3vv;
            densities[x][y][z][5] = one36th * one_3v3vv;
            densities[x][y][z][6] = one36th * one_3v3vv;
        } else {
            densities[x][y][z][0] = one3rd * one15vv;
            densities[x][y][z][1] = one18th * one3v3vv;
            densities[x][y][z][4] = one18th * one_3v3vv;
            densities[x][y][z][7] = one18th * one15vv;
            densities[x][y][z][8] = one18th * one15vv;
            densities[x][y][z][9] = one18th * one15vv;
            densities[x][y][z][14] = one18th * one15vv;
            densities[x][y][z][2] = one36th * one3v3vv;
            densities[x][y][z][3] = one36th * one3v3vv;
            densities[x][y][z][5] = one36th * one_3v3vv;
            densities[x][y][z][6] = one36th * one_3v3vv;
            densities[x][y][z][10] = one36th * one3v3vv;
            densities[x][y][z][11] = one36th * one3v3vv;
            densities[x][y][z][12] = one36th * one_3v3vv;
            densities[x][y][z][13] = one36th * one_3v3vv;
            densities[x][y][z][15] = one36th * one3v3vv;
            densities[x][y][z][16] = one36th * one3v3vv;
            densities[x][y][z][17] = one36th * one_3v3vv;
            densities[x][y][z][18] = one36th * one_3v3vv;
        }
    }

    public void doStep() {
        collide();
        stream();
        bounce();
    }

    public void collide() {
        if (settings.getSolver().equals("2D LBM")) {
            neighbours = 9;
        } else {
            neighbours = 19;
        }
        omega = 1/(3*settings.getViscosity()+0.5f);
        for (int x=0; x<settings.getResolution().x; x++) {
            for (int y=0; y<settings.getResolution().y; y++) {
                for (int z=0; z<settings.getResolution().z; z++) {
                    if (isBarrier(x, y, z)) {continue;}

                    cellDensity = 0;
                    for (int count=0; count<neighbours; count++) {
                        cellDensity += densities[x][y][z][count];
                    }
                    // calculate the cell's x and y average velocity for calculating colours and relaxation time
                    cellXVelocity = densities[x][y][z][1]+densities[x][y][z][2]+densities[x][y][z][3]-densities[x][y][z][4]-densities[x][y][z][5]-densities[x][y][z][6];
                    cellYVelocity = densities[x][y][z][2]-densities[x][y][z][3]+densities[x][y][z][5]-densities[x][y][z][6]+densities[x][y][z][7]-densities[x][y][z][8];
                    if (settings.getSolver().equals("3D LBM")) {
                        cellXVelocity += densities[x][y][z][10]-densities[x][y][z][11]+densities[x][y][z][15]-densities[x][y][z][16];
                        cellYVelocity += densities[x][y][z][12]-densities[x][y][z][13]+densities[x][y][z][17]-densities[x][y][z][18];
                    }
                    cellXVelocity /= cellDensity;
                    cellYVelocity /= cellDensity;

                    // store the x and y velocities to draw colours later
                    cellAverageVelocities[x][y][z][0] = cellXVelocity;
                    cellAverageVelocities[x][y][z][1] = cellYVelocity;

                    // pre-calculate re-used values
                    vx3 = 3*cellXVelocity;
                    vy3 = 3*cellYVelocity;
                    vxvx = cellXVelocity*cellXVelocity;
                    vyvy = cellYVelocity*cellYVelocity;
                    twovxvy = 2*cellXVelocity*cellYVelocity;
                    vxvxvyvy = vxvx+vyvy;
                    one5vxvxvyvy = 1.5f*vxvxvyvy;
                    one9thDensity = one9th*cellDensity;
                    one36thDensity = one36th*cellDensity;
                    one18thDensity = one18th*cellDensity;

                    // relaxation equations based on the Maxwell-Boltzmann Distribution curve
                    if (settings.getSolver().equals("2D LBM")){
                        densities[x][y][z][0] += omega*(four9ths*cellDensity * (1-one5vxvxvyvy)-densities[x][y][z][0]);
                        densities[x][y][z][1] += omega*(one9thDensity*(1+vx3 + 4.5f*vxvx - one5vxvxvyvy)-densities[x][y][z][1]);
                        densities[x][y][z][4] += omega*(one9thDensity*(1-vx3 + 4.5f*vxvx - one5vxvxvyvy)-densities[x][y][z][4]);
                        densities[x][y][z][7] += omega*(one9thDensity*(1+vy3 + 4.5f*vyvy - one5vxvxvyvy)-densities[x][y][z][7]);
                        densities[x][y][z][8] += omega*(one9thDensity*(1-vy3 + 4.5f*vyvy - one5vxvxvyvy)-densities[x][y][z][8]);
                        densities[x][y][z][2] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][2]);
                        densities[x][y][z][5] += omega*(one36thDensity*(1-vx3+vy3 + 4.5f*(vxvxvyvy-twovxvy) - one5vxvxvyvy)-densities[x][y][z][5]);
                        densities[x][y][z][3] += omega*(one36thDensity*(1+vx3-vy3 + 4.5f*(vxvxvyvy-twovxvy) - one5vxvxvyvy)-densities[x][y][z][3]);
                        densities[x][y][z][6] += omega*(one36thDensity*(1-vx3-vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][6]);
                    } else {
                        densities[x][y][z][0] += omega*(one3rd*cellDensity * (1-one5vxvxvyvy)-densities[x][y][z][0]);
                        densities[x][y][z][1] += omega*(one18thDensity*(1+vx3 + 4.5f*vxvx - one5vxvxvyvy)-densities[x][y][z][1]);
                        densities[x][y][z][4] += omega*(one18thDensity*(1-vx3 + 4.5f*vxvx - one5vxvxvyvy)-densities[x][y][z][4]);
                        densities[x][y][z][7] += omega*(one18thDensity*(1+vy3 + 4.5f*vyvy - one5vxvxvyvy)-densities[x][y][z][7]);
                        densities[x][y][z][8] += omega*(one18thDensity*(1-vy3 + 4.5f*vyvy - one5vxvxvyvy)-densities[x][y][z][8]);
                        densities[x][y][z][9] += omega*(one18thDensity*(1-vy3 + 4.5f*vyvy - one5vxvxvyvy)-densities[x][y][z][9]);
                        densities[x][y][z][14] += omega*(one18thDensity*(1-vy3 + 4.5f*vyvy - one5vxvxvyvy)-densities[x][y][z][14]);

                        densities[x][y][z][2] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][2]);
                        densities[x][y][z][3] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][3]);
                        densities[x][y][z][5] += omega*(one36thDensity*(1-vx3+vy3 + 4.5f*(vxvxvyvy-twovxvy) - one5vxvxvyvy)-densities[x][y][z][5]);
                        densities[x][y][z][6] += omega*(one36thDensity*(1-vx3-vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][6]);
                        densities[x][y][z][10] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][10]);
                        densities[x][y][z][11] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][11]);
                        densities[x][y][z][12] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][12]);
                        densities[x][y][z][13] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][13]);
                        densities[x][y][z][15] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][15]);
                        densities[x][y][z][16] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][16]);
                        densities[x][y][z][17] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][17]);
                        densities[x][y][z][18] += omega*(one36thDensity*(1+vx3+vy3 + 4.5f*(vxvxvyvy+twovxvy) - one5vxvxvyvy)-densities[x][y][z][18]);
                    }
                }
            }
        }
    }

    public void stream() {
        // move fluid using the LBM optimization Esoteric Pull

        if (settings.getSolver().equals("2D LBM")) {
            for (int x=0; x<settings.getResolution().x-1; x++) {
                for (int y = (int) (settings.getResolution().y-1); y>0; y--) {
                    densities[x][y][0][7] = densities[x][y-1][0][7]; // 010
                    densities[x][y][0][5] = densities[x+1][y-1][0][5]; // -110
                }
            }
            for (int x = (int) (settings.getResolution().x-1); x>0; x--) {
                for (int y = (int) (settings.getResolution().y-1); y>0; y--) {
                    densities[x][y][0][1] = densities[x-1][y][0][1]; // 100
                    densities[x][y][0][2] = densities[x-1][y-1][0][2]; // 110
                }
            }
            for (int x = (int) (settings.getResolution().x-1); x>0; x--) {
                for (int y=0; y<settings.getResolution().y-1; y++) {
                    densities[x][y][0][8] = densities[x][y+1][0][8]; // 0-10
                    densities[x][y][0][3] = densities[x-1][y+1][0][3]; // 1-10
                }
            }
            for (int x=0; x<settings.getResolution().x-1; x++) {
                for (int y=0; y<settings.getResolution().y-1; y++) {
                    densities[x][y][0][4] = densities[x+1][y][0][4]; // -100
                    densities[x][y][0][6] = densities[x+1][y+1][0][6]; // -1-10
                }
            }

            for (int y=0; y<settings.getResolution().y-1; y++) {
                densities[0][y][0][8] = densities[0][y+1][0][8]; // 0-10
            }
            for (int y = (int) (settings.getResolution().y-1); y>0; y--) {
                densities[(int) (settings.getResolution().x-1)][y][0][7] = densities[(int) (settings.getResolution().x-1)][y-1][0][7]; // 010
            }

            // set the left, right, top and bottom cells to be in equilibrium (aka inject new fluid)
            v = settings.getFlowSpeed();

            for (int y=0; y<settings.getResolution().y; y++) {
                if (!isBarrier(0, y, 0)) { // left
                    densities[0][y][0][1] = one9th*(1 + 3*v + 3*v*v); // 100
                    densities[0][y][0][2] = one36th*(1 + 3*v + 3*v*v); // 110
                    densities[0][y][0][3] = one36th*(1 + 3*v + 3*v*v); // 1-10
                }
                if (!isBarrier((int) (settings.getResolution().x-1), y, 0)) { // right
                    densities[(int) (settings.getResolution().x-1)][y][0][4] = one9th*(1 - 3*v + 3*v*v); // -100
                    densities[(int) (settings.getResolution().x-1)][y][0][5] = one36th*(1 - 3*v + 3*v*v); // -110
                    densities[(int) (settings.getResolution().x-1)][y][0][6] = one36th*(1 - 3*v + 3*v*v); // -1-10
                }
            }

            for (int x=0; x<settings.getResolution().x; x++) {
                // top
                densities[x][0][0][0] = four9ths*(1 - 1.5f*v*v); // 000
                densities[x][0][0][1] = one9th*(1 + 3*v + 3*v*v); // 100
                densities[x][0][0][4] = one9th*(1 - 3*v + 3*v*v); // -100
                densities[x][0][0][7] = one9th*(1 - 1.5f*v*v); // 010
                densities[x][0][0][8] = one9th*(1 - 1.5f*v*v); // 0-10
                densities[x][0][0][2] = one36th*(1 + 3*v + 3*v*v); // 110
                densities[x][0][0][3] = one36th*(1 + 3*v + 3*v*v); // 1-10
                densities[x][0][0][5] = one36th*(1 - 3*v + 3*v*v); // -110
                densities[x][0][0][6] = one36th*(1 - 3*v + 3*v*v); // -1-10
                // bottom
                densities[x][(int) (settings.getResolution().y-1)][0][0] = four9ths*(1 - 1.5f*v*v); // 000
                densities[x][(int) (settings.getResolution().y-1)][0][1] = one9th*(1 + 3*v + 3*v*v); // 100
                densities[x][(int) (settings.getResolution().y-1)][0][4] = one9th*(1 - 3*v + 3*v*v); // -100
                densities[x][(int) (settings.getResolution().y-1)][0][7] = one9th*(1 - 1.5f*v*v); // 010
                densities[x][(int) (settings.getResolution().y-1)][0][8] = one9th*(1 - 1.5f*v*v); // 0-10
                densities[x][(int) (settings.getResolution().y-1)][0][2] = one36th*(1 + 3*v + 3*v*v); // 110
                densities[x][(int) (settings.getResolution().y-1)][0][3] = one36th*(1 + 3*v + 3*v*v); // 1-10
                densities[x][(int) (settings.getResolution().y-1)][0][5] = one36th*(1 - 3*v + 3*v*v); // -110
                densities[x][(int) (settings.getResolution().y-1)][0][6] = one36th*(1 - 3*v + 3*v*v); // -1-10
            }
        }

        // add 3D streaming
    }

    public void bounce() {
        // "bounce" the fluid off barriers

        for (int x=0; x<settings.getResolution().x; x++) {
            for (int y=0; y<settings.getResolution().y; y++) {
                for (int z=0; z<settings.getResolution().z; z++) {
                    if (isBarrier(x, y, z)) {
                        if (densities[x][y][z][7] > 0) {densities[x][y-1][z][8] += densities[x][y][z][7];} // 010
                        if (densities[x][y][z][8] > 0) {densities[x][y+1][z][7] += densities[x][y][z][8];} // 0-10
                        if (densities[x][y][z][1] > 0) {densities[x-1][y][z][4] += densities[x][y][z][1];} // 100
                        if (densities[x][y][z][4] > 0) {densities[x+1][y][z][1] += densities[x][y][z][4];} // -100

                        if (densities[x][y][z][2] > 0) {densities[x-1][y-1][z][6] += densities[x][y][z][2];} // 110
                        if (densities[x][y][z][5] > 0) {densities[x+1][y-1][z][3] += densities[x][y][z][5];} // -110
                        if (densities[x][y][z][6] > 0) {densities[x+1][y+1][z][2] += densities[x][y][z][6];} // -1-10
                        if (densities[x][y][z][3] > 0) {densities[x-1][y+1][z][5] += densities[x][y][z][3];} // 1-10

                        if (settings.getSolver().equals("3D LBM")) {
                            // add 3D bounce-back
                        }
                    }
                }
            }
        }
        zeroBarriers();
    }

    public void render(ShapeRenderer sr) {
        if (settings.getSolver().equals("2D LBM")) {
            cellDimensions = 1920/settings.getResolution().x;
        }

        mouse.x = Gdx.input.getX();
        mouse.y = 1080-Gdx.input.getY();
        if (settings.getSolver().equals("2D LBM")) {
            cellPosition.x = Math.round(((mouse.x-(cellDimensions/2))/1920)*settings.getResolution().x);
            cellPosition.y = Math.round(((mouse.y-(cellDimensions/2))/1080)*settings.getResolution().y);

            if (!settings.getDrawBarriers()) {
                if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    settings.setDrawBarriers(true);
                }
            }

            if (settings.getDrawBarriers() && !(cellPosition.x == 0 || cellPosition.x == settings.getResolution().x-1 || cellPosition.y == 0 || cellPosition.y == settings.getResolution().y-1)) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    addBarrier((int) cellPosition.x, (int) cellPosition.y, 0);
                } /*else {
                    // add the ability to add a barrier using scroll wheel and left click in 3D
                }*/
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    if (isBarrier((int) cellPosition.x, (int) cellPosition.y, 0)) {
                        removeBarrier((int) cellPosition.x, (int) cellPosition.y, 0);
                    }
                }
            }
        }

        for (int x=0; x<settings.getResolution().x; x++) {
            for (int y=0; y<settings.getResolution().y; y++) {
                for (int z=0; z<settings.getResolution().z; z++) {
                    if (!(x == 0 || y == 0 || z == 0 || x == settings.getResolution().x-1 || y == settings.getResolution().y-1 || z == settings.getResolution().z-1)) {continue;}
                    sr.setColor(colours.get(calculateColourIndex(x, y, z))); // calculates colour based on plotMode
                    if (isBarrier(x, y, z)) {sr.setColor(Color.WHITE);}
                    if (settings.getSolver().equals("2D LBM")) {
                        sr.rect(x*cellDimensions, y*cellDimensions, cellDimensions, cellDimensions);
                    } else {
                        rotatedPoint = renderer.rotate(x-(settings.getResolution().x/2), y-(settings.getResolution().y/2), z-(settings.getResolution().z/2));
                        screenPos = renderer.pointProjection(rotatedPoint);
                        if (screenPos == null) {continue;}
                        sr.circle(screenPos.x, screenPos.y, 1);
                    }
                }
            }
        }
    }

    public ArrayList<Color> calculateColours(int numOfColours) {
        colours = new ArrayList<>();
        for (int c=0; c<numOfColours; c++) {
            double h = (2.0/3)*(1 - c*1.0/numOfColours);
            h += 0.03 * Math.sin(6*Math.PI*h);
            java.awt.Color awtColour = new java.awt.Color(java.awt.Color.HSBtoRGB((float) h, 1, 1));
            float r = awtColour.getRed()/255f;
            float g = awtColour.getGreen()/255f;
            float b = awtColour.getBlue()/255f;
            colours.add(new Color(r, g, b, 1f));
        }
        return colours;
    }

    public int calculateColourIndex(int x, int y, int z) {
        double index = 0;
        float contrast = 1/(3*settings.getFlowSpeed());
        /*if (settings.getPlot().equals("speed")) {
            index = ;
        } else */if (settings.getPlot().equals("x velocity")) {
            index = numOfColors*(0.5+(cellAverageVelocities[x][y][z][0]*contrast));
        } else if (settings.getPlot().equals("y velocity")) {
            index = numOfColors*(0.5+(cellAverageVelocities[x][y][z][1]*contrast));
        } /*else if (settings.getPlot().equals("density")) {
            index = ;
        } else if (settings.getPlot().equals("curl")) {
            index = ;
        }*/
        if (index < 0) {index = 0;}
        if (index >= numOfColors) {index = numOfColors-1;}
        return (int) index;
    }

    public void addBarrier(int x, int y, int z) {
        if (!barriers.contains(x + " " + y + " " + z)) {
            barriers.add(x + " " + y + " " + z);
        }
    }

    public void removeBarrier(int x, int y, int z) {
        barriers.remove(x + " " + y + " " + z);
        initialiseCell(x, y, z);
    }

    public void clearBarriers() {
        barriers.clear();
    }

    public void zeroBarriers() {
        String[] pos;
        for (String xyz : barriers) {
            pos = xyz.split(" ");
            Arrays.fill(densities[Integer.parseInt(pos[0])][Integer.parseInt(pos[1])][Integer.parseInt(pos[2])], 0);
        }
    }

    public boolean isBarrier(int x, int y, int z) {
        return barriers.contains(x + " " + y + " " + z);
    }
}


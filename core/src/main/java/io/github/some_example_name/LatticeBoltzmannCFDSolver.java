package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LatticeBoltzmannCFDSolver {
    private MenuUtil util;
    private Settings settings;

    // setup mesh
    private Vector3 numberOfCells;
    private Vector3 cellDimensions;

    // array of densities named by their relative offset to the cell (in 3D)
    private HashMap<String, Double>[][][] densities;
    private Integer[] directions = {-1, 0, 1};
    private Integer[][] invalidDirections = {{-1, 1, -1}, {-1, -1, -1}, {1, 1, -1}, {1, -1, -1}, {-1, 1, 1}, {-1, -1, 1}, {1, 1, 1}, {1, -1, 1}};

    public LatticeBoltzmannCFDSolver() {
        this.util = new MenuUtil();
        this.settings = Settings.getInstance();
        initialiseCells();
    }

    public void initialiseCells() {
        this.numberOfCells = new Vector3(settings.getResolution().x, settings.getResolution().y, settings.getResolution().z);
        this.cellDimensions = new Vector3(util.getScreenDimensions().x/numberOfCells.x, util.getScreenDimensions().y/numberOfCells.y, util.getScreenDimensions().y/numberOfCells.z);
        this.densities = new HashMap[(int) settings.getResolution().x][(int) settings.getResolution().y][(int) settings.getResolution().z];

        boolean skip;
        for (int x=0; x<settings.getResolution().x; x++) {
            for (int y=0; y<settings.getResolution().y; y++) {
                for (int z=0; z<settings.getResolution().z; z++) {
                    for (int relativeX : directions) {
                        for (int relativeY : directions) {
                            for (int relativeZ : directions) {
                                skip = false;
                                for (Integer[] invalid : invalidDirections) {
                                    if (relativeX == invalid[0] && relativeY == invalid[1] && relativeZ == invalid[2]) {
                                        skip = true;
                                        break;
                                    }
                                }
                                if (skip) {break;}
                                densities[x][y][z] = new HashMap<>();
                                densities[x][y][z].put(relativeX + "" + relativeY + "" + relativeZ, 0.0);
                            }
                        }
                    }
                }
            }
        }
    }
}


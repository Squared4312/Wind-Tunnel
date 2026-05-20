package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;

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
        this.densities = new HashMap<String, Double>[(int) numberOfCells.x][(int) numberOfCells.y][(int) numberOfCells.z];

        boolean skip;
        for (int x : directions) {
            for (int y : directions) {
                for (int z : directions) {
                    skip = false;
                    for (Integer[] invalid : invalidDirections) {
                        if (x == invalid[0] && y == invalid[1] && z == invalid[2]) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip) {break;}

                    densities.put(String.valueOf(x) + String.valueOf(y) + String.valueOf(z), 0.0);
                }
            }
        }
    }
}


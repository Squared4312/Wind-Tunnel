package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class LatticeBoltzmannCFDSolver {
    private MenuUtil util;
    private Settings settings;

    // setup mesh
    private Vector2 numberOfCells;
    private Vector2 cellDimensions;

    // these arrays of densities are named by their relative offset to the cell (in 3D), where _1 = -1
    private double[][] density000;
    private double[][] density010;
    private double[][] density0_10;
    private double[][] density100;
    private double[][] density_100;
    private double[][] density_110;
    private double[][] density110;
    private double[][] density_1_10;
    private double[][] density1_10;

    private double[][] density101;

    public LatticeBoltzmannCFDSolver() {
        this.util = new MenuUtil();
        this.settings = Settings.getInstance();
        initialiseCells();
    }

    public void initialiseCells() {
        this.numberOfCells = new Vector2(settings.getResolution().x, settings.getResolution().y);
        this.cellDimensions = new Vector2(util.getScreenDimensions().x/numberOfCells.x, util.getScreenDimensions().y/numberOfCells.y);
        this.density000 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
        this.density010 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
        this.density0_10 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
        this.density100 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
        this.density_100 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
        this.density_110 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
        this.density110 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
        this.density_1_10 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
        this.density1_10 = new double[(int) numberOfCells.x][(int) numberOfCells.y];

        this.density101 = new double[(int) numberOfCells.x][(int) numberOfCells.y];
    }
}


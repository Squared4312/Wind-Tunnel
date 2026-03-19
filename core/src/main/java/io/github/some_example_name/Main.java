package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.*;

public class Main extends ApplicationAdapter {

    private ShapeRenderer sr;
    private Cell[][] cells;

    private Vector2 screenDimensions;

    private Vector2 numberOfCells;
    private Vector2 cellDimensions;

    private boolean renderArrows = true;
    private float timeStep = 0.001f; // time step
    private float viscosity = 0.1f; // fluid viscosity

    private double[][] u; // horizontal velocity on vertical faces
    private double[][] v; // vertical velocity on horizontal faces
    private double[][] p; // pressure at cell centres

    @Override
    public void create() {
        screenDimensions = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        sr = new ShapeRenderer();

        // setup mesh
        numberOfCells = new Vector2(32, 18); // keep the 16:9 aspect ratio
        cellDimensions = new Vector2(screenDimensions.x/numberOfCells.x, screenDimensions.y/numberOfCells.y);

        u = new double[(int) (numberOfCells.x+1)][(int) numberOfCells.y];
        v = new double[(int) numberOfCells.x][(int) (numberOfCells.y+1)];
        p = new double[(int) numberOfCells.x][(int) numberOfCells.y];

        cells = new Cell[(int) numberOfCells.x][(int) numberOfCells.y];
        for (int row=0; row<numberOfCells.x; row++) {
            for (int column=0; column<numberOfCells.y; column++) {
                Vector2 centre = new Vector2(row*cellDimensions.x+(cellDimensions.x/2), column*cellDimensions.y+(cellDimensions.y/2));
                Vector2 dimensions = new Vector2(cellDimensions.x, cellDimensions.y);
                Vector2 velocity = new Vector2(0, 0);
                cells[row][column] = new Cell(centre, dimensions, velocity);
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // calculate the average velocity of the fluid from the cell's faces
        for (int row=0; row<numberOfCells.x; row++) {
            for (int column=0; column<numberOfCells.y; column++) {
                Vector2 averageVelocity = new Vector2((float) ((u[row][column]+u[row+1][column])/2), (float) (v[row][column]+v[row][column+1])/2);
                cells[row][column].setVelocity(averageVelocity);

                //cells[row][column].setVelocity(new Vector2(Gdx.input.getX()-cells[row][column].getCentre().x, (screenDimensions.y-Gdx.input.getY())-cells[row][column].getCentre().y));
            }
        }

        sr.begin(ShapeRenderer.ShapeType.Filled);
            for (Cell[] cellRow : cells) {
                for (Cell cell : cellRow) {
                    cell.draw(sr, screenDimensions, renderArrows);
                }
            }
        sr.end();
    }

    @Override
    public void dispose() {
        sr.dispose();
    }
}

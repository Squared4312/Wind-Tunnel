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

    @Override
    public void create() {
        screenDimensions = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        sr = new ShapeRenderer();

        // setup mesh
        numberOfCells = new Vector2(32, 18); // keep the 16:9 aspect ratio
        cellDimensions = new Vector2(screenDimensions.x/numberOfCells.x, screenDimensions.y/numberOfCells.y);

        cells = new Cell[(int) numberOfCells.x][(int) numberOfCells.y];
        for (int row=0; row<cells.length; row++) {
            for (int column=0; column<cells[row].length; column++) {
                Vector2 centre = new Vector2((float) (row*cellDimensions.x+(0.5*cellDimensions.x)), (float) (column*cellDimensions.y+(0.5*cellDimensions.y)));
                Vector2 dimensions = new Vector2(cellDimensions.x, cellDimensions.y);
                Vector2 velocity = new Vector2(0, 0);
                cells[row][column] = new Cell(centre, dimensions, velocity);
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                cell.setVelocity(new Vector2(Gdx.input.getX()-cell.getCentre().x, (screenDimensions.y-Gdx.input.getY())-cell.getCentre().y));
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

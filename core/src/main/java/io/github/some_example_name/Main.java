package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {

    private ShapeRenderer sr;
    private Cell[][] cells;

    private Integer screenWidth;
    private Integer screenHeight;

    private Integer[] numberOfCells;
    private float[] cellDimensions;

    @Override
    public void create() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        sr = new ShapeRenderer();

        // setup mesh
        numberOfCells = new Integer[] {32, 18}; // keep the 16:9 aspect ratio
        cellDimensions = new float[] {screenWidth/numberOfCells[0], screenHeight/numberOfCells[1]};

        cells = new Cell[numberOfCells[0]][numberOfCells[1]];
        for (int row=0; row<cells.length; row++) {
            for (int column=0; column<cells[row].length; column++) {
                Vector2 position = new Vector2(row*cellDimensions[0], column*cellDimensions[1]);
                Vector2 velocity = new Vector2(0, 0);
                cells[row][column] = new Cell(position, velocity);
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        sr.begin(ShapeRenderer.ShapeType.Filled);
            for (Cell[] cellRow : cells) {
                for (Cell cell : cellRow) {
                    cell.draw(sr);
                }
            }
        sr.end();
    }

    @Override
    public void dispose() {}
}

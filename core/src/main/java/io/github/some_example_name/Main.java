package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.*;

public class Main extends ApplicationAdapter {
    private ShapeRenderer sr;
    private SpriteBatch batch;

    private Cell[][] cells;

    private Vector2 screenDimensions;

    private Vector2 numberOfCells;
    private Vector2 cellDimensions;

    private double weightO = 4.0/9.0; // Origin
    private double weightAxis = 1.0/9.0; // N E S W
    private double weightDiagonals = 1.0/36.0; // NE NW SE SW

    private String menu = "main";
    private String nextMenu;
    private Stack menuHistory = new Stack(10);

    private MainMenu mainMenu;
    private AboutMenu aboutMenu;
    private SettingsMenu settingsMenu;
    private LevelsMenu levelsMenu;
    private FreeplayMenu freeplayMenu;

    @Override
    public void create() {
        screenDimensions = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        mainMenu = new MainMenu();
        aboutMenu = new AboutMenu();
        settingsMenu = new SettingsMenu();
        levelsMenu = new LevelsMenu();
        freeplayMenu = new FreeplayMenu();

        // setup mesh
        numberOfCells = new Vector2(32, 18); // keep the 16:9 aspect ratio
        cellDimensions = new Vector2(screenDimensions.x/numberOfCells.x, screenDimensions.y/numberOfCells.y);

        cells = new Cell[(int) numberOfCells.x][(int) numberOfCells.y];
        for (int column=0; column<numberOfCells.x; column++) {
            for (int row=0; row<numberOfCells.y; row++) {
                Vector2 centre = new Vector2(column*cellDimensions.x+(cellDimensions.x/2), row*cellDimensions.y+(cellDimensions.y/2));
                Vector2 dimensions = new Vector2(cellDimensions.x, cellDimensions.y);
                cells[column][row] = new Cell(centre, dimensions, settingsMenu.getFlowSpeed(), weightO, weightAxis, weightDiagonals);
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        if (menu.equals("main")) {
            nextMenu = mainMenu.checkIfButtonsClicked();
        } else if (menu.equals("about")) {
            nextMenu = aboutMenu.checkIfButtonsClicked();
        } else if (menu.equals("settings")) {
            nextMenu = settingsMenu.checkIfButtonsClicked();
        } else if (menu.equals("levels")) {
            nextMenu = levelsMenu.checkIfButtonsClicked();
        } else if (menu.equals("freeplay")) {
            nextMenu = freeplayMenu.checkIfButtonsClicked();
        }

        if (!nextMenu.equals(menu)) {
            if ("back".equals(nextMenu)) {
                if (!menuHistory.isEmpty()) {
                    menu = menuHistory.pop();
                }
            } else {
                menuHistory.push(menu);
                menu = nextMenu;
            }
        }

        if (menu.equals("main")) {
            mainMenu.render(sr, batch);
        } else if (menu.equals("about")) {
            aboutMenu.render(sr, batch);
        } else if (menu.equals("settings")) {
            settingsMenu.render(sr, batch);
        } else if (menu.equals("levels")) {
            levelsMenu.render(sr, batch);
        } else if (menu.equals("freeplay")) {
            freeplayMenu.render(sr, batch);
        }

    }

    @Override
    public void dispose() {
        sr.dispose();
        batch.dispose();
    }
}

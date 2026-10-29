package io.github.wind_tunnel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.wind_tunnel.Menus.*;

public class Main extends ApplicationAdapter {
    private ShapeRenderer sr;
    private SpriteBatch batch;

    private Vector2 screenDimensions;

    private String menu = "mainMenu";
    private String nextMenu;
    private Stack menuHistory = new Stack(10);
    private Settings settings;
    private Keybinds keybinds;
    private Levels levels;

    private MainMenu mainMenu;
    private AboutMenu aboutMenu;
    private SettingsMenu settingsMenu;
    private LevelsMenu levelsMenu;
    private FreeplayMenu freeplayMenu;
    private KeybindsMenu keybindsMenu;
    private ImportFileMenu importFileMenu;

    private LatticeBoltzmannCFDSolver cfdSolver;

    @Override
    public void create() {
        Gdx.input.setInputProcessor(new InputHandler());
        screenDimensions = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        settings = Settings.getInstance();
        keybinds = Keybinds.getInstance();
        levels = new Levels();

        mainMenu = new MainMenu();
        aboutMenu = new AboutMenu();
        settingsMenu = new SettingsMenu();
        levelsMenu = new LevelsMenu();
        freeplayMenu = new FreeplayMenu();
        keybindsMenu = new KeybindsMenu();
        importFileMenu = new ImportFileMenu();


        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        Gdx.graphics.setTitle("Wind Tunnel FPS: " + Gdx.graphics.getFramesPerSecond() + "/" + Gdx.graphics.getDisplayMode().refreshRate);

        keybinds.checkForKeyPresses();

        if (menu.equals("mainMenu")) {
            nextMenu = mainMenu.checkIfButtonsClicked();
        } else if (menu.equals("aboutMenu")) {
            nextMenu = aboutMenu.checkIfButtonsClicked();
        } else if (menu.equals("settingsMenu")) {
            nextMenu = settingsMenu.checkIfButtonsClicked();
        } else if (menu.equals("levelsMenu")) {
            nextMenu = levelsMenu.checkIfButtonsClicked();
        } else if (menu.equals("freeplayMenu")) {
            nextMenu = freeplayMenu.checkIfButtonsClicked();
        } else if (menu.equals("keybindsMenu")) {
            nextMenu = keybindsMenu.checkIfButtonsClicked();
        } else if (menu.equals("importFileMenu")) {
            nextMenu = importFileMenu.checkIfButtonsClicked();
        }

        if (nextMenu.equals("remove all barriers")) {
            cfdSolver.removeAllBarriers();
            nextMenu = "settingsMenu";
        }
        if (nextMenu.equals("importSuccess")) {
            menuHistory.pop();
            menu = "settingsMenu";
            nextMenu = "back";
        }
        if (nextMenu.equals("mainMenu")) {
            settings.setLoadedLevel(false);
            settings.resetLockedSettings();
            cfdSolver.removeAllBarriers();
        }
        if ((menu.equals("levelsMenu") || menu.equals("freeplayMenu")) && nextMenu.equals("settingsMenu")) {
            settings.setOldResolution(settings.getResolution());
            settings.setOldSolver(settings.getSolver());
        }

        if (nextMenu.equals("quit")) {
            Gdx.app.exit();
        } else if (!nextMenu.equals(menu)) {
            if (menu.equals("mainMenu") && (nextMenu.equals("levelsMenu") || nextMenu.equals("freeplayMenu"))) {
                if (nextMenu.equals("levelsMenu")) {cfdSolver.removeAllBarriers();}
                else {settings.setLoadedLevel(false);}
                cfdSolver.initialiseFluid();
                settings.setSimulationRunning(false);
                if (nextMenu.equals("levelsMenu")) {
                    levels.loadSelectedLevel();
                    settings.setLoadedLevel(true);
                }
            }
            if (nextMenu.equals("back")) {
                if (!menuHistory.isEmpty()) {
                    String previousMenu = menuHistory.pop();
                    if (menu.equals("settingsMenu") && previousMenu.equals("freeplayMenu")) {
                        cfdSolver.initialiseFluid();
                    }
                    menu = previousMenu;
                    if (menu.equals("levelsMenu")) {levels.loadSelectedLevel();}
                }
            } else {
                menuHistory.push(menu);
                menu = nextMenu;
            }
        }

        if (menu.equals("mainMenu")) {
            mainMenu.render(sr, batch);
        } else if (menu.equals("aboutMenu")) {
            aboutMenu.render(sr, batch);
        } else if (menu.equals("settingsMenu")) {
            settingsMenu.render(sr, batch);
        } else if (menu.equals("levelsMenu")) {
            levelsMenu.render(sr, batch);
        } else if (menu.equals("freeplayMenu")) {
            freeplayMenu.render(sr, batch);
        } else if (menu.equals("keybindsMenu")) {
            keybindsMenu.render(sr, batch);
        } else if (menu.equals("importFileMenu")) {
            importFileMenu.render(sr, batch);
        }
    }

    @Override
    public void dispose() {
        sr.dispose();
        batch.dispose();
    }
}

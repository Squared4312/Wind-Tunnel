package io.github.wind_tunnel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class LevelsMenu implements Menu {
    private MenuUtil util;
    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;
    private Levels levels;

    private Rectangle backButton;
    private Rectangle settingsButton;

    private Rectangle[] levelsButton = new Rectangle[2];
    private Rectangle runButton;
    private Rectangle pauseButton;
    private Rectangle stepButton;

    private Texture backIcon;
    private Texture settingsIcon;
    private Texture pauseIcon;
    private Texture stepIcon;

    public LevelsMenu() {
        this.util = new MenuUtil();
        this.cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
        this.settings = Settings.getInstance();
        this.levels = new Levels();

        this.backIcon = util.loadIcon("back");
        this.settingsIcon = util.loadIcon("settings");
        this.pauseIcon = util.loadIcon("pause");
        this.stepIcon = util.loadIcon("stepArrow");
    }

    @Override
    public void render(ShapeRenderer sr, SpriteBatch batch) {

        if (settings.getSimulationRunning()) {
            cfdSolver.doStep();
        }

        sr.begin(ShapeRenderer.ShapeType.Filled);
            cfdSolver.render(sr);

            backButton = util.getHitbox(57.5f, 1022.5f, 75, 75);
            settingsButton = util.getHitbox(1862.5f, 57.5f, 75, 75);

            if (settings.getRenderUI()) {
                util.renderRoundedRectangle(sr, util.getButtonColor(), util.getScreenDimensions().x/2, 1022.5f, 400, 75, 16);
                levelsButton[0] = util.renderRoundedTriangle(sr, Color.WHITE, 1125, 1022.5f, 12, 90);
                levelsButton[1] = util.renderRoundedTriangle(sr, Color.WHITE, 795, 1022.5f, 12, 270);

                if (!settings.getSimulationRunning()) {
                    runButton = util.renderRoundedRectangle(sr, util.getRunButtonColor(), 835, 62.5f, 200, 75, 16);
                    util.renderRoundedTriangle(sr, Color.WHITE, 790, 62.5f, 12, 90);
                } else {
                    pauseButton = util.renderRoundedRectangle(sr, util.getPauseButtonColor(), 835, 62.5f, 200, 75, 16);
                }
                stepButton = util.renderRoundedRectangle(sr, util.getStepButtonColor(), 1085, 62.5f, 200, 75, 16);

                util.renderRoundedRectangle(sr, util.getButtonColor(), 685, 1022.5f, 125, 75, 16); // barriers
                util.renderRoundedRectangle(sr, util.getButtonColor(), 1235, 1022.5f, 125, 75, 16); // score
            }
        sr.end();

        batch.begin();
            if (settings.getRenderUI()) {
                util.renderText(batch, "level " + settings.getLevelNumber(), Color.WHITE, util.getScreenDimensions().x/2, 1022.5f, 36, "centre");

                if (!settings.getSimulationRunning()) {
                    util.renderText(batch, "run", Color.WHITE, 857.5f, 62.5f, 36, "centre");
                } else {
                    util.renderText(batch, "pause", Color.WHITE, 852.5f, 62.5f, 36, "centre");
                    util.renderIcon(batch, pauseIcon, 770, 62.5f);
                }
                util.renderText(batch, "step", Color.WHITE, 1110, 62.5f, 36, "centre");
                util.renderIcon(batch, stepIcon, 1035, 62.5f);
                util.renderText(batch, "(" + (int) settings.getIntersectionPoint().x + ", " + (int) settings.getIntersectionPoint().y + ", " + (int) settings.getIntersectionPoint().z + ")", Color.WHITE, 57.5f, 57.5f, 36, "left");

                util.renderText(batch, settings.getAvailableBarriers() + "/" + settings.getMaxBarriers(), Color.WHITE, 685, 1022.5f, 36, "centre"); // barriers
                util.renderText(batch, settings.getLevelScore() + "%", Color.WHITE, 1235, 1022.5f, 36, "centre"); // score
            }

            util.renderIcon(batch, backIcon, 57.5f, 1022.5f);
            util.renderIcon(batch, settingsIcon, 1862.5f, 57.5f);
        batch.end();
    }

    @Override
    public String checkIfButtonsClicked() {
        if (settings.getRenderUI()) {
            if (util.isButtonClicked(levelsButton[0])) {
                settings.setPlaceBarriers(false);
                changeLevelNumber(1);
                levels.loadSelectedLevel();
            }
            if (util.isButtonClicked(levelsButton[1])) {
                settings.setPlaceBarriers(false);
                changeLevelNumber(-1);
                levels.loadSelectedLevel();
            }

            if (!settings.getSimulationRunning() && util.isButtonClicked(runButton)) {
                settings.setSimulationRunning(true);
                settings.setPlaceBarriers(false);
            }
            else if (settings.getSimulationRunning() && util.isButtonClicked(pauseButton)) {
                settings.setSimulationRunning(false);
                settings.setPlaceBarriers(false);
            }
            if (util.isButtonClicked(stepButton)) {
                settings.setPlaceBarriers(false);
                if (!settings.getSimulationRunning()) {
                    cfdSolver.doStep();
                }
            }
        }

        if (util.isButtonClicked(backButton)) {
            settings.setPlaceBarriers(false);
            return "back";
        }
        if (util.isButtonClicked(settingsButton)) {
            settings.setPlaceBarriers(false);
            return "settings";
        }
        return "levels";
    }

    public void changeLevelNumber(int change) {
        if (change == 1) {
            if (settings.getLevelNumber().equals(settings.getTotalLevels())) {
                settings.setLevelNumber(1);
            } else {
                settings.setLevelNumber(settings.getLevelNumber()+1);
            }
        } else if (change == -1) {
            if (settings.getLevelNumber() == 1) {
                settings.setLevelNumber(settings.getTotalLevels());
            } else {
                settings.setLevelNumber(settings.getLevelNumber()-1);
            }
        }
    }

    public LatticeBoltzmannCFDSolver getCfdSolver() {return cfdSolver;}
}

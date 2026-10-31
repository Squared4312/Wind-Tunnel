package io.github.wind_tunnel.Menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.wind_tunnel.*;
import io.github.wind_tunnel.ENums.TextAlignment;

public class MainMenu implements Menu {
    private Settings settings;
    private MenuUtil util;
    private LatticeBoltzmannCFDSolver cfdSolver;

    private Menu settingsMenu;
    private Menu levelsMenu;
    private Menu freeplayMenu;
    private Menu aboutMenu;

    private Rectangle quitButton;
    private Rectangle settingsButton;
    private Rectangle levelsButton;
    private Rectangle freeplayButton;
    private Rectangle aboutButton;

    private Texture quitIcon;
    private Texture settingsIcon;
    private Texture aboutIcon;

    public MainMenu(Menu settingsMenu, Menu levelsMenu, Menu freeplayMenu, Menu aboutMenu) {
        this.settings = Settings.getInstance();
        this.util = new MenuUtil();
        this.cfdSolver = LatticeBoltzmannCFDSolver.getInstance();

        this.settingsMenu = settingsMenu;
        this.levelsMenu = levelsMenu;
        this.freeplayMenu = freeplayMenu;
        this.aboutMenu = aboutMenu;

        this.quitIcon = util.loadIcon("quit");
        this.settingsIcon = util.loadIcon("settings");
        this.aboutIcon = util.loadIcon("about");
    }

    @Override
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
            quitButton = util.renderButton(sr, util.getQuitButtonColor(), null, 1862.5f, 1022.5f, 75, 75, 16);
            settingsButton = util.getHitbox(1862.5f, 57.5f, 75, 75);
            levelsButton = util.renderButton(sr, Color.WHITE, Color.BLACK, util.getScreenDimensions().x/2, 580, 700, 150, 16);
            freeplayButton = util.renderButton(sr, Color.WHITE, Color.BLACK, util.getScreenDimensions().x/2, 380, 700, 150, 16);
            aboutButton = util.getHitbox(57.5f, 57.5f, 75, 75);
        sr.end();

        batch.begin();
            util.renderText(batch, "wind tunnel", Color.WHITE, util.getScreenDimensions().x/2, 880, 128, TextAlignment.CENTRE);
            util.renderText(batch, "levels", Color.WHITE, util.getScreenDimensions().x/2, 580, 64, TextAlignment.CENTRE);
            util.renderText(batch, "freeplay", Color.WHITE, util.getScreenDimensions().x/2, 380, 64, TextAlignment.CENTRE);
            util.renderIcon(batch, quitIcon, 1862.5f, 1022.5f);
            util.renderIcon(batch, settingsIcon, 1862.5f, 57.5f);
            util.renderIcon(batch, aboutIcon, 57.5f, 57.5f);
        batch.end();
    }

    @Override
    public void checkIfButtonsClicked(MenuNavigator navigator) {
        if (util.isButtonClicked(quitButton)) {
            navigator.quit();
        }
        if (util.isButtonClicked(settingsButton)) {
            navigator.open(settingsMenu);
        }
        if (util.isButtonClicked(levelsButton)) {
            navigator.open(levelsMenu);
        }
        if (util.isButtonClicked(freeplayButton)) {
            navigator.open(freeplayMenu);
        }
        if (util.isButtonClicked(aboutButton)) {
            navigator.open(aboutMenu);
        }
    }

    @Override
    public void onEnter() {
        resetLevel();
    }

    @Override
    public void onResume() {
        resetLevel();
    }

    private void resetLevel() {
        settings.setLoadedLevel(false);
        settings.resetLockedSettings();
        cfdSolver.removeAllBarriers();
    }
}

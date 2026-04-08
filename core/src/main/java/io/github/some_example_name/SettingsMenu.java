package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class SettingsMenu implements Menu {
    private double flowSpeed = 0.1;
    private double viscosity = 0.05;
    private String plot = "x velocity";
    private String mode = "drag fluid";
    private boolean showFlowLines = false;

    private MenuUtil util;

    private Rectangle quitButton;
    private Rectangle backButton;
    private Rectangle settingsButton;

    private Texture quitIcon;
    private Texture backIcon;
    private Texture settingsIcon;

    public SettingsMenu() {
        this.util = new MenuUtil();

        this.quitIcon = util.loadIcon("quit");
        this.backIcon = util.loadIcon("back");
        this.settingsIcon = util.loadIcon("settings");
    }

    @Override
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
            quitButton = util.renderButton(sr, util.getQuitColor(), null, 1862.5f, 1022.5f, 75, 75, 16);
            backButton = util.renderButton(sr, Color.BLACK, null, 57.5f, 1022.5f, 75, 75, 0);
            settingsButton = util.renderButton(sr, Color.BLACK, null, 1862.5f, 57.5f, 75, 75, 0);

            util.renderRoundedRectangle(sr, util.getSettingsButtonColor(), 600, 742.5f, 600, 75, 16); // l1
            util.renderRoundedRectangle(sr, util.getSettingsButtonColor(), 600, 642.5f, 600, 75, 16); // l2
            util.renderRoundedRectangle(sr, util.getSettingsButtonColor(), 600, 542.5f, 600, 75, 16); // l3
            util.renderRoundedRectangle(sr, util.getSettingsButtonColor(), 600, 442.5f, 600, 75, 16); // l4

            util.renderRoundedRectangle(sr, util.getSettingsButtonColor(), 1320, 742.5f, 600, 75, 16); // r1
            util.renderRoundedRectangle(sr, util.getSettingsButtonColor(), 1320, 642.5f, 600, 75, 16); // r2
            util.renderRoundedRectangle(sr, util.getSettingsButtonColor(), 1320, 542.5f, 600, 75, 16); // r3
            util.renderRoundedRectangle(sr, util.getSettingsButtonColor(), 1320, 442.5f, 600, 75, 16); // r4
        sr.end();

        batch.begin();
            util.renderText(batch, "settings", Color.WHITE, util.getScreenDimensions().x/2, 953, 96, "centre");

            util.renderText(batch, "flow speed", Color.WHITE, 320, 742.5f, 36, "left"); // l1
            util.renderText(batch, "viscosity", Color.WHITE, 320, 642.5f, 36, "left"); // l2
            util.renderText(batch, "plot", Color.WHITE, 320, 542.5f, 36, "left"); // l3
            util.renderText(batch, "mode", Color.WHITE, 320, 442.5f, 36, "left"); // l4

            util.renderText(batch, "barrier shapes", Color.WHITE, 1040, 742.5f, 36, "left"); // r1
            util.renderText(batch, "clear barriers", Color.WHITE, 1040, 642.5f, 36, "left"); // r2
            util.renderText(batch, "reset fluid", Color.WHITE, 1040, 542.5f, 36, "left"); // r3
            util.renderText(batch, "show flowlines", Color.WHITE, 1040, 442.5f, 36, "left"); // r4

            util.renderIcon(batch, quitIcon, 1862.5f, 1022.5f);
            util.renderIcon(batch, backIcon, 57.5f, 1022.5f);
            util.renderIcon(batch, settingsIcon, 1862.5f, 57.5f);
        batch.end();
    }

    @Override
    public String checkIfButtonsClicked() {
        if (util.isButtonClicked(quitButton)) {Gdx.app.exit();}
        if (util.isButtonClicked(backButton)) {return "back";}
        if (util.isButtonClicked(settingsButton)) {return "settings";}
        return "settings";
    }

    public double getFlowSpeed() {return this.flowSpeed;}
    public void setFlowSpeed(double flowSpeed) {this.flowSpeed = flowSpeed;}

    public double getViscosity() {return this.viscosity;}
    public void setViscosity(double viscosity) {this.viscosity = viscosity;}

    public String getPlot() {return this.plot;}
    public void setPlot(String plot) {this.plot = plot;}

    public String getMode() {return this.mode;}
    public void setMode(String mode) {this.mode = mode;}

    public boolean isShowFlowLines() {return this.showFlowLines;}
    public void setShowFlowLines(boolean showFlowLines) {this.showFlowLines = showFlowLines;}
}

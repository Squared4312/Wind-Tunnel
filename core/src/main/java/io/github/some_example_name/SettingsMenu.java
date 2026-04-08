package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.text.DecimalFormat;

public class SettingsMenu implements Menu {
    private double flowSpeed = 0.100;
    private double viscosity = 0.020;
    private String plot;
    private String mode;
    private boolean showFlowLines = false;

    private DecimalFormat decimalFormat;
    private String[] plotValues = {"density", "x velocity", "y velocity", "speed", "curl"};
    private String[] modeValues = {"draw barriers", "erase barriers", "drag fluid"};

    private MenuUtil util;

    private Rectangle quitButton;
    private Rectangle backButton;
    private Rectangle settingsButton;

    private Rectangle[] flowSpeedButtons = new Rectangle[2];
    private Rectangle[] viscosityButtons = new Rectangle[2];

    private Texture quitIcon;
    private Texture backIcon;
    private Texture settingsIcon;

    public SettingsMenu() {
        this.util = new MenuUtil();

        this.decimalFormat = new DecimalFormat("0.000");

        this.quitIcon = util.loadIcon("quit");
        this.backIcon = util.loadIcon("back");
        this.settingsIcon = util.loadIcon("settings");

        this.plot = this.plotValues[0];
        this.mode = this.modeValues[0];
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

            flowSpeedButtons[0] = util.renderRoundedTriangle(sr, Color.WHITE, 865, 742.5f, 12, 90);
            flowSpeedButtons[1] = util.renderRoundedTriangle(sr, Color.WHITE, 665, 742.5f, 12, 270);

            viscosityButtons[0] = util.renderRoundedTriangle(sr, Color.WHITE, 865, 642.5f, 12, 90);
            viscosityButtons[1] = util.renderRoundedTriangle(sr, Color.WHITE, 665, 642.5f, 12, 270);
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

            util.renderText(batch, decimalFormat.format(flowSpeed), Color.WHITE, 765, 742.5f, 36, "centre"); // l1 value
            util.renderText(batch, decimalFormat.format(viscosity), Color.WHITE, 765, 642.5f, 36, "centre"); // l2 value

            util.renderIcon(batch, quitIcon, 1862.5f, 1022.5f);
            util.renderIcon(batch, backIcon, 57.5f, 1022.5f);
            util.renderIcon(batch, settingsIcon, 1862.5f, 57.5f);
        batch.end();
    }

    @Override
    public String checkIfButtonsClicked() {
        if (util.isButtonClicked(flowSpeedButtons[0]) && flowSpeed < 0.120) {flowSpeed += 0.005; flowSpeed = Math.round(flowSpeed*1000)/1000d;}
        if (util.isButtonClicked(flowSpeedButtons[1]) && flowSpeed > 0.005) {flowSpeed -= 0.005; flowSpeed = Math.round(flowSpeed*1000)/1000d;}

        if (util.isButtonClicked(viscosityButtons[0]) && viscosity < 0.200) {viscosity += 0.005; viscosity = Math.round(viscosity*1000)/1000d;}
        if (util.isButtonClicked(viscosityButtons[1]) && viscosity > 0.005) {viscosity -= 0.005; viscosity = Math.round(viscosity*1000)/1000d;}

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

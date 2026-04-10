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
    private Rectangle[] plotButtons = new Rectangle[2];
    private float plotTextX = 0;
    private Rectangle[] modeButtons = new Rectangle[2];
    private float modeTextX = 0;

    private Rectangle barrierShapesButton;
    private boolean renderDropdown = false;
    private String[] barrierShapeValues = {"short line", "long line", "diagonal", "shallow diagonal", "small circle", "large circle", "line with spoiler", "circle with spoiler", "right angle", "wedge", "airfoil"};
    private Rectangle[] barrierShapesDropdownButtons = new Rectangle[barrierShapeValues.length];

    private Rectangle clearBarrierButton;
    private Rectangle resetFluidButton;
    private Rectangle showFlowlinesButton;

    private Texture quitIcon;
    private Texture backIcon;
    private Texture settingsIcon;
    private Texture flowLinesCheckBoxTrue;
    private Texture flowLinesCheckBoxFalse;

    public SettingsMenu() {
        this.util = new MenuUtil();

        this.decimalFormat = new DecimalFormat("0.000");

        this.quitIcon = util.loadIcon("quit");
        this.backIcon = util.loadIcon("back");
        this.settingsIcon = util.loadIcon("settings");
        this.flowLinesCheckBoxTrue = util.loadIcon("checkBoxTrue");
        this.flowLinesCheckBoxFalse = util.loadIcon("checkBoxFalse");

        this.plot = this.plotValues[1];
        this.mode = this.modeValues[0];
    }

    @Override
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
            quitButton = util.renderButton(sr, util.getQuitButtonColor(), null, 1862.5f, 1022.5f, 75, 75, 16);
            backButton = util.renderButton(sr, Color.BLACK, null, 57.5f, 1022.5f, 75, 75, 0);
            settingsButton = util.renderButton(sr, Color.BLACK, null, 1862.5f, 57.5f, 75, 75, 0);

            util.renderRoundedRectangle(sr, util.getButtonColor(), 600, 742.5f, 600, 75, 16); // l1
            util.renderRoundedRectangle(sr, util.getButtonColor(), 600, 642.5f, 600, 75, 16); // l2
            util.renderRoundedRectangle(sr, util.getButtonColor(), 600, 542.5f, 600, 75, 16); // l3
            util.renderRoundedRectangle(sr, util.getButtonColor(), 600, 442.5f, 600, 75, 16); // l4

            barrierShapesButton = util.renderRoundedRectangle(sr, util.getButtonColor(), 1320, 742.5f, 600, 75, 16); // r1
            clearBarrierButton = util.renderRoundedRectangle(sr, util.getButtonColor(), 1320, 642.5f, 600, 75, 16); // r2
            resetFluidButton = util.renderRoundedRectangle(sr, util.getButtonColor(), 1320, 542.5f, 600, 75, 16); // r3
            showFlowlinesButton = util.renderRoundedRectangle(sr, util.getButtonColor(), 1320, 442.5f, 600, 75, 16); // r4

            flowSpeedButtons[0] = util.renderRoundedTriangle(sr, Color.WHITE, 865, 742.5f, 12, 90);
            flowSpeedButtons[1] = util.renderRoundedTriangle(sr, Color.WHITE, 665, 742.5f, 12, 270);

            viscosityButtons[0] = util.renderRoundedTriangle(sr, Color.WHITE, 865, 642.5f, 12, 90);
            viscosityButtons[1] = util.renderRoundedTriangle(sr, Color.WHITE, 665, 642.5f, 12, 270);

            plotButtons[0] = util.renderRoundedTriangle(sr, Color.WHITE, 865, 542.5f, 12, 90);
            plotButtons[1] = util.renderRoundedTriangle(sr, Color.WHITE, plotTextX-40, 542.5f, 12, 270);

            modeButtons[0] = util.renderRoundedTriangle(sr, Color.WHITE, 865, 442.5f, 12, 90);
            modeButtons[1] = util.renderRoundedTriangle(sr, Color.WHITE, modeTextX-40, 442.5f, 12, 270);

            if (renderDropdown) {
                util.renderRoundedTriangle(sr, Color.WHITE, 1585, 742.5f, 12, 0);
                util.renderRoundedRectangle(sr, util.getButtonColor(), 1320, 705-(0.5f*50*barrierShapeValues.length), 600, 50*barrierShapeValues.length, 16);
                /*for (Integer count=0; count<barrierShapeValues.length; count++) {
                    if (barrierShapesDropdownButtons[count] == null) {break;}
                    util.renderRoundedRectangle(sr, Color.RED, barrierShapesDropdownButtons[count].x+(barrierShapesDropdownButtons[count].width/2), barrierShapesDropdownButtons[count].y+(barrierShapesDropdownButtons[count].height/2), barrierShapesDropdownButtons[count].width, barrierShapesDropdownButtons[count].height, 16);
                }*/
            } else {
                util.renderRoundedTriangle(sr, Color.WHITE, 1585, 742.5f, 12, 180);
            }
        sr.end();

        batch.begin();
            util.renderText(batch, "settings", Color.WHITE, util.getScreenDimensions().x/2, 953, 96, "centre");

            util.renderText(batch, "flow speed", Color.WHITE, 320, 742.5f, 36, "left"); // l1
            util.renderText(batch, "viscosity", Color.WHITE, 320, 642.5f, 36, "left"); // l2
            util.renderText(batch, "plot", Color.WHITE, 320, 542.5f, 36, "left"); // l3
            util.renderText(batch, "mode", Color.WHITE, 320, 442.5f, 36, "left"); // l4

            util.renderText(batch, "barrier shapes", Color.WHITE, 1040, 742.5f, 36, "left"); // r1
            if (!renderDropdown) {
                util.renderText(batch, "clear barriers", Color.WHITE, 1040, 642.5f, 36, "left"); // r2
                util.renderText(batch, "reset fluid", Color.WHITE, 1040, 542.5f, 36, "left"); // r3
                util.renderText(batch, "show flowlines", Color.WHITE, 1040, 442.5f, 36, "left"); // r4
            }

            util.renderText(batch, decimalFormat.format(flowSpeed), Color.WHITE, 765, 742.5f, 36, "centre"); // l1 value
            util.renderText(batch, decimalFormat.format(viscosity), Color.WHITE, 765, 642.5f, 36, "centre"); // l2 value
            plotTextX = util.renderText(batch, plot, Color.WHITE, 825, 542.5f, 36, "right"); // l3 value
            modeTextX = util.renderText(batch, mode, Color.WHITE, 825, 442.5f, 36, "right"); // l4 value

            if (renderDropdown) {
                for (Integer count=0; count<barrierShapeValues.length; count++) {
                    util.renderText(batch, barrierShapeValues[count], Color.WHITE, 1040, 680-(50*count), 36, "left");
                    barrierShapesDropdownButtons[count] = new Rectangle(1020, 680-(50*count)-25, 600, 50);
                }
            }

            util.renderIcon(batch, quitIcon, 1862.5f, 1022.5f);
            util.renderIcon(batch, backIcon, 57.5f, 1022.5f);
            util.renderIcon(batch, settingsIcon, 1862.5f, 57.5f);

            if (!renderDropdown) {
                if (showFlowLines) {
                    util.renderIcon(batch, flowLinesCheckBoxTrue, 1585, 442.5f);
                } else {
                    util.renderIcon(batch, flowLinesCheckBoxFalse, 1585, 442.5f);
                }
            }
        batch.end();
    }

    @Override
    public String checkIfButtonsClicked() {
        if (util.isButtonClicked(flowSpeedButtons[0]) && flowSpeed < 0.120) {flowSpeed += 0.005; flowSpeed = Math.round(flowSpeed*1000)/1000d;}
        if (util.isButtonClicked(flowSpeedButtons[1]) && flowSpeed > 0.005) {flowSpeed -= 0.005; flowSpeed = Math.round(flowSpeed*1000)/1000d;}

        if (util.isButtonClicked(viscosityButtons[0]) && viscosity < 0.200) {viscosity += 0.005; viscosity = Math.round(viscosity*1000)/1000d;}
        if (util.isButtonClicked(viscosityButtons[1]) && viscosity > 0.005) {viscosity -= 0.005; viscosity = Math.round(viscosity*1000)/1000d;}

        if (util.isButtonClicked(plotButtons[0])) {plot = plotOrModeButtonClicked(1, plot, plotValues);}
        if (util.isButtonClicked(plotButtons[1])) {plot = plotOrModeButtonClicked(-1, plot, plotValues);}

        if (util.isButtonClicked(modeButtons[0])) {mode = plotOrModeButtonClicked(1, mode, modeValues);}
        if (util.isButtonClicked(modeButtons[1])) {mode = plotOrModeButtonClicked(-1, mode, modeValues);}

        if (renderDropdown) {
            for (Integer count=0; count<barrierShapeValues.length; count++) {
                if (util.isButtonClicked(barrierShapesDropdownButtons[count])) {
                    System.out.println(barrierShapeValues[count]);
                    // addShapeToCells(barrierShapeValues[count]);
                }
            }
        }
        if (util.isButtonClicked(barrierShapesButton)) {renderDropdown = !renderDropdown;}

        if (!renderDropdown && util.isButtonClicked(clearBarrierButton)) {System.out.println("clear barriers");}

        if (!renderDropdown && util.isButtonClicked(resetFluidButton)) {System.out.println("reset fluid");}

        if (!renderDropdown && util.isButtonClicked(showFlowlinesButton)) {showFlowLines = !showFlowLines;}

        if (util.isButtonClicked(quitButton)) {Gdx.app.exit();}
        if (util.isButtonClicked(backButton)) {return "back";}
        if (util.isButtonClicked(settingsButton)) {return "settings";}
        return "settings";
    }

    public String plotOrModeButtonClicked(Integer change, String currentValue, String[] possibleValues) {
        Integer index = 0;
        for (Integer count=0; count<possibleValues.length; count++) {
            if (possibleValues[count] == currentValue) {
                index = count;
                break;
            }
        }
        if (change == 1) {
            if (index == possibleValues.length-1) {
                return possibleValues[0];
            }
            return possibleValues[index+1];
        } else if (change == -1) {
            if (index == 0) {
                return possibleValues[possibleValues.length-1];
            }
            return possibleValues[index-1];
        }
        return currentValue;
    }

    public void resetSettings() {
        this.flowSpeed = 0.100;
        this.viscosity = 0.020;
        this.plot = this.plotValues[1];
        this.mode = this.modeValues[0];
        this.showFlowLines = false;
    }

    public double getFlowSpeed() {return this.flowSpeed;}
    public void setFlowSpeed(double flowSpeed) {this.flowSpeed = flowSpeed;}

    public double getViscosity() {return this.viscosity;}
    public void setViscosity(double viscosity) {this.viscosity = viscosity;}

    public String getPlot() {return this.plot;}
    public void setPlot(String plot) {this.plot = plot;}

    public String getMode() {return this.mode;}
    public void setMode(String mode) {this.mode = mode;}

    public boolean getShowFlowLines() {return this.showFlowLines;}
    public void setShowFlowLines(boolean showFlowLines) {this.showFlowLines = showFlowLines;}
}

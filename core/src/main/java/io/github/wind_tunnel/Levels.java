package io.github.wind_tunnel;

import com.badlogic.gdx.math.Vector3;

public class Levels {
    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;
    private ShapeDrawer shapeDrawer;

    private int levelNumber;

    // lockedSettings: resolution, flowspeed, viscosity, plot, brushSize, solver, barrierShapes, clearBarriers, showFlowLines, renderUI, renderSelectedZPlane

    public Levels() {
        settings = Settings.getInstance();
        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
        shapeDrawer = new ShapeDrawer();
    }

    public void loadSelectedLevel() {
        /*
        level ideas:
        vertical medium-sized line
        airfoil
        circle
        stick man
        apple
        pizza slice
        smiley face (verity)
        "hello world" as text
        octane/car
        troll face
        penis
        */

        levelNumber = settings.getLevelNumber();
        if (levelNumber == 1) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[2][0], settings.getResolutionValues()[2][1], 1)); // 128x72x1
        } else if (levelNumber == 2) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[3][0], settings.getResolutionValues()[3][1], 1)); // 256x144x1
        }
        cfdSolver.initialiseFluid();
        settings.resetLockedSettings();

        if (levelNumber == 1) {
            settings.setSolver("2D LBM");
            settings.setFlowSpeed(0.100f);
            settings.setViscosity(0.020f);
            settings.setLockedSettingAt(0, true);
            settings.setLockedSettingAt(5, true);
            settings.setLockedSettingAt(1, true);
            settings.setLockedSettingAt(2, true);
            settings.setLockedSettingAt(6, true);
            settings.setAvailableBarriers(50);
            settings.setMaxBarriers(50);
        } else if (levelNumber == 2) {
            settings.setSolver("2D LBM");
            settings.setFlowSpeed(0.125f);
            settings.setViscosity(0.025f);
            settings.setLockedSettingAt(0, true);
            settings.setLockedSettingAt(5, true);
            settings.setLockedSettingAt(1, true);
            settings.setLockedSettingAt(2, true);
            settings.setLockedSettingAt(6, true);
            settings.setAvailableBarriers(70);
            settings.setMaxBarriers(70);
        }

        settings.setFixedBarriers(new boolean[(int) settings.getResolution().x][(int) settings.getResolution().y][(int) settings.getResolution().z]);

        settings.setLoadedLevel(false); // don't count the fixed barriers as part of the available barriers
        if (levelNumber == 1) {
            shapeDrawer.drawLine(new Vector3(50, 30, 0), new Vector3(50, 42, 0), true);
        } else if (levelNumber == 2) {
            // airfoil
        }
        settings.setLoadedLevel(true);
    }
}

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
        levelNumber = settings.getLevelNumber();
        if (levelNumber == 1) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[2][0], settings.getResolutionValues()[2][1], 1)); // 128x72x1
        } else if (levelNumber == 2) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[2][0], settings.getResolutionValues()[2][1], 1)); // 128x72x1
        } else if (levelNumber == 3) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[3][0], settings.getResolutionValues()[3][1], 1)); // 256x144x1
        } else if (levelNumber == 4) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[3][0], settings.getResolutionValues()[3][1], 1)); // 256x144x1
        } else if (levelNumber == 5) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[2][0], settings.getResolutionValues()[2][1], 1)); // 128x72x1
        } else if (levelNumber == 6) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[4][0], settings.getResolutionValues()[4][1], 1)); // 512x128x1
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
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 2) {
            settings.setSolver("2D LBM");
            settings.setFlowSpeed(0.125f);
            settings.setViscosity(0.015f);
            settings.setLockedSettingAt(0, true);
            settings.setLockedSettingAt(5, true);
            settings.setLockedSettingAt(1, true);
            settings.setLockedSettingAt(2, true);
            settings.setLockedSettingAt(6, true);
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 3) {

        } else if (levelNumber == 4) {
            settings.setSolver("2D LBM");
            settings.setFlowSpeed(0.125f);
            settings.setViscosity(0.015f);
            settings.setLockedSettingAt(0, true);
            settings.setLockedSettingAt(5, true);
            settings.setLockedSettingAt(1, true);
            settings.setLockedSettingAt(2, true);
            settings.setLockedSettingAt(6, true);
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 5) {
            settings.setSolver("2D LBM");
            settings.setFlowSpeed(0.125f);
            settings.setViscosity(0.015f);
            settings.setLockedSettingAt(0, true);
            settings.setLockedSettingAt(5, true);
            settings.setLockedSettingAt(1, true);
            settings.setLockedSettingAt(2, true);
            settings.setLockedSettingAt(6, true);
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 6) {
            settings.setSolver("2D LBM");
            settings.setFlowSpeed(0.125f);
            settings.setViscosity(0.015f);
            settings.setLockedSettingAt(0, true);
            settings.setLockedSettingAt(5, true);
            settings.setLockedSettingAt(1, true);
            settings.setLockedSettingAt(2, true);
            settings.setLockedSettingAt(6, true);
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        }

        settings.setFixedBarriers(new boolean[(int) settings.getResolution().x][(int) settings.getResolution().y][(int) settings.getResolution().z]);
        /*
        level ideas:
        1: vertical line
        2: circle
        3: airfoil
        4: pizza slice
        5: apple
        6: car
        7: stick man
        8: smiley face (verity)
        9: "hello world" as text
        10: troll face
        11: penis
        */
        settings.setLoadedLevel(false); // don't count the fixed barriers as part of the available barriers
        if (levelNumber == 1) {
            shapeDrawer.line(new Vector3(50, 30, 0), new Vector3(50, 42, 0), true);
        } else if (levelNumber == 2) {
            shapeDrawer.circle(48, 36, 0, 8, false, true);
        } else if (levelNumber == 3) {
            //shapeDrawer.airfoil();
        } else if (levelNumber == 4) {
            shapeDrawer.loadBitmap("pizza_slice", true);
        } else if (levelNumber == 5) {
            shapeDrawer.loadBitmap("apple", true);
        } else if (levelNumber == 6) {
            shapeDrawer.loadBitmap("f1_car", true);
        }
        settings.setLoadedLevel(true);
    }
}

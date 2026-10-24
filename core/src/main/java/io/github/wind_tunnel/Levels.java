package io.github.wind_tunnel;

import com.badlogic.gdx.math.Vector3;

public class Levels {
    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;
    private ShapeDrawer shapeDrawer;
    private ImportFile importFile;

    private int levelNumber;
    private String folder = "assets/levels/";

    // lockedSettings: resolution, flowspeed, viscosity, plot, brushSize, solver, barrierShapes, clearBarriers, showFlowLines, renderUI, renderSelectedZPlane

    public Levels() {
        settings = Settings.getInstance();
        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
        shapeDrawer = new ShapeDrawer();
        importFile = new ImportFile();
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
            settings.setResolution(new Vector3(settings.getResolutionValues()[4][0], settings.getResolutionValues()[4][1], 1)); // 512x288x1
        } else if (levelNumber == 7) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[2][0], settings.getResolutionValues()[2][1], 1)); // 128x72x1
        } else if (levelNumber == 8) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[2][0], settings.getResolutionValues()[2][1], 1)); // 128x72x1
        } else if (levelNumber == 9) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[4][0], settings.getResolutionValues()[4][1], 1)); // 512x288x1
        } else if (levelNumber == 10) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[4][0], settings.getResolutionValues()[4][1], 1)); // 512x288x1
        } else if (levelNumber == 11) {
            settings.setResolution(new Vector3(settings.getResolutionValues()[4][0], settings.getResolutionValues()[4][1], 1)); // 512x288x1
        }
        cfdSolver.initialiseFluid();
        settings.resetLockedSettings();

        if (levelNumber >= 1 && levelNumber <= 10) {
            settings.setSolver("2D LBM");
            settings.setFlowSpeed(0.125f);
            settings.setViscosity(0.015f);
            settings.setLockedSettingAt(0, true);
            settings.setLockedSettingAt(5, true);
            settings.setLockedSettingAt(1, true);
            settings.setLockedSettingAt(2, true);
            settings.setLockedSettingAt(6, true);
        }

        if (levelNumber == 1) {
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 2) {
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 3) {
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 4) {
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 5) {
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 6) {
            settings.setAvailableBarriers(200);
            settings.setMaxBarriers(200);
        } else if (levelNumber == 7) {
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 8) {
            settings.setAvailableBarriers(100);
            settings.setMaxBarriers(100);
        } else if (levelNumber == 9) {
            settings.setAvailableBarriers(200);
            settings.setMaxBarriers(200);
        } else if (levelNumber == 10) {
            settings.setAvailableBarriers(200);
            settings.setMaxBarriers(200);
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
        */
        settings.setLoadedLevel(false); // don't count the fixed barriers as part of the available barriers
        if (levelNumber == 1) {
            shapeDrawer.line(new Vector3(50, 30, 0), new Vector3(50, 42, 0), true);
        } else if (levelNumber == 2) {
            shapeDrawer.circle(48, 36, 0, 8, false, true);
        } else if (levelNumber == 3) {
            importFile.loadBitmap(folder, "airfoil", true);
        } else if (levelNumber == 4) {
            importFile.loadBitmap(folder, "pizza_slice", true);
        } else if (levelNumber == 5) {
            importFile.loadBitmap(folder, "apple", true);
        } else if (levelNumber == 6) {
            importFile.loadBitmap(folder, "f1_car", true);
        } else if (levelNumber == 7) {
            importFile.loadBitmap(folder, "stickman", true);
        } else if (levelNumber == 8) {
            importFile.loadBitmap(folder, "nerd_face", true);
        } else if (levelNumber == 9) {
            importFile.loadBitmap(folder, "hello_world", true);
        } else if (levelNumber == 10) {
            importFile.loadBitmap(folder, "trollface", true);
        }
        settings.setLoadedLevel(true);
    }
}

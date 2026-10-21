package io.github.wind_tunnel;

import com.badlogic.gdx.math.Vector3;

public class Levels {
    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;

    private int levelNumber;

    // lockedSettings: resolution, flowspeed, viscosity, plot, brushSize, solver, barrierShapes, clearBarriers, showFlowLines, renderUI, renderSelectedZPlane

    public Levels() {
        settings = Settings.getInstance();
        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
    }

    public void loadSelectedLevel() {
        /*
        level ideas:
        1 - vertical medium-sized line
        2 - airfoil
        3 - circle
        4 - penis
        5 - stick man
        6 - apple
        7 - smiley face (verity)
        8 - octane/car
        9 - pizza slice
        10 - "hello world" as text
        11 - troll face
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
            settings.setFlowSpeed(0.1f);
            settings.setViscosity(0.02f);
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

        settings.setLoadedLevel(false);
        if (levelNumber == 1) {
            settings.setFixedBarriers(cfdSolver.placeBarriers(new Vector3(50, 30, 0), new Vector3(50, 42, 0)));
        } else if (levelNumber == 2) {
            // airfoil
        }
        settings.setLoadedLevel(true);
    }
}

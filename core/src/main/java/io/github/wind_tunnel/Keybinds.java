package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Keybinds {

    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;

    private int[] binds = {
        Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D, // rotation of the simulation area in 3D, forwards, backwards, left, right
        Input.Keys.UP, Input.Keys.DOWN, // zoom in 3D, zoom in, zoom out
        Input.Keys.SPACE, Input.Keys.RIGHT, // pause/run the simulation, step the simulation (when paused)
        Input.Keys.R // clear all barriers
    };

    private static Keybinds instance;

    public static Keybinds getInstance() {
        if (instance == null) {
            instance = new Keybinds();
        }
        return instance;
    }

    private Keybinds() {
        settings = Settings.getInstance();
        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
    }

    public void checkForKeyPresses() {
        if (settings.getSolver().equals("3D LBM")) {
            if (Gdx.input.isKeyPressed(binds[0])) {
                settings.setRotationAnglesX((float) (settings.getRotationAnglesX()-(Math.PI/180)));
            }
            if (Gdx.input.isKeyPressed(binds[1])) {
                settings.setRotationAnglesX((float) (settings.getRotationAnglesX()+(Math.PI/180)));
            }
            if (Gdx.input.isKeyPressed(binds[2])) {
                settings.setRotationAnglesY((float) (settings.getRotationAnglesY()+(Math.PI/180)));
            }
            if (Gdx.input.isKeyPressed(binds[3])) {
                settings.setRotationAnglesY((float) (settings.getRotationAnglesY()-(Math.PI/180)));
            }
            if (Gdx.input.isKeyPressed(binds[4])) {
                settings.setCameraDistance(settings.getCameraDistance()-1);
            }
            if (Gdx.input.isKeyPressed(binds[5])) {
                settings.setCameraDistance(settings.getCameraDistance()+1);
            }
        }
        if (Gdx.input.isKeyJustPressed(binds[6])) {
            settings.setSimulationRunning(!settings.getSimulationRunning());
        }
        if (Gdx.input.isKeyJustPressed(binds[7])) {
            if (!settings.getSimulationRunning()) {
                cfdSolver.doStep();
            }
        }
        if (Gdx.input.isKeyJustPressed(binds[8])) {
            cfdSolver.removeAllBarriers();
        }
    }

    public int[] getBinds() {return this.binds;}
    public void setBindsAt(int index, int key) {this.binds[index] = key;}
}

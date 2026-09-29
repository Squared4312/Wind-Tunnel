package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Keybinds {

    private Settings settings;

    private int[] binds = {
        Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D, // rotation of the simulation area in 3D, forwards, backwards, left, right
        Input.Keys.UP, Input.Keys.DOWN, // zoom in 3D, zoom in, zoom out
        Input.Keys.SPACE // Pause/Run the simulation
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
    }

    public void checkForKeyPresses() {
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
        if (Gdx.input.isKeyPressed(binds[6])) {
            settings.setSimulationRunning(!settings.getSimulationRunning());
        }
    }

    public int[] getBinds() {return this.binds;}
    public void setBindsAt(int index, int key) {this.binds[index] = key;}
}

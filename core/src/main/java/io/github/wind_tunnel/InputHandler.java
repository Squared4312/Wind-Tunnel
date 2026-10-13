package io.github.wind_tunnel;

import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {

    private Settings settings;

    public InputHandler() {
        this.settings = Settings.getInstance();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (settings.getSolver().equals("3D LBM")) {
            int direction;
            if (amountY == 0) {
                return false;
            } else if (amountY > 0) {
                direction = -1;
            } else {
                direction = 1;
            }
            settings.moveSelectedZPlane(direction);
            return true;
        }
        return false;
    }
}

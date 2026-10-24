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
            if (amountY == 0) {
                return false;
            }
            settings.moveSelectedZPlane((int) amountY);
            return true;
        }
        return false;
    }
}

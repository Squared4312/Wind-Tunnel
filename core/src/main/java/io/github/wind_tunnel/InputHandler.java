package io.github.wind_tunnel;

import com.badlogic.gdx.InputAdapter;
import io.github.wind_tunnel.ENums.Solver;

public class InputHandler extends InputAdapter {

    private Settings settings;

    public InputHandler() {
        this.settings = Settings.getInstance();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (settings.getSolver().equals(Solver.threeD)) {
            settings.moveSelectedZPlane((int) -amountY);
            return true;
        }
        return false;
    }
}

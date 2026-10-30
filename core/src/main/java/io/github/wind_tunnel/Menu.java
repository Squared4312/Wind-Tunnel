package io.github.wind_tunnel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Menu {
    public void render(ShapeRenderer sr, SpriteBatch batch);
    public void checkIfButtonsClicked(MenuNavigator navigator);

    public default void onEnter() {} // menu is opened
    public default void onPause() {} // a menu is opened on top to (freeplayMenu -> settingsMenu)
    public default void onResume() {} // menu is returned to (freeplayMenu -> settingsMenu -> freeplayMenu)
    public default void onExit() {} // menu is exited by going back
}

package io.github.wind_tunnel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.wind_tunnel.Menus.*;

public class Main extends ApplicationAdapter {
    private ShapeRenderer sr;
    private SpriteBatch batch;

    private MenuNavigator navigator;
    private Keybinds keybinds;
    private int shownFps = -1;

    @Override
    public void create() {
        Gdx.input.setInputProcessor(new InputHandler());

        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        // this shows what menus can be reached by others
        Menu settingsMenu = new SettingsMenu(new KeybindsMenu(), new ImportFileMenu());
        Menu mainMenu = new MainMenu(settingsMenu, new LevelsMenu(settingsMenu), new FreeplayMenu(settingsMenu), new AboutMenu());
        navigator = new MenuNavigator(mainMenu);

        keybinds = Keybinds.getInstance();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        if (Gdx.graphics.getFramesPerSecond() != shownFps) {
            shownFps = Gdx.graphics.getFramesPerSecond();
            Gdx.graphics.setTitle("Wind Tunnel FPS: " + shownFps + "/" + Gdx.graphics.getDisplayMode().refreshRate);
        }

        keybinds.checkForKeyPresses();

        navigator.getCurrentMenu().checkIfButtonsClicked(navigator);
        navigator.getCurrentMenu().render(sr, batch);
    }

    @Override
    public void dispose() {
        sr.dispose();
        batch.dispose();
    }
}

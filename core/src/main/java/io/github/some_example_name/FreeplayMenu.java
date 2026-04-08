package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class FreeplayMenu implements Menu {
    private MenuUtil util;

    private Rectangle backButton;
    private Rectangle settingsButton;

    private Texture backIcon;
    private Texture settingsIcon;

    public FreeplayMenu() {
        this.util = new MenuUtil();

        this.backIcon = util.loadIcon("back");
        this.settingsIcon = util.loadIcon("settings");
    }

    @Override
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
            backButton = util.renderButton(sr, Color.BLACK, null, 57.5f, 1022.5f, 75, 75, 0);
            settingsButton = util.renderButton(sr, Color.BLACK, null, 1862.5f, 57.5f, 75, 75, 0);
        sr.end();

        batch.begin();
            util.renderText(batch, "freeplay (WIP)", Color.WHITE, util.getScreenDimensions().x/2, 953, 96, "centre");
            util.renderIcon(batch, backIcon, 57.5f, 1022.5f);
            util.renderIcon(batch, settingsIcon, 1862.5f, 57.5f);
        batch.end();
    }

    @Override
    public String checkIfButtonsClicked() {
        if (util.isButtonClicked(backButton)) {return "back";}
        if (util.isButtonClicked(settingsButton)) {return "settings";}
        return "freeplay";
    }
}

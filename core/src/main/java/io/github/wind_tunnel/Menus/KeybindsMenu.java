package io.github.wind_tunnel.Menus;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.wind_tunnel.*;
import io.github.wind_tunnel.ENums.TextAlignment;
import io.github.wind_tunnel.ENums.Colours;

public class KeybindsMenu implements Menu {
    private MenuUtil util;
    private Keybinds keybinds;

    private Rectangle backButton;

    private Texture backIcon;
    private Texture keybindsIcon;

    public KeybindsMenu() {
        this.util = new MenuUtil();
        this.keybinds = Keybinds.getInstance();

        this.backIcon = util.loadIcon("back");
        this.keybindsIcon = util.loadIcon("keybinds");
    }

    @Override
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
            backButton = util.getHitbox(57.5f, 1022.5f, 75, 75);

            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 600, 742.5f, 600, 75, 16); // l1
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 600, 642.5f, 600, 75, 16); // l2
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 600, 542.5f, 600, 75, 16); // l3
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 600, 442.5f, 600, 75, 16); // l4
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 600, 342.5f, 600, 75, 16); // l5
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 600, 242.5f, 600, 75, 16); // l6

            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 1320, 742.5f, 600, 75, 16); // r1
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 1320, 642.5f, 600, 75, 16); // r2
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 1320, 542.5f, 600, 75, 16); // r3
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 1320, 442.5f, 600, 75, 16); // r4
            util.renderRoundedRectangle(sr, Colours.BUTTON_BG.get(), 1320, 342.5f, 600, 75, 16); // r5
        sr.end();

        batch.begin();
            util.renderText(batch, "keybinds", Colours.TEXT.get(), util.getScreenDimensions().x/2, 953, 96, TextAlignment.CENTRE);

            util.renderText(batch, "rotate", Colours.TEXT.get(), 320, 742.5f, 36, TextAlignment.LEFT); // l1
            util.renderText(batch, "zoom", Colours.TEXT.get(), 320, 642.5f, 36, TextAlignment.LEFT); // l2
            util.renderText(batch, "pause/run", Colours.TEXT.get(), 320, 542.5f, 36, TextAlignment.LEFT); // l3
            util.renderText(batch, "step simulation", Colours.TEXT.get(), 320, 442.5f, 36, TextAlignment.LEFT); // l4
            util.renderText(batch, "draw barriers", Colours.TEXT.get(), 320, 342.5f, 36, TextAlignment.LEFT); // l5
            util.renderText(batch, "erase barriers", Colours.TEXT.get(), 320, 242.5f, 36, TextAlignment.LEFT); // l6

            util.renderText(batch, "clear all barriers", Colours.TEXT.get(), 1040, 742.5f, 36, TextAlignment.LEFT); // r1
            util.renderText(batch, "toggle flowlines", Colours.TEXT.get(), 1040, 642.5f, 36, TextAlignment.LEFT); // r2
            util.renderText(batch, "initialise fluid", Colours.TEXT.get(), 1040, 542.5f, 36, TextAlignment.LEFT); // r3
            util.renderText(batch, "change brush size", Colours.TEXT.get(), 1040, 442.5f, 36, TextAlignment.LEFT); // r4
            util.renderText(batch, "fill", Colours.TEXT.get(), 1040, 342.5f, 36, TextAlignment.LEFT); // r5

        util.renderText(batch, (Input.Keys.toString(keybinds.getBindAt(0))+" "+Input.Keys.toString(keybinds.getBindAt(2))+" "+Input.Keys.toString(keybinds.getBindAt(1))+" "+Input.Keys.toString(keybinds.getBindAt(3))).toLowerCase(), Colours.TEXT.get(), 865, 742.5f, 36, TextAlignment.RIGHT); // l1 value
            util.renderText(batch, (Input.Keys.toString(keybinds.getBindAt(4))+" "+Input.Keys.toString(keybinds.getBindAt(5))).toLowerCase(), Colours.TEXT.get(), 865, 642.5f, 36, TextAlignment.RIGHT); // l2 value
            util.renderText(batch, Input.Keys.toString(keybinds.getBindAt(6)).toLowerCase(), Colours.TEXT.get(), 865, 542.5f, 36, TextAlignment.RIGHT); // l3 value
            util.renderText(batch, Input.Keys.toString(keybinds.getBindAt(7)).toLowerCase(), Colours.TEXT.get(), 865, 442.5f, 36, TextAlignment.RIGHT); // l4 value
            util.renderText(batch, "left click", Colours.TEXT.get(), 865, 342.5f, 36, TextAlignment.RIGHT); // l5 value
            util.renderText(batch, "right click", Colours.TEXT.get(), 865, 242.5f, 36, TextAlignment.RIGHT); // l6 value

            util.renderText(batch, Input.Keys.toString(keybinds.getBindAt(8)).toLowerCase(), Colours.TEXT.get(), 1585, 742.5f, 36, TextAlignment.RIGHT); // r1 value
            util.renderText(batch, Input.Keys.toString(keybinds.getBindAt(11)).toLowerCase(), Colours.TEXT.get(), 1585, 642.5f, 36, TextAlignment.RIGHT); // r2 value
            util.renderText(batch, Input.Keys.toString(keybinds.getBindAt(12)).toLowerCase(), Colours.TEXT.get(), 1585, 542.5f, 36, TextAlignment.RIGHT); // r3 value
            util.renderText(batch, ("+ "+Input.Keys.toString(keybinds.getBindAt(14))).toLowerCase(), Colours.TEXT.get(), 1585, 442.5f, 36, TextAlignment.RIGHT); // r4 value
            util.renderText(batch, Input.Keys.toString(keybinds.getBindAt(15)).toLowerCase(), Colours.TEXT.get(), 1585, 342.5f, 36, TextAlignment.RIGHT); // r5 value

            util.renderIcon(batch, backIcon, 57.5f, 1022.5f);
            util.renderIcon(batch, keybindsIcon, 57.5f, 57.f);
        batch.end();
    }

    @Override
    public void checkIfButtonsClicked(MenuNavigator navigator) {
        if (util.isButtonClicked(backButton)) {
            navigator.back();
        }
    }
}

package io.github.wind_tunnel.Menus;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.wind_tunnel.ENums.TextAlignment;
import io.github.wind_tunnel.ENums.Colours;

public class AboutMenu implements Menu {
    private MenuUtil util;

    private Rectangle backButton;

    private Texture backIcon;
    private Texture aboutIcon;

    public AboutMenu() {
        this.util = new MenuUtil();

        this.backIcon = util.loadIcon("back");
        this.aboutIcon = util.loadIcon("about");
    }

    @Override
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
            backButton = util.getHitbox(57.5f, 1022.5f, 75, 75);
        sr.end();

        batch.begin();
            util.renderText(batch, "about", Colours.TEXT.get(), util.getScreenDimensions().x/2, 953, 96, TextAlignment.CENTRE);
            util.renderText(batch, "This is a 2D and 3D incompressible Wind Tunnel made by Nathan Becker", Colours.TEXT.get(), util.getScreenDimensions().x/2, 850, 32, TextAlignment.CENTRE);
            util.renderText(batch, "as an A-Level Computer Science NEA project.", Colours.TEXT.get(), util.getScreenDimensions().x/2, 800, 32, TextAlignment.CENTRE);
            util.renderText(batch, "The Wind Tunnel uses computational fluid dynamics such as the", Colours.TEXT.get(), util.getScreenDimensions().x/2, 700, 32, TextAlignment.CENTRE);
            util.renderText(batch, "Lattice Boltzmann method to simulate a fluid flowing around an object", Colours.TEXT.get(), util.getScreenDimensions().x/2, 650, 32, TextAlignment.CENTRE);
            util.renderText(batch, "based on variable flow speed and viscosity.", Colours.TEXT.get(), util.getScreenDimensions().x/2, 600, 32, TextAlignment.CENTRE);
            util.renderText(batch, "xxx lines of code", Colours.TEXT.get(), util.getScreenDimensions().x/2, 500, 32, TextAlignment.CENTRE);
            util.renderText(batch, "~ xxx hrs of coding", Colours.TEXT.get(), util.getScreenDimensions().x/2, 450, 32, TextAlignment.CENTRE);
            util.renderText(batch, "xxx words of documentation", Colours.TEXT.get(), util.getScreenDimensions().x/2, 400, 32, TextAlignment.CENTRE);
            util.renderText(batch, "xx / xx for documentation", Colours.TEXT.get(), util.getScreenDimensions().x/2, 300, 32, TextAlignment.CENTRE);
            util.renderText(batch, "xx / xx for coding", Colours.TEXT.get(), util.getScreenDimensions().x/2, 250, 32, TextAlignment.CENTRE);
            util.renderIcon(batch, backIcon, 57.5f, 1022.5f);
            util.renderIcon(batch, aboutIcon, 57.5f, 57.5f);
        batch.end();
    }

    @Override
    public void checkIfButtonsClicked(MenuNavigator navigator) {
        if (util.isButtonClicked(backButton)) {
            navigator.back();
        }
    }
}

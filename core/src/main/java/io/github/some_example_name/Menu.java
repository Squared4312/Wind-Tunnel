package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Menu {
    public void render(ShapeRenderer sr, SpriteBatch batch);
    public String checkIfButtonsClicked();
}

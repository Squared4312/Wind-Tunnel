package io.github.wind_tunnel;

import com.badlogic.gdx.graphics.Color;

public class ENums {
    public enum Solver {
        TWO_D,
        THREE_D
    }

    public enum TextAlignment {
        LEFT,
        RIGHT,
        CENTRE
    }

    public enum Colours {
        FIXED_2D_BARRIER(Color.BLACK),
        FIXED_3D_BARRIER(Color.GRAY),
        BARRIER(Color.WHITE),

        TEXT(Color.WHITE),
        BG(Color.BLACK),

        BUTTON_BG(new Color(128/255f, 128/255f, 128/255f, 1f)),
        BUTTON_OUTLINE(Color.WHITE),
        BUTTON_FILL(Color.BLACK),
        TRIANGLE(Color.WHITE),
        QUIT_BUTTON(new Color(199/255f, 0f, 0f, 1f)),
        RUN_BUTTON(new Color(0f, 128/255f, 0f, 1f)),
        PAUSE_BUTTON(new Color(199/255f, 0f, 0f, 1f)),
        STEP_BUTTON(new Color(56/255f, 149/255f, 211/255f, 1f)),

        LOCKED_SETTINGS(Color.RED),
        ;

        private Color color;
        Colours(Color color) {this.color = color;}
        public Color get() {return color;}
    }
}

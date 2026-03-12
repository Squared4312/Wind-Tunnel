package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Cell {
    private Vector2 position;
    private Vector2 velocity;

    public Cell(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public void draw(ShapeRenderer sr) {
        double magnitude = Math.sqrt(velocity.x*velocity.x + velocity.y*velocity.y);

        sr.setColor(Color.WHITE);
        /*if (magnitude < 10) {
            sr.setColor(Color.BLUE);
        }*/
        sr.circle(position.x, position.y, 1);
    }

    public Vector2 getPosition() {return position;}
    public void setPosition(Vector2 position) {this.position = position;}

    public Vector2 getVelocity() {return velocity;}
    public void setVelocity(Vector2 velocity) {this.velocity = velocity;}
}

package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Cell {
    private Vector2 centre;
    private Vector2 dimensions;
    private Vector2 velocity;

    public Cell(Vector2 centre, Vector2 dimensions, Vector2 velocity) {
        this.centre = centre;
        this.dimensions = dimensions;
        this.velocity = velocity;
    }

    public void draw(ShapeRenderer sr, Vector2 screenDimensions, boolean renderArrows) {
        if (renderArrows) {drawArrows(sr, screenDimensions);}
    }

    public void drawArrows(ShapeRenderer sr, Vector2 screenDimensions) {
        sr.setColor(Color.WHITE);

        velocity.x /= screenDimensions.x;
        velocity.y /= screenDimensions.y;
        velocity.x *= dimensions.x;
        velocity.y *= dimensions.y;

        Vector2 tail = new Vector2(centre.x-(velocity.x/2), centre.y-(velocity.y/2));
        Vector2 head = new Vector2(centre.x+(velocity.x/2), centre.y+(velocity.y/2));
        sr.rectLine(tail, head, 2);

        // arrow heads
        Integer length = (int) velocity.len()/3;

        double arcAngle = Math.toDegrees(Math.atan2(tail.y-head.y, tail.x-head.x));

        double xChange = Math.cos(Math.toRadians(arcAngle));
        double yChange = Math.sin(Math.toRadians(arcAngle));

        double[] angles = {arcAngle+45, arcAngle-45};
        for (double angle : angles) {
            double xOffset = length*Math.cos(Math.toRadians(angle));
            double yOffset = length*Math.sin(Math.toRadians(angle));
            sr.rectLine((float) (head.x+xChange), (float) (head.y+yChange), (float) (head.x+xOffset+xChange), (float) (head.y+yOffset+yChange), 2);
        }
    }

    public Vector2 getCentre() {return centre;}
    public void setCentre(Vector2 centre) {this.centre = centre;}

    public Vector2 getVelocity() {return velocity;}
    public void setVelocity(Vector2 velocity) {this.velocity = velocity;}
}

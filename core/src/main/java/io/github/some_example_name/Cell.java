package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Cell {
    private Vector2 centre;
    private Vector2 dimensions;

    // cell densities named by v directions (North = Up)
    private double densityO, densityN, densityE, densityS, densityW, densityNE, densityNW, densitySE, densitySW = 0;

    // these are calculated from the densities above
    private double density = 1.0;
    private Vector2 v; // v
    private double speedSquared = 0;

    private boolean isBarrier = false;

    public Cell(Vector2 centre, Vector2 dimensions, double flowSpeed) {
        this.centre = centre;
        this.dimensions = dimensions;
        this.v = new Vector2((float) flowSpeed, 0);
    }

    public void draw(ShapeRenderer sr, Vector2 screenDimensions, boolean renderFlowLines) {
        if (isBarrier) {
            sr.setColor(Color.WHITE);
        } else {
            float averageDensity = (float) ((densityO+densityN+densityE+densityS+densityW+densityNE+densityNW+densitySE+densitySW)/9);
            System.out.println(averageDensity);
            sr.setColor(averageDensity/255, 0f, 1-(averageDensity/255), 1f);
        }
        sr.rect(centre.x-dimensions.x/2, centre.y-dimensions.y/2, centre.x+dimensions.x/2, centre.y+dimensions.y/2);
        if (renderFlowLines) {drawFlowLines(sr, screenDimensions);}
    }

    public void drawFlowLines(ShapeRenderer sr, Vector2 screenDimensions) {
        sr.setColor(Color.GRAY);

        v.x /= screenDimensions.x;
        v.y /= screenDimensions.y;
        v.x *= dimensions.x;
        v.y *= dimensions.y;

        Vector2 tail = new Vector2(centre.x-(v.x/2), centre.y-(v.y/2));
        Vector2 head = new Vector2(centre.x+(v.x/2), centre.y+(v.y/2));
        sr.rectLine(tail, head, 2);

        if (false) {// arrow heads
            Integer length = (int) v.len()/3;

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
    }

    public Vector2 getCentre() {return centre;}
    public void setCentre(Vector2 centre) {this.centre = centre;}

    public Vector2 getVelocity() {return v;}
    public void setVelocity(Vector2 v) {this.v = v;}

    public double getDensity() {return density;}
    public void setDensity(double density) {this.density = density;}

    public double getSpeedSquared() {return speedSquared;}
    public void setSpeedSquared(double speedSquared) {this.speedSquared = speedSquared;}

    public boolean getIsBarrier() {return isBarrier;}
    public void setIsBarrier(boolean barrier) {this.isBarrier = barrier;}

    public double getDensityO() {return densityO;}
    public void setDensityO(double densityO) {this.densityO = densityO;}

    public double getDensityN() {return densityN;}
    public void setDensityN(double densityN) {this.densityN = densityN;}

    public double getDensityE() {return densityE;}
    public void setDensityE(double densityE) {this.densityE = densityE;}

    public double getDensityS() {return densityS;}
    public void setDensityS(double densityS) {this.densityS = densityS;}

    public double getDensityW() {return densityW;}
    public void setDensityW(double densityW) {this.densityW = densityW;}

    public double getDensityNE() {return densityNE;}
    public void setDensityNE(double densityNE) {this.densityNE = densityNE;}

    public double getDensityNW() {return densityNW;}
    public void setDensityNW(double densityNW) {this.densityNW = densityNW;}

    public double getDensitySE() {return densitySE;}
    public void setDensitySE(double densitySE) {this.densitySE = densitySE;}

    public double getDensitySW() {return densitySW;}
    public void setDensitySW(double densitySW) {this.densitySW = densitySW;}
}

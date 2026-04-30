package io.github.some_example_name;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Cell {
    private Vector2 centre;
    private Vector2 dimensions;

    // cell densities named by v directions (North = Up)
    private float densityO, densityN, densityE, densityS, densityW, densityNE, densityNW, densitySE, densitySW = 1;

    // these are calculated from the densities above
    private float density = 1f;
    private Vector2 v; // v
    private float speedSquared = 0;

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
            float averageDensity = (densityO+densityN+densityE+densityS+densityW+densityNE+densityNW+densitySE+densitySW)/9;
            //System.out.println(averageDensity);
            sr.setColor(averageDensity/255, 0f, 1-(averageDensity/255), 1f);
        }
        sr.rect(centre.x-dimensions.x/2, centre.y-dimensions.y/2, centre.x+dimensions.x/2, centre.y+dimensions.y/2);
        if (renderFlowLines) {drawFlowLines(sr, screenDimensions);}
    }

    public void drawFlowLines(ShapeRenderer sr, Vector2 screenDimensions) {
        sr.setColor(Color.GRAY);

        float scaledVx = (v.x/screenDimensions.x)*dimensions.x;
        float scaledVy = (v.y/screenDimensions.y)*dimensions.y;

        Vector2 tail = new Vector2(centre.x-(scaledVx/2), centre.y-(scaledVy/2));
        Vector2 head = new Vector2(centre.x+(scaledVx/2), centre.y+(scaledVy/2));
        sr.rectLine(tail, head, 2);
    }

    public void initialise(float flowSpeed) {
        float w0 = 4/9f;
        float wAxis = 1/9f;
        float wDiag = 1/36f;

        densityO = w0*(1-1.5f*flowSpeed*flowSpeed);
        densityN = wAxis*(1-1.5f*flowSpeed*flowSpeed);
        densityS = wAxis*(1-1.5f*flowSpeed*flowSpeed);
        densityE = wAxis*(1+3*flowSpeed+4.5f*flowSpeed*flowSpeed-1.5f*flowSpeed*flowSpeed);
        densityW = wAxis*(1-3*flowSpeed+4.5f*flowSpeed*flowSpeed-1.5f*flowSpeed*flowSpeed);
        densityNE = wDiag*(1+3*flowSpeed+4.5f*flowSpeed*flowSpeed-1.5f*flowSpeed*flowSpeed);
        densitySE = wDiag*(1+3*flowSpeed+4.5f*flowSpeed*flowSpeed-1.5f*flowSpeed*flowSpeed);
        densityNW = wDiag*(1-3*flowSpeed+4.5f*flowSpeed*flowSpeed-1.5f*flowSpeed*flowSpeed);
        densitySW = wDiag*(1-3*flowSpeed+4.5f*flowSpeed*flowSpeed-1.5f*flowSpeed*flowSpeed);
    }

    public Vector2 getCentre() {return centre;}
    public void setCentre(Vector2 centre) {this.centre = centre;}

    public Vector2 getVelocity() {return v;}
    public void setVelocity(Vector2 v) {this.v = v;}

    public float getDensity() {return density;}
    public void setDensity(float density) {this.density = density;}

    public float getSpeedSquared() {return speedSquared;}
    public void setSpeedSquared(float speedSquared) {this.speedSquared = speedSquared;}

    public boolean getIsBarrier() {return isBarrier;}
    public void setIsBarrier(boolean barrier) {this.isBarrier = barrier;}

    public float getDensityO() {return densityO;}
    public void setDensityO(float densityO) {this.densityO = densityO;}

    public float getDensityN() {return densityN;}
    public void setDensityN(float densityN) {this.densityN = densityN;}

    public float getDensityE() {return densityE;}
    public void setDensityE(float densityE) {this.densityE = densityE;}

    public float getDensityS() {return densityS;}
    public void setDensityS(float densityS) {this.densityS = densityS;}

    public float getDensityW() {return densityW;}
    public void setDensityW(float densityW) {this.densityW = densityW;}

    public float getDensityNE() {return densityNE;}
    public void setDensityNE(float densityNE) {this.densityNE = densityNE;}

    public float getDensityNW() {return densityNW;}
    public void setDensityNW(float densityNW) {this.densityNW = densityNW;}

    public float getDensitySE() {return densitySE;}
    public void setDensitySE(float densitySE) {this.densitySE = densitySE;}

    public float getDensitySW() {return densitySW;}
    public void setDensitySW(float densitySW) {this.densitySW = densitySW;}
}

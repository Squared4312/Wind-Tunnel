package io.github.wind_tunnel;

import com.badlogic.gdx.math.Vector3;

public class ShapeDrawer {

    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;

    public ShapeDrawer() {
        settings = Settings.getInstance();
        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
    }

    public void fill(Vector3 from, Vector3 to, boolean fixedBarrier) {
        for (float x=Math.min(from.x, to.x); x<=Math.max(from.x, to.x); x++) {
            for (float y=Math.min(from.y, to.y); y<=Math.max(from.y, to.y); y++) {
                for (float z=Math.min(from.z, to.z); z<=Math.max(from.z, to.z); z++) {
                    cfdSolver.addBarrier((int) x, (int) y, (int) z);
                    if (fixedBarrier) {settings.addFixedBarrier((int) x, (int) y, (int) z);}
                }
            }
        }
    }

    public void drawLine(Vector3 from, Vector3 to, boolean fixedBarrier) {

    }

    public void drawCircle(int x, int y, int z, int radius, boolean fixedBarrier) {
        for (int dx=-radius; dx<=radius; dx++) {
            for (int dy=-radius; dy<=radius; dy++) {
                if (dx*dx + dy*dy > radius*radius) { // if point is outside circle
                    continue;
                }
                int cellX = x+dx;
                int cellY = y+dy;

                // point outside simulation area
                if (cellX <= 0 || cellX >= settings.getResolution().x-1 || cellY <= 0 || cellY >= settings.getResolution().y-1) {
                    continue;
                }
                cfdSolver.addBarrier(cellX, cellY, z);
                if (fixedBarrier) {settings.addFixedBarrier(cellX, cellY, z);}
            }
        }
    }
}

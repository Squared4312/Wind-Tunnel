package io.github.wind_tunnel;

import com.badlogic.gdx.math.Vector3;

public class ShapeDrawer {

    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;

    private Vector3 rayDirection;
    private double rayDirectionLength;
    private Vector3 rayUnitStep = new Vector3();
    private Vector3 check;
    private Vector3 rayLength = new Vector3();
    private Vector3 step = new Vector3();

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

    public void rectangle(Vector3 frontTopLeft, Vector3 backBottomRight, boolean fixedBarrier) {
        float x1 = frontTopLeft.x;
        float y1 = frontTopLeft.y;
        float z1 = frontTopLeft.z;
        float x2 = backBottomRight.x;
        float y2 = backBottomRight.y;
        float z2 = backBottomRight.z;

        // front face
        line(new Vector3(x1, y1, z1), new Vector3(x2, y1, z1), fixedBarrier); // front top
        line(new Vector3(x1, y2, z1), new Vector3(x2, y2, z1), fixedBarrier); // front bottom
        line(new Vector3(x1, y1, z1), new Vector3(x1, y2, z1), fixedBarrier); // front left
        line(new Vector3(x2, y1, z1), new Vector3(x2, y2, z1), fixedBarrier); // front right

        // back face
        line(new Vector3(x1, y1, z2), new Vector3(x2, y1, z2), fixedBarrier); // back top
        line(new Vector3(x1, y2, z2), new Vector3(x2, y2, z2), fixedBarrier); // back bottom
        line(new Vector3(x1, y1, z2), new Vector3(x1, y2, z2), fixedBarrier); // back left
        line(new Vector3(x2, y1, z2), new Vector3(x2, y2, z2), fixedBarrier); // back right

        // connecting edges
        line(new Vector3(x1, y1, z1), new Vector3(x1, y1, z2), fixedBarrier); // top left
        line(new Vector3(x2, y1, z1), new Vector3(x2, y1, z2), fixedBarrier); // top right
        line(new Vector3(x1, y2, z1), new Vector3(x1, y2, z2), fixedBarrier); // bottom left
        line(new Vector3(x2, y2, z1), new Vector3(x2, y2, z2), fixedBarrier); // bottom right
    }

    public void line(Vector3 from, Vector3 to, boolean fixedBarrier) {

        // centre both from and to so that it raycasts from the centre of the cell
        from.x += 0.5f;
        from.y += 0.5f;
        to.x += 0.5f;
        to.y += 0.5f;

        // draw a line using the DDA algorithm
        rayDirection = new Vector3(to.x-from.x, to.y-from.y, to.z-from.z);
        rayDirectionLength = Math.sqrt(rayDirection.x*rayDirection.x + rayDirection.y*rayDirection.y + rayDirection.z*rayDirection.z);

        if (rayDirectionLength == 0) {
            return; // from equals to, no direction to cast
        }
        rayDirection.x = (float) (rayDirection.x/rayDirectionLength);
        rayDirection.y = (float) (rayDirection.y/rayDirectionLength);
        rayDirection.z = (float) (rayDirection.z/rayDirectionLength);

        // ray distances needed to cross one cell on each axis
        if (rayDirection.x != 0) {
            rayUnitStep.x = (float) Math.sqrt(1 + (rayDirection.y/rayDirection.x)*(rayDirection.y/rayDirection.x) + (rayDirection.z/rayDirection.x)*(rayDirection.z/rayDirection.x));
        } else {
            rayUnitStep.x = (float) Double.POSITIVE_INFINITY;
        }

        if (rayDirection.y != 0) {
            rayUnitStep.y = (float) Math.sqrt(1 + (rayDirection.x/rayDirection.y)*(rayDirection.x/rayDirection.y) + (rayDirection.z/rayDirection.y)*(rayDirection.z/rayDirection.y));
        } else {
            rayUnitStep.y = (float) Double.POSITIVE_INFINITY;
        }

        if (rayDirection.z != 0) {
            rayUnitStep.z = (float) Math.sqrt(1 + (rayDirection.x/rayDirection.z)*(rayDirection.x/rayDirection.z) + (rayDirection.y/rayDirection.z)*(rayDirection.y/rayDirection.z));
        } else {
            rayUnitStep.z = (float) Double.POSITIVE_INFINITY;
        }

        check = new Vector3((int) Math.floor(from.x), (int) Math.floor(from.y), (int) Math.floor(from.z));

        if (rayDirection.x < 0) {
            step.x = -1;
            rayLength.x = (from.x-check.x)*rayUnitStep.x;
        } else {
            step.x = 1;
            rayLength.x = (check.x-from.x+1)*rayUnitStep.x;
        }

        if (rayDirection.y < 0) {
            step.y = -1;
            rayLength.y = (from.y-check.y)*rayUnitStep.y;
        } else {
            step.y = 1;
            rayLength.y = (check.y-from.y+1)*rayUnitStep.y;
        }

        if (rayDirection.z < 0) {
            step.z = -1;
            rayLength.z = (from.z-check.z)*rayUnitStep.z;
        } else {
            step.z = 1;
            rayLength.z = (check.z-from.z+1)*rayUnitStep.z;
        }

        while (true) {
            if (check.x > 0 && check.x < settings.getResolution().x-1 && check.y > 0 && check.y < settings.getResolution().y-1 && check.z > 0 && check.z < settings.getResolution().z-1) {
                cfdSolver.addBarrier((int) check.x, (int) check.y, (int) check.z);
                if (fixedBarrier) {settings.addFixedBarrier((int) check.x, (int) check.y, (int) check.z);}
            } else { // Vector3 check is outside simulation area
                break;
            }

            // move along the axis that has the shortest ray length
            if (rayLength.x < rayLength.y && rayLength.x < rayLength.z) {
                check.x += step.x;
                rayLength.x += rayUnitStep.x;
            } else if (rayLength.y < rayLength.z) {
                check.y += step.y;
                rayLength.y += rayUnitStep.y;
            } else {
                check.z += step.z;
                rayLength.z += rayUnitStep.z;
            }

            if ((int) check.x == (int) to.x && (int) check.y == (int) to.y && (int) check.z == (int) to.z) { // check equals endpoint, so add endpoint and exit
                cfdSolver.addBarrier((int) check.x, (int) check.y, (int) check.z);
                if (fixedBarrier) {settings.addFixedBarrier((int) check.x, (int) check.y, (int) check.z);}
                break;
            }
        }
    }

    public void circle(int x, int y, int z, int radius, boolean fill, boolean fixedBarrier) {
        for (int dx=-radius; dx<=radius; dx++) {
            for (int dy=-radius; dy<=radius; dy++) {
                if (dx*dx + dy*dy > radius*radius) { // if point is outside circle
                    continue;
                }
                if (!fill) { // skip filling the middle to make a hollow circle
                    if (dx*dx + dy*dy < (radius-1)*(radius-1)) {
                        continue;
                    }
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

    public void arc(int x, int y, int z, int radius, double startAngle, double endAngle, boolean fixedBarrier) {
        /*
        x(t) = x + r*cos(t)
        y(t) = y + r*sin(t)
        where t varies from start angle to end angle in radians
         */

        Vector3 point = new Vector3();
        startAngle = Math.toRadians(startAngle);
        endAngle = Math.toRadians(endAngle);

        for (double angle=startAngle; angle<endAngle; angle+=Math.toRadians(1)) {
            point.x = (float) (x + radius*Math.cos(angle));
            point.y = (float) (y + radius*Math.sin(angle));
            point.z = z;

            // point outside simulation area
            if (point.x <= 0 || point.x >= settings.getResolution().x-1 || point.y <= 0 || point.z >= settings.getResolution().y-1) {
                continue;
            }

            cfdSolver.addBarrier((int) point.x, (int) point.y, z);
            if (fixedBarrier) {settings.addFixedBarrier((int) point.x, (int) point.y, z);}
        }
    }
}

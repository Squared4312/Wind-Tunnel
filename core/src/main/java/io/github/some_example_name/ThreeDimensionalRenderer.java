package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ThreeDimensionalRenderer {

    private Settings settings;

    private Integer cameraDistance;
    private Integer fov;
    private Vector3 rotationAngles;
    private Vector3 origin;

    private Vector2 screenDimensions;

    public ThreeDimensionalRenderer() {
        this.settings = Settings.getInstance();

        /*this.rotationAngles = settings.getRotationAngles();
        this.cameraDistance = settings.getCameraDistance();
        this.fov = settings.getFov();*/

        this.screenDimensions = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public Vector3 rotate(float x, float y, float z) {
        rotationAngles = new Vector3(settings.getRotationAnglesX(), settings.getRotationAnglesY(), settings.getRotationAnglesZ());
        // 3x3 rotation matrices in each x y z dimension
        double[][] rotationX = {
            {1, 0, 0},
            {0, Math.cos(rotationAngles.x), -Math.sin(rotationAngles.x)},
            {0, Math.sin(rotationAngles.x), Math.cos(rotationAngles.x)},
        };

        double[][] rotationY = {
            {Math.cos(rotationAngles.y), 0, Math.sin(rotationAngles.y)},
            {0, 1, 0},
            {-Math.sin(rotationAngles.y), 0, Math.cos(rotationAngles.y)},
        };

        double[][] rotationZ = {
            {Math.cos(rotationAngles.z), -Math.sin(rotationAngles.z), 0},
            {Math.sin(rotationAngles.z), Math.cos(rotationAngles.z), 0},
            {0, 0, 1},
        };

        // this calculates the dot product of all the rotation vectors above with a point
        origin = settings.getOrigin();
        Vector3 rotated = new Vector3(x-origin.x, y-origin.y, z-origin.z); // translated point

        // x rotation
        rotated.x = (float) (rotationX[0][0]*rotated.x + rotationX[0][1]*rotated.y + rotationX[0][2]*rotated.z);
        rotated.y = (float) (rotationX[1][0]*rotated.x + rotationX[1][1]*rotated.y + rotationX[1][2]*rotated.z);
        rotated.z = (float) (rotationX[2][0]*rotated.x + rotationX[2][1]*rotated.y + rotationX[2][2]*rotated.z);
        // y rotation
        rotated.x = (float) (rotationY[0][0]*rotated.x + rotationY[0][1]*rotated.y + rotationY[0][2]*rotated.z);
        rotated.y = (float) (rotationY[1][0]*rotated.x + rotationY[1][1]*rotated.y + rotationY[1][2]*rotated.z);
        rotated.z = (float) (rotationY[2][0]*rotated.x + rotationY[2][1]*rotated.y + rotationY[2][2]*rotated.z);
        // z rotation
        rotated.x = (float) (rotationZ[0][0]*rotated.x + rotationZ[0][1]*rotated.y + rotationZ[0][2]*rotated.z);
        rotated.y = (float) (rotationZ[1][0]*rotated.x + rotationZ[1][1]*rotated.y + rotationZ[1][2]*rotated.z);
        rotated.z = (float) (rotationZ[2][0]*rotated.x + rotationZ[2][1]*rotated.y + rotationZ[2][2]*rotated.z);

        // translate back
        rotated.x += origin.x;
        rotated.y += origin.y;
        rotated.z += origin.z;

        return rotated;
    }

    public Vector2 pointProjection(Vector3 point) {
        float zFar = 1000f; // max distance the camera can see
        float zNear = 0.1f; // min distance the camera can see
        float aspectRatio = screenDimensions.x/screenDimensions.y;
        float fovCalculation = (float) (1/Math.tan(0.5*Math.toRadians(settings.getFov())));
        float zNormalisation = zFar/(zFar-zNear);

        // removes points that are too close to the camera to prevent the wrapping/clipping of points
        point.z += settings.getCameraDistance();
        if (point.z <= zNear) {return null;}

        Matrix4 projectionMatrix = new Matrix4(new float[]{
            fovCalculation/aspectRatio, 0, 0, 0,
            0, fovCalculation, 0, 0,
            0, 0, zNormalisation, 1,
            0, 0, -zNear*zNormalisation, 0
        });

        // apply the projection matrix
        point.prj(projectionMatrix);

        // convert 3d point to the 2d screen
        point.x = (point.x+1)*(screenDimensions.x/2);
        point.y = (1-point.y)*(screenDimensions.y/2);

        return new Vector2(point.x, point.y);
    }
}

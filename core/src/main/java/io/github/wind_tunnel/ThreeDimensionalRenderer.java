package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;

public class ThreeDimensionalRenderer {

    private Settings settings;

    private Vector3 rotationAngles;
    private Vector3 origin;

    private Vector2 screenDimensions;

    private Vector3 translated = new Vector3();
    private Vector3 rotatedX = new Vector3();
    private Vector3 rotatedXY = new Vector3();
    private Vector3 rotatedXYZ = new Vector3();

    private Vector2 projectedPoint = new Vector2();
    private Vector4 pos = new Vector4();
    private float zFar = 1000f; // max distance the camera can see
    private float zNear = 0.1f; // min distance the camera can see
    private float aspectRatio;
    private float fovCalculation;
    private float zNormalisation = zFar/(zFar-zNear);

    public ThreeDimensionalRenderer() {
        this.settings = Settings.getInstance();

        this.rotationAngles = settings.getRotationAngles();
        this.screenDimensions = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.aspectRatio = screenDimensions.x/screenDimensions.y;
    }

    public Vector3 rotate(float x, float y, float z) {
        rotationAngles.x = settings.getRotationAnglesX();
        rotationAngles.y = settings.getRotationAnglesY();
        rotationAngles.z = settings.getRotationAnglesZ();

        float sinX = (float) Math.sin(rotationAngles.x);
        float cosX = (float) Math.cos(rotationAngles.x);
        float sinY = (float) Math.sin(rotationAngles.y);
        float cosY = (float) Math.cos(rotationAngles.y);
        float sinZ = (float) Math.sin(rotationAngles.z);
        float cosZ = (float) Math.cos(rotationAngles.z);

        // 3x3 rotation matrices in x y z
        float[][] rotationX = {
            {1, 0, 0},
            {0, cosX, -sinX},
            {0, sinX, cosX},
        };

        float[][] rotationY = {
            {cosY, 0, sinY},
            {0, 1, 0},
            {-sinY, 0, cosY},
        };

        float[][] rotationZ = {
            {cosZ, -sinZ, 0},
            {sinZ, cosZ, 0},
            {0, 0, 1},
        };

        // this calculates the dot product of all the rotation vectors above with a point
        origin = settings.getOrigin();
        translated.x = x-origin.x;
        translated.y = y-origin.y;
        translated.z = z-origin.z;

        // x rotation
        rotatedX.x = rotationX[0][0]*translated.x + rotationX[0][1]*translated.y + rotationX[0][2]*translated.z;
        rotatedX.y = rotationX[1][0]*translated.x + rotationX[1][1]*translated.y + rotationX[1][2]*translated.z;
        rotatedX.z = rotationX[2][0]*translated.x + rotationX[2][1]*translated.y + rotationX[2][2]*translated.z;
        // y rotation
        rotatedXY.x = rotationY[0][0]*rotatedX.x + rotationY[0][1]*rotatedX.y + rotationY[0][2]*rotatedX.z;
        rotatedXY.y = rotationY[1][0]*rotatedX.x + rotationY[1][1]*rotatedX.y + rotationY[1][2]*rotatedX.z;
        rotatedXY.z = rotationY[2][0]*rotatedX.x + rotationY[2][1]*rotatedX.y + rotationY[2][2]*rotatedX.z;
        // z rotation
        rotatedXYZ.x = rotationZ[0][0]*rotatedXY.x + rotationZ[0][1]*rotatedXY.y + rotationZ[0][2]*rotatedXY.z;
        rotatedXYZ.y = rotationZ[1][0]*rotatedXY.x + rotationZ[1][1]*rotatedXY.y + rotationZ[1][2]*rotatedXY.z;
        rotatedXYZ.z = rotationZ[2][0]*rotatedXY.x + rotationZ[2][1]*rotatedXY.y + rotationZ[2][2]*rotatedXY.z;

        // translate back
        rotatedXYZ.x += origin.x;
        rotatedXYZ.y += origin.y;
        rotatedXYZ.z += origin.z;

        return rotatedXYZ;
    }

    public Vector2 pointProjection(Vector3 point) {
        fovCalculation = (float) (1/Math.tan(0.5*(settings.getFov()*(Math.PI/180))));

        // removes points that are too close to the camera to prevent the wrapping/clipping of points
        point.z += settings.getCameraDistance();
        if (point.z <= zNear) {return null;}

        float[][] projectionMatrix = new float[][]{
            {fovCalculation/aspectRatio, 0, 0, 0},
            {0, fovCalculation, 0, 0},
            {0, 0, zNormalisation, -zNear*zNormalisation},
            {0, 0, 1, 0}
        };

        // w = 1 as this is a position not a direction
        point = multiplyVector4withMatrix(new Vector4(point.x, point.y, point.z, 1), projectionMatrix);

        // convert 3d point to the 2d screen
        projectedPoint.x = (point.x+1)*(screenDimensions.x/2);
        projectedPoint.y = (1-point.y)*(screenDimensions.y/2);

        return projectedPoint;
    }

    public Vector3 getIntersectionBetweenPlaneAnd(Vector2 mouse) {
        // map mouse screen coordinates to normalised device coordinates (NDC)

        float xNDC = ((2*mouse.x)/settings.getResolution().x)-1;
        float yNDC = ((2*mouse.y)/settings.getResolution().y)-1;

        Vector3 near = new Vector3(xNDC, yNDC, -1);
        Vector3 far = new Vector3(xNDC, yNDC, 1);

        float[][] inverseProjectionMatrix = new float[][]{
            {aspectRatio/fovCalculation, 0, 0, 0},
            {0, 1/fovCalculation, 0, 0},
            {0, 0, 0, 1},
            {0, 0, -1/(zNear*zNormalisation), 1/zNear}
        };

        near = multiplyVector4withMatrix(new Vector4(near.x, near.y, near.z, 1), inverseProjectionMatrix);
        far = multiplyVector4withMatrix(new Vector4(far.x, far.y, far.z, 1), inverseProjectionMatrix);

        /*
        3D Ray: R(t) = near + t*(far-near) = selectedZPlane
        Solve for t:
        t = (selectedZPlane-near.z)/(far.z-near.z)

        therefore:
        x = near.x + t*(far.x-near.x)
        y = near.y + t*(far.y-near.y)
        z = selectedZPlane
         */

        float t = (settings.getSelectedZPlane()-near.z)/(far.z-near.z);

        return new Vector3(near.x + t*(far.x-near.x), near.y + t*(far.y-near.y), settings.getSelectedZPlane());
    }

    public Vector3 multiplyVector4withMatrix(Vector4 point, float[][] matrix) {
        // apply the matrix
        pos.x = point.x;
        pos.y = point.y;
        pos.z = point.z;
        pos.w = point.w;

        // multiply the matrix by pos
        point.x = matrix[0][0]*pos.x + matrix[0][1]*pos.y + matrix[0][2]*pos.z + matrix[0][3]*pos.w;
        point.y = matrix[1][0]*pos.x + matrix[1][1]*pos.y + matrix[1][2]*pos.z + matrix[1][3]*pos.w;
        point.z = matrix[2][0]*pos.x + matrix[2][1]*pos.y + matrix[2][2]*pos.z + matrix[2][3]*pos.w;
        pos.w = matrix[3][0]*pos.x + matrix[3][1]*pos.y + matrix[3][2]*pos.z + matrix[3][3]*pos.w;

        // perspective divide
        point.x /= pos.w;
        point.y /= pos.w;
        point.z /= pos.w;

        return new Vector3(point.x, point.y, point.z);
    }
}

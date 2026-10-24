package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector3;

public class ImportFile {

    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;
    private ShapeDrawer shapeDrawer;

    private String modelsFolder = "assets/models/";
    private String[] fileNames;

    public ImportFile() {
        settings = Settings.getInstance();
        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
        shapeDrawer = new ShapeDrawer();
    }

    public void loadBitmap(String filePath, boolean fixedBarrier) {
        FileHandle file = Gdx.files.internal(filePath);
        if (!file.exists()) {return;}

        Pixmap image = new Pixmap(file);
        if (image.getWidth() > settings.getResolution().x || image.getHeight() > settings.getResolution().y) {return;} // too big

        for (int x=0; x<image.getWidth(); x++) {
            for (int y=0; y<image.getHeight(); y++) {
                int colour = image.getPixel(x, image.getHeight()-y);
                if (colour == 0x000000FF) { // black
                    cfdSolver.addBarrier(x, y, 0);
                    if (fixedBarrier) {settings.addFixedBarrier(x, y, 0);}
                }
            }
        }
    }

    public void loadOBJModel(String filePath, boolean fixedBarrier) {
        FileHandle file = Gdx.files.internal(filePath);
        if (!file.exists()) {return;}

        String[] contents = file.readString().split("\\r?\\n"); // split at every new line
        int vertexCount = 0;
        int faceCount = 0;

        for (String line : contents) {
            if (line.isEmpty()) {continue;}

            if (line.startsWith("v ")) {
                vertexCount += 1;
            } else if (line.startsWith("f ")) {
                faceCount += 1;
            }
        }

        Vector3[] vertices = new Vector3[vertexCount];
        int verticesPointer = 0;
        int[][] faces = new int[faceCount][];
        int facesPointer = 0;

        String[] lineList;
        for (String line : contents) {
            if (line.isEmpty()) {continue;}

            lineList = line.split("\\s+");
            if (line.startsWith("v ")) {
                vertices[verticesPointer] = new Vector3(Float.parseFloat(lineList[1]), Float.parseFloat(lineList[2]), Float.parseFloat(lineList[3]));
                verticesPointer += 1;
            } else if (line.startsWith("f ")) {
                faces[facesPointer] = new int[lineList.length-1];
                for (int count=1; count<lineList.length; count++) {
                    faces[facesPointer][count-1] = Integer.parseInt(lineList[count])-1;
                }
                facesPointer += 1;
            }
        }

        for (Vector3 point : vertices) { // move to middle of simulation area
            point.x += settings.getResolution().x/2;
            point.y += settings.getResolution().y/2;
            point.z += settings.getResolution().z/2;
        }

        // draw lines (using shapeDrawer.line()) from each corner to its neighbour according to the faces from the loaded .obj
        for (int[] face : faces) {
            for (int count=0; count<face.length; count++) {
                int index = face[count];
                int nextIndex;
                if (count+1 >= face.length) {nextIndex = face[0];}
                else {nextIndex = face[count+1];}

                if (vertices[index].x <= 0 || vertices[index].x >= settings.getResolution().x-1 || vertices[index].y <= 0 || vertices[index].y >= settings.getResolution().y-1 || vertices[index].z <= 0 || vertices[index].z >= settings.getResolution().z-1) {continue;}
                if (vertices[nextIndex].x <= 0 || vertices[nextIndex].x >= settings.getResolution().x-1 || vertices[nextIndex].y <= 0 || vertices[nextIndex].y >= settings.getResolution().y-1 || vertices[nextIndex].z <= 0 || vertices[nextIndex].z >= settings.getResolution().z-1) {continue;}

                shapeDrawer.line(new Vector3(vertices[index]), new Vector3(vertices[nextIndex]), fixedBarrier);
            }
        }
    }

    public int getNumberOfFiles() {
        return Gdx.files.internal(modelsFolder).list().length;
    }

    public String[] getAllFileNames() {
        FileHandle[] files = Gdx.files.internal(modelsFolder).list();
        fileNames = new String[files.length];
        for (int count=0; count<files.length; count++) {
            fileNames[count] = files[count].name();
        }
        return fileNames;
    }

    public String getFileNameAt(int index) {
        FileHandle[] files = Gdx.files.internal(modelsFolder).list();
        return files[index].name();
    }

    public String getFilePathAt(int index) {
        FileHandle[] files = Gdx.files.internal(modelsFolder).list();
        return files[index].path();
    }

    public String getFileExtensionAt(int index) {
        FileHandle[] files = Gdx.files.internal(modelsFolder).list();
        return files[index].extension();
    }

    public String getModelType(String filePath) {
        if (getModelResolution(filePath).z == 0) {
            return "2D";
        } else if (getModelResolution(filePath).z > 0) {
            return "3D";
        }
        return null;
    }

    public Integer getModelBarriers(String filePath) {
        FileHandle file = Gdx.files.internal(modelsFolder + filePath);
        if (!file.exists()) {return null;}

        int barriers = 0;
        if (file.extension().equals("bmp")) { // 2D file
            Pixmap image = new Pixmap(file);
            for (int x=0; x<image.getWidth(); x++) {
                for (int y=0; y<image.getHeight(); y++) {
                    int colour = image.getPixel(x, image.getHeight()-y);
                    if (colour == 0x000000FF) { // black
                        barriers += 1;
                    }
                }
            }
            return barriers;
        } else if (file.extension().equals("blend")) { // 3D
            System.out.println("3D file");
            return barriers;
        }
        return null;
    }

    public Vector3 getModelResolution(String filePath) {
        FileHandle file = Gdx.files.internal(modelsFolder + filePath);
        if (!file.exists()) {return new Vector3(0, 0, 0);}

        if (file.extension().equals("bmp")) { // 2D file
            Pixmap image = new Pixmap(file);
            return new Vector3(image.getWidth(), image.getHeight(), 0);
        } else if (file.extension().equals("blend")) { // 3D
            System.out.println("3D file");
        }
        return new Vector3(0, 0, 0);
    }

    public String getModelsFolder() {return this.modelsFolder;}
}

package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

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
                int colour = image.getPixel(x, image.getHeight()-y-1);
                if (colour == 0x000000FF) { // black
                    cfdSolver.addBarrier(x, y, 0, fixedBarrier);
                }
            }
        }
    }

    public void loadOBJModel(String filePath, boolean fixedBarrier) {
        /*
        blender export as .obj rules

        turn off:
        UV coordinates
        normals
        colours
        curves as NURBS
        materials
        animation

        turn on:
        triangulated mesh
        apply modifiers
        apply transform

        ensure:
        forward axis = y
        up axis = z
        */
        FileHandle file = Gdx.files.internal(filePath);
        if (!file.exists()) {return;}

        String[] contents = file.readString().split("\\r?\\n"); // split at every new line
        ArrayList<Vector3> vertices = new ArrayList<>();
        ArrayList<int[]> faces = new ArrayList<>();

        String[] lineList;
        for (String line : contents) {
            if (line.isEmpty()) {continue;}

            lineList = line.split("\\s+");
            if (line.startsWith("v ")) {
                vertices.add(new Vector3(Float.parseFloat(lineList[1]), Float.parseFloat(lineList[2]), Float.parseFloat(lineList[3])));
            } else if (line.startsWith("f ")) {
                int[] face = new int[lineList.length-1];
                for (int count=1; count<lineList.length; count++) {
                    face[count-1] = Integer.parseInt(lineList[count])-1;
                }
                faces.add(face);
            }
        }
        if (vertices.isEmpty()) {return;}

        // draw lines (using shapeDrawer.line()) from each vertex to its neighbour according to the faces from the loaded .obj

        // bounding box of the model, where min is the front-bottom-left corner and max is the back-top-right corner
        Vector3 min = new Vector3(vertices.get(0));
        Vector3 max = new Vector3(vertices.get(0));
        for (Vector3 vertex : vertices) {
            min.x = Math.min(min.x, vertex.x);
            min.y = Math.min(min.y, vertex.y);
            min.z = Math.min(min.z, vertex.z);
            max.x = Math.max(max.x, vertex.x);
            max.y = Math.max(max.y, vertex.y);
            max.z = Math.max(max.z, vertex.z);
        }

        Vector3 centre = new Vector3((min.x+max.x)/2, (min.y+max.y)/2, (min.z+max.z)/2); // centre of the bounding box
        float size = Math.max(max.x-min.x, Math.max(max.y-min.y, max.z-min.z)); // finds the longest side as if it fits everything does
        if (size == 0) {return;}

        // scale the model so that it takes up half the height
        float scale = settings.getResolution().y/(2*size);

        for (Vector3 vertex : vertices) {
            // shifts the entire model so its bounding box centre is at the origin, scales it to be half the simulation height, and move it to the centre of the simulation area
            vertex.x = (vertex.x-centre.x)*scale + settings.getResolution().x/2;
            vertex.y = (vertex.y-centre.y)*scale + settings.getResolution().y/2;
            vertex.z = (vertex.z-centre.z)*scale + settings.getResolution().z/2;
        }

        for (int[] face : faces) {
            for (int count=0; count<face.length; count++) {
                Vector3 vertex1 = vertices.get(face[count]);
                Vector3 vertex2 = vertices.get(face[(count+1)%face.length]);
                shapeDrawer.line(vertex1, vertex2, fixedBarrier);
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
                    int colour = image.getPixel(x, image.getHeight()-y-1);
                    if (colour == 0x000000FF) { // black
                        barriers += 1;
                    }
                }
            }
            image.dispose();
            return barriers;
        } else if (file.extension().equals("obj")) { // 3D
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
        } else if (file.extension().equals("obj")) { // 3D file
            System.out.println("3D file");
        }
        return new Vector3(0, 0, 0);
    }

    public String getModelsFolder() {return this.modelsFolder;}
}

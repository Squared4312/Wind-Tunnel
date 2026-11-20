package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import io.github.wind_tunnel.ENums.Solver;

public class ImportFile {

    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;
    private ShapeDrawer shapeDrawer;

    private String modelsFolder = "assets/models/";
    private String levelsFolder = "assets/levels/";
    private String presetBarrierFolder = "assets/presets/";

    private Vector3 min = new Vector3();
    private Vector3 max = new Vector3();
    private Vector3 centre = new Vector3();

    private ArrayList<Vector3> vertices = new ArrayList<>();
    private ArrayList<int[]> faces = new ArrayList<>();
    private String[] contents;
    private String[] lineList;

    private Vector3 oldResolution;
    private float z;

    private Pixmap image;
    private FileHandle file;

    private Vector3 vertex1;
    private Vector3 vertex2;
    private Vector3 vertex3;


    public ImportFile() {
        settings = Settings.getInstance();
        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
        shapeDrawer = new ShapeDrawer();
    }

    public boolean importFile(String filePath, boolean scaleToResolution) {
        if (getFileExtension(filePath).equals("bmp")) {
            if (scaleToResolution) {
                return placeModel(filePath, new Vector3(settings.getResolutionValues()[0][0], settings.getResolutionValues()[0][1], 1), Solver.TWO_D, true, false, false);
            } else {
                for (int[] resolution : settings.getResolutionValues()) {
                    if (getModelResolution(filePath).x <= resolution[0] && getModelResolution(filePath).y <= resolution[1]) {
                        return placeModel(filePath, new Vector3(resolution[0], resolution[1], 1), Solver.TWO_D, false, false, false);
                    }
                }
            }
        } else if (getFileExtension(filePath).equals("obj")) {
            if (getVerticesFromOBJModel(filePath) == null || getFacesFromOBJModel(filePath) == null) {return false;}
            return placeModel(filePath, new Vector3(settings.getResolutionValues()[2][0], settings.getResolutionValues()[2][1], settings.getResolutionValues()[2][1]), Solver.THREE_D, false, false, true);
        }
        return false;
    }

    public boolean placeModel(String filePath, Vector3 resolution, Solver solver, boolean scaleToResolution, boolean fixedBarrier, boolean fill) {
        oldResolution = settings.getResolution();
        cfdSolver.removeAllBarriers();
        settings.setSolver(solver);
        settings.setResolution(resolution);
        settings.setOldSolver(solver);
        cfdSolver.initialiseFluid();
        if (solver.equals(Solver.TWO_D)) {
            loadBitmap(filePath, fixedBarrier, fill);
            z = 1;
        } else if (solver.equals(Solver.THREE_D)) {
            loadOBJModel(filePath, fixedBarrier, fill);
            z = oldResolution.z;
        } else {
            return false;
        }
        if (scaleToResolution && oldResolution != settings.getResolution()) {
            settings.setResolution(new Vector3(oldResolution.x, oldResolution.y, z));
            cfdSolver.initialiseFluid();
        }
        return true;
    }

    public void loadBitmap(String filePath, boolean fixedBarrier, boolean fill) {
        file = Gdx.files.internal(filePath);
        if (!file.exists()) {return;}

        image = new Pixmap(file);
        if (image.getWidth() > settings.getResolution().x || image.getHeight() > settings.getResolution().y || settings.getResolution().z != 1) { // too big
            image.dispose();
            return;
        }

        for (int x=0; x<image.getWidth(); x++) {
            for (int y=0; y<image.getHeight(); y++) {
                int colour = image.getPixel(x, image.getHeight()-y-1);
                if (colour == 0x000000FF) { // black
                    cfdSolver.addBarrier(x, y, 0, fixedBarrier);
                }
            }
        }
        image.dispose();

        if (fill) {
            cfdSolver.fillEnclosedShapes(fixedBarrier);
        }
    }

    public void loadOBJModel(String filePath, boolean fixedBarrier, boolean fill) {
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
        forward axis = x
        up axis = y
        */

        vertices = getVerticesFromOBJModel(filePath);
        faces = getFacesFromOBJModel(filePath);

        if (vertices == null || faces == null) {return;}

        // draw lines (using shapeDrawer.line()) from each vertex to its neighbour according to the faces from the loaded .obj

        // bounding box of the model, where min is the front-bottom-left corner and max is the back-top-right corner
        min.set(vertices.get(0));
        max.set(vertices.get(0));
        for (Vector3 vertex : vertices) {
            min.x = Math.min(min.x, vertex.x);
            min.y = Math.min(min.y, vertex.y);
            min.z = Math.min(min.z, vertex.z);
            max.x = Math.max(max.x, vertex.x);
            max.y = Math.max(max.y, vertex.y);
            max.z = Math.max(max.z, vertex.z);
        }

        centre.set((min.x+max.x)/2, (min.y+max.y)/2, (min.z+max.z)/2); // centre of the bounding box
        float size = Math.max(max.x-min.x, Math.max(max.y-min.y, max.z-min.z)); // finds the longest side, as if it fits, everything does
        if (size == 0) {return;}

        // find the biggest axis to scale the model to fit the simulation area correctly
        float scale = getScale(max, min, size);

        for (Vector3 vertex : vertices) {
            // shifts the entire model so its bounding box centre is at the origin, scales it to be half the simulation height, and move it to the centre of the simulation area
            vertex.x = (vertex.x-centre.x)*scale + settings.getResolution().x/2;
            vertex.y = (vertex.y-centre.y)*scale + settings.getResolution().y/2;
            vertex.z = (vertex.z-centre.z)*scale + settings.getResolution().z/2;
        }

        for (int[] face : faces) {
            for (int count=0; count<face.length; count++) {
                vertex1 = vertices.get(face[count]);
                vertex2 = vertices.get(face[(count+1)%face.length]);
                vertex3 = vertices.get(face[(count+2)%face.length]);
                shapeDrawer.triangle(vertex1, vertex2, vertex3, fixedBarrier);
            }
        }

        if (fill) {
            cfdSolver.fillEnclosedShapes(fixedBarrier);
        }
    }

    private float getScale(Vector3 max, Vector3 min, float size) {
        float axis = settings.getResolution().x;
        float biggestAxisRatio = (max.x-min.x)/axis;
        if ((max.y-min.y)/settings.getResolution().y > biggestAxisRatio) {
            axis = settings.getResolution().y;
            biggestAxisRatio = (max.y-min.y)/axis;
        }
        if ((max.z-min.z)/settings.getResolution().z > biggestAxisRatio) {
            axis = settings.getResolution().z;
        }

        // scale the model so that it takes up 3/4 of the biggest axis
        return (3/4f)*(axis/size);
    }

    public ArrayList<Vector3> getVerticesFromOBJModel(String filePath) {
        file = Gdx.files.internal(filePath);
        if (!file.exists()) {return null;}

        contents = file.readString().split("\\r?\\n"); // split at every new line
        vertices.clear();

        for (String line : contents) {
            if (line.isEmpty()) {continue;}

            lineList = line.split("\\s+");
            if (line.startsWith("v ")) {
                if (lineList.length < 4) {continue;} //  vertex needs x y z
                vertices.add(new Vector3(Float.parseFloat(lineList[1]), Float.parseFloat(lineList[2]), Float.parseFloat(lineList[3])));
            }
        }
        if (vertices.isEmpty()) {return null;}

        return vertices;
    }

    public ArrayList<int[]> getFacesFromOBJModel(String filePath) {
        file = Gdx.files.internal(filePath);
        if (!file.exists()) {return null;}

        contents = file.readString().split("\\r?\\n"); // split at every new line
        faces.clear();
        int vertexCount = 0;

        for (String line : contents) {
            if (line.isEmpty()) {continue;}

            lineList = line.split("\\s+");
            if (line.startsWith("v ")) {
                vertexCount += 1;
            } else if (line.startsWith("f ")) {
                if (lineList.length != 4) {return null;} // must be f 1 2 3 to draw triangles
                int[] face = new int[lineList.length-1];
                int index;
                for (int count=1; count<lineList.length; count++) {
                    index = Integer.parseInt(lineList[count].split("/")[0]);
                    if (index < 0) {
                        face[count-1] = vertexCount+index;
                    } else {
                        face[count-1] = index-1;
                    }
                }
                faces.add(face);
            }
        }
        if (faces.isEmpty()) {return null;}

        for (int[] face : faces) {
            for (int index : face) {
                if (index < 0 || index >= vertexCount) { // vertex index is out of range
                    return null;
                }
            }
        }

        return faces;
    }

    public String[] getAllFilePaths() {
        FileHandle[] modelFiles = Gdx.files.internal(modelsFolder).list();
        FileHandle[] levelFiles = Gdx.files.internal(levelsFolder).list();
        String[] filePaths = new String[modelFiles.length+levelFiles.length];
        for (int count=0; count<modelFiles.length; count++) { // model files
            filePaths[count] = modelFiles[count].path();
        }
        for (int count=modelFiles.length; count<modelFiles.length+levelFiles.length; count++) { // level files
            filePaths[count] = levelFiles[count-modelFiles.length].path();
        }
        return filePaths;
    }

    public String getFileNameAndExtension(String filePath) {
        return Gdx.files.internal(filePath).name();
    }

    public String getFullFilePath(String filePath) {
        return Gdx.files.internal(filePath).path();
    }

    public String getFileExtension(String filePath) {
        return Gdx.files.internal(filePath).extension();
    }

    public int getNumberOfFiles(String folder) {
        return Gdx.files.internal(folder).list().length;
    }

    public Solver getModelType(String filePath) {
        if (getFileExtension(filePath).equals("bmp")) {
            return Solver.TWO_D;
        } else if (getFileExtension(filePath).equals("obj")) {
            return Solver.THREE_D;
        }
        return null;
    }

    public Integer getModelBarriers(String filePath) {
        file = Gdx.files.internal(filePath);
        if (!file.exists()) {return null;}

        int barriers = 0;
        if (file.extension().equals("bmp")) { // 2D file
            image = new Pixmap(file);
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
        }

        return null;
    }

    public Vector3 getModelResolution(String filePath) {
        file = Gdx.files.internal(filePath);
        if (!file.exists()) {return new Vector3(0, 0, 0);}

        if (file.extension().equals("bmp")) { // 2D file
            image = new Pixmap(file);
            return new Vector3(image.getWidth(), image.getHeight(), 0);
        }
        return new Vector3(0, 0, 0);
    }

    public String getModelsFolder() {return this.modelsFolder;}
    public String getLevelsFolder() {return this.levelsFolder;}
    public String getPresetBarrierFolder() {return this.presetBarrierFolder;}
}

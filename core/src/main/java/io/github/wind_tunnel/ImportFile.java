package io.github.wind_tunnel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector3;

public class ImportFile {

    private Settings settings;
    private LatticeBoltzmannCFDSolver cfdSolver;

    private String modelsFolder = "assets/models/";
    private String[] fileNames;

    public ImportFile() {
        settings = Settings.getInstance();
        cfdSolver = LatticeBoltzmannCFDSolver.getInstance();
    }

    public void loadBitmap(String folder, String name, boolean fixedBarrier) {
        FileHandle file = Gdx.files.internal(folder + name + ".bmp");
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

    public void loadBlenderModel(String folder, String name, boolean fixedBarrier) {
        FileHandle file = Gdx.files.internal(folder + name + ".bmp");
        if (!file.exists()) {return;}
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
        if (!file.exists()) {return null;}

        if (file.extension().equals("bmp")) { // 2D file
            Pixmap image = new Pixmap(file);
            return new Vector3(image.getWidth(), image.getHeight(), 0);
        } else if (file.extension().equals("blend")) { // 3D
            System.out.println("3D file");
        }
        return null;
    }

    public String getModelsFolder() {return this.modelsFolder;}
}

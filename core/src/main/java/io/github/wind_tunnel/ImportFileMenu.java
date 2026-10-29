package io.github.wind_tunnel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class ImportFileMenu implements Menu {
    private Settings settings;
    private MenuUtil util;
    private ImportFile fileImporter;

    private Rectangle backButton;
    private Rectangle importButton;
    private Rectangle cancelButton;

    private Texture backIcon;
    private Texture importFileIcon;

    private String[] fileNames;
    private Rectangle[] fileButtons;
    private String selectedFile = null;

    public ImportFileMenu() {
        this.util = new MenuUtil();
        this.settings = Settings.getInstance();
        this.fileImporter = new ImportFile();

        this.backIcon = util.loadIcon("back");
        this.importFileIcon = util.loadIcon("folder");
    }

    @Override
    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
            backButton = util.getHitbox(57.5f, 1022.5f, 75, 75);
            importButton = util.getHitbox(1163.75f, 242.5f, 287.5f, 75);
            cancelButton = util.getHitbox(1476.25f, 242.5f, 287.5f, 75);

            util.renderRoundedRectangle(sr, util.getButtonColor(), 600, 492.5f, 600, 575, 16); // file list

            util.renderRoundedRectangle(sr, util.getButtonColor(), 1320, 542.5f, 600, 475, 16); // selected file

            util.renderRoundedRectangle(sr, util.getButtonColor(), 1163.75f, 242.5f, 287.5f, 75, 16); // import
            util.renderRoundedRectangle(sr, util.getButtonColor(), 1476.25f, 242.5f, 287.5f, 75, 16); // cancel
        sr.end();

        batch.begin();
            util.renderText(batch, "import a file", Color.WHITE, util.getScreenDimensions().x/2, 953, 96, "centre");

            fileNames = fileImporter.getAllFileNames();
            fileButtons = new Rectangle[fileNames.length];

            if (fileNames.length == 0) {
                util.renderText(batch, "no files found in", Color.WHITE, 600, 517.5f, 36, "centre");
                util.renderText(batch, fileImporter.getModelsFolder(), Color.WHITE, 600, 467.5f, 36, "centre");
            } else {
                for (int count=0; count<fileNames.length; count++) {
                    util.renderText(batch, fileNames[count], Color.WHITE, 320, 742.5f-(50*count), 36, "left");
                    fileButtons[count] = new Rectangle(300, 742.5f-(50*count)-25, 600, 50);
                }
            }

            if (selectedFile == null) {
                util.renderText(batch, "no file selected", Color.WHITE, 1320, 542.5f, 36, "centre");
            } else {
                util.renderText(batch, selectedFile, Color.WHITE, 1320, 742.5f, 36, "centre");
                util.renderText(batch, "resolution: " + (int) fileImporter.getModelResolution(selectedFile).x + "x" + (int) fileImporter.getModelResolution(selectedFile).y + "x" + (int) fileImporter.getModelResolution(selectedFile).z, Color.WHITE, 1320, 667.5f, 36, "centre");
                util.renderText(batch, "type: " + fileImporter.getModelType(selectedFile), Color.WHITE, 1320, 617.5f, 36, "centre");
                util.renderText(batch, "barriers: " + fileImporter.getModelBarriers(selectedFile), Color.WHITE, 1320, 567.5f, 36, "centre");
            }

            util.renderText(batch, "import", Color.WHITE, 1163.75f, 242.5f, 36, "centre"); // import
            util.renderText(batch, "cancel", Color.WHITE, 1476.25f, 242.5f, 36, "centre"); // cancel

            util.renderIcon(batch, backIcon, 57.5f, 1022.5f);
            util.renderIcon(batch, importFileIcon, 57.5f, 57.5f);
        batch.end();
    }

    @Override
    public String checkIfButtonsClicked() {
        if (selectedFile != null && util.isButtonClicked(importButton)) {
            System.out.println("import " + selectedFile);
        }
        if (util.isButtonClicked(cancelButton)) {
            selectedFile = null;
        }

        for (int count=0; count<fileNames.length; count++) {
            if (util.isButtonClicked(fileButtons[count])) {
                selectedFile = fileNames[count];
                break;
            }
        }

        if (util.isButtonClicked(backButton)) {
            settings.setPlaceBarriers(false);
            return "back";
        }
        return "importFileMenu";
    }
}

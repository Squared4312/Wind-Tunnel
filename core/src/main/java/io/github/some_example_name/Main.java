package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

public class Main extends ApplicationAdapter {
    private Environment environment;
    private PerspectiveCamera camera;
    private CameraInputController cameraController;
    private ModelBatch modelBatch;

    private Integer screenWidth;
    private Integer screenHeight;

    @Override
    public void create() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        // create an environment + lighting
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        // create a perspective camera
        camera = new PerspectiveCamera(67, screenWidth, screenHeight);
        camera.position.set(0, 32, 32);
        camera.lookAt(0, 0, 0);
        camera.near = 1;
        camera.far = 300;
        camera.update();
        cameraController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraController);

        // chessBoardModel = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("assets/models/chessBoard/chessBoard.g3dj"));
        // for (Material m : chessBoardModel.materials) {m.remove(ColorAttribute.Emissive); m.set(new IntAttribute(IntAttribute.CullFace, 0));} // fixes washed look
        // chessBoardInstance = new ModelInstance(chessBoardModel);
    }

    @Override
    public void render() {
        cameraController.update();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f); // sets background colour

        Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
            // modelBatch.render( <modelInstance> ,  environment)
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        camera.viewportWidth = screenWidth;
        camera.viewportHeight = screenHeight;
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}

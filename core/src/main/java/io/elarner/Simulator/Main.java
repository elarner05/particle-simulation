package io.elarner.Simulator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import io.elarner.AbstractElements.Element;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    private int scale = 4;

    private FrameBuffer gameFrameBuffer;
    private FrameBuffer scaledFrameBuffer;

    private TextureRegion srcRegion;
    private TextureRegion finalRegion;

    private InputProcessor input;

    private SpriteBatch mainBatch;
    private SpriteBatch internalBatch;
    private SpriteBatch scaledBatch;

    static boolean clicked = false;
    static public boolean zoneMode = false;
    static int xShift = 160;
    static int yShift = 80;

    final private int gameResX = 400;
    final private int gameResY = 200;

    private PixelGrid pixelGrid;


    @Override
    public void create() {
        
        gameFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, gameResX, gameResY, false);
        scaledFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, gameResX*scale, gameResY*scale, false);

        srcRegion = new TextureRegion();
        finalRegion = new TextureRegion();

        this.mainBatch = new SpriteBatch();

        this.internalBatch = new SpriteBatch();
        OrthographicCamera internalCamera = new OrthographicCamera(gameResX, gameResY);
        internalCamera.setToOrtho(false, gameResX, gameResY);
        this.internalBatch.setProjectionMatrix(internalCamera.combined);

        this.scaledBatch = new SpriteBatch();
        OrthographicCamera scaledCamera = new OrthographicCamera(gameResX, gameResY);
        scaledCamera.setToOrtho(false, gameResX*scale, gameResY*scale);
        this.scaledBatch.setProjectionMatrix(scaledCamera.combined);

        pixelGrid = new PixelGrid(gameResX, gameResY);

        this.input = new InputProcessor() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.UP) {
                    Main.yShift += 5;
                } else if (keycode == Keys.DOWN) {
                    Main.yShift -= 5;
                } else if (keycode == Keys.RIGHT) {
                    Main.xShift +=5;
                } else if (keycode == Keys.LEFT) {
                    Main.xShift -= 5;
                } else if (keycode == Keys.SPACE) {
                    PixelGrid.elementType = PixelGrid.elementType.next();
                }  else {
                    System.out.println("Key pressed keycode: " + keycode);
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                return true;
            }

            @Override
            public boolean keyTyped(char character) {
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return true;
            }

            @Override
            public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return true;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return true;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return true;
            }
        };
        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void render() {
       gameLogic();
       this.pixelGrid.simulationFrame(Gdx.graphics.getDeltaTime());

        this.gameFrameBuffer.begin();
        
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        internalBatch.begin();
        pixelGrid.render(internalBatch);
        internalBatch.end();
        
        this.gameFrameBuffer.end();

        Texture srcTexture = gameFrameBuffer.getColorBufferTexture();
        srcTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        srcRegion = new TextureRegion(srcTexture);
        srcRegion.flip(false, true);

        this.scaledFrameBuffer.begin();

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.scaledBatch.begin();

        this.scaledBatch.draw(srcRegion, 0, 0, this.scaledFrameBuffer.getWidth(), this.scaledFrameBuffer.getHeight());

        this.scaledBatch.end();
        this.scaledFrameBuffer.end();

        Texture finalTex = scaledFrameBuffer.getColorBufferTexture();
        finalTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        finalRegion = new TextureRegion(finalTex);
        finalRegion.flip(false, true);

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.mainBatch.begin();
        this.mainBatch.draw(finalRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.mainBatch.end();

    }

    @Override
    public void resize(int width, int height) {

    }


    public void gameLogic() {
        int scale = Gdx.graphics.getHeight() / gameResY;
        int mx = Gdx.input.getX() / scale;
        int my = (Gdx.graphics.getHeight() - Gdx.input.getY() - 1) / scale;

        if (mx >= 0 && mx < gameResX && my >= 0 && my < gameResY) {
            if (Gdx.input.isTouched()) {
                pixelGrid.addParticle(mx, my);
                pixelGrid.addParticle(mx+1, my);
                pixelGrid.addParticle(mx-1, my);
                pixelGrid.addParticle(mx, my+1);
                pixelGrid.addParticle(mx, my-1);
                pixelGrid.addParticle(mx+1, my+1);
                pixelGrid.addParticle(mx-1, my-1);
                pixelGrid.addParticle(mx+1, my-1);
                pixelGrid.addParticle(mx-1, my+1);
            }
        }

    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        Element.disposeTextures();
        this.mainBatch.dispose();
        this.internalBatch.dispose();
        this.scaledBatch.dispose();

        this.gameFrameBuffer.dispose();
        this.scaledFrameBuffer.dispose();
    }
    

}

package io.elarner.AbstractElements;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import io.elarner.Simulator.PixelGrid;


public abstract class Element {
    private Color color; // Assuming color is represented as an integer (ARGB or similar)
    protected int x, y; // Position of the element in the grid
    protected float xVelocity, yVelocity;
    protected float accuYChange = 0.0f; // accumulated y change. stores up to 1.0f of y accumulated before trying to add change
    
    private static Map<Color, Texture> textureCache = new HashMap<>();

    private static final float GRAVITY = 50.0f; // Gravity constant
    private static final float FRICTION = 0.1f; // Friction constant
    private static final float MAX_VELOCITY = 10f; // Maximum velocity for the element
    public static Random random = new Random();
    

    public Element(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.xVelocity = 0.0f;
        this.yVelocity = 0.0f;
        if (!textureCache.containsKey(color)) {
            Element.setupTexture(this.color);
        }
    }

    public Color getColor() {
        return color;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getYVelocity() {
        return yVelocity;
    }

    public void render(Batch batch, int x, int y) {
        batch.draw(Element.getTexture(this.color), x, y, 1, 1);
    }

    public abstract void update(float delta, PixelGrid matrix);

    public void updateBufferPosition(int index, PixelGrid matrix) {
        matrix.updateBuffer(index, this); // Update the buffer with the new position
        this.x = index % matrix.width;
        this.y = index / matrix.width;
    }

    public void applyForces(float delta) {
        this.applyGravity(delta);
    }

    public void applyGravity(float delta) {
        this.yVelocity -= GRAVITY * delta; // Apply gravity
    };

    private static void setupTexture(Color color) {
        if (!textureCache.containsKey(color)) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(color);
            pixmap.fill();
            Texture texture = new Texture(pixmap);
            Element.textureCache.put(color, texture);
            pixmap.dispose();
        }
    }

    public static Texture getTexture(Color color) {
        try {
            return textureCache.get(color);
        } catch (NullPointerException e) {
            setupTexture(color);
            return textureCache.get(color);
        }
    }

    public static void disposeTextures() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
    }
}

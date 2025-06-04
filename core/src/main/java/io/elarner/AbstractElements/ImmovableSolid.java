package io.elarner.AbstractElements;

import com.badlogic.gdx.graphics.Color;

import io.elarner.Simulator.Matrix;

public abstract class ImmovableSolid extends Solid {
    
    public final float xVelocity = 0f; // No horizontal movement
    public final float yVelocity = 0f; // No vertical movement
    
    public ImmovableSolid(Color color, int x, int y) {
        super(color, x, y);
    }

    @Override
    public void update(float delta, Matrix matrix) {
        // matrix.updateMatrix(y * matrix.width + x, this); // Keep the element in its position
    }

    @Override
    public void setVelocity(float vel) {
        return;
    }
}

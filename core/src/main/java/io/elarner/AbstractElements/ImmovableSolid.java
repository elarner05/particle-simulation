package io.elarner.AbstractElements;

import com.badlogic.gdx.graphics.Color;

import io.elarner.Simulator.PixelGrid;

public abstract class ImmovableSolid extends Element {
    
    public ImmovableSolid(Color color, int x, int y) {
        super(color, x, y);
    }

    @Override
    public void update(float delta, PixelGrid matrix) {
        matrix.updateBuffer(y * matrix.width + x, this); // Keep the element in its position
    }
}

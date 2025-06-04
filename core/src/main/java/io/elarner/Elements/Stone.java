package io.elarner.Elements;

import com.badlogic.gdx.graphics.Color;

import io.elarner.AbstractElements.ImmovableSolid;

public class Stone extends ImmovableSolid {

    private static final Color DEFAULT_COLOR = Color.GRAY; // Default color for stone

    public Stone(int x, int y) {
        super(DEFAULT_COLOR, x, y);
    }

    @Override
    public int getPriority() {
        return 100;
    }
}


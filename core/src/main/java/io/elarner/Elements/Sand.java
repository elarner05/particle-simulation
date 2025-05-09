package io.elarner.Elements;

import com.badlogic.gdx.graphics.Color;

import io.elarner.AbstractElements.MovableSolid;

public class Sand extends MovableSolid {

    private static final Color DEFAULT_COLOR = Color.YELLOW; // Default color for sand

    public Sand(int x, int y) {
        super(DEFAULT_COLOR, x, y);
    }
}

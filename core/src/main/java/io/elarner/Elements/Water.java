package io.elarner.Elements;

import com.badlogic.gdx.graphics.Color;

import io.elarner.AbstractElements.Liquid;

public class Water extends Liquid {

    private static final Color DEFAULT_COLOR = Color.BLUE; // Default color for water

    public Water(int x, int y) {
        super(DEFAULT_COLOR, x, y);
    }

}

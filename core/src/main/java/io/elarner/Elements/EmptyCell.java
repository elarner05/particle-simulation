package io.elarner.Elements;

import com.badlogic.gdx.graphics.g2d.Batch;

import io.elarner.AbstractElements.Element;
import io.elarner.Simulator.Matrix;

public class EmptyCell extends Element {

    public EmptyCell(int x, int y) {
        super(null, x, y);
    }

    @Override
    public void update(float delta, Matrix matrix) {
        return;
    }

    @Override
    public void render(Batch batch, int x, int y) {
        return;
    }
    @Override
    public int getPriority() {
        return 0;
    }
}

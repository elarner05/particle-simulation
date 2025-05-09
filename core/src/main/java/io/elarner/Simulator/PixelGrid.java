package io.elarner.Simulator;


import com.badlogic.gdx.graphics.g2d.Batch;

import io.elarner.AbstractElements.Element;
import io.elarner.Elements.ElementType;
import io.elarner.Elements.Sand;
import io.elarner.Elements.Stone;
import io.elarner.Elements.Water;

public class PixelGrid {

    private Element[] grid;
    private Element[] buffer;
    public int width;
    public int height;

    private float accDelta = 0.0f;

    private float timeStep = 1.0f / 60.0f; // 60 FPS

    public static ElementType elementType = ElementType.SAND; 

    public PixelGrid(int w, int h) {

        width = w;
        height = h;
        this.grid = new Element[width * height];
        this.buffer = new Element[width * height];
        for (int i = 0; i < grid.length; i++) {
            grid[i] = null;
        }
    }   

    public void render(Batch batch) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                if (grid[index] != null) {
                    grid[index].render(batch, x, y);
                }
            }
        }
    }

    public void addParticle(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            int index = y * width + x;
            if (grid[index] == null) {
                if (elementType == ElementType.SAND) {
                    grid[index] = new Sand(x, y);
                } else if (elementType == ElementType.STONE) {
                    grid[index] = new Stone(x, y);
                } else if (elementType == ElementType.WATER) {
                    grid[index] = new Water(x, y);
                } 
            }
        }
    }

    public void simulationFrame(float delta) {
        // System.out.println("Simulation frame: " + (int) (1/delta) + " FPS");
        accDelta += delta;
        while (accDelta >= timeStep) {
            update(timeStep);
            accDelta -= timeStep;
        }
    }

    public boolean isInBounds(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    public boolean isInBounds(int index) {
        return (index >= 0 && index < width * height);
    }


    public void swap(int fromX, int fromY, int toX, int toY) {
        if (fromX == toX && fromY == toY) {
            return; // No swap needed
        }
        if (isInBounds(fromX, fromY) && isInBounds(toX, toY)) {
            int fromIndex = fromY * width + fromX;
            int toIndex = toY * width + toX;
            if (grid[toIndex] == null) {
                buffer[toIndex] = grid[fromIndex];
            } else {
                buffer[toIndex] = grid[fromIndex];
                buffer[fromIndex] = grid[toIndex];
            }
        }
    }

    public Element getParticle(int x, int y) {
        if (isInBounds(x, y)) {
            int index = y * width + x;
            if (buffer[index] != null) {
                return buffer[index];
            } else {
                return grid[index];
            } 
        }
        return null;
    }

    public Element getParticle(int index) {
        if (isInBounds(index)) {
            if (buffer[index] != null) {
                return buffer[index];
            } else {
                return grid[index];
            } 
            
        }
        return null;
    }

    public boolean isEmpty(int x, int y) {
        if (isInBounds(x, y)) {
            int index = y * width + x;
            return grid[index] == null && buffer[index] == null;
        }
        return false;
    }

    public boolean isEmpty(int index) {
        if (isInBounds(index)) {
            return grid[index] == null && buffer[index] == null;
        }
        return false;
    }

    public int nextEmptyBresenham(int x0, int y0, int x1, int y1) {
        // Returns the last valid index before hitting a non-empty cell

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = Integer.compare(x1, x0);
        int sy = Integer.compare(y1, y0);
        int err = dx - dy;

        int lastValidIndex = y0 * width + x0;

        while (true) {

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            if (!isInBounds(x0, y0) || !isEmpty(x0, y0)) break;
            lastValidIndex = y0 * width + x0;

            if (x0 == x1 && y0 == y1) break;
        }

        return lastValidIndex;
    }

    public int nextFullBresenham(int x0, int y0, int x1, int y1) {
        // Returns the last valid index before hitting a non-empty cell
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = Integer.compare(x1, x0);
        int sy = Integer.compare(y1, y0);
        int err = dx - dy;

        int lastValidIndex = y0 * width + x0;

        while (true) {

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }

            if (!isInBounds(x0, y0)) break;
            lastValidIndex = y0 * width + x0;
            if (!isEmpty(x0, y0)) break; // Check if the cell is not empty

            if (x0 == x1 && y0 == y1) break;
        }

        return lastValidIndex;
    }

    public void updateBuffer(int x, int y, Element p) {
        if (isInBounds(x, y)) {
            buffer[y * width + x] = p;
        }
    }
    public void updateBuffer(int index, Element p) {
        if (isInBounds(index)) {
            buffer[index] = p;
        }
    }


    void update(float delta) {

        for (int i = 0; i < grid.length; i++) {
            buffer[i] = null;
        }

        for (int i = 0; i < grid.length; i++){
            Element p = grid[i];
            if (p != null) {
                p.update(delta, this);
                // int x = i % width;
                // int y = i / width;

                // int belowIndex = (y - 1) * width + x;
                // int belowLeftIndex = (y - 1) * width + (x - 1);
                // int belowRightIndex = (y - 1) * width + (x + 1);

                // boolean canMoveDown = (y - 1) >= 0;
                // boolean canMoveLeft = (x - 1) >= 0;
                // boolean canMoveRight = (x + 1) < width;

                // if (canMoveDown && grid[belowIndex] == null) {
                //     buffer[belowIndex] = p;
                // } else if (canMoveDown && canMoveLeft && canMoveRight && grid[belowLeftIndex] == null && grid[belowRightIndex] == null) {
                //     if (x+y % 2 == 0) {
                //         buffer[belowLeftIndex] = p;
                //     } else {
                //         buffer[belowRightIndex] = p;
                //     }
                // } else if (canMoveDown && canMoveLeft && grid[belowLeftIndex] == null) {
                //     buffer[belowLeftIndex] = p;
                // } else if (canMoveDown && canMoveRight && grid[belowRightIndex] == null) {
                //     buffer[belowRightIndex] = p;
                // } else {
                //     buffer[i] = p; 
                // }
            }
        }
        for (int i = 0; i < grid.length; i++) {
            grid[i] = buffer[i];
        }
    }

    
}

package io.elarner.Simulator;

import com.badlogic.gdx.graphics.g2d.Batch;

import io.elarner.AbstractElements.Element;
import io.elarner.Elements.ElementType;
import io.elarner.Elements.EmptyCell;
import io.elarner.Elements.Sand;
import io.elarner.Elements.Stone;
import io.elarner.Elements.Water;

public class Matrix {

    private Element[] grid;
    // private Element[] grid;
    public int width;
    public int height;

    private float accDelta = 0.0f;

    private float timeStep = 1.0f / 60.0f; // 60 FPS

    public static ElementType elementType = ElementType.SAND; 

    public int simulationFrameCounter;

    public Matrix(int w, int h) {

        width = w;
        height = h;
        this.grid = new Element[width * height];
        for (int i = 0; i < grid.length; i++) {
            grid[i] = new EmptyCell(0, 0);
        }
        simulationFrameCounter = 0;
    }   

    public void render(Batch batch) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                if (!(grid[index] instanceof EmptyCell)) {
                    grid[index].render(batch, x, y);
                }
            }
        }
    }

    public void addParticle(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            int index = y * width + x;
            if (grid[index] == null || grid[index] instanceof EmptyCell) {
                if (elementType == ElementType.SAND) {
                    grid[index] = new Sand(x, y);
                } else if (elementType == ElementType.STONE) {
                    grid[index] = new Stone(x, y);
                } else if (elementType == ElementType.WATER) {
                    grid[index] = new Water(x, y);
                }  
            } else if (elementType == ElementType.EMPTY) {
                grid[index] = new EmptyCell(x, y);
            }
                   
        }
    }

    public void simulationFrame(float delta) {
        // System.out.println("Simulation frame: " + (int) (1/delta) + " FPS");
        accDelta += delta;
        while (accDelta >= timeStep) {
            update(1);
            accDelta -= timeStep;
        }
    }

    public boolean isInBounds(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }

    public boolean isInBounds(int index) {
        return (index >= 0 && index < width * height);
    }


    // public void swap(int fromX, int fromY, int toX, int toY) {
    //     if (fromX == toX && fromY == toY) {
    //         return; // No swap needed
    //     }
    //     if (isInBounds(fromX, fromY) && isInBounds(toX, toY)) {
    //         int fromIndex = fromY * width + fromX;
    //         int toIndex = toY * width + toX;
    //         if (grid[toIndex] == null) {
    //             grid[toIndex] = grid[fromIndex];
    //         } else {
    //             grid[toIndex] = grid[fromIndex];
    //             grid[fromIndex] = grid[toIndex];
    //         }
    //     }
    // }

    public Element getParticle(int x, int y) {
        if (isInBounds(x, y)) {
            int index = y * width + x;
            if (grid[index] != null) {
                return grid[index];
            } else {
                return grid[index];
            } 
        }
        return null;
    }

    public Element getParticle(int index) {
        if (isInBounds(index)) {
            if (grid[index] != null) {
                return grid[index];
            } else {
                return grid[index];
            } 
            
        }
        return null;
    }

    public boolean isEmpty(int x, int y) {
        if (isInBounds(x, y)) {
            int index = y * width + x;
            return grid[index] instanceof EmptyCell;
        }
        return false;
    }

    public boolean isEmpty(int index) {
        if (isInBounds(index)) {
            return grid[index] instanceof EmptyCell;
        }
        return false;
    }

    public boolean lessPriority(int x, int y, int priority) {
        if (isInBounds(x, y)) {
            int index = y * width + x;
            if (grid[index] == null) return true;

            return grid[index].getPriority() < priority;
        }
        return false;
    }

    public boolean lessPriority(int index, int priority) {
        if (isInBounds(index)) {
            if (grid[index] == null) return true;
            return grid[index].getPriority() < priority;
        }
        return false;
    }

    public int nextEmptyBresenham(int x0, int y0, int x1, int y1, int priority) {
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

            if (!isInBounds(x0, y0) || !lessPriority(x0, y0, priority)) break;
            lastValidIndex = y0 * width + x0;

            if (x0 == x1 && y0 == y1) break;
        }

        return lastValidIndex;
    }
    

    public int nextFullBresenham(int x0, int y0, int x1, int y1, int priority) {
        // Returns the first valid index of a non-empty cell
        
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
            if (!lessPriority(x0, y0, priority)) break; // Check if the cell is not empty

            if (x0 == x1 && y0 == y1) break;
        }

        return lastValidIndex;
    }

    public void updateMatrix(int x, int y, Element p) {
        if (isInBounds(x, y)) {
            grid[y * width + x] = p;
        } else {
            System.err.println("Index out of bounds: " + x + ", " + y);
        }
    }
    public void updateMatrix(int index, Element p) {
        if (isInBounds(index)) {
            grid[index] = p;
        } else {
            System.err.println("Index out of bounds: " + index);
        }
    }


    void update(float delta) {
        simulationFrameCounter++;

        // for (int i = 0; i < grid.length; i++) {
        //     grid[i] = null;
        // }
        if (simulationFrameCounter%2==0) {
            for (int x = width-1; x >= 0; x--){
                for (int y = 0; y<height; y++) {
                    Element p = grid[y * width + x];
                    if (p != null) {
                        p.update(delta, this);
                    } else {
                        System.err.println("Null found!");
                    }
                }
                
            }
        } else {

            for (int i = 0; i < grid.length; i++){
                Element p = grid[i];
                if (p != null) {
                    p.update(delta, this);
                }
            }
        }   
    }

    
}

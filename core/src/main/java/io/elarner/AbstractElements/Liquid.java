package io.elarner.AbstractElements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import io.elarner.Simulator.Matrix;

public abstract class Liquid extends Element {

    protected static final float dispersaleRate = 5f; // default velocity at which the liquid disperses
    protected final float liquidDamping = 0.8f;
    // protected final float friction = 1f;
    // protected static float minimumXVelocity = 0.01f;

    // protected boolean freeFalling = true;
    // protected int stoppedMovingCount = 0;
    // protected final int stoppedMovingThreshold = 10;

    public Liquid(Color color, int x, int y) {
        super(color, x, y);
        this.yVelocity = -0.1f;
        this.xVelocity = dispersaleRate * (random.nextBoolean() ? -1 : 1);
        // this.directionBias = random.nextBoolean() ? -1 : 1; // Randomly set the direction bias to left or right
    }

    private int calcNewX(float delta) {
        this.accuXChange += this.xVelocity * delta;
        int newX = x + (int) this.accuXChange;
        if (Math.abs(this.accuXChange) >= 1) {
            this.accuXChange -= (float) (int) this.accuXChange;
        }

        return newX;
    }

    private int calcNewY(float delta) {
        this.accuYChange += yVelocity * delta;
            
        int newY = y + (int) this.accuYChange;
        if (Math.abs(this.accuYChange) >= 1) {
            this.accuYChange -= (float) (int) this.accuYChange;
        }

        return newY;
    }

    @Override
    public void update(float delta, Matrix matrix) {

        if (yVelocity < 0) { //matrix.isEmpty(x, y-1)) {
            // if (yVelocity >=0) yVelocity = -0.1f;
            applyGravity(delta);
            int newY = calcNewY(delta);
            int newIndex = matrix.nextEmptyBresenham(x, y, x, newY, this.getPriority());

            if (newIndex != newY * matrix.width + x) {
                int parIndex = matrix.nextFullBresenham(x, y, x, newY, this.getPriority());
                if (parIndex != newIndex) {
                    this.yVelocity = matrix.getParticle(parIndex).getYVelocity();
                } else {
                    this.yVelocity = 0;
                }
            }

            this.updateGridPosition(newIndex, matrix);
        }
        else {
            if (!matrix.lessPriority(x + (int) Math.signum(xVelocity), y, this.getPriority())) {
                xVelocity *= -1;
            }
            yVelocity = 0;
            // if (yVelocity >=0) yVelocity = -0.1f;
            int newX = calcNewX(delta);
            int newIndex = matrix.nextEmptyBresenham(x, y, newX, y, this.getPriority());
            if (newIndex!= y * matrix.width + newX) {
                xVelocity *= -1;
            }
            this.updateGridPosition(newIndex, matrix);
        }

        if (matrix.lessPriority(x, y-1, this.getPriority()) && yVelocity >= 0) {
            yVelocity = -0.1f;
        }

    }
    
    // @Override
    // public void update(float delta, Matrix matrix) {
    //     applyGravity(delta);
    //     if (freeFalling) xVelocity *= liquidDamping;

    //     float velYDeltaTimeFloat = (yVelocity * delta);
    //     float velXDeltaTimeFloat = (xVelocity * delta);
    //     int velXDeltaTime;
    //     int velYDeltaTime;
    //     if (Math.abs(velXDeltaTimeFloat) < 1) {
    //         accuXChange += Math.abs(velXDeltaTimeFloat);
    //         velXDeltaTime = (int) accuXChange;
    //         if (Math.abs(velXDeltaTime) > 0) {
    //             accuXChange -= (float) (int) accuXChange;
    //         }
    //     } else {
    //         accuYChange = 0;
    //         velXDeltaTime = (int) velXDeltaTimeFloat;
    //     }
    //     if (Math.abs(velYDeltaTimeFloat) < 1) {
    //         accuYChange += Math.abs(velYDeltaTimeFloat);
    //         velYDeltaTime = (int) accuYChange;
    //         if (Math.abs(velYDeltaTime) > 0) {
    //             accuYChange = 0;
    //         }
    //     } else {
    //         accuYChange = 0;
    //         velYDeltaTime = (int) velYDeltaTimeFloat;
    //     }

    //     int yModifier = yVelocity < 0 ? -1 : 1;
    //     int xModifier = xVelocity < 0 ? -1 : 1;

    //     boolean xDiffIsLarger = Math.abs(velXDeltaTime) > Math.abs(velYDeltaTime);

    //     int upperBound = Math.max(Math.abs(velXDeltaTime), Math.abs(velYDeltaTime));
    //     int min = Math.min(Math.abs(velXDeltaTime), Math.abs(velYDeltaTime));
    //     float slope = (min == 0 || upperBound == 0) ? 0 : ((float) (min + 1) / (upperBound + 1));

    //     int smallerCount;
    //     int formerIndex = this.y * matrix.width + this.x;
    //     int lastValidIndex = formerIndex;
    //     for (int i = 1; i <= upperBound; i++) {
    //         smallerCount = (int) Math.floor(i * slope);

    //         int yIncrease, xIncrease;
    //         if (xDiffIsLarger) {
    //             xIncrease = i;
    //             yIncrease = smallerCount;
    //         } else {
    //             yIncrease = i;
    //             xIncrease = smallerCount;
    //         }

    //         int y0 = this.y + (yIncrease * yModifier);
    //         int x0 = this.x + (xIncrease * xModifier);

    //         if (i == upperBound) {
    //             this.freeFalling = true;
    //             this.updateGridPosition(lastValidIndex, matrix);
    //             break;
    //         }

    //         // if (!matrix.isInBounds(x0, y0) || !matrix.isEmpty(x0, y0)) break;
    //         if (matrix.isInBounds(x0, y0)) {
    //             Element e = matrix.getParticle(x0, y0);
    //             if (e == this) {
    //                 continue;
    //             }
    //             boolean stopped = actUponPosition(y0*matrix.width + x0, lastValidIndex, matrix, (i == upperBound), i == 1, 0);

    //             if (stopped) break;

    //             lastValidIndex = y0 * matrix.width + x0;
    //         } else {
    //             this.updateGridPosition(lastValidIndex, matrix);
    //             break;
    //         }
        
    //     }

    //     // if (matrix.getParticle((this.y-1)*matrix.width + this.x) != null) {
    //     //     this.yVelocity = -1.0f;
    //     // }

    //     if (formerIndex == lastValidIndex && stoppedMovingCount < stoppedMovingThreshold) {
    //         stoppedMovingCount += 1;
    //     } else {
    //         stoppedMovingCount = 0;
    //     }
    // }

    // public boolean actUponPosition(int index, int lastIndex, Matrix matrix, boolean isFinal, boolean isFirst, int depth) {
    //     Element neighbour = matrix.getParticle(index);
    //     if (neighbour == null) {
    //         if (isFinal) {
    //             this.freeFalling = true;
    //             this.updateGridPosition(index, matrix);
    //             return true;
    //         }
    //         return false;
    //     } else if (neighbour instanceof Liquid) {
    //         Liquid liquidNeighbour = (Liquid) neighbour;
    //         if (false) { // compare densities
    //             // swap
    //             if (isFinal) {
    //                 // swap
    //                 return true;
    //             } else {
    //                 return false;
    //             }
    //         }

    //     } else if (neighbour instanceof Solid) {}


    //     if (neighbour instanceof Liquid || neighbour instanceof Solid) {

    //         if (depth > 0) {
    //             return true;
    //         }

    //         if (isFinal) {
    //             this.updateGridPosition(lastIndex, matrix);
    //             return true;
    //         }

    //         if (this.freeFalling) {
    //             float absY = Math.max(Math.abs(yVelocity) / 31, 2.0f);
    //             xVelocity = xVelocity < 0 ? -absY : absY;
    //         }

    //         float[] norVelocity = this.normalizedVelocity();
    //         int extraX = norVelocity[0] < -0.1f ? (int) Math.floor(norVelocity[0]) : (norVelocity[0] > 0.1f ? (int) Math.ceil(norVelocity[0]) : 0);
    //         int extraY = norVelocity[1] < -0.1f ? (int) Math.floor(norVelocity[1]) : (norVelocity[1] > 0.1f ? (int) Math.ceil(norVelocity[1]) : 0);
    //         // System.out.println(extraX + " " + extraY);

    //         int distance = extraX * (Math.random() > 0.5 ? dispersaleRate + 2 : dispersaleRate - 1);

    //         Element diagonalNeighbour = matrix.getParticle(this.x + extraX, this.y + extraY);
    //         if (isFirst && diagonalNeighbour != null) {
    //             this.yVelocity = getAverageVelOrGravity(yVelocity, diagonalNeighbour.getYVelocity());
    //         } else {
    //             this.yVelocity = -1.0f;
    //             System.out.println("Reset Y velocity for " + this. x + " " + this.y);
    //         }

    //         neighbour.setVelocity(this.yVelocity);
    //         this.xVelocity *= friction;

    //         // if (diagonalNeighbour != null) {
    //             boolean stoppedDiagonally = iterateToAdditional(matrix, this.x + extraX, this.y + extraY, distance, lastIndex);
    //             if (!stoppedDiagonally) {
    //                 this.freeFalling = true;
                    
    //                 return true;
    //             }
    //         // }

    //         Element adjacentNeighbour = matrix.getParticle(this.x + extraX, this.y);
    //         if (/*adjacentNeighbour != null && */adjacentNeighbour != diagonalNeighbour) {
    //             boolean stoppedAdjacently = iterateToAdditional(matrix, this.x + extraX, this.y, distance, lastIndex);
    //             if (stoppedAdjacently) xVelocity *= -1;
    //             if (!stoppedAdjacently) {
    //                 this.freeFalling = false;
    //                 return true;
    //             }
    //         }

    //         this.freeFalling = false;

    //         this.updateGridPosition(lastIndex, matrix);
    //         return true;

    //     }
    //     return false;
    // }

    // private boolean iterateToAdditional(Matrix matrix, int startX, int startY, int distance, int lastIndex) {
    //     int distanceModifier = distance > 0 ? 1 : -1;

    //     for (int i = 0; i <= Math.abs(distance); i++) {
    //         int modifiedX = startX + i * distanceModifier;
    //         Element neighbour = matrix.getParticle(modifiedX, startY);
    //         // if (neighbour == null) {
    //         //     return true;
    //         // }
            
    //         boolean isFirst = i == 0;
    //         boolean isFinal = i == Math.abs(distance);
    //         if (neighbour == null) { // or particle
    //             if (isFinal) {
    //                 this.updateGridPosition(startY * matrix.width + modifiedX, matrix);
    //                 return false;
    //             }
    //             lastIndex = startY * matrix.width + modifiedX;
    //         } else if (neighbour instanceof Liquid) {
    //             Liquid liquidNeighbour = (Liquid) neighbour;
    //             if (isFinal) {
    //                 if (false) { // compare density
    //                     // swap
    //                     return false;
    //                 }
    //             }
    //         } else if (neighbour instanceof Solid) {
    //             if (isFirst) {
    //                 return true;
    //             }
    //             this.updateGridPosition(lastIndex, matrix);
    //             return false;
    //         }
    //     }
    //     return true;
    // }

    // public float getAverageVelOrGravity(float vel, float otherVel) {
    //     if (otherVel > -2f) {
    //         return -2f;
    //     }
    //     float avg = (vel + otherVel) / 2;
    //     if (avg > 0) {
    //         return avg;
    //     } else {
    //         return Math.min(avg, -2f);
    //     }
    // }

    // @Override
    // public void update(float delta, Matrix matrix) {
    //     System.out.println("XVelocity: (" + xVelocity + "), YVelocity: (" + yVelocity + ")");

    //     // Check if the liquid can move down
    //     int i = y * matrix.width + x;

    //     int belowIndex = (y - 1) * matrix.width + x;
    //     int rightIndex = y * matrix.width + (x + 1);
    //     int leftIndex = y * matrix.width + (x - 1);
    //     int belowLeftIndex = (y - 1) * matrix.width + (x - 1);
    //     int belowRightIndex = (y - 1) * matrix.width + (x + 1);

    //     boolean canMoveDown = (y - 1) >= 0;
    //     boolean canMoveLeft = (x - 1) >= 0;
    //     boolean canMoveRight = (x + 1) < matrix.width;

        
    //     if (yVelocity <= 0 && canMoveDown && matrix.isEmpty(belowIndex)) {
            
    //         int newX = calcNewX(delta);
    //         int newY = calcNewY(delta);
            
    //         int newIndex = matrix.nextEmptyBresenham(x, y, newX, newY);
    //         if (newIndex != newY*matrix.width + x) {
    //             int parIndex = matrix.nextFullBresenham(x, y, newX, newY);
    //             if (parIndex != newIndex) {

                    
    //                 this.yVelocity = matrix.getParticle(parIndex).getYVelocity(); // Transfer velocity from the particle we hit
    //                 if (this.yVelocity == 0) {
    //                     this.xVelocity = dispersaleRate * Math.signum(xVelocity);
    //                 }
    //             }
    //             //this.yVelocity = 0; // Reset velocity if we hit something
                
    //         } 
            
    //         this.updateGridPosition(newIndex, matrix);
    //         applyForces(delta);
    //         if (i == newIndex) {
    //             yVelocity *= 0.25;
    //             if (yVelocity < 0.01) {
    //                 yVelocity = 0;
    //             }
    //         }
    //         if (this.xVelocity != 0) {
    //             this.xVelocity *= liquidDamping; // Apply damping to the x velocity
    //             if (this.xVelocity < minimumXVelocity) {
    //                 this.xVelocity = 0;
    //             }
    //         }
            

    //     } else if (canMoveDown && canMoveLeft && canMoveRight && matrix.isEmpty(belowLeftIndex) && matrix.isEmpty(belowRightIndex)) {

    //         if (this.xVelocity < 0) {//x+y % 2 == 0) {
    //             this.updateGridPosition(belowLeftIndex, matrix);
    //         } else {
                
    //             this.updateGridPosition(belowRightIndex, matrix);
    //         }
            

    //     } else if (canMoveDown && canMoveLeft && matrix.isEmpty(belowLeftIndex) && yVelocity == 0) {
    //         // this.directionBias = -1;
    //         this.xVelocity = -Math.abs(xVelocity);
            
            
    //         this.updateGridPosition(belowLeftIndex, matrix);
    //     } else if (canMoveDown && canMoveRight && matrix.isEmpty(belowRightIndex) && yVelocity == 0) {
    //         // this.directionBias = 1;
    //         this.xVelocity = Math.abs(xVelocity);
            
    //         this.updateGridPosition(belowRightIndex, matrix);
        
    //     } else if (canMoveLeft && matrix.isEmpty(leftIndex) && canMoveRight && matrix.isEmpty(rightIndex)) {

    //         if (this.xVelocity < 0) {//x+y % 2 == 0) {
    //             int newX = calcNewX(delta);
    //             int newIndex = matrix.nextEmptyBresenham(x, y, newX, y);
    //             this.updateGridPosition(newIndex, matrix);
    //         } else {
    //             int newX = calcNewX(delta);
    //             int newIndex = matrix.nextEmptyBresenham(x, y, newX, y);
    //             this.updateGridPosition(newIndex, matrix);
    //         }
            
            

        
    //     } else if (canMoveLeft && matrix.isEmpty(leftIndex)) {
    //         this.xVelocity = -Math.abs(xVelocity);
    //         int newX = calcNewX(delta);
    //         int newIndex = matrix.nextEmptyBresenham(x, y, newX, y);
    //         this.updateGridPosition(newIndex, matrix);


    //     } else if(canMoveRight && matrix.isEmpty(rightIndex)) {
    //         this.xVelocity = +Math.abs(xVelocity);
    //         int newX = calcNewX(delta);

    //         int newIndex = matrix.nextEmptyBresenham(x, y, newX, y);
    //         this.updateGridPosition(newIndex, matrix);

    //     } else {
    //         if (xVelocity < minimumXVelocity) {
    //         } 
    //         this.xVelocity = dispersaleRate * (Math.signum(xVelocity)*-1);

            
    //         if (canMoveDown && !matrix.isEmpty(belowIndex)) {
    //             this.yVelocity = matrix.getParticle(belowIndex).getYVelocity();
    //         } else if (!canMoveDown) {
    //             this.yVelocity = 0; // Bottom of screen
    //         }
    //         matrix.updateMatrix(i, this); // Stay in place if no movement is possible
    //     }

        
    // }
    
}

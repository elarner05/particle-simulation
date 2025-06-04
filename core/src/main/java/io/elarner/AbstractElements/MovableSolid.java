package io.elarner.AbstractElements;

import com.badlogic.gdx.graphics.Color;

import io.elarner.Simulator.Matrix;

public abstract class MovableSolid extends Solid {

    public MovableSolid(Color color, int x, int y) {
        super(color, x, y);
        this.yVelocity = -0.1f; // Set initial velocity to 0.1f downwards; this prevents the hilling effect when the solid is created, and lets the particle freefall
    }

    @Override 
    public void update(float delta, Matrix matrix) {
        
        int i = y * matrix.width + x;

        int belowIndex = (y - 1) * matrix.width + x;
        int belowLeftIndex = (y - 1) * matrix.width + (x - 1);
        int belowRightIndex = (y - 1) * matrix.width + (x + 1);

        boolean canMoveDown = (y - 1) >= 0;
        boolean canMoveLeft = (x - 1) >= 0;
        boolean canMoveRight = (x + 1) < matrix.width;

        if (yVelocity <= 0 && canMoveDown && matrix.lessPriority(belowIndex, this.getPriority())) {
            // System.out.println("Y: " + y + " YVelocity: " + yVelocity + " Delta: " + delta + " Change: " + yVelocity*delta);
            this.accuYChange += yVelocity * delta;
            
            int newY = y + (int) this.accuYChange;
            if (Math.abs(this.accuYChange) >= 1) {
                this.accuYChange -= (int) this.accuYChange;
            }
            
            int newIndex = matrix.nextEmptyBresenham(x, y, x, newY, this.getPriority());
            if (newIndex != newY*matrix.width + x) {
                int parIndex = matrix.nextFullBresenham(x, y, x, newY, this.getPriority());
                if (parIndex != newIndex) {

                    this.yVelocity = matrix.getParticle(parIndex).getYVelocity(); // Transfer velocity from the particle we hit
                }
            }

            this.updateGridPosition(newIndex, matrix);
            applyForces(delta);

        } else if (canMoveDown && canMoveLeft && canMoveRight && matrix.lessPriority(belowLeftIndex, this.getPriority()) && matrix.lessPriority(belowRightIndex, this.getPriority()) && yVelocity == 0) {
            
            if (random.nextBoolean()) {//x+y % 2 == 0) {
                this.updateGridPosition(belowLeftIndex, matrix);
            } else {
                
                this.updateGridPosition(belowRightIndex, matrix);
            }
        } else if (canMoveDown && canMoveLeft && matrix.lessPriority(belowLeftIndex, this.getPriority()) && yVelocity == 0) {
            
            this.updateGridPosition(belowLeftIndex, matrix);
        } else if (canMoveDown && canMoveRight && matrix.lessPriority(belowRightIndex, this.getPriority()) && yVelocity == 0) {
            
            this.updateGridPosition(belowRightIndex, matrix);
        } else {
            if (canMoveDown && !matrix.lessPriority(belowIndex, this.getPriority())) {
                this.yVelocity = matrix.getParticle(belowIndex).getYVelocity();
            } else if (!canMoveDown) {
                this.yVelocity = 0; // Bottom of screen

            }
            matrix.updateMatrix(i, this); // Stay in place if no movement is possible
        }
    }
}

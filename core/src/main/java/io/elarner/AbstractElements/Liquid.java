package io.elarner.AbstractElements;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;

import io.elarner.Simulator.PixelGrid;

public abstract class Liquid extends Element{

    protected int dispersaleRate = 20; // Rate at which the liquid disperses

    public Liquid(Color color, int x, int y) {
        super(color, x, y);
    }

    @Override
    public void update(float delta, PixelGrid matrix) {
        // Check if the liquid can move down
        int i = y * matrix.width + x;

        int belowIndex = (y - 1) * matrix.width + x;
        int rightIndex = y * matrix.width + (x + 1);
        int leftIndex = y * matrix.width + (x - 1);
        int belowLeftIndex = (y - 1) * matrix.width + (x - 1);
        int belowRightIndex = (y - 1) * matrix.width + (x + 1);

        boolean canMoveDown = (y - 1) >= 0;
        boolean canMoveLeft = (x - 1) >= 0;
        boolean canMoveRight = (x + 1) < matrix.width;
        boolean canMoveUp = (y + 1) < matrix.height;

        if (yVelocity <= 0 && canMoveDown && matrix.isEmpty(belowIndex)) {
            // System.out.println("Y: " + y + " YVelocity: " + yVelocity + " Delta: " + delta + " Change: " + yVelocity*delta);
            this.accuYChange += yVelocity * delta;
            
            int newY = y + (int) this.accuYChange;
            if (Math.abs(this.accuYChange) >= 1) {
                this.accuYChange -= (int) this.accuYChange;
            }
            
            int newIndex = matrix.nextEmptyBresenham(x, y, x, newY);
            if (newIndex != newY*matrix.width + x) {
                int parIndex = matrix.nextFullBresenham(x, y, x, newY);
                if (parIndex != newIndex) {

                    this.yVelocity = matrix.getParticle(parIndex).getYVelocity(); // Transfer velocity from the particle we hit
                }
                //this.yVelocity = 0; // Reset velocity if we hit something
            } 
            // System.out.println("Current: (" + x + ", " + y + "), Target: (" + x + ", " + newY + "), Current Index: (" + i + "), New Index: " + newIndex);
            this.updateBufferPosition(newIndex, matrix);
            applyForces(delta);

        } else if (yVelocity>0 && canMoveUp) {
            this.accuYChange += yVelocity * delta;
            int newY = y + (int) this.accuYChange;
            if (Math.abs(this.accuYChange) >= 1) {
                this.accuYChange -= (int) this.accuYChange;
            }

            int newIndex = matrix.nextEmptyBresenham(x, y, x, newY);
            if (newIndex != newY*matrix.width + x) {
                int parIndex = matrix.nextFullBresenham(x, y, x, newY);
                if (parIndex != newIndex) {

                    this.yVelocity = matrix.getParticle(parIndex).getYVelocity(); // Transfer velocity from the particle we hit
                }
                //this.yVelocity = 0; // Reset velocity if we hit something
            } 
            // System.out.println("Current: (" + x + ", " + y + "), Target: (" + x + ", " + newY + "), Current Index: (" + i + "), New Index: " + newIndex);
            this.updateBufferPosition(newIndex, matrix);
            applyForces(delta);

        } else if (canMoveDown && canMoveLeft && canMoveRight && matrix.isEmpty(belowLeftIndex) && matrix.isEmpty(belowRightIndex)) {

            if (random.nextBoolean()) {//x+y % 2 == 0) {
                this.updateBufferPosition(belowLeftIndex, matrix);
            } else {
                
                this.updateBufferPosition(belowRightIndex, matrix);
            }
        } else if (canMoveDown && canMoveLeft && matrix.isEmpty(belowLeftIndex)) {
            
            this.updateBufferPosition(belowLeftIndex, matrix);
        } else if (canMoveDown && canMoveRight && matrix.isEmpty(belowRightIndex)) {
            
            this.updateBufferPosition(belowRightIndex, matrix);

        } else if(canMoveLeft && canMoveRight && matrix.isEmpty(leftIndex) && matrix.isEmpty(rightIndex)) {
            if (random.nextBoolean()) {// left
                int newX = x - this.dispersaleRate;
                int newIndex = matrix.nextEmptyBresenham(x, y, newX, y);
                this.updateBufferPosition(newIndex, matrix);
            } else {
                int newX = x + this.dispersaleRate;
                int newIndex = matrix.nextEmptyBresenham(x, y, newX, y);
                this.updateBufferPosition(newIndex, matrix);
            }
        } else if(canMoveLeft && matrix.isEmpty(leftIndex)) {
            
            int newX = x - this.dispersaleRate;
            int newIndex = matrix.nextEmptyBresenham(x, y, newX, y);
            this.updateBufferPosition(newIndex, matrix);
        } else if(canMoveRight && matrix.isEmpty(rightIndex)) {
        
            int newX = x + this.dispersaleRate;
            int newIndex = matrix.nextEmptyBresenham(x, y, newX, y);
            this.updateBufferPosition(newIndex, matrix);
        
        } else {
            this.yVelocity = 0; // Reset velocity if we hit something
            matrix.updateBuffer(i, this); // Stay in place if no movement is possible
        }
    }
    
}

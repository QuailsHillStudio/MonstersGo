package com.sar2016.panczuk.monstersgo;

/**
 * Created by olivier on 25/01/17.
 */

public class SwipeMove {
    private int chosenDirection = 0;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;

    public SwipeMove(){
        chosenDirection = UP + (int)(Math.random() * (RIGHT - UP));
    }

    public int getChosenDirection(){
        return this.chosenDirection;
    }

    public boolean swipeUp(){
        if(this.chosenDirection == UP) return true;

        return false;
    }

    public boolean swipeDown(){
        if(this.chosenDirection == DOWN) return true;

        return false;
    }

    public boolean swipeLeft(){
        if(this.chosenDirection == LEFT) return true;

        return false;
    }

    public boolean swipeRight(){
        if(this.chosenDirection == RIGHT) return true;

        return false;
    }

    public int getDrawableId() {
        if(chosenDirection == UP)return R.drawable.up;
        if(chosenDirection == DOWN) return R.drawable.down;
        if(chosenDirection == LEFT) return R.drawable.left;
        if(chosenDirection == RIGHT) return R.drawable.right;
        return 0;
    }
}

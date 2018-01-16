package edu.pennstate.science_olympiad.util;


/**
 * This is an object that is of Object type L and R. This is similar to a key Value pair but is easier to access the key
 * @param <L> The Left side of the pair
 * @param <R> The right side of the pair
 */
public class Pair<L,R> {

    private L left;
    private R right;

    /**
     * Creates a new pair
     * @param left The left side value for this pair
     * @param right The right side value for this pair
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    //Getters for left and right
    public L getLeft() { return left; }
    public R getRight() { return right; }


    @Override
    public String toString() {
        return left + " " + right;
    }

}



package de.fhkiel.ki.cathedral;

import de.fhkiel.ki.cathedral.game.Direction;

public class MatchResult {

    private int[] catPosition = new int[2];
    private Direction rotation = Direction._0;
    private int scoreDifference;

    public MatchResult(int[] catPosition, int scoreDifference, Direction rotation) {
        this.catPosition = catPosition;
        this.rotation = rotation;
        this.scoreDifference = scoreDifference;
    }

    /**
     * @return the fieldId
     */
    public int[] getCatPosition() {
        return catPosition;
    }

    /**
     * @param catPosition the fieldId to set
     */
    public void setFieldId(int x, int y) {
        this.catPosition[0] = x;
        this.catPosition[1] = y;
    }

/**
 * @return the fieldId
 */
public Direction getRotation() {
    return rotation;
}

/**
 * @param catPosition the fieldId to set
 */
public void setRotation(Direction rotation) {
    this.rotation = rotation;
}

    /**
     * @param value the value to set
     */
    /**
     * @return the value
     *         higher score is better for white
     */
    public int getScoreDifference() {
        return scoreDifference;
    }

    /**
     * @param value the value to set
     */
    public void setScoreDifference(int blackScore, int whiteScore) {
        this.scoreDifference = blackScore - whiteScore;
    }

}

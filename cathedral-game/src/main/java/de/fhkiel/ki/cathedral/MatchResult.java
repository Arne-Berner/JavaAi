package de.fhkiel.ki.cathedral;

public class MatchResult {

    private int[] catPosition = new int[2];
    private int scoreDifference;

    public MatchResult(int[] catPosition, int scoreDifference) {
        this.catPosition = catPosition;
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

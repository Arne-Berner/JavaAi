package de.fhkiel.ki.cathedral;

public class MatchResult {

        private int[] fieldId = new int[2];
        private int numberOfWins;
        private int numberOfGames;
        private int scoreDifference;

        /**
         * @return the fieldId
         */
        public int[] getFieldId() {
            return fieldId;
        }

        /**
         * @param fieldId the fieldId to set
         */
        public void setFieldId(int x, int y) {
            this.fieldId[0] = x;
            this.fieldId[1] = y;
        }

        /**
         * @return the value
         */
        public int getNumberOfWins() {
            return numberOfWins;
        }

        /**
         * @param value the value to set
         */
        public void setNumberOfWins(int numberOfWins) {
            this.numberOfWins = numberOfWins;
        }

        /**
         * @return the value
         */
        public int getNumberOfGames() {
            return numberOfGames;
        }

        /**
         * @param value the value to set
         */
        public void setNumberOfGames(int numberOfGames) {
            this.numberOfGames = numberOfGames;
        }

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

}

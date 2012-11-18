package uk.co.optimisticpanda.db.apply;
public enum DelimiterType {
    /**
     * Delimiter is interpreted whenever it appears at the end of a line
     */
    normal {
        @Override
		public boolean matches(String line, String delimiter) {
            return line.endsWith(delimiter);
        }
    },

    /**
     * Delimiter must be on a line all to itself
     */
    row {
        @Override
		public boolean matches(String line, String delimiter) {
            return line.equals(delimiter);
        }
    };

    public abstract boolean matches(String line, String delimiter);
}

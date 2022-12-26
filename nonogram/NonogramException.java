package nonogram;

/**
 * A problem-specific run time exception for a Nonogram puzzle.
 *
 */
@SuppressWarnings("serial")
public class NonogramException extends RuntimeException {
	/**
	 * Constructor with explanatory message
	 * 
	 * @param msg the explanatory message
	 */
	public NonogramException(String msg) {
		super(msg);
	}
}
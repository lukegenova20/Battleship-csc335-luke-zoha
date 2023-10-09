package utilities;


/**
 * This a custom made exception class which is a subclass of
 * the Exception class. This exception class is used to check
 * for invalid move or ship placement onto a grid.
 * 
 * @author Luke Genova
 * @author Amimul Ehsan Zoha
 *
 */
public class IllegalPlacementException extends Exception {
	
	/**
	 * IllegalGuessException constructor
	 * 
	 * @param message A string that represents the message that caused the exception.
	 */
	public IllegalPlacementException(String message) {
		super(message);
	}
	
	/**
	 * This method is a getter method that returns the message that
	 * comes with the exception.
	 *
	 */
	public String getMessage() {
		return super.getMessage();
	}
}

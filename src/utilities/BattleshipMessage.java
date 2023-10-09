package utilities;

import java.awt.Point;
import java.io.Serializable;

/**
 * This class is used by the both instances of the view as a message that 
 * notifies one player that the other has changed or is trying to make an 
 * action. When the object is created, it sets all fields to null or empty, but 
 * one of these fields change depending on the type of message. The type of message 
 * can change depending on what an instance of this class holds. 
 * 
 * The object can either hold:
 * 
 * 1. An updated grid from one player that resulted from a move. 
 * 
 * 2. A pointer object that represents a move made by a player that contains 
 * the position on a grid as to where the move was made.
 * 
 * 3. A boolean value and a string that determines if there was an exception that
 * occurred or invalid action occurred. A message will most likely have a boolean when 
 * one of the player's fleet has been destroyed. 
 * 
 * 
 * 
 * @author Luke Genova
 * @author Amimul Ehsan Zoha
 *
 */
public class BattleshipMessage implements Serializable {

	
	private POSITION_RESULT[][] updatedGrid;
	
	private POSITION_RESULT[][] answerGrid;
	
	private Point move;
	
	private boolean exceptionFound;
	
	private String exceptionMessage;
	
	/**
	 * BattleshipMessage constructor. 
	 * 
	 */
	public BattleshipMessage() {
		updatedGrid = null;
		answerGrid = null;
		move = null;
		exceptionFound = false;
		exceptionMessage = "";
	}
	
	/**
	 * This method is a setter that makes the message a notifier that
	 * the player has updated their grid after the other player made a move.
	 * 
	 * @param grid the other player's updated grid.
	 */
	public void setUpdatedGrid(POSITION_RESULT[][] grid) {
		this.updatedGrid = grid;
	}
	
	/**
	 * This method is a getter that returns the an updated grid of one of the
	 * players.
	 * 
	 * @return the other player's updated grid.
	 */
	public POSITION_RESULT[][] getUpdateGrid(){
		return this.updatedGrid;
	}
	/**
	 * This method is a setter that makes the message a notifier that
	 * the player is giving the other player their ship locations since the 
	 * other player has lost.
	 * 
	 * @param grid the other player's updated grid.
	 */
	public void setAnswerGrid(POSITION_RESULT[][] grid) {
		this.answerGrid = grid;
	}
	
	/**
	 * This method is a getter that returns the original grid of one of the
	 * players.
	 * 
	 * @return the other player's updated grid.
	 */
	public POSITION_RESULT[][] getAnswerGrid(){
		return this.answerGrid;
	}
	
	/**
	 * This method is a setter that makes that makes a message a
	 * notifier that a player had made a move.
	 * 
	 * @param move A point object that represents a move. 
	 */
	public void setMove(Point move) {
		this.move = move;
	}
	
	/**
	 * This method is a getter that returns the move that one of the 
	 * players made.
	 * 
	 * @return A point object that represents the move. 
	 */
	public Point getMove() {
		return this.move;
	}
	
	/**
	 * This method is a setter that makes the message a warning to 
	 * another player
	 * 
	 * @param message A string that states the reason for the invalid 
	 * action.
	 */
	public void setException(String message) {
		exceptionFound = true;
		exceptionMessage = message;
	}
	
	/**
	 * This method is a getter that return the reason for the exception 
	 * represented as a string.
	 * 
	 * @return The reason for the exception or invalid action.
	 */
	public String getMessage() {
		return this.exceptionMessage;
	}
	
	/**
	 * This method is a getter that determines if the message is a warning to a
	 * player.
	 * 
	 * @return A boolean value that determined if a player made an illegal move.
	 */
	public boolean exceptionFound() {
		return this.exceptionFound;
	}
	
}

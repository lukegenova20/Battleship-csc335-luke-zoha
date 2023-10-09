package utilities;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import controller.BattleshipController;
import players.Battleship;

/**
 * This class is used by both players to constantly be ready to receive 
 * a message and do the necessary actions after reading in a message object.
 * This class also determines which kind of message the other player gave based 
 * on what fields of the message object aren't null. This class is also seen as 
 * a thread that constantly runs while the view is running and it stops once the 
 * Battleship game is over.  
 * 
 * 
 * @author Luke Genova
 * @author Amimul Ehsan Zoha
 *
 */
public class BattleshipNetwork implements Runnable {

	private Socket connection;
	
	private BattleshipController controller;
	
	private ObjectOutputStream output;
	
	
	/**
	 * BattleshipNetwork constructor.
	 * 
	 * @param connection An object that represents the established connection between the server and the 
	 * client. 
	 * @param controller An object used by the view to pass data through which then determines if the 
	 * data is valid.
	 * @param output An object that is used to transfer data from one process to another. 
	 */
	public BattleshipNetwork(Socket connection, BattleshipController controller, ObjectOutputStream output) {
		this.connection = connection;
		this.controller = controller;
		this.output = output;
	}
	
	/**
	 * This method contains a loop that constantly checks for input from the 
	 * other player. Once a message is acquired, it then checks what kind of message
	 * it was given and do the necessary actions needed to continue the game. This method
	 * is called by a thread object created in the view.  
	 *
	 */
	@Override
	public void run() {
		
		try {
			ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
			
			BattleshipMessage message;
			do {
				message = (BattleshipMessage) input.readObject();
				
				isExceptionMessage(message);
				
				isMoveMessage(message);
				
				isUpdateGridMessage(message);
				
			} while (message.getAnswerGrid() == null);
			
			endGame(message);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This private method determines if the message received is meant 
	 * to notify the current player that an exception occurred with the other
	 * player. The message also checks if the other player has no more ships 
	 * left, meaning that the game has ended.
	 * 
	 * @param message An object that represents the message that was received.
	 * 
	 */
	private void isExceptionMessage(BattleshipMessage message) {
		
		try {
			// Checks if the message was to notify the other player that the current player made an
			// invalid move.
			if (message.exceptionFound()) {
				
				// Alert player that they have won the game. 
				if (message.getMessage().equals("Game over")){
			
					// Give the losing player the locations of all the current player's ship.
					BattleshipMessage newMessage = new BattleshipMessage();
					newMessage.setAnswerGrid(controller.getPlayerAnswerGrid());
					output.writeObject(newMessage);
				} 
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * This private method determines if the message received contains a move 
	 * that the other player just made. It then calls the controller to register
	 * the move and give it to the model. Handle the case where the other player
	 * made a invalid move.
	 * 
	 * 
	 * @param message An object that represents the message that was received.
	 */
	private void isMoveMessage(BattleshipMessage message) {
		// If the message from the other player was a move, it makes calls the controller.
		if (message.getMove() != null) {
			Point coord = message.getMove();
			controller.moveResult(coord);
		}
	}
	
	
	/**
	 * This private method checks if the current message is meant to 
	 * have the other player update their grid after they made a move.
	 * 
	 * @param message An object that represents the message that was received.
	 */
	private void isUpdateGridMessage(BattleshipMessage message) {
		
		// When the current player made a move, the player gets an updated grid.
		if (message.getUpdateGrid() != null) {
			POSITION_RESULT[][] updatedGrid = message.getUpdateGrid();
			controller.updateOpponentGrid(updatedGrid, false);
			
		}
	}
	
	/**
	 * This private method determines what happens after the game ends. 
	 * If the current player lost their fleet, then they would get alert 
	 * that they lost the game and would get the locations of the opponents 
	 * grid. If the current player won, then they would close the connection
	 * between the two players.
	 * 
	 * @param message An object that represents the message that was received.
	 * 
	 */
	private void endGame(BattleshipMessage message) {
		controller.updateOpponentGrid(message.getAnswerGrid(), true);
		try {
			if (controller.isGameOver()) {
				BattleshipMessage newMessage = new BattleshipMessage();
				newMessage.setAnswerGrid(controller.getPlayerAnswerGrid());
				output.writeObject(newMessage);
				
			} else {
				connection.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
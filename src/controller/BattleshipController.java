package controller;

import java.awt.Point;
import utilities.POSITION_RESULT;
import utilities.Constants;

import model.BattleshipModel;
import utilities.IllegalPlacementException;

/**
 * This class represents the controller component of the Battleship game
 * that use the MVC architecture. The controller takes in user input 
 * from the view and determines if the user placed the ship correctly. 
 * Once input is validated, it gives the input to the model. This class 
 * also determines when the game is over. Basically, the controller handles all of 
 * the game logistics.
 * 
 * 
 * @author Luke Genova
 * @author Amimul Ehsan Zoha
 *
 */
public class BattleshipController {
	
	private BattleshipModel model;
	
	/**
	 * BattleshipController constructor. 
	 * 
	 * @param model A model object that represents the model component of the MVC.
	 */
	public BattleshipController(BattleshipModel model) {
		this.model = model;
	}
	
	
	/**
	 * This method is used to determine if the game is over. Checks how many
	 * hits the current player has. If the current player's fleet withstood the 
	 * max number of hits they can, then the game is over and notifies the other player.
	 * 
	 * @return A boolean whether the game has ended or not.
	 */
	public boolean isGameOver() {
		int totalHits = model.getTotalHits();
		if (totalHits == Constants.MAX_HITS) {
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a grid that contains the locations of all the attacks 
	 * the current player had made to the opposing player's grid.
	 * 
	 * @return a grid that contains the locations of all the attacks 
	 * the current player had made to the opposing player's grid.
	 * 
	 */
	public POSITION_RESULT[][] getPlayerAnswerGrid(){
		return model.getPlayerAnswerGrid();
	}
	
	/**
	 * This method takes in an updated grid from the opposing player that 
	 * resulted from the current player's move and updates its current information 
	 * of the opponent's grid. 
	 * 
	 * @param updatedGrid A grid of constants that represents the opponent's updated grid 
	 * after the current player made a move.
	 * @param gameEnded A boolean value that determines if the view can show the opponents 
	 * ship locations after the game ends.
	 * 
	 */
	public void updateOpponentGrid(POSITION_RESULT[][] updatedGrid, boolean gameEnded) {
		model.updateOpponentGrid(updatedGrid, gameEnded);
	}
	
	/**
	 * This method checks for any potential exceptions that can occur when the 
	 * user places a ship, then passes the necessary values to the model.
	 * 
	 * @param coord A Point object that holds the x and y position on the ship
	 * will be placed.
	 * @param size An integer that holds the size of the ship.
	 * @param dir An integer that determines which direction the user is going
	 * to place their ship. 
	 * @throws IllegalPlacementException An exception that occurs when the user 
	 * tried to place something in a position that already had something or when 
	 * a move made is not allowed.
	 */
	public void addShip(Point coord, int size, int dir) throws IllegalPlacementException {
		if (checkShipPlacement(coord, size, dir)) {
			throw new IllegalPlacementException("Ship placement goes over the grid.");
		} else if (filledSpaceFound(coord, size, dir)){
			throw new IllegalPlacementException("A ship is already placed in the position you are "
					+ "trying to place the current ship.");
		} else {
			model.addShip(coord, size, dir);
		}
	}
	
	/**
	 * This method passes the move the other player made to the model to
	 * update the data within the model.
	 * 
	 * @param coord A Point object that holds the x and y position on the ship
	 * will be placed.
	 */
	public void moveResult(Point coord) {
		model.moveResult(coord);
	}
	
	/**
	 * This private method checks if the current player already placed 
	 * a ship in any of the positions where the next ship will be.
	 * 
	 * @param coord A Point object that holds the x and y position on the ship
	 * will be placed.
	 * @param size An integer that holds the size of the ship.
	 * @param dir An integer that determines which direction the user is going
	 * to place their ship. 
	 * @return A boolean value (either true or false) that determines whether the current player
	 * already placed a ship in any of the positions where the next ship will be. 
	 */
	private boolean filledSpaceFound(Point coord, int size, int dir) {
		int xPos = (int) coord.getX();
		int yPos = (int) coord.getY();
		POSITION_RESULT[][] grid = model.getPlayerProgressGrid();
		
		// Iterates over certain positions to see if there is already a ship there.
		for (int i = 0; i < size; i++) {
			if (dir == Constants.HORIZONTAL) {
				if (grid[yPos][xPos+i] != null) {
					String position = grid[yPos][xPos+i].getDescription();
					if (position.equals("A Ship is here")) {
						return true;
					}
				}
			} else {
				if (grid[yPos+i][xPos] != null) {
					String position = grid[yPos+i][xPos].getDescription();
					if (position.equals("A Ship is here")) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * This private method checks if the current player's ship placement goes
	 * out of bounds.
	 * 
	 * @param coord A Point object that holds the x and y position on the ship
	 * will be placed.
	 * @param size An integer that holds the size of the ship.
	 * @param dir An integer that determines which direction the user is going
	 * to place their ship. 
	 * @return A boolean value (either true or false) that determines whether the 
	 * current player's ship placement goes out of bound.
	 */
	private boolean checkShipPlacement(Point coord, int size, int dir) {
		int xPos = (int) coord.getX();
		int yPos = (int) coord.getY();
		
		int max_val = 0;
		if (dir == Constants.VERTICAL) {
			max_val = yPos + size-1;
		} else {
			max_val = xPos + size-1;
		}
		
		if (max_val >= Constants.GRID_SIZE) {
			return true;
		}
		return false;
	}

	
}

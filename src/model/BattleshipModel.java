package model;


import utilities.Constants;
import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

import utilities.POSITION_RESULT;

/**
 * This class represents the model component of the Battleship game which
 * uses the MVC architecture. Each player will have their own instance of 
 * the model that stores the data of each player. That data being the player's 
 * current grid and what ships the player placed and which spot was a hit or a 
 * miss. This model also stores a grid that has what information the current player
 * has from the opposing player's grid. Finally, the model also stores an answer the 
 * it shows to the other player once the game is over. The model follows the 
 * Observer/Observable design in that the model is the Observable and notifies the 
 * observer(the view) that it has updated.
 * 
 * 
 * @author Luke Genova
 * @author Amimul Ehsan Zoha
 *
 */
public class BattleshipModel extends Observable {
	
	// Grid that will be used during the game.
	private POSITION_RESULT[][] playerProgressGrid;
	
	// Grid that will be used after the game.
	private POSITION_RESULT[][] playerAnswerGrid;
	
	// Grid that shows the position
	private POSITION_RESULT[][] opponentGrid;
	
	private int totalHits;
	
	/**
	 * BattleshipModel constructor.
	 */
	public BattleshipModel() {
		playerProgressGrid = new POSITION_RESULT[Constants.GRID_SIZE][Constants.GRID_SIZE];
		playerAnswerGrid = new POSITION_RESULT[Constants.GRID_SIZE][Constants.GRID_SIZE];
		opponentGrid = new POSITION_RESULT[Constants.GRID_SIZE][Constants.GRID_SIZE];
		totalHits = 0;
		
	}
	
	/**
	 * Adds an observer to the set of observers for this object, 
	 * provided that it is not the same as some observer already in 
	 * the set.
	 * 
	 * @param o an observer to be added.
	 */
	public void addObserver(Observer o) {
		super.addObserver(o);
	}
	
	/**
	 * This method adds a ship to the grid by use the position of the front of the ship
	 * and iterating through the grid and changing what is inside a position based on
	 * the size and the direction of the ship. Notifies the view when the model added 
	 * the ships.
	 * 
	 * @param coord A Point object that holds the x and y position on the ship
	 * will be placed.
	 * @param size An integer that holds the size of the ship.
	 * @param dir An integer that determines which direction the user is going
	 * to place their ship. 
	 */
	public void addShip(Point coord, int size, int dir) {
		int xPos = (int) coord.getX();
		int yPos = (int) coord.getY();
		
		for (int i = 0; i < size; i++) {
			if (dir == Constants.HORIZONTAL) {
				playerProgressGrid[yPos][xPos+i] = POSITION_RESULT.SHIP;
				playerAnswerGrid[yPos][xPos+i] = POSITION_RESULT.SHIP;
			} else {
				playerProgressGrid[yPos+i][xPos] = POSITION_RESULT.SHIP;
				playerAnswerGrid[yPos+i][xPos] = POSITION_RESULT.SHIP;
			}
		}
		
		super.setChanged();
		super.notifyObservers("Ship Added.");
		super.clearChanged();
		return;
		
		
	}
	
	/**
	 * This method takes in a grid that holds the position on where the other 
	 * player attacked and determines if the attack hit a ship or missed. 
	 * 
	 * @param coord A Point object that holds the x and y position on the ship
	 * will be placed.
	 */
	public void moveResult(Point coord) {
		int xPos = (int) coord.getX();
		int yPos = (int) coord.getY();
		
		if (playerProgressGrid[yPos][xPos] == null) {
			playerProgressGrid[yPos][xPos] = POSITION_RESULT.MISS;
		} else {
			playerProgressGrid[yPos][xPos] = POSITION_RESULT.HIT;
			totalHits++;
		}
		
		super.setChanged();
		super.notifyObservers("Move Made.");
		super.clearChanged();
		return;
		
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
	 */
	public void updateOpponentGrid(POSITION_RESULT[][] updatedGrid, boolean gameEnded) {
		for (int i = 0; i < Constants.GRID_SIZE; i++) {
			for (int j = 0; j < Constants.GRID_SIZE; j++) {
				
				// If the position isn't null, then it could contain a ship, a hit, or a miss.
				if (updatedGrid[i][j] != null) {
					String position = updatedGrid[i][j].getDescription();
					
					// This method is given only the ship locations when the game is over.
					// Checks if the game is over, if so then it places ships in the opponent grid,
					if (gameEnded) {
						if (position.equals("A Ship is here"))
						this.opponentGrid[i][j] = POSITION_RESULT.SHIP;
					} else {
						if (position.equals("A Ship has been hit")) {
							this.opponentGrid[i][j] = POSITION_RESULT.HIT;
						}
						if (position.equals("A missile missed")) {
							this.opponentGrid[i][j] = POSITION_RESULT.MISS;
						}
					}
				}
			}
		}
		super.setChanged();
		super.notifyObservers("Opponent Grid Updated.");
		super.clearChanged();
		return;
	}
	
	/**
	 * Returns the current player's grid. 
	 * 
	 * @return the current player's grid.
	 */
	public POSITION_RESULT[][] getPlayerProgressGrid(){
		return this.playerProgressGrid;
	}
	
	/**
	 * Returns a grid that contains the locations of all the ships
	 * before there was any attacks. Used at the end to show where the 
	 * opponents ships were.
	 * 
	 * @return a grid that contains the locations of all the ships.
	 */
	public POSITION_RESULT[][] getPlayerAnswerGrid(){
		return this.playerAnswerGrid;
	}
	
	/**
	 * Returns a grid that contains the locations of all the attacks 
	 * the current player had made to the opposing player's grid.
	 * 
	 * @return a grid that contains the locations of all the attacks 
	 * the current player had made to the opposing player's grid.
	 */
	public POSITION_RESULT[][] getOpponentGrid(){
		return this.opponentGrid;
	}
	
	/**
	 * Returns the total amount of hits the current user's fleet has taken.
	 * 
	 * @return the total hits the current player's fleet has taken.
	 */
	public int getTotalHits() {
		return this.totalHits;
	}
	
}

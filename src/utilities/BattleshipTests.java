package utilities;



import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import controller.BattleshipController;
import model.BattleshipModel;
import utilities.POSITION_RESULT;
import java.awt.Point;


/**
 * This class tests the backend of the Battleship game we made, except for the 
 * GUI and code related to GUI. The controller and model of MVC architecture, 
 * exception handling and edge cases have been thoroughly checked and a proper whole game
 * has been simulated to test the processes and logic throughout the whole game.
 * A part of the project, the GUI part have not been tested since it was not required.
 * We achieved 100 percent coverage on controller and nearly 100 percent on the model
 * 
 * @author Luke Genova
 * @author Amimul Ehsan Zoha
 * 
 */

class BattleshipTests {
	
	/**
	 *  This method tests the adding/ placing of a ship and tests if the ship placed
	 *  rightly changes the progress grid that holds the players' progress
	 */

	@Test
	void addShipTest() {
		BattleshipModel model = new BattleshipModel();
		BattleshipController controller = new BattleshipController(model);
		try {
			controller.addShip(new Point(0,0), 5, 0);
		} catch (IllegalPlacementException e) {
		}
		POSITION_RESULT[][] playerProgressGrid = model.getPlayerProgressGrid();
		// checks if the ship is placed in the grid correctly accounting for ship
		// size as well.
		for(int i = 0; i <5; i++) {
			assertEquals(POSITION_RESULT.SHIP, playerProgressGrid[i][0]);
		}
		// checks a nearby slot where ship is not placed yet to confirm
		assertEquals(null, playerProgressGrid[5][0]);
	}
	
	/**
	 * This method tests the exceptions thrown by some exceptional moves by the user 
	 * which is handled in our battleship program. assertThrows and lambda expressions 
	 * have been used to test this.
	 * @throws IllegalPlacementException when a user places a ship at the wrong place 
	 * or makes an illegal move
	 */
	@Test
	void exceptionTest() throws IllegalPlacementException  {
		BattleshipModel model = new BattleshipModel();
		BattleshipController controller = new BattleshipController(model);
		controller.addShip(new Point(0,0), 5, 0);
		assertThrows(IllegalPlacementException.class, ()-> controller.addShip(new Point(0,0), 5, 0));
		assertThrows(IllegalPlacementException.class, ()-> controller.addShip(new Point(9,9), 5, 1));
		assertThrows(IllegalPlacementException.class, ()-> controller.addShip(new Point(9,9), 4, 1));	
	}
	
	/**
	 *  This test tests the special case when a ship is placed vertically but then 
	 *  another ship placed horizontally overlaps and therefore throws an  exception
	 * @throws IllegalPlacementException when a user places a ship at the wrong place 
	 * or makes an illegal move.
	 */
	@Test
	void filledSpacesFoundTest() throws IllegalPlacementException {
		BattleshipModel model = new BattleshipModel();
		BattleshipController controller = new BattleshipController(model);
		controller.addShip(new Point(4,1), 5, 0);
		assertThrows(IllegalPlacementException.class, ()-> controller.addShip(new Point(3,2), 4, 1));
	}
	
	
	/**
	 *  This test tests the special case when a ship is placed horizontally but then 
	 *  another ship placed vertically overlaps and therefore throws an  exception
	 *  @throws IllegalPlacementException when a user places a ship at the wrong place 
	 *  or makes an illegal move.
	 */
	@Test 
	void filledSpacesTest() throws IllegalPlacementException {
		BattleshipModel model = new BattleshipModel();
		BattleshipController controller = new BattleshipController(model);
		controller.addShip(new Point(4,1), 5, 1);
		assertThrows(IllegalPlacementException.class, ()-> controller.addShip(new Point(4,0), 4, 0));
	
		
	}
	
	/**
	 * This method tests the case when a move causes to hit a ship. We 
	 * further test if our player progress grid updates correctly
	 * @throws IllegalPlacementException when a user places a ship at the wrong place 
	 * or makes an illegal move.
	 */
	@Test
	void shipsHitTest() throws IllegalPlacementException {
		BattleshipModel model1 = new BattleshipModel();
		BattleshipController controller1 = new BattleshipController(model1);
		BattleshipModel model2 = new BattleshipModel();
		BattleshipController controller2 = new BattleshipController(model2);
		controller1.addShip(new Point(0,0), 5, 1);
		controller2.addShip(new Point(0,0), 5, 0);
		controller1.moveResult(new Point(0,0));
		controller1.moveResult(new Point(1,0));
		controller1.moveResult(new Point(2,0));
		controller2.moveResult(new Point(0,3));
		POSITION_RESULT[][] playerProgressGrid = model1.getPlayerProgressGrid();
		assertEquals(POSITION_RESULT.HIT, playerProgressGrid[0][0]);	
		assertEquals(3,model1.getTotalHits());
	}
	
	/**
	 * This method tests the case when a game is not over by making an 
	 * incomplete game situation. We also test a few moves.
	 * @throws IllegalPlacementException when a user places a ship at the wrong place 
	 * or makes an illegal move.
	 */
	@Test
	void isGameOverAddShipTest() throws IllegalPlacementException {
		BattleshipModel model1 = new BattleshipModel();
		BattleshipController controller1 = new BattleshipController(model1);
		BattleshipModel model2 = new BattleshipModel();
		BattleshipController controller2 = new BattleshipController(model2);
		controller1.addShip(new Point(5,5), 5, 0);
		controller1.moveResult(new Point(0,0));
		controller2.moveResult(new Point(0,0));
		assertEquals(false, controller1.isGameOver());
		assertEquals(false, controller2.isGameOver());	
	}
	
	
	/**
	 * This method tests the state of our player progress and player answer
	 * grids after a number of moves to ensure our inner data structures work fine
	 * @throws IllegalPlacementException when a user places a ship at the wrong place 
	 * or makes an illegal move.
	 */
	@Test
	void playerGridTesttest() throws IllegalPlacementException {
		BattleshipModel model1 = new BattleshipModel();
		BattleshipController controller1 = new BattleshipController(model1);
		BattleshipModel model2 = new BattleshipModel();
		BattleshipController controller2 = new BattleshipController(model2);
		controller1.addShip(new Point(0,0), 5, 0);
		controller2.addShip(new Point(0,0), 5, 0);
		controller1.moveResult(new Point(0,0));
		controller2.moveResult(new Point(0,0));
		for (int i = 0; i<10 ; i++) {
			for(int j =0; j <10;j++) {
				System.out.println(controller1.getPlayerAnswerGrid()[i][j]);
			}	
		}
		controller1.updateOpponentGrid(model1.getPlayerProgressGrid(), false);
		assertEquals(POSITION_RESULT.SHIP, controller1.getPlayerAnswerGrid()[0][0]);
		assertEquals(POSITION_RESULT.SHIP, controller1.getPlayerAnswerGrid()[1][0]);
		assertEquals(POSITION_RESULT.SHIP, controller1.getPlayerAnswerGrid()[2][0]);
		assertEquals(POSITION_RESULT.SHIP, controller1.getPlayerAnswerGrid()[3][0]);
		assertEquals(POSITION_RESULT.SHIP, controller1.getPlayerAnswerGrid()[4][0]);
	}
	
	
	/**
	 * This method tests a lot of things by doing a full game simulation where we have 
	 * two players playing (two instances of each model and controller) and tests if the 
	 * game is over when one player wins a game.
	 * @throws IllegalPlacementException when a user places a ship at the wrong place 
	 * or makes an illegal move.
	 */
	@Test
	void wholeGameSimulationTest() throws IllegalPlacementException {
		BattleshipModel model1 = new BattleshipModel();
		BattleshipController controller1 = new BattleshipController(model1);
		BattleshipModel model2 = new BattleshipModel();
		BattleshipController controller2 = new BattleshipController(model2);
	
		controller1.addShip(new Point(0,0), 5, 0);
		controller1.addShip(new Point(1,0), 4, 0);
		controller1.addShip(new Point(2,0), 3, 0);
		controller1.addShip(new Point(3,0), 2, 0);
		controller1.addShip(new Point(3,2), 2, 0);
		controller1.addShip(new Point(4,0), 1, 0);
		controller1.addShip(new Point(4,1), 1, 0);
		// We commented out this test printout which shows the 
		// player answer grid works fine.
//		for (int i = 0; i<10 ; i++) {
//			for(int j =0; j <10;j++) {
//				System.out.println(controller1.getPlayerAnswerGrid()[i][j]);
//			}	
//		}
		controller2.addShip(new Point(0,0), 5, 0);
		controller2.addShip(new Point(1,0), 4, 0);
		controller2.addShip(new Point(2,0), 3, 0);
		controller2.addShip(new Point(3,0), 2, 0);
		controller2.addShip(new Point(3,2), 2, 0);
		controller2.addShip(new Point(4,0), 1, 0);
		controller2.addShip(new Point(4,1), 1, 0);
		
		controller1.moveResult(new Point(0,0));
		controller2.moveResult(new Point(0,0));
		// missing the ship
		controller1.moveResult(new Point(9,5));
		controller2.moveResult(new Point(9,5));
		controller1.moveResult(new Point(0,1));
		controller2.moveResult(new Point(0,1));
		controller2.updateOpponentGrid(model1.getPlayerProgressGrid(), false);
		// hitting the ship
		controller1.moveResult(new Point(0,2));
		controller2.moveResult(new Point(0,2));
		controller1.moveResult(new Point(0,3));
		controller2.moveResult(new Point(0,3));
		controller1.moveResult(new Point(0,4));
		controller2.moveResult(new Point(0,4));
		controller1.moveResult(new Point(1,0));
		controller2.moveResult(new Point(1,0));
		controller1.moveResult(new Point(1,1));
		controller2.moveResult(new Point(1,1));
		controller1.moveResult(new Point(1,2));

		controller2.moveResult(new Point(1,2));
		controller1.moveResult(new Point(1,3));
		controller2.moveResult(new Point(1,3));
		controller1.moveResult(new Point(2,0));
		controller2.moveResult(new Point(2,0));
		controller1.moveResult(new Point(9,1));
		controller1.moveResult(new Point(2,1));
		controller2.moveResult(new Point(2,1));
		controller1.moveResult(new Point(2,2));
		controller2.moveResult(new Point(2,2));
		controller1.moveResult(new Point(3,0));
		controller2.moveResult(new Point(3,0));
		controller1.moveResult(new Point(3,1));
		controller2.moveResult(new Point(3,1));
		controller1.moveResult(new Point(3,2));
		controller2.moveResult(new Point(3,2));
		controller1.moveResult(new Point(3,3));
		controller2.moveResult(new Point(3,3));
		controller1.moveResult(new Point(4,0));
		controller2.moveResult(new Point(4,0));
		controller1.moveResult(new Point(4,1));
		controller2.moveResult(new Point(9,1));
		POSITION_RESULT[][] grid = model1.getPlayerProgressGrid();
		controller2.updateOpponentGrid(grid, true); 
		POSITION_RESULT[][] opponentGrid = model1.getOpponentGrid();
		controller1.updateOpponentGrid(model2.getPlayerAnswerGrid(), true);
		assertEquals(true,controller1.isGameOver());
		System.out.println(controller1.isGameOver());
		assertEquals(false,controller2.isGameOver());
		System.out.println(controller2.isGameOver());	
	}
	
}

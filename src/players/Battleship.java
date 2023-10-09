package players;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.BattleshipModel;
import controller.BattleshipController;
import utilities.BattleshipMessage;
import utilities.BattleshipNetwork;
import utilities.Constants;
import utilities.IllegalPlacementException;
import utilities.POSITION_RESULT;

/**
 * This class is the main view class of the entire Battleship game. To start
 * the game, two instances of this view class needs to be created, the server(player 1)
 * or the client(player 2). The type of instance created is based on what command line arguments
 * are inputed. Each instances will create their own GUI application that interacts 
 * with the users. Once the GUIS are displayed, the game is ready to play. Here is how the 
 * game works:
 * 
 * 1. Each player must place all of their ships anywhere on their own grid before making
 * a move.
 * 
 * 2. Player 1 gets to go first and makes a move.
 * 
 * 3. Player 2 takes the move and determines the result. 
 * 
 * 4. Player 2 notifies Player 1 of the result. 
 * 
 * 5. PLayer 2 gets to go next and Vice Versa until one of the player's fleet is complete 
 * destroyed.
 * 
 * Here is the command line argument structure when starting the program: (server or client) (port number).
 * 
 * ** Note ** You need two process to play the game, so you must run the program twice. 
 * 
 * ** Note ** You must create the server(player 1) process first or else the game wouldn't start.
 * 
 * @author Luke Genova
 * @author Amimul Ehsan Zoha
 *
 */
public class Battleship extends Application implements Observer{
	
	// Determines the gap space between the various nodes in the scene.
	private static final int HBOX_GAP = 30;
	
	private static final int RECT_SIZE = 50;
	
	private static final int SCENE_WIDTH = 1100;
	private static final int SCENE_HEIGHT = 800;
	
	private static final int VBOX_TRANSLATE_Y = -50;
	
	// Image constants.
	private static final int IMAGEVIEW_WIDTH = 900;
	private static final int IMAGEVIEW_HEIGHT = 200;
	
	// Text constants.
	private static final int TEXT1_XPOS = 50;
	private static final int TEXT1_YPOS = 100;
	
	private static final int TEXT2_XPOS = 900;
	private static final int TEXT2_YPOS = 100;
	
	private static final int FONT_SIZE = 30;
		
	// Coordinate sizes of the grid panes.
	private static final int GRID_MIN_SIZE = 0;
	private static final int GRID_MAX_SIZE = 500;
		
	// Unique colors used for the project.
	private static final Color UNIQUE_GRAY = Color.rgb(100, 100, 100);
	
	private static final Color UNIQUE_YELLOW = Color.rgb(236, 227, 161);
	
	private static final Color UNIQUE_BLUE = Color.rgb(3, 26, 141);
	
	private static final Color UNIQUE_GREEN = Color.rgb(57, 255, 20);
		
	// Controller and Model
	private BattleshipModel model = new BattleshipModel();
	private BattleshipController controller = new BattleshipController(model);
		
	// Reference to the grids.
	private Rectangle[][] gridOne  = new Rectangle[Constants.GRID_SIZE][Constants.GRID_SIZE];
	private Rectangle[][] gridTwo = new Rectangle[Constants.GRID_SIZE][Constants.GRID_SIZE];
		
		// Keeps track of how many ships were placed.
	private int shipsPlaced = 0;
		
	// Keeps track of the direction of a specific ship.
	private int shipDirection = Constants.VERTICAL;
	
	// Used to send messages to the other player.
	private ObjectOutputStream mainOutput;
	
	// Keeps track of the current player's turn.
	private boolean myTurn;
	
	// Text that determine if a player won or lost.
	private Text firstText;
	private Text secondText;
	
	/**
	 * This is the main class that will run the program.
	 * 
	 * @param args This array of strings represents the command line arguments.
	 */
	public static void main(String[] args) {
		String computer = args[0].toLowerCase();
		if (args.length == 2) {
			int portNumber = Integer.valueOf(args[1]);
			if (computer.equals("server")) {
				launch(Battleship.class, Integer.toString(portNumber+1));
			} else if (computer.equals(computer)){
				launch(Battleship.class,Integer.toString(portNumber+2));
			} else {
				System.out.println("Command line argument invalid.");
			}
		} else {
			System.out.println("Command line argument invalid.");
		}
		
	}
	
	/**
	 * This private method creates the server(player 1) of the game and waits
	 * for the client to establish a connection. Once connected, the game will
	 * start between the server and the client. This method also creates the 
	 * instance's own thread which takes in output from the other player.
	 * 
	 * @param portNumber An integer that represents the port number of the connection.
	 */
	private void makeServer(int portNumber) {
		ServerSocket server;
		try {
			server = new ServerSocket(portNumber);
			Socket connection = server.accept();
			mainOutput = new ObjectOutputStream(connection.getOutputStream());
			myTurn = true;
			BattleshipNetwork network = new BattleshipNetwork(connection,controller,mainOutput);
			Thread thread = new Thread(network);
			thread.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This private method creates the client(player 2) of the game which is 
	 * needed to start the game. Once connected, the game will
	 * start between the server and the client. This method also creates the 
	 * instance's own thread which takes in output from the other player.
	 * 
	 * @param portNumber An integer that represents the port number of the connection.
	 */
	private void makeClient(int portNumber) {
		try {
			Socket server = new Socket("localhost", portNumber);
			mainOutput = new ObjectOutputStream(server.getOutputStream());
			myTurn = false;
			BattleshipNetwork network = new BattleshipNetwork(server,controller,mainOutput);
			Thread thread = new Thread(network);
			thread.start();
			
		} catch (ConnectException e) {
			System.out.println("Connection Failed. Need player 1 to start the game.");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * This method is called when the Observed object has changed and 
	 * notifies the observer. This method also notifies the other player 
	 * thats connected when there is a change to itself.  
	 * 
	 * @param o the observable object.
	 * @param arg this argument contains the changed object.
	 *
	 */
	@Override
	public void update(Observable o, Object arg) {
		BattleshipModel newModel = (BattleshipModel) o; 
		String str = (String) arg;
		updatePlayerGrid(newModel.getPlayerProgressGrid());
		if (str.equals("Move Made.")) {
			
			BattleshipMessage message;
			
			// If the current player's fleet is destroyed, notify the user and 
			//the other player and display text.
			if (controller.isGameOver()) {
				message = new BattleshipMessage();
				message.setException("Game over");
				firstText.setFill(Color.WHITE);
				secondText.setText("YOU LOSE");
				secondText.setFill(Color.WHITE);
				
			} else {
				
				// Send a message that includes the updated player grid.
				message = new BattleshipMessage();
				message.setUpdatedGrid(newModel.getPlayerProgressGrid());
			}
			
			
			try {
				mainOutput.reset();
				mainOutput.writeObject(message);
				myTurn = true;
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		if (str.equals("Opponent Grid Updated.")) {
			updateOpponentGrid(newModel.getOpponentGrid());
		} 
		
	}

	/**
	 * The main entry point for all JavaFX applications. 
	 * 
	 * @param stage the primary stage for the application, onto which the
	 * application scene can be set.
	 */
	@Override
	public void start(Stage stage) {
		model.addObserver(this);
		
		String str = this.getParameters().getRaw().get(0);
		int player = Integer.valueOf(str.charAt(str.length()-1)) % 48;
		int portNumber = Integer.valueOf(str) - player; 
		
		if (player == 1) {
			makeServer(portNumber);
		} else {
			makeClient(portNumber);
		}
		
		stage.setTitle("Player " + player);
		
		VBox vbox = new VBox();
		HBox hbox = new HBox(HBOX_GAP);
		GridPane grid1 = new GridPane();
		GridPane grid2 = new GridPane();
	
		vbox.setAlignment(Pos.CENTER);
		hbox.setAlignment(Pos.CENTER);
		grid1.setAlignment(Pos.CENTER); 
		grid2.setAlignment(Pos.CENTER);
		
		
		// Set up the image on the top of the screen.
		String imageName = "/dexpo2d-a0b5e3e7-4657-48e8-b3f3-3f61de20d3b0.png";
		
		Image image = new Image(imageName);
		
		ImageView imageview = new ImageView(image);
		
		imageview.setFitHeight(IMAGEVIEW_HEIGHT);
		imageview.setFitWidth(IMAGEVIEW_WIDTH);
		imageview.setPreserveRatio(true);
		
		// Sets up the node object composite hierarchy
		vbox.getChildren().add(imageview);
		vbox.getChildren().add(hbox);
		hbox.getChildren().add(grid1);
		hbox.getChildren().add(grid2);
		
		// Add rectangles to grid panes.
		setUpGrid(grid1, 1);
		setUpGrid(grid2, 2);
		
		// Sets a group object as the root node to has text be displayed anywhere.
		Group root = new Group(vbox);
		
		// Adjust vbox position to make the gui look nice.
		vbox.setPrefWidth(SCENE_WIDTH);
		vbox.setPrefHeight(SCENE_HEIGHT);
		vbox.setTranslateY(VBOX_TRANSLATE_Y);
		
		createText(root);
		
		// Set event handlers.
		grid1.setOnMouseMoved((MouseEvent m) -> showShipPlacement(m));
		
		grid1.setOnMouseClicked((MouseEvent m) -> placeShip(m));
		
		grid2.setOnMouseClicked((MouseEvent m) -> makeMove(m));
		
		Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
		
		scene.setOnKeyReleased((KeyEvent k) -> changeShipDirection(k));
		
		vbox.setStyle("-fx-background-color: #000000;");
		
		stage.setScene(scene);
		stage.show();
		
		
	}
	
	/**
	 * This private method creates text objects on two separate 
	 * corners of the screen. These texts will be used to notify the
	 * current player if they won the game or lost. 
	 * 
	 * @param group An object that represents the root of the gui 
	 * structure that will be the parents of the text. 
	 */
	private void createText(Group group) {
		
		firstText = new Text(TEXT1_XPOS, TEXT1_YPOS, "GAME OVER");
		firstText.setFont(Font.font("Times New Roman", FontWeight.BLACK, 
        		FontPosture.ITALIC, FONT_SIZE));
		firstText.setFontSmoothingType(FontSmoothingType.LCD);
		firstText.setFill(Color.BLACK);
        
        secondText = new Text(TEXT2_XPOS, TEXT2_YPOS, "");
        secondText.setFont(Font.font("Times New Roman", FontWeight.BLACK, 
        		FontPosture.ITALIC, FONT_SIZE));
        secondText.setFontSmoothingType(FontSmoothingType.LCD);
        secondText.setFill(Color.BLACK);
        
        group.getChildren().add(firstText);
        group.getChildren().add(secondText);
	}
	
	/**
	 * This is a private method that sets up an individual grid onto the 
	 * GUI by adding rectangle object onto a grid pane. 
	 * 
	 * @param grid A grid pane that will hold the rectangle objects. 
	 * @param gridNum A number that determines which grid was passed as a parameter.
	 */
	private void setUpGrid(GridPane grid, int gridNum) {
		
		for (int i = 0; i < Constants.GRID_SIZE; i++) {
			for (int j = 0; j < Constants.GRID_SIZE; j++) {
				Rectangle rect = new Rectangle();
				rect.setWidth(RECT_SIZE);
				rect.setHeight(RECT_SIZE);
				rect.setFill(UNIQUE_BLUE);
				rect.setStroke(UNIQUE_GREEN);
				grid.add(rect, j, i);
				if (gridNum == 1) {
					gridOne[i][j] = rect;
				} else {
					gridTwo[i][j] = rect;
				}
				
			}
		}
	}
	
	/**
	 * This private method is an event handler that determines where the 
	 * user would could place their ship. 
	 * 
	 * @param m  An object that represents a mouse event that resulted from the 
	 * user moving their mouse on the displayed GUI.
	 */
	private void showShipPlacement(MouseEvent m) {
		int MouseX = (int) m.getX();
		int MouseY = (int) m.getY();
		
		// Stops showing the potential ship placement when all the ships are placed.
		if (shipsPlaced == Constants.SHIP_SIZES.length) {
			return;
		}
		
		if (MouseX >= GRID_MIN_SIZE && MouseX <= GRID_MAX_SIZE) {
			if (MouseY >= GRID_MIN_SIZE && MouseY <= GRID_MAX_SIZE) {
				int newX = Math.floorDiv((MouseX - GRID_MIN_SIZE), RECT_SIZE);
				int newY = Math.floorDiv((MouseY - GRID_MIN_SIZE), RECT_SIZE);
				
				int currentShipSize = Constants.SHIP_SIZES[shipsPlaced];
				
				// Makes sure that the current position isn't going out of bounds.
				if (newX >= Constants.GRID_SIZE || newX < 0) {
					return;
				}
				if (newY >= Constants.GRID_SIZE || newY < 0) {
					return;
				}
				
				// Handles index out of bound exception by checking if the current ship my go of the board.
				int max_index = 0;
				if (shipDirection == Constants.VERTICAL) {
					max_index = newY + currentShipSize-1;
				} else {
					max_index = newX + currentShipSize-1;
				}
				if (max_index >= Constants.GRID_SIZE) {
					return;
				}
				
				// Fills in the background of every rectangle after each mouse moved event.
				restartGrid();
				
				// Draws titles that shows were the potential ship can potentially be placed.
				for (int i = 0; i < currentShipSize; i++) {
					int temp = 0;
					
					// Draws horizontal or vertical based on the direction the user wants to place it.
					if (shipDirection == Constants.VERTICAL) {
						temp = newY+i;
					} else {
						temp = newX+i;
					}
					if (temp < Constants.GRID_SIZE) {
						Rectangle tempRect = null;
						if (shipDirection == Constants.VERTICAL) {
							tempRect = gridOne[temp][newX];
						} else {
							tempRect = gridOne[newY][temp];
						}
						
						// Ignores ships that were already placed.
						if (tempRect.getFill() != UNIQUE_GRAY) {
							tempRect.setFill(UNIQUE_YELLOW);
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * This private method is called by the mouseMoved event method that redraws the
	 * entire grid after the user moved the ship to another spot on the grid. Ignores
	 * grids that already have a ship placed.
	 * 
	 */
	private void restartGrid() {
		
		// Remove any changes made by the previous event except for titles that 
		// shows the ship.
		for (int i = 0; i < Constants.GRID_SIZE; i++) {
			for (int j = 0; j < Constants.GRID_SIZE; j++) {
				Rectangle tempRect = gridOne[i][j];
				if (tempRect.getFill() != UNIQUE_GRAY) {
					tempRect.setFill(UNIQUE_BLUE);
				}
			}
		}
	}
	
	/**
	 * This is private help method is an event handler that determines
	 * what happens when the user places a ship. 
	 * 
	 * @param m An object that represents a mouse event that resulted from the 
	 * user clicking on the left mouse button.
	 */
	private void placeShip(MouseEvent m) {
		int MouseX = (int) m.getX();
		int MouseY = (int) m.getY();
		
		// Stops the user from placing anymore ships on their board.
		if (shipsPlaced == Constants.SHIP_SIZES.length) {
			return;
		}
		
		// Checks event occurred within valid space.
		if (MouseX >= GRID_MIN_SIZE && MouseX <= GRID_MAX_SIZE) {
			if (MouseY >= GRID_MIN_SIZE && MouseY <= GRID_MAX_SIZE) {
				
				// Change the coordinate positions into index values. 
				int newX = Math.floorDiv((MouseX - GRID_MIN_SIZE), RECT_SIZE);
				int newY = Math.floorDiv((MouseY - GRID_MIN_SIZE), RECT_SIZE);
				try {
					Point coord = new Point(newX, newY);
					controller.addShip(coord, Constants.SHIP_SIZES[shipsPlaced], shipDirection);
					shipsPlaced++;
				} catch (IllegalPlacementException e) {
					String message = e.getMessage();
					showAlert(message);
				} 
			}
		}
	}
	
	/**
	 * This private method is an event handler that determines what will
	 * happen when the user selects a position on the opposing player's grid. 
	 * Sends the results to the either player. 
	 * 
	 * @param m An object that represents a mouse event that resulted from the 
	 * user clicking on the left mouse button.
	 */
	private void makeMove(MouseEvent m) {
		int MouseX = (int) m.getX();
		int MouseY = (int) m.getY();
		
		// Checks that the user placed all of their ships.
		if (shipsPlaced < Constants.SHIP_SIZES.length) {
			String message = "You must place all of your ships before making a move.";
			showAlert(message);
			return;
		}
		
		// If the game is over, no more move will be registered. 
		// (White text on the screen means that the game is over).
		if (firstText.getFill() == Color.WHITE) {
			return;
		}
		
		// Checks if the event occurred within valid space.
		if (MouseX >= GRID_MIN_SIZE && MouseX <= GRID_MAX_SIZE) {
			if (MouseY >= GRID_MIN_SIZE && MouseY <= GRID_MAX_SIZE) {
				
				// Change the coordinate positions into index values. 
				int newX = Math.floorDiv((MouseX - GRID_MIN_SIZE), RECT_SIZE);
				int newY = Math.floorDiv((MouseY - GRID_MIN_SIZE), RECT_SIZE);
				Point coord = new Point(newX, newY);
				
				// If its not the player's turn, it doesn't make the move.
				if (!myTurn) {
					showAlert("It's not your turn.");
					return;
				}
				
				// If the user tries to attack a position the already attacked, an alert is created.
				if (gridTwo[newY][newX].getFill() == Color.RED) {
					showAlert("You already attacked this position.");
					return;
				}
				if (gridTwo[newY][newX].getFill() == Color.WHITE) {
					showAlert("You already attacked this position.");
					return;
				}
				
				BattleshipMessage message = new BattleshipMessage();
				message.setMove(coord);
				
				
				
				// Send a message to the other player that the current player made a move.
				try {
					mainOutput.reset();
					mainOutput.writeObject(message);
					myTurn = false;
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * This private helper method is an event handler that determines 
	 * what happens when the user presses the 'r' key. When the user presses the
	 * 'r' key, the direction the ship that will be placed will be changed. 
	 * 
	 * @param key A object that represents an key event that resulted from user input
	 * from their keyboard.
	 */
	private void changeShipDirection(KeyEvent key) {
		String input = key.getCode().getName();
		if (input.length() == 1) {
			if (input.charAt(0) == 'r' || input.charAt(0) == 'R') {
				
				// Change the direction of the ship. 
				if (shipDirection == Constants.HORIZONTAL) {
					shipDirection = Constants.VERTICAL;
				} else {
					shipDirection = Constants.HORIZONTAL;
				}
			}
		}
	}
	
	/**
	 * This private helper method updates the current player's grid based on 
	 * any changes made in the model.
	 * 
	 * @param playerGrid A grid of constant that describe the result of a specific 
	 * position in the player's grid.
	 */
	private void updatePlayerGrid(POSITION_RESULT[][] playerGrid) {
		
		for (int i = 0; i < Constants.GRID_SIZE; i++) {
			for (int j = 0; j < Constants.GRID_SIZE; j++) {
				
				// Changes the color of a position(rectangle object) based on what was updated.
				Rectangle position = gridOne[i][j];
				if (playerGrid[i][j] != null) {
					String result = playerGrid[i][j].getDescription();
					if (result.equals("A Ship is here")) {
						position.setFill(UNIQUE_GRAY);
					} else if (result.equals("A Ship has been hit")) {
						position.setFill(Color.RED);
					} else {
						position.setFill(Color.WHITE);
					}
				} else {
					position.setFill(UNIQUE_BLUE);
				}
			}
		}
		
	}
	
	/**
	 * This private helper method updates the opponent's grid based on what the current 
	 * player did.
	 * 
	 * @param opponentGrid A grid of constant that describe the result of a specific 
	 * position in the opposing player's grid.
	 */
	private void updateOpponentGrid(POSITION_RESULT[][] opponentGrid) {
		boolean playerWon = false;
		for (int i = 0; i < Constants.GRID_SIZE; i++) {
			for (int j = 0; j < Constants.GRID_SIZE; j++) {
				
				Rectangle position = gridTwo[i][j];
				if (opponentGrid[i][j] != null) {
					String result = opponentGrid[i][j].getDescription();
					if (result.equals("A Ship is here")) {
						position.setFill(UNIQUE_GRAY);
						playerWon = true;
					} else if (result.equals("A Ship has been hit")) {
						position.setFill(Color.RED);
					} else {
						position.setFill(Color.WHITE);
					}
				} else {
					position.setFill(UNIQUE_BLUE);
				}
				
			}
		}
		// This if statement checks if the current player has won the game. 
		// If so then it displays white text saying that the player won.
		if (playerWon && !controller.isGameOver()) {
			firstText.setFill(Color.WHITE);
			secondText.setText("YOU WIN");
			secondText.setFill(Color.WHITE);
		}
	}
	
	/**
	 * This method creates an alert based on what the header 
	 * states and shows it to the user.
	 * 
	 * @param content a string representing that the content text would look like in the alert.
	 */
	private void showAlert(String content) {
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.setTitle("Error");
		a.setHeaderText("Error");
		a.setContentText(content);
		a.showAndWait();
		
		return;
	}
	
	
}

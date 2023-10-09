package utilities;

/**
 * This enumeration is used to hold various different constants on various
 * positions in a grid or a 2d array. These enums will be used to determine 
 * the status of a position, the position can either have a ship on it, a ship
 * that has been hit, or an empty spot that was attacked. The user of the 
 * class may use 'getDescription' to get a printable description of each enum.
 * 
 * 
 * @author Luke Genova
 * @author Amimul Ehsan Zoha
 * 
 */
public enum POSITION_RESULT {
	
	SHIP("A Ship is here"),
	HIT("A Ship has been hit"),
	MISS("A missile missed");
	
	private String description;
	
	
	/**
	 * POSITION_RESULT constructor
	 * 
	 * @param description A string that represents the description of one of the constants.
	 */
	private POSITION_RESULT(String description) {
		this.description = description;
	}
	
	/**
	 * Returns a description of the enum value.
	 * 
	 * @return A string containing the description of the enum value.
	 * 
	 */
	public String getDescription() {
		return this.description;
	}
	
}

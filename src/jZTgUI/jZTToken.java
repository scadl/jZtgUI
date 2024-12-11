/**
 * 
 */
package jZTgUI;

import java.io.Serializable;

/**
 * 
 */
public class jZTToken implements Serializable {
	private static final long UID = 1L;
	String netID;
	tokenTypeEnum tokenType;
	String tokenVal;
	String apiURL;
	
	public Boolean loadValues()
	{
		try{
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}
}

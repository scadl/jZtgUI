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
	private String dataFileName = "settings.ser";
	
	public String netID;
	public tokenTypeEnum tokenType;
	public String tokenVal;
	public String apiURL;
	
	public Boolean loadValues()
	{
		try{
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}
}

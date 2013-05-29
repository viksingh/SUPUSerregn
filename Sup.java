package supuser;

import java.util.ResourceBundle;
public class Sup{

  public static String getProperty(String name){
		ResourceBundle rb = ResourceBundle.getBundle("sup");
		return rb.getString(name);		
	}
	
}

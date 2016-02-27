package cs263.cs263;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Login {

	public static boolean passwordMatch(String username, String password) {
		try {
			System.out.println(username+","+password);
			return UserServices.Get(username).password.equals(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
package cs263w16;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	String email;
	String userID;
	String nickname;
	List<Photo> photoList;
	
	public User(String email,String userID,String nickname)
	{
		setEmail(email);
		setUserID(userID);
		setNickname(nickname);
		photoList=new ArrayList<Photo>();
	}
	
	public User(String email, String userID)
	{
		setEmail(email);
		setUserID(userID);
		photoList=new ArrayList<Photo>();
	
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public void addPhoto(Photo photo)
	{
		photoList.add(photo);
	}
	
}

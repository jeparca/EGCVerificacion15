package main.java;

public interface Token {
	
	public boolean createToken(Integer votationId);
	
	public boolean checkToken(Integer votationId, Integer token);
}

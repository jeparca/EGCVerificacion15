package test.java;

import main.java.Token;

public class TestToken {

	public static void main(String[] args) {
		
		boolean test = Token.createToken(999999998);
		
		//System.out.println(test);
		boolean test2 = Token.checkToken(999999998, 8161653);

		System.out.println(test2);
	}

}

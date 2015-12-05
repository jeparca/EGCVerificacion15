package test.java;

import main.java.TokenImpl;

public class TestToken {

	public static void main(String[] args) {
		
		boolean test = TokenImpl.createToken(999999998);
		
		//System.out.println(test);
		boolean test2 = TokenImpl.checkToken(999999998, 8161653);

		System.out.println(test2);
	}

}

package main.java;

public class TokenImpl implements Token {

	
	private static final Integer[] tokenAuxList = {1128, 1171, 1994, 2013, 2243, 2679, 2716, 3076, 3342, 3403, 3436, 3737, 3870, 3928, 3951, 4156, 4427, 5176, 5440, 5630, 5770, 5779, 6895, 6931, 7038, 7402, 7473, 7640, 7843, 8001, 8278, 8904, 9374, 9573};
	
	
	private Integer calculateToken(Integer idVotacion){
		
		Integer token = 0;
		
		checkId(idVotacion);
		
		String binaryInteger = Integer.toBinaryString(idVotacion);
		char[] numberByNumber = binaryInteger.toCharArray();
		
		
		for(int i=0;i<numberByNumber.length; i++){
			Integer digit = new Integer(numberByNumber[i]);
			if(digit > 0){
				token += digit*tokenAuxList[i];
			}
		}
		
		return token;
		
	}

	private void checkId(Integer idVotacion) {
		assert idVotacion < 999999998;
		
	}

	
}

package main.java;

public class Token{

	
	private static final Integer[] tokenAuxList = {1128, 1171, 1994, 2013, 
			2243, 2679, 2716, 3076, 3342, 3403, 3436, 3737, 3870, 3928, 
			3951, 4156, 4427, 5176, 5440, 5630, 5770, 5779, 6895, 6931, 
			7038, 7402, 7473, 7640, 7843, 8001, 8278, 8904, 9374, 9573};
	
	
	private static Integer calculateToken(Integer votationId){
		
		Integer token = 0;
		
		checkId(votationId);
		
		String binaryInteger = Integer.toBinaryString(votationId);
		char[] numberByNumber = binaryInteger.toCharArray();
		
		
		for(int i=numberByNumber.length-1; 0 < i; i--){
			Integer digit = new Integer(numberByNumber[i]);
			for (int j=tokenAuxList.length-1; 0 < j; j--){
				if(digit > 0){
					token += digit*tokenAuxList[j];
				}
			}
		}
		
		return token*17*31;
		
	}

	private static void checkId(Integer votationId) {
		assert votationId < 999999998;
		
	}

	public static boolean createToken(Integer votationId) {
		return RemoteDataBaseManager.sendGeneratedToken(votationId, 
				calculateToken(votationId));
	}

	public static boolean checkToken(Integer votationId, Integer token) {
		boolean result = false;
		
		if(calculateToken(votationId).equals(token)){
			Integer storedToken = RemoteDataBaseManager.getAccessToken(votationId);
			result = token.equals(storedToken);
		}
		
		return result;
	}
	
	
}

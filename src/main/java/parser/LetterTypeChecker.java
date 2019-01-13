package parseJSON;
import java.util.Arrays;
public class LetterTypeChecker {

	
	public LetterType IsRequest(String content, boolean isSttachment) {
		
		String str = content.trim();
		String [] arr = str.split(" ");
		
        if(arr.length==2 && isSttachment == false){
        		return LetterType.ANSWER;	
        }else if(arr.length>3 && isSttachment == true) {
        	return LetterType.REQUEST;
        	
            }else {
        	   return LetterType.UNDEFINED;
            }
	}	
}



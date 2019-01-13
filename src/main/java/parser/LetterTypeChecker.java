package parseJSON;
import java.util.Arrays;
public class LetterTypeChecker {

	
	public LetterType IsRequest(String content, boolean isAttachment) {
		
		String str = content.trim();
		String [] arr = str.split(" ");
		
        if(arr.length==2 && !isAttachment){
        	  return LetterType.ANSWER;	
        }else if(arr.length>3 && isAttachment) {
        	      return LetterType.REQUEST;
        	
               }else {
        	       return LetterType.UNDEFINED;
               }
	}	
}



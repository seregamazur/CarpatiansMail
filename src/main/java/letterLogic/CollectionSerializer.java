package letterLogic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import exceptionsLogger.ExceptionsLogger;

public class CollectionSerializer {
	
	 String fileName;
	 ExceptionsLogger logger;
	 
	 public CollectionSerializer(String fileName, ExceptionsLogger logger) {
		 this.fileName = fileName;
		 this.logger = logger;
	 }
	
	public void saveCollection(ArrayList<Letter> letters) {
		
		 try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName)))
	        {
	            oos.writeObject(letters);
	        }
	        catch(Exception ex){
	             logger.log(ex);
	        } 
		 

	}

	@SuppressWarnings("unchecked")
	public ArrayList<Letter> readCollection() {
		ArrayList<Letter> letters = new ArrayList<>();
		
		File file = new File(fileName);
		
		if(file.exists()) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName)))
	        {
	             
				letters = ((ArrayList<Letter>)ois.readObject());
	        }
	        catch(Exception ex){          
	        	logger.log(ex);
	        } 
		}
		return letters;
	}
}

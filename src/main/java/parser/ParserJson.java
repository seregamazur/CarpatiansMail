package parseJSON;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;


public class ParserJson {
	
    public ArrayList<Employee> parseJSON(String filePath) throws IOException  {
		ArrayList<Employee> list = new ArrayList<>();
	    try {
	    	InputStream fileInputStream = new FileInputStream(filePath);
	    	
	    	JsonReader reader = Json.createReader(fileInputStream);
	    	JsonObject employeesObject = reader.readObject();
	
	        reader.close();
			fileInputStream.close();
	        
	        JsonArray employees = employeesObject.getJsonArray("employees");
	        
	        employees.forEach(jsonValue -> {
	        	JsonObject value = (JsonObject)jsonValue;
	        	
	        	list.add(
	    			new Employee(
						value.getString("name"), 
						value.getString("position"), 
						value.getString("eMail"), 
						value.getInt("level")
					)
				);
	        });
	         
	
		} catch(Exception e) {
			System.out.println(e);
		}
	    
		return list;
	}	
		
}


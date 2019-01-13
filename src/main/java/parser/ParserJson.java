package parser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import employee.Employee;


public class ParserJson {
	
    public ArrayList<Employee> parseJSON(String filePath) throws IOException  {
		ArrayList<Employee> list = new ArrayList<>();
		
		InputStream fileInputStream = null;
    	JsonReader reader = null;
	    
    	try {
	    	fileInputStream = new FileInputStream(filePath);
	    	
	    	reader = Json.createReader(fileInputStream);
	    	JsonObject employeesObject = reader.readObject();
	        
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
	         
	
		} 
	      finally {
	    	  reader.close();
			  fileInputStream.close();
		  }
	    
		return list;
	}	
		
}


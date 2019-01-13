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
						value.get("name").toString().substring(1, value.get("name").toString().length()-1), 
						value.get("position").toString().substring(1, value.get("position").toString().length()-1), 
						value.get("eMail").toString().substring(1, value.get("eMail").toString().length()-1), 
						value.getInt("level")
					)
				);
	        });
	
		} 
	    finally {
	    	  if(reader != null) {
	    		  reader.close();
	    	  }
	    	  if(fileInputStream != null) {
	    		  fileInputStream.close();
	    	  }
		  }
	    
		return list;
	}	
		
}


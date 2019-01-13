package controller;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import employee.Employee;
import exceptionsLogger.ExceptionsLogger;
import letterLogic.Letter;
import parser.Parser;
import parser.ParserJson;

public class Controller {
	Timer updateTimer;
	TimerTask getNewLettersTask;
	private Parser parser = new Parser();
	ArrayList<Letter> letters = new ArrayList<>();
	ArrayList<Employee> employees;
 	ExceptionsLogger logger = new ExceptionsLogger("D:/serverExceptions.log");
	int timerUpdatePeriod = 10 * 60 * 1000;
 	
 	
	public static void main(String[] args) {
		
		
		Controller controller = new Controller();
		controller.runServer();
		
	}
	
	
	private void runServer() {
		employees =  initializeEmployeesCollection();
	/*	
		employees.add(new Employee("Ігор Полатайко", "developer", "igor.bogdanovich39@gmail.com", 0));
		employees.add(new Employee("Олег MADCAT", "developer", "igor.bogdanovich39@gmail.com", 1));
		employees.add(new Employee("Назар Щербій", "developer", "igor.bogdanovich39@gmail.com", 1));
	*/	
		try {
			ArrayList<Employee> empl = parser.parseXls("C:\\Users\\Ігор\\Desktop\\employees.xlsx", employees);
			for(Employee e : empl) {
				System.out.println(e.getName());
			}
		} catch (Exception e) {
			logger.log(e);
		}
		
		
		updateTimer = new Timer();
		getNewLettersTask = new TimerTask() {
			public void run() {
				// get new letters method()
				 
				// ** answer or request
				// **** parse xls create Letter objects and add it to letters
				// **** or setAnswer to letter which id that same that in letter
				
			}
		};
		
		updateTimer.scheduleAtFixedRate(getNewLettersTask, timerUpdatePeriod, timerUpdatePeriod);
	}
	
	private ArrayList<Employee> initializeEmployeesCollection() {
		
		ArrayList<Employee> employees = null;
		try {
		employees = new ParserJson().parseJSON("C:\\Users\\Ігор\\Desktop\\employees.json");
		}
		catch(Exception e) {
			logger.log(e);
		}
		return employees;
	}
}

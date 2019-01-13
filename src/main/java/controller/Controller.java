package controller;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import employee.Employee;
import exceptionsLogger.ExceptionsLogger;
import letterLogic.Letter;
import parser.ParserJson;

public class Controller {
	Timer updateTimer;
	TimerTask getNewLettersTask;
	ArrayList<Letter> letters = new ArrayList<>();
	ArrayList<Employee> employees;
 	ExceptionsLogger logger = new ExceptionsLogger("D:/serverExceptions.log");
	
	public static void main(String[] args) {
		
		
		Controller controller = new Controller();
		
		controller.employees = controller.initializeEmployeesCollection();
		
		
		controller.updateTimer = new Timer();
		controller.getNewLettersTask = new TimerTask() {
			public void run() {
				// get new letters
				// TODO how to differentiate answer from action
				// TODO як дізнатись до якого листа належить відповідь
			}
		};
		
	}
	
	private ArrayList<Employee> initializeEmployeesCollection() {
		ArrayList<Employee> employees = null;
		try {
		employees = new ParserJson().parseJSON("");
		}
		catch(Exception e) {
			logger.log(e);
		}
		return employees;
	}
}

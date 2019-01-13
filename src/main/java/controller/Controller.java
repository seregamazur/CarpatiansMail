package controller;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import employee.Employee;
import exceptionsLogger.ExceptionsLogger;
import letterLogic.Letter;
import letterLogic.LetterType;
import parser.LetterTypeChecker;
import parser.Parser;
import parser.ParserJson;

public class Controller {
	private Timer updateTimer;
	private TimerTask getNewLettersTask;
	private Parser parser = new Parser();
	private ArrayList<Letter> letters = new ArrayList<>();
	private ArrayList<Employee> employees;
	private ExceptionsLogger logger = new ExceptionsLogger("D:/serverExceptions.log");
	private int timerUpdatePeriod = 10 * 60 * 1000;
	private LetterTypeChecker letterTypeChecker = new LetterTypeChecker();
 	
 	
	public static void main(String[] args) {
		
		
		Controller controller = new Controller();
		controller.runServer();
		
	}
	
	
	private void runServer() {
		employees =  initializeEmployeesCollection();
		
		updateTimer = new Timer();
		getNewLettersTask = new TimerTask() {
			public void run() {
				// TODO get new letters method()
				 
				if(letterTypeChecker.IsRequest(content, isAttachment) == LetterType.ANSWER) {
					// **** or setAnswer to letter which id that same that in letter
					
				}
				else if(letterTypeChecker.IsRequest(content, isAttachment) == LetterType.REQUEST) {
					// **** parse xls create Letter objects and add it to letters
				}
				else {
					sentBadLetterTypeError(senderEmail);
				}
			}
		};
		
		updateTimer.scheduleAtFixedRate(getNewLettersTask, 0, timerUpdatePeriod);
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

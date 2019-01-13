package letterLogic;

import client.authenticator.EmailAuthenticator;
import client.core.BaseGmailClient;
import client.core.GmailClient;
import client.core.common.SendedMessage;
import employee.Employee;
import exceptionsLogger.ExceptionsLogger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;



public class Letter{
	final BaseGmailClient client = getClient().auth();
	private ExceptionsLogger logger = new ExceptionsLogger("C:/serverExcetions.log");
	
	private Timer timer;
	private TimerTask deadlineCheckTask;
	private final int deadlineCheckTaskPeriod = 1 * 24 * 3600 * 1000;
	
	private ArrayList<Employee> employees;
	private String content;
	private String senderEmail;
	private String bossEmail;
	private int currentLevel;
	private int[] letterState;
	private LetterState currentGeneralLetterState = LetterState.UNDEFINED;
	private LocalDateTime[] sendTime;
	
	
	public Letter(ArrayList<Employee> employees, String senderEmail, String bossEmail, String content) {
		this.employees = employees;
		this.senderEmail = senderEmail;
		this.bossEmail = bossEmail;
		this.content = content;
		this.currentLevel = getMaxLevel();
		this.letterState = new int[employees.size()];
		this.sendTime = new LocalDateTime[employees.size()];
		sent();
		createDeadlineTask();
	}
	
	private int getMaxLevel() {
		int maxLevel = 0;
		for(Employee e : employees) {
			if(e.getLevel() > maxLevel) {
				maxLevel = e.getLevel();
			}
		}
		return maxLevel;
	}
	
	public void setAnswer(boolean isAccepted, String eMail) {
		int index = getIndex(eMail);
		if(index == -1) {
			throw new IllegalArgumentException();
		}
		letterState[index] = (isAccepted) ? 1 : -1; 
		if(checkLevelAnswers()) {
			LevelUp();
		}
	}
	
	private int getIndex(String eMail) {
		int index = -1;
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getEmail().equals(eMail.trim())) {
				index = i;
			}
		}
		return index;
	}
	
	private boolean checkFullLevel() {
		boolean isAccepted = true;
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel && letterState[i] == -1) {
				isAccepted = false;
			}
		}
		return isAccepted;
	}
	
	private boolean checkLevelAnswers() {
		boolean answered = true;
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel && letterState[i] == 0) {
				answered = false;
			}
		}
		return answered;
	}
	
	private void LevelUp() {
		if(checkFullLevel()) {
			if(currentLevel > 0) {
				currentLevel--;
			}
			else {
				currentGeneralLetterState = LetterState.ACCEPTED;
			}
		}
		else {
			currentGeneralLetterState = LetterState.REJECTED;
		}
	}
	
	private void sentToBoss() {
		client.send(messageToBoss());
	}
	
	private void sentToAllFromCurrentLevel() {
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel) {
				client.send(messageTo(employees.get(i).getEmail()));
				sendTime[i] = LocalDateTime.now();
			}
		}
	}

	private void sentBackToSender() {
		client.send(messageToSender());
	}
	
	private boolean breakAnswerDeadline(int index) {
		if(sendTime[index].plusDays(14).isAfter(LocalDateTime.now())) {
			return true;
		}
		return false;
	}
	
	private void deadlineControl() {
		for(int i = 0; i < employees.size(); i++) {
			if(breakAnswerDeadline(i)) {
				client.send(messageTo(employees.get(i).getEmail()));
			}
		}
	}
	
	private void createDeadlineTask()
	{
		timer = new Timer();
		deadlineCheckTask = new TimerTask() {
			public void run() {
				deadlineControl();
			}
		};
		timer.scheduleAtFixedRate(deadlineCheckTask, deadlineCheckTaskPeriod, deadlineCheckTaskPeriod);
	}
	private void sent() {
		switch(currentGeneralLetterState) {
			case UNDEFINED:
				sentToAllFromCurrentLevel();
				break;
			case ACCEPTED:
				sentToBoss();
				break;
			case REJECTED:
				sentBackToSender();
				break;
		}
	}
	private GmailClient getClient() {
		return GmailClient.get()
				.loginWith(EmailAuthenticator.Gmail.auth("serhiy.mazur1@gmail.com", "****"))
				.beforeLogin(() -> {})
				.onLoginError(e -> logger.log(e))
				.onLoginSuccess(() -> {});
	}
	private SendedMessage messageToBoss() {
		return new SendedMessage("Hey", content)
				.from("Server")
				.to(bossEmail);
	}

	private SendedMessage messageTo(String eMail) {
		return new SendedMessage("Hey", content)
				.from("Server")
				.to(eMail.trim());
	}

	private SendedMessage messageToSender() {
		return new SendedMessage("Hey", content)
				.from("Server")
				.to(senderEmail);
	}}

 

enum LetterState{
	UNDEFINED, ACCEPTED, REJECTED;
}
 

		
 
 
 
 
 

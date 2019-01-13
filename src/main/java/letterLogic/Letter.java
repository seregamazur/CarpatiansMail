package letterLogic;

import client.authenticator.EmailAuthenticator;
import client.core.BaseGmailClient;
import client.core.GmailClient;
import client.core.common.SendedMessage;
import employee.Employee;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Letter{
	final BaseGmailClient client = getClient().auth();
	private ArrayList<Employee> employees;
	private String content;
	private String senderEmail;
	private String bossEmail;
	private int currentLevel;
	private int[] letterState;
	private LetterState currentGeneralLetterState = LetterState.UNDEFINED;
	private LocalDateTime sendTime;
	
	
	public Letter(ArrayList<Employee> employees, String senderEmail, String bossEmail, String content) {
		this.employees = employees;
		this.senderEmail = senderEmail;
		this.bossEmail = bossEmail;
		this.content = content;
		this.currentLevel = getMaxLevel();
		this.letterState = new int[employees.size()];
		sent();
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
		sendTime = LocalDateTime.now();
	}
	
	private void sentToAllFromCurrentLevel() {
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel) {
				client.send(messageToAll());
				sendTime = LocalDateTime.now();
			}
		}
	}

	private void sentBackToSender() {
		client.send(messageToSender());
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
				.beforeLogin(() -> System.out.println("Process login..."))
				.onLoginError(e -> e.printStackTrace())
				.onLoginSuccess(() -> System.out.println("Login successfully"));
	}
	private SendedMessage messageToBoss() {
		return new SendedMessage("Hey", "Boss")
				.from("Server")
				.to("boss@gmail.com");
	}

	private SendedMessage messageToAll() {
		return new SendedMessage("Hey", "Employee")
				.from("Server")
				.to("employee@gmail.com").to("another.employee@gmail.com");
	}

	private SendedMessage messageToSender() {
		return new SendedMessage("Hey", "Bad news")
				.from("Server")
				.to("komys'@gmail.com");
	}}

 

enum LetterState{
	UNDEFINED, ACCEPTED, REJECTED;
}
 

		
 
 
 
 
 

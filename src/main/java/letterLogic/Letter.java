package letterLogic;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Letter{
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
		// TODO send back to boss (using 'bossEmail' variable)
		sendTime = LocalDateTime.now();
	}
	
	private void sentToAllFromCurrentLevel() {
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel) {
				// TODO send to employees.get(i)
				sendTime = LocalDateTime.now();
			}
		}
	}

	private void sentBackToSender() {
		// TODO send back to sender (using 'senderEmail' variable)
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
}
 

enum LetterState{
	UNDEFINED, ACCEPTED, REJECTED;
}
 

		
 
 
 
 
 

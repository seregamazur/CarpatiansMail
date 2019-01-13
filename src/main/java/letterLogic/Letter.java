package letterLogic;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Letter{
	private ArrayList<Employee> employees;
	private String content;
	private int currentLevel;
	private int[] letterState;
	private LetterState currentGeneralLetterState = LetterState.UNDEFINED;
	private LocalDateTime sendTime;
	
	
	public Letter(ArrayList<Employee> employees, String content) {
		this.employees = employees;
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
	
	public void setAnswer(boolean isAccepted, int index) {
		letterState[index] = (isAccepted) ? 1 : -1; 
		if(checkLevelAnswers()) {
			LevelUp();
		}
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
		
		sendTime = LocalDateTime.now();
	}
	
	private void sentToAllFromCurrentLevel() {
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel) {
				
				sendTime = LocalDateTime.now();
			}
		}
	}

	private void sentBackToSender() {
		
	}
	
	private void sent() {
		switch(currentGeneralLetterState) {
			case UNDEFINED:
				// sent current level
				break;
			case ACCEPTED:
				// sent to boss
				break;
			case REJECTED:
				// back to sender
				break;
		}
	}
}
 

enum LetterState{
	UNDEFINED, ACCEPTED, REJECTED;
}
 

		
 
 
 
 
 

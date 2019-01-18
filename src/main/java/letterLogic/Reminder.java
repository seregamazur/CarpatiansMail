package letterLogic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Reminder {

	private Timer timer;
	private TimerTask deadlineCheckTask;
	private int deadlineCheckTaskPeriod = 60*1000; //1 * 24 * 3600 * 1000;
	private int answerDeadlineMinutes = 3;
	
	private ArrayList<Letter> letters;
	
	public Reminder(ArrayList<Letter> letters) {
		this.letters = letters;
	}
	
	
	
	private void deadlineControl() {
		for(int i = 0; i < letters.size(); i++) {
			for(int k = 0; k < letters.get(i).getLetterState().length; k++) {
				if(letters.get(i).getCurrentGeneralLetterState() == LetterState.UNDEFINED) {
					if(letters.get(i).getLetterSendTime()[k] != null && letters.get(i).getLetterState()[k] == 0 && letters.get(i).getLetterSendTime()[k].isBefore(LocalDateTime.now().minusMinutes(answerDeadlineMinutes))) {
							letters.get(i).sentToPerson(k);
						
					}
				}
			}
			if(letters.get(i).getCurrentGeneralLetterState() == LetterState.ACCEPTED) {
				if(letters.get(i).getLetterSendTime()[letters.get(i).getLetterSendTime().length - 1] != null && letters.get(i).getLetterSendTime()[letters.get(i).getLetterSendTime().length - 1].isBefore(LocalDateTime.now().minusMinutes(answerDeadlineMinutes))) {
					letters.get(i).sentToBoss();
				}
			}
		}
	}
	
	public void createDeadlineTask()
	{
		timer = new Timer();
		deadlineCheckTask = new TimerTask() {
			public void run() {
				deadlineControl();
			}
		};
		timer.scheduleAtFixedRate(deadlineCheckTask, deadlineCheckTaskPeriod, deadlineCheckTaskPeriod);
	}
	
	
}

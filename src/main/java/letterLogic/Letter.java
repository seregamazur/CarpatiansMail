package letterLogic;

import client.authenticator.EmailAuthenticator;
import client.core.BaseGmailClient;
import client.core.GmailClient;
import client.core.common.SendedMessage;
import employee.Employee;
import exceptionsLogger.ExceptionsLogger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;



@SuppressWarnings("serial")
public class Letter implements Serializable{
	final BaseGmailClient client = getClient().auth();
	private ExceptionsLogger logger;
	
	
	private Timer timer;
	private TimerTask deadlineCheckTask;
	private final int deadlineCheckTaskPeriod = 60*1000; //1 * 24 * 3600 * 1000;
	private int answerDeadlineMinutes = 10;
	private int answerDeadlineMinutesAfterBreak = 5;
	
	private ArrayList<Employee> employees;
	private String content;
	private String senderEmail;
	private String bossEmail;
	private String bossName;
	private int currentLevel;
	private int[] letterState;
	
	private LetterState currentGeneralLetterState = LetterState.UNDEFINED;
	private LocalDateTime[] sendTime;
	
	private String letterID;
	
	
	public Letter(ArrayList<Employee> employees, String senderEmail, String bossName, String bossEmail, String content, ExceptionsLogger logger) {
		this.employees = employees;
		this.senderEmail = senderEmail;
		this.bossEmail = bossEmail;
		this.bossName = bossName;
		this.content = content;
		this.currentLevel = getMaxLevel();
		this.letterState = new int[employees.size()];
		this.sendTime = new LocalDateTime[employees.size()];
		letterID = UUID.randomUUID().toString();
		this.logger = logger;
		sent();
		createDeadlineTask();
	}
	
	public Letter(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public void setAnswer(boolean isAccepted, String eMail) {
		if(currentGeneralLetterState == LetterState.ACCEPTED) {
			handleBossAnswer(isAccepted);
		}
		else {
			int index = getIndex(eMail);
			if(index == -1) {
				throw new IllegalArgumentException();
			}
			letterState[index] = (isAccepted) ? 1 : -1; 
			if(checkLevelAnswers()) {
				LevelUp();
			}
		}
	}
	
	public void badAttachmentFormat() {
		client.send(sentErrorMessage("Помилка в Excel таблиці. Зірочкою (*) було відмічено одне або більше полів з іменами людей"
						  + " відомостей про яких немає в базі даних"));
	}
	
	public void sentBadIDError() {
		client.send(sentErrorMessage("Листа з вказаним ID не знайдено!\r\n"
								   + "Перевірте правильність введеного в листі ID. "));
	}
	
	public void sentBadLetterTypeError() {
		client.send(sentErrorMessage("Лист не відповідає вимогам, неможливо визначити його тип\r\n"
							       + "Темою листа повенне бути одне з наступних слів: Запит, Відповідь.\r\n"
							       + "Лист-запит повинен мати Excel таблицю.\r\nЛист-відповідь не повинен "
							       + "містити прикріплень"));
	}
	
	public void sentBadAnswerLetterTypeError() {
		client.send(sentErrorMessage("Лист-відповідь не відповідає вимогам, неможливо визначити тип відповіді\r\n"
			       + "Лист-відповідь повинен відповідати одному з наступних шаблонів:\r\n"
			       + "ID Погоджено\r\n"
			       + "ID Відхилено"));
	}
	
	public void sentAlreadyAcceptedError() {
		client.send(sentErrorMessage("Даний запит уже було погоджено керіником"));
	}
	
	public String getLetterID() {
		return letterID;
	}
	
	public LetterState getLetterState() {
		return currentGeneralLetterState;
	}
	
	private void handleBossAnswer(boolean isAccepted) {
		if(isAccepted) {
			sentBackToSenderPositiveAnswer();
			currentGeneralLetterState = LetterState.ACCEPTED_BY_BOSS;
		}
		else {
			ArrayList<Employee> sigleListForBoss = new ArrayList<>();
			sigleListForBoss.add(new Employee(bossName, "Керівник", bossEmail, -9999));
			sentBackToSenderNegativeAnswer(sigleListForBoss, employees);
			currentGeneralLetterState = LetterState.REJECTED;
		}
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
		if(checkIfCurrentLevelNotEmpty() && currentLevel >= 0) {
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
		else {
			currentLevel--;
		}
		sent();
	}
	
	private boolean checkIfCurrentLevelNotEmpty() {
		return employees.size() != 0;
	}
	
	private void sentToBoss() {
		client.send(messageTo(bossEmail));
	}
	
	private void sentToAllFromCurrentLevel() {
		for(int i = 0; i < employees.size(); i++) {
			if(employees.get(i).getLevel() == currentLevel) {
				client.send(messageTo(employees.get(i).getEmail()));
				sendTime[i] = LocalDateTime.now();
			}
		}
	}

	private void sentBackToSenderPositiveAnswer() {
		client.send(messagePositiveAnswerToSender());
	}
	
	private void sentBackToSenderNegativeAnswer(ArrayList<Employee> peopleWhoRejectIt, ArrayList<Employee> peopleWhoAcceptIt) {
		client.send(messageNegativeAnswerToSender(peopleWhoRejectIt, peopleWhoAcceptIt));
	}
	
	private boolean breakAnswerDeadline(int index) {
		if(sendTime[index] != null && sendTime[index].plusMinutes(answerDeadlineMinutes).isBefore(LocalDateTime.now()) && letterState[index] == 0){
			sendTime[index] = LocalDateTime.now();
			answerDeadlineMinutes = answerDeadlineMinutesAfterBreak;
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
	
	private ArrayList<Employee> getWhoRejectIt() {
		ArrayList<Employee> peopleWhoRejectIt = new ArrayList<>();
		for(int i = 0; i < employees.size(); i++) {
			if(letterState[i] == -1) {
				peopleWhoRejectIt.add(employees.get(i));
			}
		}
		return peopleWhoRejectIt;
	}
	
	private ArrayList<Employee> getWhoAcceptIt() {
		ArrayList<Employee> peopleWhoAcceptIt = new ArrayList<>();
		for(int i = 0; i < employees.size(); i++) {
			if(letterState[i] == 1) {
				peopleWhoAcceptIt.add(employees.get(i));
			}
		}
		return peopleWhoAcceptIt;
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
				ArrayList<Employee> peopleWhoRejectIt = getWhoRejectIt();
				ArrayList<Employee> peopleWhoAcceptIt = getWhoAcceptIt();
				sentBackToSenderNegativeAnswer(peopleWhoRejectIt, peopleWhoAcceptIt);
				break;
			case ACCEPTED_BY_BOSS:
				sentAlreadyAcceptedError();
				break;
		}
	}
	private GmailClient getClient() {
		return GmailClient.get()
				.loginWith(EmailAuthenticator.Gmail.auth("vokarpaty.server.mail@gmail.com", "vokarpatyIPZ"))
				.beforeLogin(() -> {})
				.onLoginError(e -> logger.log(e))
				.onLoginSuccess(() -> {});
	}

	private SendedMessage messageTo(String eMail) {
		return new SendedMessage("Запит", letterID +" - скопіюйте це і вставте першим словом у відповіді \r\n"+
								"Запит від " + senderEmail + "\r\n\r\n" + content)
				.from("ВО \"Карпати\" (сервер)")
				.to(eMail.trim());
	}

	private SendedMessage messagePositiveAnswerToSender() {
		return new SendedMessage("Відповідь","Ваш запит погоджено!!!\r\n\r\n"+ content)
				.from("ВО \"Карпати\" (сервер)")
				.to(senderEmail);
	}
	
	private SendedMessage messageNegativeAnswerToSender(ArrayList<Employee> peopleWhoRejectIt, ArrayList<Employee> peopleWhoAcceptIt) {
		
		StringBuffer whoRejectItString = new StringBuffer();
		StringBuffer whoAcceptItString = new StringBuffer();
		
		
		for(Employee e : peopleWhoRejectIt) {
			whoRejectItString.append(e.getName() + "    " + e.getEmail() + "\r\n");
		}
		
		for(Employee e : peopleWhoAcceptIt) {
			whoAcceptItString.append(e.getName() + "    " + e.getEmail() + "\r\n");
		}
		
		return new SendedMessage("Відповідь","Ваш запит відхилено!\r\n\r\n"
				+ "Запит відхилили: \r\n"+ whoRejectItString + "\r\n\r\n" 
				+ "Запит погодили: \r\n" + whoAcceptItString + "\r\n\r\n" 
				+ "Керівники вищого рівня не отримують листа, якщо на нього була хоча б одна відмова\r\n\r\n"
				+ content)
				.from("ВО \"Карпати\" (сервер)")
				.to(senderEmail);
	}
	
	private SendedMessage sentErrorMessage(String errMessage) {
		return new SendedMessage( "Помилка відправлення!!!","Лист відповідь на запит " + senderEmail
				+ " не було відправлено\r\n"+ errMessage)
			.from("ВО \"Карпати\" (сервер)")
			.to(senderEmail);
	}
	
}

 

		
 
 
 
 
 

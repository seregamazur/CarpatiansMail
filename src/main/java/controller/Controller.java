package controller;

import client.authenticator.EmailAuthenticator.Gmail;
import client.core.GmailClient;
import client.core.common.ReceivedMessage;
import client.core.interfaces.IReceiver;
import employee.Employee;
import exceptionsLogger.ExceptionsLogger;
import letterLogic.CollectionSerializer;
import letterLogic.GarbageCollector;
import letterLogic.Letter;
import letterLogic.LetterType;
import parser.LetterTypeChecker;
import parser.Parser;
import parser.ParserJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

public class Controller {

    private Parser parser = new Parser();
    private ArrayList<Letter> letters;
    private ArrayList<Employee> employees;
    private static ExceptionsLogger logger = new ExceptionsLogger("D:/serverExceptions.log");
    private LetterTypeChecker letterTypeChecker = new LetterTypeChecker();
    private GarbageCollector garbageCollector = new GarbageCollector(letters);
    private CollectionSerializer collectionSerializer = new CollectionSerializer("D:/Letters.dat", logger);
    
    private static String bossEmail = "igor.bogdanovich39@gmail.com"; // like a boss
    private static String bossName = "Your boss";

    public static void main(String[] args) {

        Controller controller = new Controller();
        try {
        	controller.runServer();
        }
        catch(Exception e) {
       	 	logger.log(e);
        }
    }


    private void runServer() {
        employees = initializeEmployeesCollection();
        letters = collectionSerializer.readCollection();
        
        
        client().receive(new IReceiver.ReceiveCallback() {
            @Override
            public void onReceive(Set<ReceivedMessage> messages) {
                System.out.println("Received messages: " + messages
                        .stream()
                        .map(m ->  " => " + m.getDate())
                        .collect(Collectors.joining("\n"))
                );
                 
            }

            @Override
            public void onUpdate(ReceivedMessage message) {
                System.out.println("New message: " + message.getMessage() + " => " + message.getDate()); 
                if (letterTypeChecker.IsRequest(message.getSubject(),
                        message.getAttachment() != null) == LetterType.ANSWER) {
                    
                	try {
                		if(letterTypeChecker.isAnswerPositive(message.getMessage())) {
                			letterTypeChecker.getLetterById(letterTypeChecker.getLetterID(message.getMessage()),
                											letters).setAnswer(true, message.getFrom());
                		}
                		else {
                			letterTypeChecker.getLetterById(letterTypeChecker.getLetterID(message.getMessage()),
									letters).setAnswer(false, message.getFrom());
                		}
                	}
                	catch(IllegalArgumentException ise) {
                		new Letter(message.getFrom()).sentBadAnswerLetterTypeError();
                	}

                } else if (letterTypeChecker.IsRequest(message.getSubject(),
                        message.getAttachment()!= null) == LetterType.REQUEST) {
                    try {
						letters.add(new Letter(
								parser.parseXls(message.getAttachment()[0], employees),
								message.getFrom(),
								bossName,
								bossEmail,
								message.getMessage(),
								logger
								));
						
						collectionSerializer.saveCollection(letters);
					} 
                    catch(IOException ioe) {
                    	new Letter(message.getFrom()).badAttachmentFormat();
                    }
                    catch (Exception e) {
						logger.log(e);
					}
                } else {
                  new Letter(message.getFrom()).sentBadLetterTypeError();
                }
             
                garbageCollector.deleteNonRelevant();
            }

            @Override
            public void onError(MessagingException e) {
                logger.log(e);
            }
        });
    }


    private ArrayList<Employee> initializeEmployeesCollection() {

        ArrayList<Employee> employees = null;
        try {
            employees = new ParserJson().parseJSON("D:\\employees.json");
        } catch (Exception e) {
            logger.log(e);
        }
        return employees;
    }

    public GmailClient client() {
        GmailClient client = GmailClient.get()
                .loginWith(Gmail.auth("vokarpaty.server.mail@gmail.com", "vokarpatyIPZ"))
                .beforeLogin(() -> System.out.println("Login..."))
                .onLoginError(e -> logger.log(e))
                .onLoginSuccess(() -> System.out.println("Success login!"));
        return client.auth();
    }
}

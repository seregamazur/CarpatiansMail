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
	
	private static String configPath = "D:\\config.json";
	
	  
    private static String bossEmail;
    private static String bossName;
    private static String serverName;
    private static String serverEmail;
    private static String serverPassword;
    private static String JSON_Path;
    private static String DirPath;
    
    
    private Parser parser = new Parser();
    private ParserJson parserJSON = new ParserJson();
    private static ArrayList<Letter> letters;
    private ArrayList<Employee> employees;
    private static ExceptionsLogger logger;
    private LetterTypeChecker letterTypeChecker = new LetterTypeChecker();
    private static GarbageCollector garbageCollector;
    private CollectionSerializer collectionSerializer;
  


	public static void main(String[] args) {
    	
    	
    	
        Controller controller = new Controller();
        controller.InitializeConfig();
        logger = new ExceptionsLogger(DirPath + "serverExceptions.log");
        controller.collectionSerializer =  new CollectionSerializer(DirPath + "Letters.dat", logger);
        
        
        try {
        	controller.runServer();
        }
        catch(Exception e) {
       	 	logger.log(e);
        }
    }


    private void runServer() {
        employees = initializeEmployeesCollection();
        letters =  collectionSerializer.readCollection();
        garbageCollector = new GarbageCollector(letters);
        
        Letter.staticInitialization(serverName, bossEmail, bossName, logger, client());
        
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
								message.getMessage()
								));
						

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
				collectionSerializer.saveCollection(letters);
				System.out.println("Letters objects in system: " + letters.size());
            }

            @Override
            public void onError(MessagingException e) {
                logger.log(e);
            }
        });
    }

     private void InitializeConfig() {
    	String[] config = null;
    	try {
			config = parserJSON.getServerConfiguration(configPath);
		} catch (Exception e) {
			logger.log(e);
		}
    	bossEmail        =  config[0]; 
    	bossName         =  config[1]; 
    	serverName       =  config[2]; 
    	serverEmail      =  config[3]; 
    	serverPassword   =  config[4]; 
    	JSON_Path        =  config[5]; 
    	DirPath 	     =  config[6]; 
    }

    private ArrayList<Employee> initializeEmployeesCollection() {

        ArrayList<Employee> employees = null;
        try {
            employees = parserJSON.getEmoloyeesCollectionFromJSON(JSON_Path);
        } catch (Exception e) {
            logger.log(e);
        }
        return employees;
    }

    public GmailClient client() {
        GmailClient client = GmailClient.get()
                .loginWith(Gmail.auth(serverEmail, serverPassword))
                .beforeLogin(() -> System.out.println("Login..."))
                .onLoginError(e -> logger.log(e))
                .onLoginSuccess(() -> System.out.println("Success login!"));
        return client.auth();
    }
}

package controller;

import client.authenticator.EmailAuthenticator.Gmail;
import client.core.GmailClient;
import client.core.common.ReceivedMessage;
import client.core.interfaces.IReceiver;
import employee.Employee;
import exceptionsLogger.ExceptionsLogger;
import letterLogic.Letter;
import letterLogic.LetterType;
import parser.LetterTypeChecker;
import parser.Parser;
import parser.ParserJson;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class Controller {

    private Parser parser = new Parser();
    private ArrayList<Letter> letters = new ArrayList<>();
    private ArrayList<Employee> employees;
    private ExceptionsLogger logger = new ExceptionsLogger("D:/serverExceptions.log");
    private LetterTypeChecker letterTypeChecker = new LetterTypeChecker();


    public static void main(String[] args) {


        Controller controller = new Controller();
        controller.runServer();

    }


    private void runServer() {
        employees = initializeEmployeesCollection();
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
                if (letterTypeChecker.IsRequest(message.getMessage(),
                        message.getAttachment()==null) == LetterType.ANSWER) {
                    // **** or setAnswer to letter which id that same that in letter

                } else if (letterTypeChecker.IsRequest(message.getMessage(),
                        message.getAttachment()!=null) == LetterType.REQUEST) {
                    // **** parse xls create Letter objects and add it to letters
                } else {
                    sentBadLetterTypeError(message.getFrom());
                }
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
            employees = new ParserJson().parseJSON("C:\\Users\\����\\Desktop\\employees.json");
        } catch (Exception e) {
            logger.log(e);
        }
        return employees;
    }

    public GmailClient client() {
        GmailClient client = GmailClient.get()
                .loginWith(Gmail.auth("", ""))
                .beforeLogin(() -> System.out.println("Login..."))
                .onLoginError(e -> logger.log(e))
                .onLoginSuccess(() -> System.out.println("Success login!"));
        return client.auth();
    }
}

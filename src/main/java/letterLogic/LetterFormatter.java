package letterLogic;

import java.util.ArrayList;

import client.core.common.SendedMessage;
import employee.Employee;

public class LetterFormatter {

    String serverName;

    public LetterFormatter(String serverName) {
        this.serverName = serverName;
    }


    public SendedMessage messageTo(String eMail, String senderEmail, String content, String letterID) {
        return new SendedMessage("Запит", letterID + " - скопіюйте це і вставте першим словом у відповіді \r\n" +
                "Запит від " + senderEmail + "\r\n\r\n" + content)
                .from(serverName)
                .to(eMail.trim());
    }

    public SendedMessage messageToBoss(ArrayList<Employee> peopleWhoAcceptIt, String eMail, String senderEmail, String content, String letterID) {

        StringBuffer whoAcceptItString = new StringBuffer();

        for (Employee e : peopleWhoAcceptIt) {
            whoAcceptItString.append(e.getName() + "    " + e.getEmail() + "\r\n");
        }

        return new SendedMessage("Запит", letterID + " - скопіюйте це і вставте першим словом у відповіді \r\n" +
                "Запит від " + senderEmail + "\r\n\r\n" +
                "Запит погодили: \r\n" + (peopleWhoAcceptIt.size() > 0 ? whoAcceptItString :
                "(список пустий)")
                + "\r\n\r\n"
                + content)
                .from(serverName)
                .to(eMail.trim());
    }

    public SendedMessage messagePositiveAnswerToSender(String senderEmail, String content) {
        return new SendedMessage("Відповідь", "Ваш запит погоджено!!!\r\n\r\n" + content)
                .from(serverName)
                .to(senderEmail);
    }

    public SendedMessage messageNegativeAnswerToSender(ArrayList<Employee> peopleWhoRejectIt, ArrayList<Employee> peopleWhoAcceptIt, String senderEmail, String content) {

        StringBuffer whoRejectItString = new StringBuffer();
        StringBuffer whoAcceptItString = new StringBuffer();


        for (Employee e : peopleWhoRejectIt) {
            whoRejectItString.append(e.getName() + "    " + e.getEmail() + "\r\n");
        }

        for (Employee e : peopleWhoAcceptIt) {
            whoAcceptItString.append(e.getName() + "    " + e.getEmail() + "\r\n");
        }

        return new SendedMessage("Відповідь", "Ваш запит відхилено!\r\n\r\n"
                + ((peopleWhoRejectIt.size() > 0) ? "Запит відхилили: \r\n" + whoRejectItString + "\r\n\r\n" : "")
                + ((peopleWhoAcceptIt.size() > 0) ? "Запит погодили: \r\n" + whoAcceptItString + "\r\n\r\n" : "")
                + "Керівники вищого рівня не отримують листа, якщо на нього була хоча б одна відмова\r\n\r\n"
                + content)
                .from(serverName)
                .to(senderEmail);
    }

    public SendedMessage sentErrorMessage(String errMessage, String senderEmail) {
        return new SendedMessage("Помилка відправлення!!!", "Лист відповідь на запит " + senderEmail
                + " не було відправлено\r\n" + errMessage)
                .from(serverName)
                .to(senderEmail);
    }

}

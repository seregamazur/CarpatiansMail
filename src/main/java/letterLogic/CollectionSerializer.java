package letterLogic;

import exceptionsLogger.ExceptionsLogger;

import java.io.*;
import java.util.ArrayList;

public class CollectionSerializer {

    String fileName;
    ExceptionsLogger logger;

    public CollectionSerializer(String fileName, ExceptionsLogger logger) {
        this.fileName = fileName;
        this.logger = logger;
    }

    public void saveCollection(ArrayList<Letter> letters) {

        ObjectOutputStream oos = null;
        ObjectOutputStream oosb = null;

        try {
            oos = new ObjectOutputStream(new FileOutputStream(fileName + ".dat"));
            oos.writeObject(letters);
            oosb = new ObjectOutputStream(new FileOutputStream(fileName + "Backup.dat"));
            oosb.writeObject(letters);

        } catch (Exception ex) {
            logger.log(ex);
        } finally {
            if (oos != null) {
                try {
                    oos.flush();
                    oos.close();
                } catch (IOException e) {
                    logger.log(e);
                }
            }
            if (oosb != null) {
                try {
                    oosb.flush();
                    oosb.close();
                } catch (IOException e) {
                    logger.log(e);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public ArrayList<Letter> readCollection() {
        ArrayList<Letter> letters = new ArrayList<>();

        File file = new File(fileName + ".dat");
        File fileBackup = new File(fileName + "Backup.dat");

        if (file.exists() || fileBackup.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(fileName + ".dat"));
                letters = ((ArrayList<Letter>) ois.readObject());
            } catch (Exception ex) {
                try {
                    ois = new ObjectInputStream(new FileInputStream(fileName + "Backup.dat"));
                    letters = ((ArrayList<Letter>) ois.readObject());
                } catch (Exception e) {
                    logger.log(e);
                }
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return letters;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Letter> readBackupCollection() {
        ArrayList<Letter> letters = new ArrayList<>();

        File file = new File(fileName + "Backup.dat");

        if (file.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(fileName + "Backup.dat"));
                letters = ((ArrayList<Letter>) ois.readObject());
            } catch (Exception ex) {
                logger.log(ex);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return letters;
    }


}

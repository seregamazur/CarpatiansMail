package letterLogic;

import java.util.ArrayList;

public class GarbageCollector {

    ArrayList<Letter> letters;

    public GarbageCollector(ArrayList<Letter> letters) {
        this.letters = letters;
    }


    public void deleteNonRelevant() {
        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i).getCurrentGeneralLetterState() == LetterState.REJECTED ||
                    letters.get(i).getCurrentGeneralLetterState() == LetterState.ACCEPTED_BY_BOSS) {
                letters.remove(i);
            }
        }
    }
}

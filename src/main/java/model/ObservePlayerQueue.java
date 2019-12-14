package model;

public class ObservePlayerQueue implements Runnable {   // определение хода комьютера

    private Logic logic;

    public void run() {
        try {
            while (!logic.data.gameExit) {
                Thread.sleep(1000);
                if ((!logic.data.whiteIsHuman && logic.data.whiteSessionContinue && !logic.data.gameOver)
                        || (!logic.data.blackIsHuman && logic.data.blackSessionContinue && !logic.data.gameOver)) {
                    logic.compStep(logic.data);
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }

    public ObservePlayerQueue(Logic logic) {
        this.logic = logic;
    }
}
public class ArmyIdle extends Routine {
    private int waittime;

    public ArmyIdle(int time){
        super();
        waittime = time;
    }

    public void reset() {

    }

    public void act(ChalkBoard info) {
        waittime--;
        if (waittime < 0){
            System.out.println("ARMY: Waiting period over");
            succeed();
        }
    }
}

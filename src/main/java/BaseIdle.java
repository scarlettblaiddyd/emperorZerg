public class BaseIdle extends Routine {
    private int waittime;

    public BaseIdle(int time){
        super();
        waittime = time;
    }

    public void reset() {

    }

    public void act(ChalkBoard info) {
        waittime--;
        if (waittime < 0){
            System.out.println("BASE: Waiting period over");
            fail();
        }
    }
}

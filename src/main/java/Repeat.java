public class Repeat extends Routine {

    public final Routine routine;
    public final Selector selector;
    private int times;
    private int originalTimes;

    public Repeat(Routine routine) {
        super();
        this.routine = routine;
        this.selector = null;
        this.times = -1; // infinite
        this.originalTimes = times;
    }

    public Repeat(Selector selector) {
        super();
        this.selector = selector;
        this.routine = selector;
        this.times = -1; // infinite
        this.originalTimes = times;
    }

    public Repeat(Routine routine, int times) {
        super();
        if (times < 1) {
            throw new RuntimeException("Can't repeat negative times.");
        }
        this.routine = routine;
        this.selector = null;
        this.times = times;
        this.originalTimes = times;
    }

    public Repeat(Selector selector, int times) {
        super();
        if (times < 1) {
            throw new RuntimeException("Can't repeat negative times.");
        }
        this.selector = selector;
        this.routine = selector;
        this.times = times;
        this.originalTimes = times;
    }

    public void reset() {
        start();
    }

    @Override
    public void act(ChalkBoard info) {
        if(routine.isFailure()){
            fail();
        }
        else if(routine.isSuccess()){
            if(times == 0){
                succeed();
                return;
            }
            if(times > 0 || times <= -1){
                times--;
                routine.reset();
                routine.start();
            }
        }
        if (routine.isRunning()) {
            routine.act(info);
        }
    }
}
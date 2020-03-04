public class Repeat extends Routine {

    public  Routine routine;
    public  Selector selector;
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
        if(routine.state == null){
            routine.start();
            return;
        }
        if(routine.isFailure()){
            fail();
        }
        else if(routine.isSuccess()){
            System.out.println("Repetition completed");
            if(times == 0){
                System.out.println("Repeated the specified number of times, succeeding");
                succeed();
                return;
            }
            if(times > 0 || times <= -1){
                System.out.println("Have not repeated enough, or infinitely repeating");
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
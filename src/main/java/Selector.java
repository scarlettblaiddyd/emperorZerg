import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Selector extends Routine {
    public Selector() {
        super();
        this.currentRoutine = null;
    }

    private Routine currentRoutine;
    List<Routine> routines = new LinkedList<Routine>();
    Queue<Routine> routineQueue = new LinkedList<Routine>();

    public void addRoutine(Routine routine) {
        routines.add(routine);
    }

    @Override
    public void reset() {
        for (Routine routine : routines) {
            routine.reset();
        }
    }

    @Override
    public void start() {
        // start the current sequence
        super.start();
        // reset the current queue and copy the routines from setup
        routineQueue.clear();
        routineQueue.addAll(routines);
        currentRoutine = routineQueue.poll();
        currentRoutine.start();
    }

    public void act(ChalkBoard info){
        if (currentRoutine != null) {
            currentRoutine.act(info);
            // if is still running, then carry on
            if (currentRoutine.isRunning()) {
                return;
            }

            // check if the routine is successful and finish the sequence
            if (currentRoutine.isSuccess()) {
                System.out.println("Selector: " + currentRoutine.toString() + "is success");
                succeed();
                return;
            }

            // We need to progress the sequence. If there are no more routines
            // then the state is the last routine's state. (Success for OR was already handled)
            if (routineQueue.peek() == null) {
                this.state = currentRoutine.getState();
                System.out.println("SELECTOR: No more routines, final routine = " +currentRoutine.toString());
            }
            else {
                currentRoutine = routineQueue.poll();
                System.out.println("SELECTOR: polled: " + currentRoutine.toString());
                currentRoutine.start();
            }
        }
        else {
            start();
            System.out.println("SELECTOR: Starting routine: " + currentRoutine.toString());
        }

    }

}

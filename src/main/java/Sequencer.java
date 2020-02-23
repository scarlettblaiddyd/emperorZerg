import bwapi.Game;
import bwapi.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Sequencer extends Routine {
    public Sequencer() {
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

    public void act(Game game, Player self, enemyChalkBoard enemy){
        if (currentRoutine != null) {
            //game.drawTextScreen(10, 80, "current routine: " + currentRoutine.toString());
            currentRoutine.act(game, self, enemy);
            // if is still running, then carry on
            if (currentRoutine.isRunning()) {
                return;
            }

            if (currentRoutine.isSuccess()) {
                System.out.println(currentRoutine.toString() + "is success");
                //succeed();
            }

            // We need to progress the sequence. If there are no more routines
            // then the state is the last routine's state. (Success for OR was already handled)
            /*if (routineQueue.peek() == null) {
                this.state = currentRoutine.getState();
                return;
            }
             */
            if (routineQueue.peek() == null) {
                System.out.println("No more routines, final routine = " +currentRoutine.toString());
                this.state = currentRoutine.getState();
                if(currentRoutine.isSuccess()){
                    System.out.println("Final sucess, should kick out: " + currentRoutine.isSuccess());
                    succeed();
                }
                return;
            }
            else {
                currentRoutine = routineQueue.poll();
                System.out.println("polled: " + currentRoutine.toString());
                currentRoutine.start();
            }
        }
        else {
            start();
            System.out.println("Starting routine: " + currentRoutine.toString());
        }

    }

}


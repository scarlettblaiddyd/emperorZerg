import bwapi.Game;
import bwapi.Player;
import bwapi.Race;

public class Repeat extends Routine {

    private final Routine routine;
    private int times;
    private int originalTimes;

    public Repeat(Routine routine) {
        super();
        this.routine = routine;
        this.times = -1; // infinite
        this.originalTimes = times;
    }

    public Repeat(Routine routine, int times) {
        super();
        if (times < 1) {
            throw new RuntimeException("Can't repeat negative times.");
        }
        this.routine = routine;
        this.times = times;
        this.originalTimes = times;
    }


    public void reset() {
        start();
    }

    @Override
    public void act(Game game, Player self, enemyChalkBoard enemy) {
        if (isRunning()) {
            if (!game.isInGame()) {
                fail();
                return;
            }
            if (enemy.race == Race.Protoss ){
                //System.out.println("Facing protoss");
            }
            else if (enemy.race == Race.Terran){
                routine.act(game, self, enemy);
                //System.out.println("Facing Terran");
            }
            else if (enemy.race == Race.Zerg){
                //System.out.println("Facing Zerg");
            }
        }
    }
}
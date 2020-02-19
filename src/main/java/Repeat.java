import bwapi.Game;
import bwapi.Player;
import bwapi.Race;

public class Repeat extends Routine {

    private final Routine routine;
    private final Selector selector;
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
    public void act(Game game, Player self, enemyChalkBoard enemy) {
        if (isRunning()) {
            if (!game.isInGame()) {
                fail();
                return;
            }
            if (selector != null) {
                selector.addRoutine(new ZergStrat(game, self, enemy));
                selector.addRoutine(new ProtossStrat(game, self, enemy));
                selector.addRoutine(new TerranStrat(game, self, enemy));
                selector.act(game, self, enemy);
            }
        }
    }
}
import bwapi.Game;
import bwapi.Player;

public class BaseRepeat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Repeat repeater;

    public BaseRepeat(ChalkBoard info, Repeat repeater) {
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.repeater = repeater;
    }

    public BaseRepeat(Game game, Player self, enemyChalkBoard enemy, Repeat repeater) {
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
        this.repeater = repeater;
    }

    public void reset() {
        start();
    }

    @Override
    public void act(ChalkBoard info) {
        if (isRunning()) {
            if (!game.isInGame()) {
                fail();
                return;
            }
            if (repeater.selector != null) {
                repeater.selector.addRoutine(new ZergStrat(info));
                repeater.selector.addRoutine(new ProtossStrat(info));
                repeater.selector.addRoutine(new TerranStrat(info, new Sequencer()));
                repeater.selector.act(info);
            }
        }
    }
}
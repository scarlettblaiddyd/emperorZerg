import bwapi.Game;
import bwapi.Player;
import bwapi.UnitType;

public class BaseRepeat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Repeat repeater;
    private Selector selector;

    public BaseRepeat(ChalkBoard info) {
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }

    public void start(ChalkBoard info){
        if(selector == null){
            selector = new Selector();
            System.out.println("BASE: Creating new selector for base repeater");
        }
        selector.addRoutine(new ZergStrat(info, new Selector()));
        selector.addRoutine(new ProtossStrat(info, new Selector()));
        selector.addRoutine(new TerranStrat(info, new Selector()));
        this.state = RoutineState.Running;
    }

    public void reset() {
        System.out.println("BASE: Resetting base repeat");
        this.selector = null;
        this.state = null;
        super.start();
    }

    @Override
    public void act(ChalkBoard info) {
        if(selector == null){
            this.start(info);
            selector.start();
            return;
        }
        if (selector.isRunning()) {
            if (!game.isInGame()) {
                fail();
                return;
            }
            selector.act(info);
        }
        else if (selector.isSuccess()){
            succeed();
            System.out.println("BASE: Base repeater has succeeded");
            this.reset();
        }
        else if (selector.isFailure()){
            fail();
            System.out.println("BASE: Base repeater has failed");
            this.reset();
        }
    }
}
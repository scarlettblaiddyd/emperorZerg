import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class ArmyRepeat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Selector selector;

    public ArmyRepeat(ChalkBoard info) {
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }

    public void start(ChalkBoard info){
        System.out.println("ARMY: Starting new routine in ArmyRepeat");
        if(selector == null){
            selector = new Selector();
            System.out.println("AMRY: Creating new selector for army repeater");
        }
        selector.addRoutine(new ScoutEnemy(info));
        selector.addRoutine(new ArmyIdle(100));
        if(info.pcb.playstyle == Playstyle.OFFENSIVE){
            selector.addRoutine(new OffensiveArmy(info));
        }
        this.state = RoutineState.Running;
    }

    public void reset() {
        System.out.println("ARMY: Resetting army repeat");
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
        else if(selector.isSuccess()){
            succeed();
            System.out.println("ARMY: Army repeater has succeeded");
            this.reset();
        }
        else if(selector.isFailure()){
            fail();
            System.out.println("ARMY: Army repeater has failed");
            this.reset();
        }
        else{
            selector.start();
        }
    }
}
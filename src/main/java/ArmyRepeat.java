import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class ArmyRepeat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Routine routine;

    public ArmyRepeat(ChalkBoard info) {
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }

    public void start(ChalkBoard info){
        System.out.println("ARMY: Starting new routine in ArmyRepeat");
        if(!info.pcb.buildOrderComplete && !info.ecb.buildTypes.contains(UnitType.Terran_Command_Center)){
            System.out.println("ARMY: Setting routine to scouting");
            this.routine = new ScoutEnemy(info);
        }
        else if (info.pcb.buildOrderComplete && info.ecb.buildTypes.contains(UnitType.Terran_Command_Center)){
            System.out.println("ARMY: Setting routing to Zergling Rush");
            this.routine = new ZerglingRush(info, 6);
        }
        else{
            // Just wait
            System.out.println("ARMY: Army idling");
            this.routine = new ArmyIdle(200);
        }
        this.state = RoutineState.Running;
    }

    public void reset() {
        System.out.println("ARMY: Resetting army repeat");
        this.routine = null;
        this.state = null;
        super.start();
    }

    @Override
    public void act(ChalkBoard info) {
        if(routine == null){
            this.start(info);
            routine.start();
            return;
        }
        if (routine.isRunning()) {
            if (!game.isInGame()) {
                fail();
                return;
            }
            routine.act(info);
        }
        else if(routine.isSuccess()){
            succeed();
        }
        else if(routine.isFailure()){
            fail();
        }
        else{
            routine.start();
        }
    }
}
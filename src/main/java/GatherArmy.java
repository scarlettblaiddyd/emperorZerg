import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;

public class GatherArmy extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Position target = null;

    public void reset() {

    }

    public void start(){
        super.start();
    }

    public GatherArmy(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }
    public void act(ChalkBoard info) {
        // Check if any units are currently on the move
        Unit rally = null;
        for(Unit unit: info.pcb.army){
            if(!unit.isIdle()){
                if(unit.isAttacking() || unit.isUnderAttack()){
                    rally = unit;
                    break;
                }
            }
        }
        if(rally == null){
            fail();
            System.out.println("ARMY: No units to rally army to");
            return;
        }
        target = rally.getPosition();
        for(Unit unit: info.pcb.army){
            if(unit.isIdle()) {
                unit.move(target);
                System.out.println("ARMY: Sending one " + unit.getType().toString() + " that is idle to the frontlines");
            }
        }
        System.out.println("ARMY: Rallied units to target position");
        succeed();
    }
}

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;

public class AttackVisible extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit target;
    public void reset() {

    }

    public AttackVisible(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.target = null;
    }

    public void start(ChalkBoard info){
        for(Unit unit: enemy.army){
            if(unit.isVisible(self)){
                target = unit;
            }
        }
    }

    public void act(ChalkBoard info) {
        if(target == null){
            start(info);
            if(target == null){
                fail();
                System.out.println("ARMY: No visible units to attack");
                return;
            }
        }
        boolean attacked = false;
        for(Unit unit: info.pcb.army){
            if(unit.isIdle() || unit.isMoving()){
                System.out.println("ARMY: Moving or idle unit found. Attacking visible enemy");
                unit.attack(target);
                attacked = true;
            }
        }
        if(attacked)
            succeed();
        else
            fail();
    }
}

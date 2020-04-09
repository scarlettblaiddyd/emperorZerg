import bwapi.*;
import javafx.geometry.Pos;

public class UnitDefendBase extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit target = null;
    private final UnitType type;
    public void reset() {

    }

    public UnitDefendBase(ChalkBoard info, UnitType type){
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.type = type;
    }

    public void start(ChalkBoard info){
        if(target == null) {
            for (Unit enemy : info.ecb.army) {
                if (enemy.isVisible(self)) {
                    for (Unit building : info.pcb.buildings) {
                        if (enemy.getDistance(building) < 1000) {
                            target = enemy;
                        }
                    }
                }
            }
        }
    }

    public void act(ChalkBoard info) {
        if(target == null)
            start(info);
        boolean sent = false;
        for(Unit unit: info.pcb.army) {
            if(!unit.isAttacking() && unit.getType() == type && unit.isIdle()){
                unit.attack(target);
                sent = true;
            }
        }
        if(sent) {
            System.out.println("Ordered " + type + " to defend.");
            succeed();
        }
        else
            fail();
    }
}

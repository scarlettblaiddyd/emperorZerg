import bwapi.*;
import javafx.geometry.Pos;

public class PatrolBase extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Position target = null;
    private Position target1 = null;
    public void reset() {

    }

    public PatrolBase(ChalkBoard info){
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }

    public void start(ChalkBoard info){
        for(Unit unit: info.pcb.buildings){
            if(target == null)
                target = unit.getPosition();
            else{
                if (target.getDistance(self.getStartLocation().toPosition()) < unit.getDistance(self.getStartLocation().toPosition())) {
                    target = unit.getPosition();
                }
            }
        }
        for(Unit unit: info.pcb.buildings){
            if(target1 == null)
                target1 = unit.getPosition();
            else{
                if (target1.getDistance(target) < unit.getDistance(target)) {
                    target1 = unit.getPosition();
                }
            }
        }
    }

    public void act(ChalkBoard info) {
        if(target == null)
            start(info);
        boolean setpatrol = false;
        for(Unit unit: info.pcb.army){
            if(unit.getDistance(target) < 200 && unit.isIdle() && unit.getType() == UnitType.Zerg_Zergling){
                unit.attack(target1, false);
                setpatrol = true;
            }
            else if(unit.getDistance(target1) < 200 && unit.isIdle() && unit.getType() == UnitType.Zerg_Zergling){
                unit.attack(target, false);
                setpatrol = true;
            }
        }
        if(setpatrol)
            succeed();
        else
            fail();
        return;
    }
}

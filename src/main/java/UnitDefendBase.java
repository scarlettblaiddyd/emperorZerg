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
            for(Unit building: info.pcb.buildings){
                if(building.getType() == UnitType.Zerg_Sunken_Colony){
                    System.out.println("ARMY: Found sunken colony to defend");
                    target = building;
                    break;
                }
            }
            if(target == null)
            {
                System.out.println("ARMY: No sunken colony found, defending random building");
                target = info.pcb.buildings.getFirst();
            }
            for(Unit unit: info.pcb.army){
                if(unit.getType() == type)
                    unit.move(target.getPosition());
            }
        }
    }

    public void act(ChalkBoard info) {
        if(target == null)
            start(info);
        boolean sent = false;
        for(Unit unit: info.pcb.army) {
            if(unit.getDistance(target) > 200){
                unit.move(target.getPosition());
            }
            else{
                for(Unit enemy : game.getUnitsInRadius(target.getPosition(), 200)){
                    if(enemy.getPlayer() != self) {
                        unit.attack(enemy.getPosition());
                        sent = true;
                    }
                }
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

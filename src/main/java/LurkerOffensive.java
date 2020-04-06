import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class LurkerOffensive extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit enemyUnit;
    public void reset() {

    }

    public LurkerOffensive(ChalkBoard info){
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.enemyUnit = null;
    }

    public void start(ChalkBoard info){
        for(Unit unit: info.ecb.army){
            if(unit.isVisible(self)){
                this.enemyUnit = unit;
            }
        }
        for(Unit unit: info.ecb.buildings){
            if(unit.isVisible(self)){
                enemyUnit = unit;
            }
        }
    }

    public void act(ChalkBoard info) {
       if(enemyUnit == null){
           start();
       }
       if(enemyUnit == null){
           fail();
           System.out.println("ARMY: No enemy found for Lurker to attack");
       }
       for(Unit lurker: info.pcb.army){
           if(lurker.getType() == UnitType.Zerg_Lurker){
               if(lurker.getDistance(enemyUnit) <= 150){
                   lurker.burrow();
                   lurker.attack(enemyUnit, true);
                   System.out.println("ARMY: Lurker told to burrow and attack");
                   succeed();
                   return;
               }
               lurker.move(enemyUnit.getPosition());
               break;
           }
       }
       fail();
    }
}

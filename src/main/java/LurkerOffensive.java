import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class LurkerOffensive extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit enemyUnit;
    private boolean ordered;

    public void reset() {

    }

    public LurkerOffensive(ChalkBoard info){
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.enemyUnit = null;
    }

    public void start(ChalkBoard info){
        super.start();
        ordered = false;
        enemyUnit = null;
    }

    public void act(ChalkBoard info) {
       start(info);

        for(Unit lurker: info.pcb.army){
           // only apply to our lurkers
           if(lurker.getType() == UnitType.Zerg_Lurker){
               // For our lurkers, find closest enemy combatant
               for(Unit unit: info.ecb.army){
                   if(unit.isVisible(self)){
                       if(enemyUnit == null)
                           enemyUnit = unit;
                       else if(lurker.getDistance(unit) < lurker.getDistance(enemyUnit))
                           enemyUnit = unit;
                   }
               }
               // or building of no combatant is available
               if(enemyUnit == null) {
                   for (Unit unit : info.ecb.buildings) {
                       if (unit.isVisible(self)) {
                           if (enemyUnit == null)
                               enemyUnit = unit;
                           else if (lurker.getDistance(unit) < lurker.getDistance(enemyUnit))
                               enemyUnit = unit;
                       }
                   }
               }
               // If no buildings or army units are visible, fail
               if(enemyUnit == null){
                   fail();
                   System.out.println("ARMY: No enemy found for Lurker to attack");
                   return;
               }

               if(lurker.getDistance(enemyUnit) <= 192){
                   if(lurker.isAttacking())
                       continue;
                   lurker.burrow();
                   System.out.println("ARMY: Lurker told to burrow");
                   ordered = true;
                   continue;
               }
               else if (lurker.isBurrowed()){
                   lurker.unburrow();
               }
               lurker.move(enemyUnit.getPosition());
               ordered = true;
           }
       }
       if(!ordered)
           fail();
       else
           succeed();
    }
}

import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class GenericUnitOffensive extends Routine{
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private final UnitType type;
    private Unit enemyUnit;
    private boolean ordered;

    public void reset() {

    }

    public GenericUnitOffensive(ChalkBoard info, UnitType type){
        System.out.println("ARMY: Trying to attack with all " + type);
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.enemyUnit = null;
        this.type = type;
    }

    public void start(ChalkBoard info){
        super.start();
        ordered = false;
        enemyUnit = null;
    }

    public void act(ChalkBoard info) {
        start(info);

        for(Unit attacker: info.pcb.army){
            // Only apply to our attackers
            if(attacker.getType() == type){
                // Find closest enemy combatant
                for(Unit unit: info.ecb.army){
                    if(unit.isVisible(self)){
                        if(enemyUnit == null)
                            enemyUnit = unit;
                        else if(attacker.getDistance(unit) < attacker.getDistance(enemyUnit))
                            enemyUnit = unit;
                    }
                }
                // or building of no combatant is available
                if(enemyUnit == null) {
                    for (Unit unit : info.ecb.buildings) {
                        if(unit.getType() == UnitType.Resource_Vespene_Geyser)
                            continue;
                        if (unit.isVisible(self) && unit.exists()) {
                            if (enemyUnit == null)
                                enemyUnit = unit;
                            else if (attacker.getDistance(unit) < attacker.getDistance(enemyUnit))
                                enemyUnit = unit;
                        }
                    }
                }
                // If no buildings or army units are visible, fail
                if(enemyUnit == null){
                    fail();
                    System.out.println("ARMY: No enemy found for attacker to attack");
                    return;
                }
                System.out.print("ARMY: Enemy found to target: " + enemyUnit.getType() + " ");

                // Otherwise, attack!
                if(attacker.isAttacking()) {
                    System.out.print(attacker.getType() + " attacking. ");
                    continue;
                }
                else {
                    attacker.attack(enemyUnit.getPosition());
                    ordered = true;
                    System.out.print(type + " told to attack " + enemyUnit.getType());
                }
            }


        }
        /*
        if(!ordered)
            fail();
        else
            succeed();
         */
        System.out.println("");
        fail();
    }
}

import bwapi.*;

import java.util.LinkedList;

public class ScoutEnemy extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private LinkedList<TilePosition> startLocations;
    private Unit scout;
    private Position moveTo = null;

    public void reset() {

    }

    public void start(){
        super.start();
    }

    public ScoutEnemy(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.scout = info.pcb.scout;
        this.startLocations = new LinkedList<TilePosition>(game.getStartLocations());
    }

    public ScoutEnemy(Game game, Player self, enemyChalkBoard enemy){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
        this.startLocations = new LinkedList<TilePosition>(game.getStartLocations());
    }

    public void act(ChalkBoard info) {
        // First, make sure we don't already have the enemies base location
        if(enemy.basePos.size() > 0){
            fail();
            return;
        }
        if(this.startLocations.contains(self.getStartLocation())){
            System.out.println("ARMY: Removing our start location from the list of possible locations");
            if( this.startLocations.remove(self.getStartLocation()) ){
                System.out.println("ARMY: Successfully removed");
            }
            else{
                System.out.println("ARMY: Failed to remove");
            }
        }
        if (scout == null){
            System.out.println("ARMY: Looking for a unit to turn into a scout");
            for(Unit unit : self.getUnits()){
                UnitType unitType = unit.getType();
                if(unitType.isWorker() && !unit.isCarryingMinerals() && !unit.isCarryingGas()){
                    System.out.println("ARMY: Found a unit to turn into scout");
                    scout = unit;
                    info.pcb.scout = unit;
                    scout.stop();
                    break;
                }
            }
        }
        if((enemy.buildTypes.contains(UnitType.Terran_Command_Center) || enemy.buildTypes.contains(UnitType.Protoss_Nexus) || enemy.buildTypes.contains(UnitType.Zerg_Hatchery) ) && moveTo != null){
            System.out.println("ARMY: Found enemy base at: " + moveTo.toString());
            enemy.basePos.add(moveTo);
            scout.move(self.getStartLocation().toPosition());
            info.pcb.scout = null;
            succeed();
            return;
        }
        if(scout != null && scout.isIdle()){
            moveTo = startLocations.poll().toPosition();
            scout.move(moveTo);
        }
    }
}

import bwapi.*;

import java.util.LinkedList;

public class ScoutEnemy extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private LinkedList<TilePosition> startLocations;
    private Unit scout = null;
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
        if(this.startLocations.contains(self.getStartLocation())){
            System.out.println("Removing our start location from the list of possible locations");
            if( this.startLocations.remove(self.getStartLocation()) ){
                System.out.println("Successfully removed");
            }
            else{
                System.out.println("Failed to remove");
            }
        }
        if (scout == null){
            System.out.println("Looking for a unit to turn into a scout");
            for(Unit unit : self.getUnits()){
                UnitType unitType = unit.getType();
                if(unitType.isWorker()){
                    System.out.println("Found a unit to turn into scout");
                    scout = unit;
                    scout.stop();
                    break;
                }
            }
        }
        if(enemy.buildings.contains(UnitType.Terran_Command_Center) && moveTo != null){
            System.out.println("Found enemy base at: " + moveTo.toString());
            enemy.basePos.add(moveTo);
            scout.move(self.getStartLocation().toPosition());
            succeed();
            return;
        }
        if(scout != null && scout.isIdle()){
            moveTo = startLocations.poll().toPosition();
            scout.move(moveTo);
        }
    }
}

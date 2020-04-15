import bwapi.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class ScoutBase extends Routine{

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
        this.region = game.getRegionAt(enemy.basePos.getFirst());
    }

    public ScoutEnemy(Game game, Player self, enemyChalkBoard enemy){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
        this.region = game.getRegionAt(enemy.basePos.getFirst());
    }

    public void act(ChalkBoard info) {
        if(enemy.basePos.size() > 0){
            //ArrayList<Unit> closeEnemies = new ArrayList<>();
            for(Unit u : region.getUnits()){
                if(u.isWorker() || u.isBuilding()){
                    continue;
                }
                else if(u.getDistance(scout.getPosition()) <= 25){
                    //closeEnemies.add(u);
                    ScoutRetreat(info);
                    succeed();
                    return;
                }
            }
            /*if(!closeEnemies.isEmpty()){
                ScoutRetreat(info);
                succeed();
                return;
            }*/
            scout.patrol(enemy.basePos);
            succeed();
            return;
        } // Should only be called if base is found
        else{
            fail();
            return;
        }
    }
}

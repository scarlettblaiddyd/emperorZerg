import bwapi.*;

import java.util.LinkedList;

public class ScoutRetreat extends Routine{
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit scout;
    private ArrayList<Unit> enemyList;

    public void reset() {

    }

    public void start(){
        super.start();
    }

    public ScoutRetreat(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.scout = info.pcb.scout;
    }

    public ScoutRetreat(Game game, Player self, enemyChalkBoard enemy{
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
    }

    public void act(ChalkBoard info) {
        scout.move(self.basePos);
        succeed();
        return;
    }
}

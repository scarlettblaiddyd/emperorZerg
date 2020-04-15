import bwapi.*;

import java.util.LinkedList;

public class ScoutRetreat extends Routine{
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit scout;
    private Position moveTo = null;
    private final Position patrolStart;
    private final Position patrolEnd;

    public void reset() {

    }

    public void start(){
        super.start();
    }

    public ScoutRetreat(ChalkBoard info, Position pStart, Position pEnd){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.scout = info.pcb.scout;
        this.patrolStart = pStart;
        this.patrolEnd = pEnd;
    }

    public ScoutRetreat(Game game, Player self, enemyChalkBoard enemy, Position pStart, Position pEnd){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
        this.patrolStart = pStart;
        this.patrolEnd = pEnd;
    }

    public void act(ChalkBoard info) {

    }
}

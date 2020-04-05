import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;

public class ArmyRetreat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    public void reset() {

    }

    public ArmyRetreat(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }

    public void act(ChalkBoard info) {
        boolean retreated = false;
        for(Unit unit: info.pcb.army){
            if(unit.getDistance(self.getStartLocation().toPosition())  > 500){
                unit.move(self.getStartLocation().toPosition());
                retreated = true;
            }
        }
        if(retreated) {
            System.out.println("ARMY: Order to retreat issued");
            succeed();
        }
        else
            fail();
    }
}

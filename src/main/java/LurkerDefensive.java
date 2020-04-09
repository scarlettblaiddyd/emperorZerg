import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class LurkerDefensive extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit sunken;

    public void reset() {

    }

    public LurkerDefensive(ChalkBoard info){
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.sunken = null;
    }

    public void start(ChalkBoard info){
        start();
        for(Unit colony: info.pcb.buildings){
            if(colony.getType() == UnitType.Zerg_Sunken_Colony) {
                System.out.println("ARMY: Colony found for lurkers to defend");
                this.sunken = colony;
            }
        }
    }

    public void act(ChalkBoard info) {
        if(this.sunken == null){
            this.start(info);
        }
        if(sunken == null)
        {
            fail();
            System.out.println("ARMY: No colony found for Lurker to defend");
            return;
        }
        for(Unit lurker: info.pcb.army){
            if(lurker.getType() == UnitType.Zerg_Lurker){
                if(lurker.getDistance(sunken) < 30){
                    lurker.burrow();
                    System.out.println("ARMY: Ordered Lurker to burrow near colony");
                    succeed();
                }
                lurker.move(sunken.getPosition());
            }
        }
        fail();
    }
}

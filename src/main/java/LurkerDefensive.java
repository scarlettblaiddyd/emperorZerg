import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class LurkerDefensive extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit sunken;
    private boolean ordered;

    public void reset() {

    }

    public LurkerDefensive(ChalkBoard info){
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.sunken = null;
        ordered = false;
    }

    public void start(ChalkBoard info){
        start();
        sunken = null;
        ordered = false;
    }

    public void act(ChalkBoard info) {
        this.start(info);

        for(Unit lurker: info.pcb.army){
            // Selectively order lurkers
            if(lurker.getType() == UnitType.Zerg_Lurker && lurker.isIdle()){
                // Search for a colony for the lurker to defend
                for(Unit colony: info.pcb.buildings){
                    if(colony.getType() == UnitType.Zerg_Sunken_Colony) {
                        if(sunken == null){
                            System.out.println("ARMY: Colony found for lurker to defend");
                            this.sunken = colony;
                        }
                        else if (lurker.getDistance(colony) < lurker.getDistance(sunken)){
                                System.out.println("ARMY: Closer colony found for lurker to defend");
                                this.sunken = colony;
                            }
                        }
                }
                if(sunken == null){
                    for(Unit building: info.pcb.buildings){
                        System.out.println("ARMY: No colony found to fortify, lurkers instead defending nearest building");
                        if(sunken == null)
                            this.sunken = building;
                        else if(lurker.getDistance(building) < lurker.getDistance(sunken))
                            this.sunken = building;
                    }
                }

                // If the lurker is close enough, burrow
                if(lurker.getDistance(sunken) < 30 && !lurker.isBurrowed()){
                    lurker.burrow();
                    System.out.println("ARMY: Ordered Lurker to burrow near colony");
                    ordered = true;
                }
                else if (lurker.isIdle() && lurker.getDistance(sunken) > 30){
                    if(lurker.isBurrowed())
                        lurker.unburrow();
                    System.out.println("ARMY: Lurker not close enough to burrow, moving closer");
                    lurker.move(sunken.getPosition());
                    ordered = true;
                }
            }
        }
        if(ordered) {
            System.out.println("SUCCESS");
            succeed();
        }
        else
            fail();
    }
}

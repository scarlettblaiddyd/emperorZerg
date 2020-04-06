import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

import java.lang.reflect.ParameterizedType;

public class DefensiveArmy extends Routine {
    private final ChalkBoard info;
    private Selector selector;


    public void reset() {
        this.selector = null;
        this.state = null;
    }

    public void start(ChalkBoard info){
        super.start();
        this.selector = new Selector();

        selector.addRoutine(new ArmyRetreat(info));

        /*
        boolean idle = false;
        for(Unit unit: info.pcb.army){
            if(unit.isIdle() && unit.getType() == UnitType.Zerg_Zergling) {
                idle = true;
                break;
            }
        }
        if(idle)
            selector.addRoutine(new PatrolBase(info));
        */
        selector.addRoutine(new LurkerDefensive(info));
        selector.addRoutine(new UnitDefendBase(info, UnitType.Zerg_Zergling));
        selector.addRoutine(new UnitDefendBase(info, UnitType.Zerg_Hydralisk));
        //selector.addRoutine(new GatherArmy(info));

        this.state = RoutineState.Running;
        selector.start();
    }

    public DefensiveArmy(ChalkBoard info){
        super();
        this.info = info;
    }

    public void act(ChalkBoard info) {
        // Check if any enemy units are visible (which would indicate that we are engaged in combat)
        if (selector == null){
            this.start(info);
            return;
        }
        if(selector.isRunning()){
            selector.act(info);
        }
        else if(selector.isSuccess()){
            succeed();
            System.out.println("ARMY: Defensive strategy deployed");
        }
        else if(selector.isFailure()){
            fail();
            System.out.println("ARMY: Failed to issue defensive orders");
        }
    }
}

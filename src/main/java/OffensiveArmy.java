import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class OffensiveArmy extends Routine {
    private final ChalkBoard info;
    private Selector selector;


    public void reset() {
        this.selector = null;
        this.state = null;
    }

    public void start(){
        super.start();
        this.selector = new Selector();
        for(Unit unit: info.ecb.army){
            if(unit.isVisible(info.pcb.self)){
                selector.addRoutine(new RunSimulation(info));
                System.out.println("ARMY: Enemy visible, running combat simulation");
                break;
            }
        }
        // Check if we have idle units?
        int idleZerglings = 0;
        for(Unit unit: info.pcb.army){
            if(unit.getType() == UnitType.Zerg_Zergling && unit.isIdle()){
                idleZerglings += 1;
            }
        }
        if(idleZerglings >= 8) {
            selector.addRoutine(new ZerglingRush(info, idleZerglings));
            System.out.println("ARMY: Enough idle zerglings to justify a rush");
        }

        selector.addRoutine(new GatherArmy(info));

        this.state = RoutineState.Running;
        selector.start();
    }

    public OffensiveArmy(ChalkBoard info){
        super();
        this.info = info;
    }

    public void act(ChalkBoard info) {
        // Check if any enemy units are visible (which would indicate that we are engaged in combat)
        if (selector == null){
            this.start();
            return;
        }
        if(selector.isRunning()){
            selector.act(info);
        }
        else if(selector.isSuccess()){
            succeed();
            System.out.println("ARMY: Offensive strategy deployed");
        }
        else if(selector.isFailure()){
            fail();
            System.out.println("ARMY: Failed to issue offensive orders");
        }
    }
}

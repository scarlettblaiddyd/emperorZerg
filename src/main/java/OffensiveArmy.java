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
                System.out.println("ARMY: Enemy visible, running combat simulation");
                selector.addRoutine(new AssSimulator(info));
                break;
            }
        }
        // Check if we have idle units?
        int idleZerglings = 0;
        int idleHydralisks = 0;
        for(Unit unit: info.pcb.army){
            if(unit.getType() == UnitType.Zerg_Zergling && unit.isIdle()){
                idleZerglings += 1;
            }
            else if(unit.getType() == UnitType.Zerg_Hydralisk && unit.isIdle()){
                idleHydralisks++;
            }
        }
        if(idleZerglings >= 6) {
            selector.addRoutine(new ZerglingRush(info, idleZerglings));
            System.out.println("ARMY: Enough idle zerglings to justify a rush");
        }
        else if(idleZerglings > 0) {
            System.out.println("ARMY: Idle zerglings in army");
            selector.addRoutine(new GenericUnitOffensive(info, UnitType.Zerg_Zergling));
        }
        if(idleHydralisks > 0) {
            System.out.println("ARMY: Idle Hydralisks in army");
            selector.addRoutine(new GenericUnitOffensive(info, UnitType.Zerg_Hydralisk));
        }
        selector.addRoutine(new LurkerOffensive(info));
        //selector.addRoutine(new AttackVisible(info));
        //selector.addRoutine(new GatherArmy(info));

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

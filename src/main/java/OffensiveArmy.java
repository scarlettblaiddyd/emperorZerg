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
        int retreatingZerglings = 0;
        int idleHydralisks = 0;
        int retreatingHydralisks = 0;
        int idleUltralisks = 0;
        int retreatingUltralisks = 0;
        int idleMutalisks = 0;
        int retreatingMutalisks = 0;
        for(Unit unit: info.pcb.army){
            if(unit.getType() == UnitType.Zerg_Zergling){
                if(unit.isIdle())
                    idleZerglings += 1;
                if(unit.getTargetPosition().getDistance(info.pcb.self.getStartLocation().toPosition()) < 500)
                    retreatingZerglings++;
            }
            else if(unit.getType() == UnitType.Zerg_Hydralisk){
                if(unit.isIdle())
                    idleHydralisks++;
                if(unit.getTargetPosition().getDistance(info.pcb.self.getStartLocation().toPosition()) < 500)
                    retreatingHydralisks++;
            }
            else if(unit.getType() == UnitType.Zerg_Ultralisk){
                if(unit.isIdle())
                    idleUltralisks++;
                if(unit.getTargetPosition().getDistance(info.pcb.self.getStartLocation().toPosition()) < 500)
                    retreatingUltralisks++;
            }
            else if(unit.getType() == UnitType.Zerg_Mutalisk){
                if(unit.isIdle())
                    idleMutalisks++;
                if(unit.getTargetPosition().getDistance(info.pcb.self.getStartLocation().toPosition()) < 500)
                    retreatingMutalisks++;
            }
        }
        if(idleZerglings >= 6) {
            selector.addRoutine(new ZerglingRush(info, idleZerglings));
            System.out.println("ARMY: Enough idle zerglings to justify a rush");
        }
        else if(idleZerglings + retreatingZerglings > 0) {
            System.out.println("ARMY: Idle/retreating zerglings in army");
            selector.addRoutine(new GenericUnitOffensive(info, UnitType.Zerg_Zergling));
        }
        if(idleHydralisks + retreatingHydralisks > 0) {
            System.out.println("ARMY: Idle/retreating Hydralisks in army");
            selector.addRoutine(new GenericUnitOffensive(info, UnitType.Zerg_Hydralisk));
        }
        if(idleUltralisks + retreatingUltralisks > 0){
            System.out.println("ARMY: Idle/retreating Ultralisks in army");
            selector.addRoutine(new GenericUnitOffensive(info, UnitType.Zerg_Ultralisk));
        }
        if(idleMutalisks + retreatingMutalisks > 0){
            System.out.println("ARMY: Idle/retreating Mutalisks in army");
            selector.addRoutine(new GenericUnitOffensive(info, UnitType.Zerg_Mutalisk));
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

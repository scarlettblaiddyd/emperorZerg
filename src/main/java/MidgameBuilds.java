import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class MidgameBuilds extends Routine{
    private final ChalkBoard info;
    private Selector selector;

    public MidgameBuilds(ChalkBoard info){
        super();
        this.info = info;
        this.selector = null;
    }

    public void start(){
        super.start();
        System.out.println("Starting midgame builds routine");
        this.selector = new Selector();
        this.selector.addRoutine(new BaseIdle(50));
        this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Metabolic_Boost,1));
        if(!info.pcb.buildTypes.contains(UnitType.Zerg_Lair)) {
            this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Lair, 1));
        }
        int hatches = 0;
        for(Unit unit: info.pcb.self.getUnits()){
            if(unit.getType() == UnitType.Zerg_Hatchery || unit.getType() == UnitType.Zerg_Lair){
                hatches++;
            }
        }
        if(hatches < 2) {
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Hatchery, false));
        }
        this.state = RoutineState.Running;
        selector.start();
    }

    public void reset() {
        this.state = null;
        this.selector = null;
    }

    public void act(ChalkBoard info) {
        if (selector == null){
            this.start();
            return;
        }
        if(selector.isRunning()){
            selector.act(info);
        }
        else if(selector.isSuccess()){
            succeed();
            System.out.println("BASE: Midgame orders successful");
        }
        else if(selector.isFailure()){
            fail();
            System.out.println("BASE: Failed to issue midgame orders");
        }
    }
}

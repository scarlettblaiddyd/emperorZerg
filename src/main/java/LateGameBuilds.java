import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

import javax.swing.*;

public class LateGameBuilds extends Routine {
    private Selector selector;
    private final ChalkBoard info;

    public void reset() {

    }

    public LateGameBuilds(ChalkBoard info){
        super();
        this.info = info;
        this.selector = null;
    }

    public void start(){
        super.start();
        System.out.println("BASE: Starting lategame builds routine");
        this.selector = new Selector();

        int creeps = 0;
        int spores = 0;
        int sunken = 0;
        for(UnitType builds: info.pcb.buildTypes){
            if(builds == UnitType.Zerg_Sunken_Colony){
                sunken++;
            }
            else if(builds == UnitType.Zerg_Spore_Colony){
                spores++;
            }
            else if(builds == UnitType.Zerg_Creep_Colony){
                creeps++;
            }
        }

        if(creeps > 0 && spores < 3) {
            this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Spore_Colony, 1, true));
        }
        else if(creeps + spores + sunken < 7) {
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Creep_Colony, true, false));
        }

        int ults = 0;
        int mutas = 0;
        for(Unit unit : info.pcb.self.getUnits()){
            UnitType unitType = unit.getType();
            if(unitType == UnitType.Zerg_Ultralisk) {
                ults++;
            }
            else if(unitType == UnitType.Zerg_Mutalisk){
                mutas++;
            }
        }

        if(info.pcb.buildTypes.contains(UnitType.Zerg_Hive) && !info.pcb.buildTypes.contains(UnitType.Zerg_Ultralisk_Cavern)){
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Ultralisk_Cavern, false));
        }
        if(ults < 4 && info.pcb.buildTypes.contains(UnitType.Zerg_Ultralisk_Cavern)){
            this.selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Ultralisk, 1, true));
        }
        else if(info.pcb.armyTypes.contains(UnitType.Zerg_Ultralisk)){
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Anabolic_Synthesis, 1));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Chitinous_Plating, 1));
        }
        if((info.pcb.buildTypes.contains(UnitType.Zerg_Lair) || info.pcb.buildTypes.contains(UnitType.Zerg_Hive)) && !info.pcb.buildTypes.contains(UnitType.Zerg_Spire)){
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Spire, true));
        }
        else if (mutas < 10 && info.pcb.buildTypes.contains(UnitType.Zerg_Spire)){
            this.selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Mutalisk, 1, true));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Flyer_Attacks, 1));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Flyer_Carapace, 1));
        }


        this.selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Ultralisk, 1, false));


        this.state = RoutineState.Running;
        this.selector.start();
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
            System.out.println("BASE: Lategame orders successful");
        }
        else if(selector.isFailure()){
            fail();
            System.out.println("BASE: Failed to issue Lategame orders");
        }
    }
}

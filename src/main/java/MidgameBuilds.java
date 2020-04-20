import bwapi.*;

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
        System.out.println("BASE: Starting midgame builds routine");
        this.selector = new Selector();

        int hatches = 0;
        for(Unit unit: info.pcb.self.getUnits()){
            if(unit.getType() == UnitType.Zerg_Hatchery || unit.getType() == UnitType.Zerg_Lair || unit.getType() == UnitType.Zerg_Hive){
                hatches++;
            }
        }
        if(hatches < 2 && info.pcb.playstyle == Playstyle.OFFENSIVE) {
            if(info.ecb.race == Race.Terran || info.ecb.race == Race.Zerg)
                this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Hatchery, true));
            else
                this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Hatchery, false));
        }
        else if (hatches < 3 && info.pcb.buildTypes.contains(UnitType.Zerg_Lair) && !info.pcb.startExp && info.pcb.playstyle == Playstyle.OFFENSIVE){
            System.out.println("BASE/EXPANSION: Sending signal to start looking for expansion");
            info.pcb.startExp = true;
            //this.selector.addRoutine(new BuildExpansion(info));
        }
        else if(info.pcb.buildTypes.contains(UnitType.Zerg_Lair) && !info.pcb.buildTypes.contains((UnitType.Zerg_Hive)) && info.pcb.buildTypes.contains(UnitType.Zerg_Queens_Nest)){
            System.out.println("BASE: Requirements for hive met, would like to build");
            this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Hive, 1, false));
        }
        else if(info.pcb.buildTypes.contains(UnitType.Zerg_Hive) && !info.pcb.goLate){
            info.pcb.goLate = true;
        }

        if(info.pcb.expansionSecured){
            int expLarva = 0;
            int expDrones = 0;
            int expExt = 0;
            int expCol = 0;
            for(Unit unit: info.pcb.self.getUnits()){
                UnitType type = unit.getType();
                if(unit.getDistance(info.pcb.expansion) < 500) {
                    if (type == UnitType.Zerg_Drone) {
                        expDrones++;
                    }
                    else if(type == UnitType.Zerg_Extractor){
                        expExt++;
                    }
                    else if(type == UnitType.Zerg_Creep_Colony || type == UnitType.Zerg_Sunken_Colony || type == UnitType.Zerg_Spore_Colony){
                        expCol++;
                    }
                    else if(type == UnitType.Zerg_Larva)
                        expLarva++;
                }
            }
            if(expLarva >= 1) {
                if (expDrones < 6) {
                    System.out.println("EXPANSION: Not enough drones, morphing more, will not spin until complete");
                    this.selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, UnitType.Zerg_Larva, 2, true, true));
                }
            }
            if(expExt != 1) {
                System.out.println("EXPANSION: No extractor, building one, will not spin until complete");
                this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Extractor, false, true));
            }
            if(expCol < 2) {
                System.out.println("EXPANSION: Not enough colonies, building one, will not spin until complete");
                this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Creep_Colony, false, true));
            }

        }

        this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Pneumatized_Carapace, 1, true));


        if(!info.pcb.buildTypes.contains(UnitType.Zerg_Lair) && !info.pcb.buildTypes.contains(UnitType.Zerg_Hive)) {
            this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Lair, 1));
        }
        else if (!info.pcb.buildTypes.contains(UnitType.Zerg_Hydralisk_Den)){
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Hydralisk_Den, false));
        }
        else if(!info.pcb.buildTypes.contains(UnitType.Zerg_Spire) && (info.ecb.race == Race.Terran || info.ecb.race == Race.Zerg)){
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Spire, false));
        }
        else if(!info.pcb.buildTypes.contains(UnitType.Zerg_Evolution_Chamber)){
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Evolution_Chamber, false));
        }
        else if(!info.pcb.buildTypes.contains(UnitType.Zerg_Queens_Nest)){
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Queens_Nest, false));
        }



        if(info.pcb.buildTypes.contains(UnitType.Zerg_Hydralisk_Den)){
            this.selector.addRoutine(new ResearchTech(info, TechType.Lurker_Aspect, false));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Grooved_Spines, 1));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Muscular_Augments, 1));
        }
        if(info.pcb.buildTypes.contains(UnitType.Zerg_Evolution_Chamber)){
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Melee_Attacks, 1));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Carapace, 1));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Missile_Attacks, 1));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Melee_Attacks, 2));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Carapace, 2));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Missile_Attacks, 2));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Melee_Attacks, 3));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Carapace, 3));
            this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Zerg_Missile_Attacks, 3));
        }

        this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Metabolic_Boost,1));
        this.selector.addRoutine(new ResearchUpgrade(info, UpgradeType.Adrenal_Glands, 1));


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

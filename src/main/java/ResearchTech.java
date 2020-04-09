import bwapi.*;

public class ResearchTech extends Routine {
    TechType techType;
    Player self;
    UnitType required;
    boolean wait;

    public ResearchTech(ChalkBoard info, TechType techType, boolean wait){
        this.self = info.pcb.self;
        this.techType = techType;
        this.required = techType.whatResearches();
        this.wait = wait;
    }

    public void reset() {

    }

    public void act(ChalkBoard info) {
        if(self.isResearching(techType)){
            succeed();
            return;
        }
        if(!info.pcb.buildTypes.contains(techType.requiredUnit())){
            if(!wait) {
                System.out.println("BASE: Cannot research " + techType.toString() + ", required unit " + techType.requiredUnit() + " not built yet");
                fail();
            }
            return;
        }
        if(info.pcb.upgrades.get(techType) != null){
            if(self.hasResearched(techType)){
                System.out.println("BASE: Failed to research tech " + techType + ". Tech has already been researched");
                fail();
                return;
            }
        }
        Unit researches = null;
        for (Unit unit: self.getUnits()){
            if (unit.getType() == required){
                researches = unit;
                break;
            }
        }
        if (researches == null){
            System.out.println("BASE: Unable to research " + techType + ", lacking unit of type: " + required);
            if(!wait) {
                fail();
            }
        }
        else{
            if(self.gas() < techType.gasPrice() || self.minerals() < techType.mineralPrice()){
                if(!wait) {
                    fail();
                    System.out.println("BASE: Not enough gas or minerals. Required gas: " + techType.gasPrice() + ", required minerals: " + techType.mineralPrice());
                }
            }
            else{
                if (researches.research(techType)) {
                    info.pcb.tech.add(techType);
                }
                else{
                    fail();
                }
            }
        }
    }
}

import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ResearchUpgrade extends Routine {
    UpgradeType upgrade;
    Player self;
    UnitType required;
    Integer level;

    public ResearchUpgrade(ChalkBoard info, UpgradeType upgrade, Integer level){
        this.self = info.pcb.self;
        this.upgrade = upgrade;
        this.required = upgrade.whatUpgrades();
        this.level = level;
    }

    public void reset() {

    }

    public void act(ChalkBoard info) {
        if(info.pcb.upgrades.get(upgrade) != null){
            if(info.pcb.upgrades.get(upgrade) >= level){
                System.out.println("BASE: Failed to upgrade " + upgrade + " level " + level + ". Upgrade has already reached level: " + info.pcb.upgrades.get(upgrade));
                fail();
                return;
            }
        }
        Unit upgrades = null;
        for (Unit unit: self.getUnits()){
            if (unit.getType() == required){
                upgrades = unit;
                break;
            }
        }
        if (upgrades == null){
            System.out.println("BASE: Unable to upgrade " + upgrade + ", lacking unit of type: " + required);
            fail();
        }
        else{
            if(self.gas() < upgrade.gasPrice() || self.minerals() < upgrade.mineralPrice()){
                fail();
                //System.out.println("BASE: Not enough gas or minerals. Required gas: " + upgrade.gasPrice() + ", required minerals: " + upgrade.mineralPrice());
            }
            else{
                if (upgrades.upgrade(upgrade)) {
                    info.pcb.upgrades.put(upgrade, level);
                    succeed();
                }
                else{
                    fail();
                }
            }
        }
    }
}

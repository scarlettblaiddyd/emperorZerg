import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class ResearchUpgrade extends Routine {
    UpgradeType upgrade;
    Player self;
    UnitType required;

    public ResearchUpgrade(ChalkBoard info, UpgradeType upgrade){
        this.self = info.pcb.self;
        this.upgrade = upgrade;
        this.required = upgrade.whatUpgrades();
    }

    public void reset() {

    }

    public void act(ChalkBoard info) {
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
                return;
            }
            else{
                if (upgrades.upgrade(upgrade)) {
                    succeed();
                }
                else{
                    fail();
                }
            }
        }
    }
}

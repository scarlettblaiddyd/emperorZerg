import bwapi.Unit;
import bwapi.UnitType;

public class OverLordBehavior extends Routine {
    private boolean ordered;
    private Unit friendly;

    public void reset() {

    }

    public OverLordBehavior(ChalkBoard info){
        friendly = null;
        ordered = false;
    }

    public void start(ChalkBoard info){
        System.out.println("ARMY: Trying to find friendly unit to send Overlords to");
        super.start();
        if(info.pcb.playstyle == Playstyle.DEFENSIVE){
            for(Unit building: info.pcb.buildings){
                if(building.getType() == UnitType.Zerg_Sunken_Colony)
                    friendly = building;
            }
            if(friendly == null)
                friendly = info.pcb.buildings.getFirst();
        }
        else if (info.pcb.playstyle == Playstyle.OFFENSIVE) {
            if(info.pcb.armyLead != null)
                friendly = info.pcb.armyLead;
        }
    }

    public void act(ChalkBoard info) {
        if (friendly == null)
            start(info);

        if (friendly == null){
            System.out.println("ARMY: No units found for Overlord head towards");
            fail();
            return;
        }
        for(Unit overlord: info.pcb.self.getUnits()){
            if(overlord.getType() == UnitType.Zerg_Overlord){
                if(overlord.getHitPoints() * 2 < overlord.getType().maxHitPoints())
                {
                    System.out.println("ARMY: Overlord damaged, retreating");
                    overlord.move(info.pcb.self.getStartLocation().toPosition());
                    ordered = true;
                    continue;
                }
                overlord.move(friendly.getPosition());
                ordered = true;
            }
        }

        if(ordered)
            System.out.println("ARMY: At least one overlord sent to a friendly unit of type: " + friendly.getType());

        fail();

    }
}

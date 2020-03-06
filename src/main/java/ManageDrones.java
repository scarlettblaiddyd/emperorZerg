import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

import java.util.LinkedList;

public class ManageDrones extends Routine {
    Player self;
    int mineral;
    int gas;
    int extractors;
    LinkedList<Unit> extractorList;

    public ManageDrones(ChalkBoard info){
        this.self = info.pcb.self;
        mineral = 0;
        gas = 0;
        extractorList = new LinkedList<Unit>();
    }

    public void reset() {

    }

    public void act(ChalkBoard info) {
        for(Unit unit: self.getUnits()){
            if(unit.isGatheringMinerals()){
                mineral++;
            }
            else if(unit.isGatheringGas()){
                gas++;
            }
            if(unit.getType() == UnitType.Zerg_Extractor){
                extractors++;
                extractorList.add(unit);
            }
        }
        if(gas < extractors * 2){
            for(Unit unit: self.getUnits()){
                if(unit.getType().isWorker()){
                    // TODO: Make the worker find the nearest extractor from the list
                    if(unit.isIdle()){
                        System.out.println("BASE: Sending idle worker to extractor");
                        unit.gather(extractorList.get(0));
                        succeed();
                        return;
                    }
                    else if(unit.isGatheringMinerals()){
                        System.out.println("BASE: Sending mineral gatherer to gather gas");
                        unit.gather(extractorList.get(0));
                        succeed();
                        return;
                    }
                }
            }
        }// If we don't need to be gathering more gas, just gather minerals
        else{
            for(Unit unit: self.getUnits()) {
                if (unit.getType().isWorker() && unit.isIdle()) {
                    // Find the closest mineral to it
                    Unit closestMineral = null;
                    int closestDistance = Integer.MAX_VALUE;
                    for (Unit mineral : info.game.getMinerals()) {
                        int distance = unit.getDistance(mineral);
                        if (distance < closestDistance) {
                            closestMineral = mineral;
                            closestDistance = distance;
                        }
                    }
                    // Gather the closest
                    unit.gather(closestMineral);
                    return;
                }
            }
        }
    }
}

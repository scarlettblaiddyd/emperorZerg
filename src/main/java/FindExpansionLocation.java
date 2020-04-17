import bwapi.*;
import javafx.geometry.Pos;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

public class FindExpansionLocation extends Routine {
    private final ChalkBoard info;
    private final Game game;
    private Unit scout;
    private LinkedList<Region> explored;
    private Region destination;
    private final boolean needGas;
    public void reset() {

    }

    public void start(){
        System.out.println("EXPANSION: Looking for a unit to scout");
        for(Unit unit: info.pcb.self.getUnits()){
            if(unit.getType().isWorker() && (unit.isIdle() || unit.isGatheringMinerals()) && !unit.isCarrying() && unit != info.pcb.scout){
                scout = unit;
                System.out.println("EXPANSION: Found unit to scout");
                break;
            }
        }
        for(Region region: game.getRegionAt(info.pcb.self.getStartLocation().toPosition()).getNeighbors()){
            destination = region;
            break;
        }
        scout.holdPosition();
        scout.move(destination.getCenter());
        super.start();
    }

    public FindExpansionLocation(ChalkBoard info, boolean gas){
        this.info = info;
        this.game = info.game;
        this.scout = null;
        this.destination = null;
        this.explored = new LinkedList<Region>();
        this.needGas = gas;
    }
    public void act(ChalkBoard info) {
        start();
        if(info.pcb.expansion != null){
            System.out.println("EXPANSION: Location already found");
            fail();
            return;
        }
        if(scout == null || !scout.exists()) {
            System.out.println("EXPANSION: Scout killed or not found, giving up control");
            fail();
        }

        if(scout.getDistance(destination.getCenter()) < 100){
            System.out.println("EXPANSION: New region discovered, is this a good expansion?");
            info.pcb.expansionUnexplored.remove(destination);
            // Is this a good expansion?
            int min = 0;
            int gas = 0;
            for(Unit unit: game.getMinerals()){
                if(unit.getDistance(scout) < 500){
                    min++;
                }
            }
            if(needGas) {
                for (Unit unit : game.getGeysers()) {
                    if (unit.getDistance(scout) < 500) {
                        gas++;
                    }
                }
                if(gas >= 1 && min >=2){
                    info.pcb.expansion = scout.getRegion().getCenter();
                    scout.holdPosition();
                    System.out.println("EXPANSION: Location with gas found");
                    succeed();
                    return;
                }
            }
            else {
                if (min >= 4) {
                    info.pcb.expansion = scout.getRegion().getCenter();
                    scout.holdPosition();
                    System.out.println("EXPANSION: Location found");
                    succeed();
                    return;
                }
            }

            System.out.println("EXPANSION: Region not suitable, moving on to next region");
            Region prev = destination;
            LinkedList<Region> neighbors = new LinkedList<Region>(destination.getNeighbors());
            Collections.shuffle(neighbors);
            for(Region region: neighbors){
                if(info.pcb.expansionUnexplored.contains(region) && region.isAccessible()){
                    System.out.println("EXPANSION: Found unexplored region");
                    destination = region;
                    break;
                }
            }

            if(info.pcb.expansionUnexplored.size() <= 0){
                System.out.println("EXPANSION: All regions explored, resetting list");
                info.pcb.expansionUnexplored = new LinkedList<Region>(game.getAllRegions());
            }
            if(prev == destination){
                System.out.println("EXPANSION: No new region found in neighbors list, looking for closes region from unexplored list");
                Region closest = null;
                for(Region region: info.pcb.expansionUnexplored){
                    if(!region.isAccessible())
                        continue;
                    game.drawCircleMap(region.getCenter(), 5, Color.Red);
                    if(closest == null)
                        closest = region;
                    else if(scout.getDistance(region.getCenter()) < scout.getDistance(closest.getCenter()))
                        closest = region;
                }
                if(prev == destination){
                    System.out.println("EXPANSION: For some reason scout cannot find a valid region");
                }
                else{
                    System.out.println("EXPANSION: New region found, not neighbor but should be close");
                    destination = closest;
                }
            }

            scout.move(destination.getCenter());
        }

        game.drawCircleMap(scout.getPosition(), 10, Color.Black);
        game.drawTextMap(scout.getPosition(), "scouting for expansion");
        game.drawTextMap(destination.getCenter(), "expansion destination");
    }
}

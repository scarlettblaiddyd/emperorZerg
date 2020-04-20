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
    private Position prevPos;
    int stuck = 0;
    public void reset() {

    }

    public void start(){
        System.out.println("EXPANSION: Looking for a unit to scout");
        for(Unit unit: info.pcb.self.getUnits()){
            if(unit.getType().isWorker() && (unit.isIdle() || unit.isGatheringMinerals()) && !unit.isCarrying() && unit != info.pcb.scout){
                info.pcb.expScout = unit;
                scout = info.pcb.expScout;
                System.out.println("EXPANSION: Found unit to scout");
                break;
            }
        }
        for(Region region: game.getRegionAt(info.pcb.self.getStartLocation().toPosition()).getNeighbors()){
            destination = region;
            break;
        }
        prevPos = scout.getPosition();
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
        this.prevPos = null;
    }
    public void act(ChalkBoard info) {
        if(scout == null)
            start();
        if(info.pcb.expansion != null){
            System.out.println("EXPANSION: Location already found");
            fail();
            return;
        }
        if(scout == null || !scout.exists()) {
            System.out.println("EXPANSION: Scout killed or not found");
            reset();
            start();
            return;
        }

        if(scout.getPosition().getDistance(prevPos) < 1){
            System.out.println("EXPANSION: Scout might be stuck, waiting to see");
            stuck++;
            if(stuck >= 50){
                System.out.println("EXPANSION: Scout stuck on something, getting new destination");
                info.pcb.expansionUnexplored.remove(destination);
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
                if(closest != null) {
                    System.out.println("EXPANSION: New region found, not neighbor but should be close");
                    destination = closest;
                }
                stuck = 0;
                scout.move(destination.getCenter());
            }
        }
        else{
            stuck = 0;
        }
        prevPos = scout.getPosition();


        if(scout.getDistance(destination.getCenter()) < 100){
            System.out.println("EXPANSION: New region discovered, is this a good expansion?");
            info.pcb.expansionUnexplored.remove(destination);
            // Is this a good expansion?
            Unit mineral = null;
            int min = 0;
            int gas = 0;
            for(Unit unit: game.getMinerals()){
                if(unit.getDistance(scout) < 200){
                    min++;
                    mineral = unit;
                }
            }
            if(needGas) {
                Unit geyser = null;
                for (Unit unit : game.getGeysers()) {
                    if (unit.getDistance(scout) < 200) {
                        geyser = unit;
                        gas++;
                    }
                }
                if(gas >= 1 && min >=2){
                    if(geyser.getDistance(info.pcb.self.getStartLocation().toPosition()) < 500){
                        System.out.println("EXPANSION: Location with gas found, too close to base, might be the geyser from starter base!");
                    }
                    info.pcb.expansion = geyser.getPosition();
                    scout.holdPosition();
                    //info.pcb.expScout = null;
                    System.out.println("EXPANSION: Location with gas found");
                    info.pcb.saveForExpansion = true;
                    scout.move(info.pcb.expansion);
                    succeed();
                    return;
                }
            }
            else {
                if (min >= 4) {
                    if(mineral.getDistance(info.pcb.self.getStartLocation().toPosition()) < 500){
                        System.out.println("EXPANSION: Location with minerals found, too close to base, might be the mineral patches from starter base!");
                    }
                    info.pcb.expansion = mineral.getPosition();
                    //info.pcb.expScout = null;
                    scout.holdPosition();
                    System.out.println("EXPANSION: Location found");
                    info.pcb.saveForExpansion = true;
                    scout.move(info.pcb.expansion);
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
                if(closest != null) {
                    System.out.println("EXPANSION: New region found, not neighbor but should be close");
                    destination = closest;
                }
                if(prev == destination){
                    System.out.println("EXPANSION: For some reason scout cannot find a valid region, getting random region from list");
                    destination = info.pcb.expansionUnexplored.getFirst();
                }
                else{
                    destination = closest;
                }
            }

            scout.move(destination.getCenter());
        }

        game.drawCircleMap(destination.getCenter(), 500, Color.Blue);

        //game.drawTextMap(scout.getPosition(), "scouting for expansion");
        game.drawTextMap(destination.getCenter(), "expansion destination");
    }
}

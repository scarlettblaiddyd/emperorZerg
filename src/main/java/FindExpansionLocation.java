import bwapi.Game;
import bwapi.Position;
import bwapi.Region;
import bwapi.Unit;
import javafx.geometry.Pos;

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
        for(Unit unit: info.pcb.self.getUnits()){
            if(unit.getType().isWorker() && (unit.isIdle() || unit.isGatheringMinerals())){
                scout = unit;
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
        if(info.pcb.expansion != null){
            fail();
            return;
        }
        if(scout == null)
            start();

        if(scout.getDistance(destination.getCenter()) < 50){
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

            explored.add(destination);
            for(Region region: destination.getNeighbors()){
                if(!explored.contains(region)){
                    destination = region;
                    break;
                }
            }
            scout.move(destination.getCenter());
        }

        game.drawTextMap(scout.getPosition(), "scout");
        game.drawTextMap(destination.getCenter(), "destination");
        for(Unit unit: game.getMinerals()){
            game.drawTextMap(unit.getPosition(), "m");
        }
    }
}

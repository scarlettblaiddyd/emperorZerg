import bwapi.*;

import java.util.Collections;
import java.util.LinkedList;

public class ScoutEnemy extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private LinkedList<TilePosition> startLocations;
    private Position moveTo = null;
    private LinkedList<Region> explored;
    private Region destination;

    public void reset() {

    }

    public void start() {
        super.start();
    }

    public ScoutEnemy(ChalkBoard info) {
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.startLocations = new LinkedList<TilePosition>(game.getStartLocations());
        this.explored = info.pcb.unexplored;
        this.destination = info.pcb.scoutDest;
    }
/*
    public ScoutEnemy(Game game, Player self, enemyChalkBoard enemy) {
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
        this.startLocations = new LinkedList<TilePosition>(game.getStartLocations());
    }
*/
    public void act(ChalkBoard info) {
        // Check to see if scout was killed
        if (info.pcb.scout == null || !info.pcb.scout.exists()) {
            System.out.println("ARMY: Looking for a unit to turn into a scout");
            for (Unit unit : self.getUnits()) {
                UnitType unitType = unit.getType();
                if (unitType.isWorker() && !unit.isCarryingMinerals() && !unit.isCarryingGas()) {
                    System.out.println("ARMY: Found a unit to turn into scout");
                    info.pcb.scout = unit;
                    info.pcb.scout.stop();
                    break;
                }
            }
        }

        if(enemy.destroyedBase && info.pcb.scout != null && info.pcb.scout.exists()){
            scoutMap(info);
            return;
        }
        // If we have the enemies base location, patrol it
        if (enemy.startBase != null && info.pcb.scout != null) {
            scoutBase(info);
            fail();
            return;
        }
        if (this.startLocations.contains(self.getStartLocation())) {
            System.out.println("ARMY: Removing our start location from the list of possible locations");
            if (this.startLocations.remove(self.getStartLocation())) {
                System.out.println("ARMY: Successfully removed");
            } else {
                System.out.println("ARMY: Failed to remove");
            }
        }


        if ((enemy.buildTypes.contains(UnitType.Terran_Command_Center) || enemy.buildTypes.contains(UnitType.Protoss_Nexus) || enemy.buildTypes.contains(UnitType.Zerg_Hatchery)) && moveTo != null) {
            System.out.println("ARMY: Found enemy starting base at: " + moveTo.toString());
            for(Unit building: enemy.buildings){
                if(identifyUnit(building) == UnitClass.enemyBase){
                    enemy.startBase = building;
                }
            }
            info.pcb.scoutRally = info.pcb.scout.getPosition();
            //enemy.basePos.add(moveTo);
            info.pcb.scoutDest = game.getRegionAt(moveTo);
            //scout.move(self.getStartLocation().toPosition());
            //info.pcb.scout = null;
            succeed();
            return;
        }
        if (info.pcb.scout != null && info.pcb.scout.isIdle()) {
            moveTo = startLocations.poll().toPosition();
            info.pcb.scout.move(moveTo);
        }
    }

    private void scoutBase(ChalkBoard info) {
        if(info.pcb.scout.isUnderAttack()){
            info.pcb.scout.move(self.getStartLocation().toPosition());
            System.out.println("ARMY: Scout under attack, retreating");
            fail();
            return;
        }
        for (Unit u : info.game.getUnitsInRadius(info.pcb.scout.getPosition(), 350)) {
            UnitClass uc = identifyUnit(u);
            info.game.drawTextMap(u.getPosition(), uc.toString());
            if(uc == UnitClass.enemyCombatant || uc == UnitClass.enemyCombatBuilding) {
                if(u.getDistance(info.pcb.scout) <= 400) {
                    if(u.getDistance(info.pcb.scoutRally) < info.pcb.scout.getDistance(info.pcb.scoutRally) || u.getDistance(info.pcb.scoutRally) < 250){
                        System.out.println("ARMY: Scout threatened by enemy combatant, threat too close to rally point, retreating to base");
                        info.pcb.scout.move(self.getStartLocation().toPosition());
                    }
                    else {
                        System.out.println("ARMY: Scout threatened by enemy combatant, retreating to rally point");
                        info.pcb.scout.move(info.pcb.scoutRally);
                    }
                    fail();
                    return;
                }
            }
            else if (uc == UnitClass.enemyWorker){
                if(u.getDistance(info.pcb.scout.getPosition()) < 150){
                    if(u.getDistance(info.pcb.scoutRally) < info.pcb.scout.getDistance(info.pcb.scoutRally)){
                        System.out.println("ARMY: Scout threatened by enemy workers, threat too close to rally point, retreating to base");
                        info.pcb.scout.move(self.getStartLocation().toPosition());
                    }
                    else {
                        System.out.println("ARMY: Scout threatened by enemy workers, retreating to rally point");
                        info.pcb.scout.move(info.pcb.scoutRally);
                    }
                    fail();
                    return;
                }
            }
        }
        info.pcb.scout.patrol(info.pcb.scoutRally);
        fail();
    }

    public void scoutMap(ChalkBoard info){
        System.out.println("ARMY: Looking to scout the map");
        Region prev = info.pcb.scoutDest;

        // Run from danger
        for (Unit u : info.game.getUnitsInRadius(info.pcb.scout.getPosition(), 350)) {
            UnitClass uc = identifyUnit(u);
            info.game.drawTextMap(u.getPosition(), uc.toString());
            if(uc == UnitClass.enemyCombatant || uc == UnitClass.enemyCombatBuilding) {
                if(u.getDistance(info.pcb.scout) <= 400) {
                    if(u.getDistance(info.pcb.scoutRally) < info.pcb.scout.getDistance(info.pcb.scoutRally) || u.getDistance(info.pcb.scoutRally) < 250){
                        System.out.println("ARMY: Scout threatened by enemy combatant, threat too close to rally point, retreating to base");
                        info.pcb.scout.move(self.getStartLocation().toPosition());
                    }
                    else {
                        System.out.println("ARMY: Scout threatened by enemy combatant, retreating to rally point");
                        info.pcb.scout.move(info.pcb.scoutRally);
                    }
                    fail();
                    return;
                }
            }
            else if (uc == UnitClass.enemyWorker){
                if(u.getDistance(info.pcb.scout.getPosition()) < 150){
                    if(u.getDistance(info.pcb.scoutRally) < info.pcb.scout.getDistance(info.pcb.scoutRally)){
                        System.out.println("ARMY: Scout threatened by enemy workers, threat too close to rally point, retreating to base");
                        info.pcb.scout.move(self.getStartLocation().toPosition());
                    }
                    else {
                        System.out.println("ARMY: Scout threatened by enemy workers, retreating to rally point");
                        info.pcb.scout.move(info.pcb.scoutRally);
                    }
                    fail();
                    return;
                }
            }
        }


        if(info.pcb.scout.getDistance(info.pcb.scoutDest.getCenter()) < 150){
            System.out.println("SCOUT: Reached region");
            info.pcb.unexplored.remove(info.pcb.scoutDest);
            LinkedList<Region> neighbors = new LinkedList<Region>(info.pcb.scoutDest.getNeighbors());
            Collections.shuffle(neighbors);
            for(Region region: neighbors){
                if(info.pcb.unexplored.contains(region) && region.isAccessible()){
                    System.out.println("SCOUT: Found unexplored region");
                    info.pcb.scoutDest = region;
                    break;
                }
            }
            // No new region found?
            // If the unexplored list is empty, make a new one
            if(info.pcb.unexplored.size() <= 0){
                System.out.println("SCOUT: All regions explored, resetting list");
                info.pcb.unexplored = new LinkedList<Region>(game.getAllRegions());
            }
            if(prev == info.pcb.scoutDest){
                System.out.println("SCOUT: No new region found, looking for closest region from unexplored list");
                Region closest = null;
                for(Region region: info.pcb.unexplored){
                    if(!region.isAccessible())
                        continue;
                    game.drawCircleMap(region.getCenter(), 5, Color.Blue);
                    if(closest == null)
                        closest = region;
                    else if(info.pcb.scout.getDistance(region.getCenter()) < info.pcb.scout.getDistance(closest.getCenter()))
                        closest = region;
                }
                if(prev == closest){
                    System.out.println("SCOUT: For some reason, scout cannot find a valid region");
                }
                else {
                    System.out.println("SCOUT: New region found, not neighbor but should be close");
                    info.pcb.scoutDest = closest;
                }
            }

        }

        info.pcb.scout.move(info.pcb.scoutDest.getCenter());

        fail();
    }

    public UnitClass identifyUnit(Unit unit){
        UnitType type = unit.getType();
        if(type.isBuilding()){
            if(unit.getPlayer() == self)
                return UnitClass.playerBuilding;
            else if(self.isEnemy(unit.getPlayer()) ) {
                if(type ==UnitType.Terran_Command_Center ||
                        type == UnitType.Zerg_Hatchery ||
                        type == UnitType.Protoss_Nexus)
                    return UnitClass.enemyBase;
                if(type == UnitType.Protoss_Photon_Cannon)
                    return UnitClass.enemyCombatBuilding;
                return UnitClass.enemyBuilding;
            }
            else
                return UnitClass.unimportant;
        }
        else{
            if(unit.getPlayer() == self) {
                if(type.isWorker())
                    return UnitClass.playerWorker;
                else if(type == UnitType.Zerg_Zergling || type == UnitType.Zerg_Hydralisk || type == UnitType.Zerg_Lurker)
                    return UnitClass.playerCombatant;
                else
                    return UnitClass.unimportant;
            }
            if(self.isEnemy(unit.getPlayer()) && (unit.getPlayer().getType() == PlayerType.Player) || (unit.getPlayer().getType() == PlayerType.Computer)){
                if(type.isWorker()  || type == UnitType.Zerg_Drone || type == UnitType.Protoss_Probe || type == UnitType.Terran_SCV)
                    return UnitClass.enemyWorker;
                else
                    return UnitClass.enemyCombatant;
            }
            return UnitClass.unimportant;
        }
    }
}
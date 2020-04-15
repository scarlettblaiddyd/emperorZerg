import bwapi.*;

import java.util.LinkedList;

public class ScoutEnemy extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private LinkedList<TilePosition> startLocations;
    private Position moveTo = null;

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
    }

    public ScoutEnemy(Game game, Player self, enemyChalkBoard enemy) {
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
        this.startLocations = new LinkedList<TilePosition>(game.getStartLocations());
    }

    public void act(ChalkBoard info) {
        // If we have the enemies base location, patrol it
        if (enemy.basePos.size() > 0 && info.pcb.scout != null) {
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

        if ((enemy.buildTypes.contains(UnitType.Terran_Command_Center) || enemy.buildTypes.contains(UnitType.Protoss_Nexus) || enemy.buildTypes.contains(UnitType.Zerg_Hatchery)) && moveTo != null) {
            System.out.println("ARMY: Found enemy base at: " + moveTo.toString());
            info.pcb.scoutRally = info.pcb.scout.getPosition();
            enemy.basePos.add(moveTo);
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
        for (Unit u : info.ecb.army) {
            UnitClass uc = identifyUnit(u);
            if(uc == UnitClass.enemyCombatant || uc == UnitClass.enemyCombatBuilding) {
                if(u.getDistance(info.pcb.scout) <= 350) {
                    if(u.getDistance(info.pcb.scoutRally) < info.pcb.scout.getDistance(info.pcb.scoutRally)){
                        System.out.println("ARMY: Scout threatened, threat too close to rally point, retreating to base");
                        info.pcb.scout.move(self.getStartLocation().toPosition());
                    }
                    else {
                        System.out.println("ARMY: Scout threatened, retreating to rally point");
                        info.pcb.scout.move(info.pcb.scoutRally);
                    }
                    fail();
                    return;
                }
            }
            else if (uc == UnitClass.enemyWorker){
                if(u.getDistance(info.pcb.scout.getPosition()) < 100){
                    if(u.getDistance(info.pcb.scoutRally) < info.pcb.scout.getDistance(info.pcb.scoutRally)){
                        System.out.println("ARMY: Scout threatened, threat too close to rally point, retreating to base");
                        info.pcb.scout.move(self.getStartLocation().toPosition());
                    }
                    else {
                        System.out.println("ARMY: Scout threatened, retreating to rally point");
                        info.pcb.scout.move(info.pcb.scoutRally);
                    }
                    fail();
                    return;
                }
            }
        }
        info.pcb.scout.patrol(info.pcb.scoutRally);
        fail();
        return;
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
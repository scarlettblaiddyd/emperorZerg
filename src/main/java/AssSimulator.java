import bwapi.*;
import org.bk.ass.sim.BWMirrorAgentFactory;
import org.bk.ass.sim.Simulator;

public class AssSimulator extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Position armyLead;

    public void reset() { }

    public AssSimulator (ChalkBoard info) {
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }

    public void act(ChalkBoard info) {
        BWMirrorAgentFactory factory = new BWMirrorAgentFactory(game);
        Simulator simulator = new Simulator.Builder().build();
        armyLead = null;

        // put the armies into the simulator
        // Find the enemy to check distance against
        Position fightCenter = null;
        for(Unit unit: info.ecb.army){
            if(unit.isVisible(info.pcb.self)){
                fightCenter = unit.getPosition();
                break;
            }
        }
        info.pcb.battleField = fightCenter;
        if (fightCenter == null){
            System.out.println("ARMY: No visible enemies to simulate");
            fail();
            return;
        }
        // First, see if it's just our scout seeing the enemy
        boolean scoutPresent = false;

        for(Unit unit: self.getUnits()){
            if(unit == info.pcb.scout){
                scoutPresent = true;
            }
        }
        if(!info.pcb.scout.exists())
            scoutPresent = false;
        // If scout is present, lets see if our army is as well. If not, it's just a scouting
        // simulation of the fight and we can add EVERY one of our army units.
        boolean armyEngaged = false;
        for (Unit unit : info.pcb.army) {
            if (unit.getDistance(fightCenter) <= 600) {
                if(armyLead == null) {
                    armyLead = unit.getPosition();
                    info.pcb.armyLead = unit;
                }
                armyEngaged = true;

            }
        }
        if(scoutPresent && !armyEngaged)
            System.out.println("ARMY: Scout found enemy, army not present, running simulation with all friendly forces");
        else
            System.out.println("ARMY: Army close enough to engage, running simulation with only nearby friendly forces");

        // If scout is present but army is not, add entire army
        // If scout is present AND army is present, only add nearby units
        // If scout is NOT present but army is present, only add nearby units
        // Boils down to: If scout, check engaged. If !engaged, add all
        //              : If !scout, add only nearby
        for (Unit unit : info.ecb.army) {
            if(unit.getDistance(fightCenter) <= 600)
                simulator.addAgentB(factory.of(unit.getType()));
        }
        for (Unit unit : info.pcb.army) {
            if(scoutPresent && !armyEngaged){
                simulator.addAgentA(factory.of(unit.getType()));
            }
            else {
                if (unit.getDistance(armyLead) <= 600)
                    simulator.addAgentA(factory.of(unit.getType()));
            }
        }


        simulator.simulate(240); // Simulate 24 seconds

        if (simulator.getAgentsA().size() < simulator.getAgentsB().size()) { // means you lost since you don't have any units
            if(info.pcb.playstyle != Playstyle.DEFENSIVE) {
                System.out.println("ARMY: Simulation LOSS, going on the defensive");
                info.pcb.playstyle = Playstyle.DEFENSIVE;
                info.pcb.stratSwitch = 60;
                for(Unit unit: info.pcb.army){
                    if(unit.isMoving()){
                        unit.stop();
                    }
                }
            }
        }
        else {
            if(info.pcb.playstyle != Playstyle.OFFENSIVE) {
                System.out.println("ARMY: Simulation VICTORY, going on the offensive");
                info.pcb.playstyle = Playstyle.OFFENSIVE;
                info.pcb.stratSwitch = 60;
                for(Unit unit: info.pcb.army){
                    if(unit.isMoving()){
                        unit.stop();
                    }
                }
            }
        }
        fail();
    }
}

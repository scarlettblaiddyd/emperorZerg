import bwapi.*;
import org.bk.ass.sim.BWMirrorAgentFactory;
import org.bk.ass.sim.Simulator;

public class AssSimulator extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;

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

        // put the armies into the simulator
        // Find the enemy to check distance against
        Position fightCenter = null;
        for(Unit unit: info.ecb.army){
            if(unit.isVisible(info.pcb.self)){
                fightCenter = unit.getPosition();
                break;
            }
        }
        if (fightCenter == null){
            System.out.println("ARMY: No visible enemies to simulate");
            fail();
            return;
        }
        for (Unit unit : info.ecb.army) {
            if(unit.getDistance(fightCenter) <= 600)
                simulator.addAgentB(factory.of(unit.getType()));
        }
        for (Unit unit : info.pcb.army) {
            if(unit.getDistance(fightCenter) <= 600)
                simulator.addAgentA(factory.of(unit.getType()));
        }


        simulator.simulate(240); // Simulate 24 seconds

        if (simulator.getAgentsA().size() < simulator.getAgentsB().size()) { // means you lost since you don't have any units
            System.out.println("ARMY: Simulation LOSS, going on the defensive");
            info.pcb.playstyle = Playstyle.DEFENSIVE;
        }
        else {
            System.out.println("ARMY: Simulation VICTORY, going on the offensive");
            info.pcb.playstyle = Playstyle.OFFENSIVE;
        }
        succeed();
    }
}

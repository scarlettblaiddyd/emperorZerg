import bwapi.Game;
import bwapi.Player;
import bwapi.UnitType;
import org.bk.ass.sim.BWMirrorAgentFactory;
import org.bk.ass.sim.Simulator;

public class AssSimulator extends  Routine {

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
        for (UnitType unit : info.pcb.armyTypes) {
            simulator.addAgentA(factory.of(unit));
        }
        for (UnitType unit : info.ecb.armyTypes) {
            simulator.addAgentB(factory.of(unit));
        }

        simulator.simulate(240); // Simulate 24 seconds

        if (simulator.getAgentsA().isEmpty()) {
            // You lost the battle!
            fail();
        }
        else {
            // Huzzah! You won the battle!
            succeed();
        }
    }
}

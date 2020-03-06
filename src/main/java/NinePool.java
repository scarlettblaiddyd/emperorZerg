import bwapi.*;

public class NinePool extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Sequencer sequencer;

    @Override
    public void start(){
        super.start();
    }

    public void reset() { }

    public NinePool(ChalkBoard info, Sequencer sequencer){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.sequencer = sequencer;
    }

    public void act(ChalkBoard info) {
        if (info.pcb.buildOrderComplete) {
            System.out.println("BASE: Build order already completed");
            fail();
            return;
        }

        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 5));
        sequencer.addRoutine(new BuildStructure(info, UnitType.Zerg_Spawning_Pool));
        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 1));
        sequencer.addRoutine(new BuildStructure(info, UnitType.Zerg_Extractor));
        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 1));
        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Overlord, 1));
        if (info.ecb.race == Race.Protoss) {
            sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 3));
            sequencer.addRoutine(new BuildStructure(info, UnitType.Zerg_Hatchery));
        }
        else if (info.ecb.race == Race.Zerg) {
            sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 1));
            sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 3));
            // research
            // morph lair
            // build spire
        }
        else { // ELSE == TERRAN STRAT
            sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 3));
        }
        sequencer.act(info);

        if(sequencer.state == RoutineState.Success){
            succeed();
            info.pcb.buildOrderComplete = true;
            System.out.println("BASE: Build order complete");
            return;
        }
    }
}

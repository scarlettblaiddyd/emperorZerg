import bwapi.*;

public class TerranStrat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Sequencer sequencer;

    @Override
    public void start(){
        super.start();
    }

    public void reset() { }

    public TerranStrat(ChalkBoard info, Sequencer sequencer){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.sequencer = sequencer;
    }


    public void act(ChalkBoard info) {
        if(sequencer.state == RoutineState.Success){
            succeed();
            info.pcb.buildOrderComplete = true;
            System.out.println("BASE: Build order complete");
            return;
        }
        if (info.ecb.race != Race.Terran) {
            System.out.println("BASE: VS TERRAN FAIL");
            fail();
        }

        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 5));
        sequencer.addRoutine(new BuildPool(info));
        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 1));
        sequencer.addRoutine(new BuildExtractor(info));
        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 1));
        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Overlord, 1));
        sequencer.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 3));
        sequencer.act(info);
        //game.drawTextScreen(10, 70, sequencer.routineQueue.toString());
    }
}

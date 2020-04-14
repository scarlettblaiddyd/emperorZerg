import bwapi.*;

public class BuildExpansion extends Routine {
    private final ChalkBoard info;
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Sequencer sequencer;

    @Override
    public void start(){
        sequencer.addRoutine(new FindExpansionLocation(info, true));
        sequencer.addRoutine(new BuildStructure(info, UnitType.Zerg_Hatchery, true, true));
        super.start();
    }

    public void reset() { }

    public BuildExpansion(ChalkBoard info){
        super();
        this.info = info;
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.sequencer = new Sequencer();
    }

    public void act(ChalkBoard info) {
        if (info.pcb.expansionSecured) {
            System.out.println("BASE: Expansion already secured");
            fail();
            return;
        }



        sequencer.act(info);

        if(sequencer.state == RoutineState.Success){
            succeed();
            info.pcb.expansionSecured = true;
            System.out.println("BASE: Expansion secured");
            return;
        }

    }
}

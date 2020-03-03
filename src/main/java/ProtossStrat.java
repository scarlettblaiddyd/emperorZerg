import bwapi.Game;
import bwapi.Player;
import bwapi.Race;

public class ProtossStrat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;

    @Override
    public void start(){
        super.start();
    }

    public void reset() { }

    public ProtossStrat(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }


    public void act(ChalkBoard info) {
        if (enemy.race != Race.Protoss) {
            System.out.println("VS PROTOSS FAIL");
            fail();
        }
        else {
            // call next level routine
            System.out.println("Yer against 'Toss BOI");
            succeed();
        }
    }
}

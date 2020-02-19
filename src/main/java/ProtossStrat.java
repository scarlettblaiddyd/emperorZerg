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

    public ProtossStrat(Game game, Player self, enemyChalkBoard enemy){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
    }

    public void act(Game game, Player self, enemyChalkBoard enemy) {
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

import bwapi.Game;
import bwapi.Player;
import bwapi.Race;

public class ZergStrat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;

    @Override
    public void start(){
        super.start();
    }

    public void reset() { }

    public ZergStrat(Game game, Player self, enemyChalkBoard enemy){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
    }

    public void act(Game game, Player self, enemyChalkBoard enemy) {
        if (enemy.race != Race.Zerg) {
            System.out.println("VS ZERG FAIL");
            fail();
        }
        else {
            // call next level routine
            System.out.println("Yer against Zerg BOI");
            succeed();
        }
    }
}

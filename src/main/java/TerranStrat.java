import bwapi.Game;
import bwapi.Player;
import bwapi.Race;

public class TerranStrat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;

    @Override
    public void start(){
        super.start();
    }

    public void reset() { }

    public TerranStrat(Game game, Player self, enemyChalkBoard enemy){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
    }

    public void act(Game game, Player self, enemyChalkBoard enemy) {
        if (enemy.race != Race.Terran) {
            System.out.println("VS TERRAN FAIL");
            fail();
        }
        else {
            // call next level routine
            System.out.println("Yer against Terran BOI");
            succeed();
        }
    }
}

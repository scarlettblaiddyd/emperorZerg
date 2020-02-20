import bwapi.Bullet;
import bwapi.Game;
import bwapi.Player;
import bwapi.Race;

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

    public TerranStrat(Game game, Player self, enemyChalkBoard enemy, Sequencer sequencer){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
        this.sequencer = sequencer;
    }

    public void act(Game game, Player self, enemyChalkBoard enemy) {
        if(sequencer.state == RoutineState.Success){
            succeed();
            return;
        }
        if (enemy.race != Race.Terran) {
            System.out.println("VS TERRAN FAIL");
            fail();
        }
        sequencer.addRoutine(new MorphDrone(game, self, enemy));
        sequencer.addRoutine(new MorphDrone(game, self, enemy));
        sequencer.addRoutine(new MorphDrone(game, self, enemy));
        sequencer.addRoutine(new MorphDrone(game, self, enemy));
        sequencer.addRoutine(new MorphDrone(game, self, enemy));
        sequencer.addRoutine(new BuildPool(game, self, enemy));
        sequencer.addRoutine(new MorphDrone(game, self, enemy));
        sequencer.addRoutine(new BuildExtractor(game, self, enemy));
        sequencer.addRoutine(new MorphDrone(game, self, enemy));
        sequencer.addRoutine(new MorphOverlord(game, self, enemy));
        sequencer.act(game, self, enemy);
        game.drawTextScreen(10, 70, sequencer.routineQueue.toString());
    }
}

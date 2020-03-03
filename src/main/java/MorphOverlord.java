import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class MorphOverlord extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;

    @Override
    public void start(){
        super.start();
    }

    public void reset(){

    }

    public MorphOverlord(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }

    public MorphOverlord(Game game, Player self, enemyChalkBoard enemy){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
    }

    @Override
    public void act(ChalkBoard info){
        // CHECK FOR MINERALS
        if (self.minerals() < 100) return;
        for (Unit trainer : self.getUnits()) {
            UnitType unitType = trainer.getType();
            if (unitType.isBuilding() && !unitType.buildsWhat().isEmpty()) {
                UnitType toTrain = unitType.buildsWhat().get(0);
                if (trainer.canMorph()) {
                    trainer.morph(UnitType.Zerg_Overlord);
                    succeed();
                }
            }
        }
        //fail();
    }
}
import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class MorphZergling extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private int num;

    @Override
    public void start(){
        super.start();
    }

    public void reset(){

    }

    public MorphZergling(ChalkBoard info, int num){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.num = num;
    }

    public MorphZergling(Game game, Player self, enemyChalkBoard enemy, int num){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
        this.num = num;
    }

    @Override
    public void act(ChalkBoard info){
        // CHECK FOR MINERALS
        if (self.minerals() < 25) return;
        if(self.supplyTotal() - self.supplyUsed() <= 2) return;
        for (Unit trainer : self.getUnits()) {
            UnitType unitType = trainer.getType();
            if (unitType.isBuilding() && !unitType.buildsWhat().isEmpty() && !trainer.isMorphing()) {
                //UnitType toTrain = unitType.buildsWhat().get(0);
                if (trainer.canMorph()) {
                    trainer.morph(UnitType.Zerg_Zergling);
                    num -=1;
                    System.out.println("Morphing new zergling, number left: " + num);
                    if(num <= 0) {
                        succeed();
                    }
                }
            }
        }
        //fail();
    }
}

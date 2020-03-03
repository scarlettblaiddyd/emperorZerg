import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class MorphUnit extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private final UnitType type;
    private int num;

    @Override
    public void start(){
        super.start();
    }

    public void reset(){

    }

    public MorphUnit(ChalkBoard info, UnitType type, int num){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.type = type;
        this.num = num;
    }


    @Override
    public void act(ChalkBoard info){
        // CHECK FOR MINERALS
        if (info.pcb.self.minerals() < type.mineralPrice()) return;
        if( (type != UnitType.Zerg_Overlord) && (self.supplyTotal() - self.supplyUsed() < 2) ) return;
        for (Unit trainer : info.pcb.self.getUnits()) {
            UnitType unitType = trainer.getType();
            if (unitType.isBuilding() && !unitType.buildsWhat().isEmpty() && !trainer.isMorphing()) {
                if (trainer.canMorph()) {
                    trainer.morph(type);
                    num -=1;
                    System.out.println("Morphing new unit: " + type.toString()  +", number left: " + num);
                    if(num <= 0) {
                        succeed();
                    }
                }
            }
        }
        //fail();
    }
}
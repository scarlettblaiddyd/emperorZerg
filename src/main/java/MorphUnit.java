import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class MorphUnit extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private final UnitType type;
    private final UnitType source;
    private int num;
    private boolean wait;
    private boolean expansion;

    @Override
    public void start(){
        super.start();
    }

    public void reset(){

    }

    public MorphUnit(ChalkBoard info, UnitType type, int num, boolean wait){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.type = type;
        this.num = num;
        this.wait = wait;
        this.source = UnitType.Zerg_Larva;
        this.expansion = false;
    }

    public MorphUnit(ChalkBoard info, UnitType type, UnitType source, int num, boolean wait){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.type = type;
        this.num = num;
        this.wait = wait;
        this.source = source;
        this.expansion = false;
    }

    public MorphUnit(ChalkBoard info, UnitType type, UnitType source, int num, boolean wait, boolean expansion){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.type = type;
        this.num = num;
        this.wait = wait;
        this.source = source;
        this.expansion = expansion;
    }

    @Override
    public void act(ChalkBoard info){
        // CHECK FOR MINERALS
        if (info.pcb.self.minerals() < type.mineralPrice()) {
            if(!wait){
                fail();
            }
            return;
        }
        if( (type != UnitType.Zerg_Overlord) && (self.supplyTotal() - self.supplyUsed() < 2) ) {
            if(!wait){
                fail();
            }
            return;
        }
        else if(type == UnitType.Zerg_Overlord && info.pcb.morphingUnits.contains(UnitType.Zerg_Overlord)){
            fail();
            return;
        }
        for (Unit trainer : info.pcb.self.getUnits()) {
            UnitType unitType = trainer.getType();
            if (unitType == source && !unitType.buildsWhat().isEmpty() && !trainer.isMorphing() && !trainer.canCancelMorph()) {
                if(expansion){
                    if(trainer.getDistance(info.pcb.expansion) > 500)
                        continue;
                }
                if (trainer.canMorph()) {
                    //System.out.println("BASE: Found a unit to morph into " + type + ", " + trainer);
                    if(!trainer.morph(type)){
                        continue;
                    }
                    num -=1;
                    System.out.println("BASE: Morphing new unit: " + type.toString()  +", number left: " + num);
                    if(num <= 0) {
                        succeed();
                    }
                    return;
                }
            }
        }
        fail();
    }
}
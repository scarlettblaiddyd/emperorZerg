import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class MorphStructure extends Routine {

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private final UnitType type;
    private int num;
    private boolean wait;

    @Override
    public void start(){
        super.start();
        System.out.println("BASE: Potentially morphing a new " + type + " waiting? " + wait);
    }

    public void reset(){

    }

    public MorphStructure(ChalkBoard info, UnitType type, int num){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.type = type;
        this.num = num;
        this.wait = false;
    }

    public MorphStructure(ChalkBoard info, UnitType type, int num, boolean wait){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.type = type;
        this.num = num;
        this.wait = wait;
    }


    @Override
    public void act(ChalkBoard info){
        // CHECK FOR MINERALS
        if (info.pcb.self.minerals() < type.mineralPrice() || info.pcb.self.gas() < type.gasPrice()) {
            if(!wait) {
                fail();
            }
            return;
        }
        for (Unit building : info.pcb.self.getUnits()) {
            UnitType unitType = building.getType();
            if (unitType.isBuilding() && !unitType.buildsWhat().isEmpty() && !building.isMorphing() && !building.canCancelMorph()) {
                if (building.canMorph()) {
                    System.out.println("BASE: Found a unit to morph into " + type + ", " + building.getType());
                    if(!building.morph(type)){
                        continue;
                    }
                    num -=1;
                    System.out.println("BASE: Morphing new building: " + type.toString()  +", number left: " + num);
                    if(num <= 0) {
                        succeed();
                    }
                    //info.pcb.buildTypes.add(type);
                    return;
                }
            }
        }
        //fail();
    }
}
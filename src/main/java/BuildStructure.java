import bwapi.*;

public class BuildStructure extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private final UnitType structure;
    private Unit morpher = null;
    private TilePosition structureTile = null;
    private final boolean wait;
    private final boolean expansion;


    @Override
    public void start(){
        super.start();
    }

    public void reset() {
        morpher = null;
        structureTile = null;
        start();
    }

    public BuildStructure(ChalkBoard info, UnitType structure, boolean wait){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.structure = structure;
        this.wait = wait;
        this.expansion = false;
    }

    public BuildStructure(ChalkBoard info, UnitType structure, boolean wait, boolean exp){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.structure = structure;
        this.wait = wait;
        this.expansion = exp;
    }

    public void act(ChalkBoard info) {
        for(Unit unit: self.getUnits()){
            if(unit.getType() == structure && unit.getRemainingBuildTime() > 0){
                succeed();
                //info.pcb.buildings.add(unit);
                //info.pcb.buildTypes.add(unit.getType());
                return;
            }
        }
        if (self.minerals() < structure.mineralPrice() || self.gas() < structure.gasPrice()) {
            if(!wait){
                fail();
            }
            return;
        }
        if (morpher == null) {
            for (Unit unit : self.getUnits()) {
                if (unit.getType().isWorker() && morpher == null && info.pcb.scout != unit && !unit.isCarrying()) {
                    if(expansion){
                        if(unit.getDistance(info.pcb.expansion) > 200)
                            continue;
                    }
                    else if(unit.getDistance(self.getStartLocation().toPosition()) > 200)
                        continue;
                    morpher = unit;
                    morpher.stop();
                    break;
                }
            }
        }
        if (morpher != null) {
            if (structureTile == null) {
                if(expansion){
                    structureTile = game.getBuildLocation(structure, morpher.getTilePosition());
                }
                else {
                    structureTile = game.getBuildLocation(structure, self.getStartLocation());
                }
            }
            if(morpher.build(structure, structureTile)){
                System.out.println("BASE: Building " + structure);
                //succeed();
            }
        }
    }
}

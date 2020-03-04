import bwapi.*;

public class BuildStructure extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private final UnitType structure;
    private Unit morpher = null;
    private TilePosition structureTile = null;


    @Override
    public void start(){
        super.start();
    }

    public void reset() {
        morpher = null;
        structureTile = null;
        start();
    }

    public BuildStructure(ChalkBoard info, UnitType structure){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.structure = structure;
    }
    
    public void act(ChalkBoard info) {
        for(Unit unit: self.getUnits()){
            if(unit.getType() == structure){
                succeed();
                return;
            }
        }
        if (self.minerals() < structure.mineralPrice() || self.gas() < structure.gasPrice()) return;
        if (morpher == null) {
            for (Unit unit : self.getUnits()) {
                if (unit.getType().isWorker() && morpher == null && info.pcb.scout != unit && !unit.isCarrying() && unit.getDistance(self.getStartLocation().toPosition()) < 200) {
                    morpher = unit;
                    morpher.stop();
                    break;
                }
            }
        }
        if (morpher != null) {
            if (structureTile == null) {
                structureTile = game.getBuildLocation(structure, self.getStartLocation());
            }
            morpher.build(structure, structureTile);
        }
    }
}

import bwapi.*;

public class BuildExtractor extends Routine{

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit morpher = null;
    private TilePosition extractorTile = null;


    @Override
    public void start(){
        super.start();
    }

    public void reset() {
        morpher = null;
        extractorTile = null;
        start();
    }

    public BuildExtractor(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
    }

    public BuildExtractor(Game game, Player self, enemyChalkBoard enemy){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
    }
    public void act(ChalkBoard info) {
        // CHECK FOR MINERALS
        for(Unit unit: self.getUnits()){
            if(unit.getType() == UnitType.Zerg_Extractor){
                succeed();
                return;
            }
        }
        if (self.minerals() < 50) return;
        if(morpher == null) {
            for (Unit unit : self.getUnits()) {
                if (unit.getType().isWorker() && morpher == null && info.pcb.scout != unit) {
                    morpher = unit;
                    morpher.stop();
                    break;
                }
            }
        }
        if (morpher != null) {
            if (extractorTile == null) {
                extractorTile = game.getBuildLocation(UnitType.Zerg_Extractor, self.getStartLocation());
            }
            morpher.build(UnitType.Zerg_Extractor, extractorTile);
        }
    }
}

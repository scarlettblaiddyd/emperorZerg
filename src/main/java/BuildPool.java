import bwapi.*;

public class BuildPool extends Routine{

    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Unit morpher = null;
    private TilePosition poolTile = null;


    @Override
    public void start(){
        super.start();
    }

    public void reset() { }

    public BuildPool(Game game, Player self, enemyChalkBoard enemy){
        super();
        this.game = game;
        this.self = self;
        this.enemy = enemy;
    }
    public void act(Game game, Player self, enemyChalkBoard enemy) {
        // BUILD A SPAWNING POOL
        // CHECK FOR MINERALS
        for(Unit unit: self.getUnits()){
            if(unit.getType() == UnitType.Zerg_Spawning_Pool){
                succeed();
                return;
            }
        }
        if (self.minerals() < 200) return;
        if (morpher == null) {
            for (Unit unit : self.getUnits()) {
                if (unit.getType().isWorker() && morpher == null) {
                    morpher = unit;
                    morpher.stop();
                    break;
                }
            }
        }
        if (morpher != null) {
            if (poolTile == null) {
                poolTile = game.getBuildLocation(UnitType.Zerg_Spawning_Pool, self.getStartLocation());
            }
            morpher.build(UnitType.Zerg_Spawning_Pool, poolTile);
        }
    }
}

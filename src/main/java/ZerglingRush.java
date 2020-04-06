import bwapi.*;
import bwapi.Player;
import bwapi.Position;

public class ZerglingRush extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Position target = null;
    private final int num;

    public void reset() {

    }

    public void start(){
        super.start();
    }

    public ZerglingRush(ChalkBoard info, int num){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.num = num;
    }


    public void act(ChalkBoard info) {
        if(enemy.basePos.peek() != null && target == null){
            System.out.println("ARMY: Enemy base located, trying to prep Zergling Rush");
            target = enemy.basePos.getFirst();
        }
        int zerglings = 0;
        for(Unit unit: info.pcb.army){
            if(unit.getType() == UnitType.Zerg_Zergling && unit.isIdle()){
                zerglings++;
            }
        }
        if(zerglings >= num){
            for(Unit zergling: info.pcb.army){
                if(zergling.getType() == UnitType.Zerg_Zergling && zergling.isIdle()) {
                    zergling.attack(target);
                }
            }
            System.out.println("ARMY: Zerglings ready, rushing enemy base");
            succeed();
        }
        else{
            fail();
        }
    }
}

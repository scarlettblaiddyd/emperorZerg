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
            System.out.println("ARMY: Enemy base located, prepping attack");
            target = enemy.basePos.getFirst();
        }
        int zerglings = 0;
        for(Unit unit: self.getUnits()){
            if(unit.getType() == UnitType.Zerg_Zergling && !info.pcb.army.contains(unit)){
                zerglings++;
            }
        }
        if(zerglings >= num){
            System.out.println("ARMY: Zerglings ready, rushing enemy base");
            for(Unit zergling: self.getUnits()){
                if(zergling.getType() == UnitType.Zerg_Zergling && !info.pcb.army.contains(zergling)) {
                    info.pcb.army.add(zergling);
                    zergling.attack(target);
                }
            }
            succeed();
        }
    }
}

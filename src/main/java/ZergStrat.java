import bwapi.*;

public class ZergStrat extends Routine {
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Selector selector;

    @Override
    public void start(){
        super.start();
    }

    public void reset() {
        selector = new Selector();
        start();
    }

    public ZergStrat(ChalkBoard info, Selector selector){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.selector = selector;
    }

    public void act(ChalkBoard info) {
        if (enemy.race != Race.Zerg) {
            System.out.println("VS ZERG FAIL");
            fail();
        }
        this.selector.addRoutine(new BaseIdle(50));

        selector.addRoutine(new NinePool(info, new Sequencer()));
        int drones = 0;
        for(Unit unit : self.getUnits()){
            UnitType unitType = unit.getType();
            if(unitType.isWorker()) {
                drones++;
            }
        }

        if(info.pcb.larva >= 1) {
            if(self.supplyTotal() - self.supplyUsed() < 2){
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Overlord, 1, false));
            }
            if((drones < 14 && self.supplyTotal() >= 50) || (drones < 9 && self.supplyTotal() >= 34)){
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 1, false));
            }
            // What unit to build?
            int zerglings = 0;
            int hydralisks = 0;
            int lurkers = 0;
            for (UnitType unit : info.pcb.armyTypes) {
                if (unit == UnitType.Zerg_Zergling)
                    zerglings++;
                else if (unit == UnitType.Zerg_Hydralisk)
                    hydralisks++;
                else if (unit == UnitType.Zerg_Lurker)
                    lurkers++;
            }


            if (zerglings < 8) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 1, false));
            } else if (zerglings > hydralisks * 2 && info.pcb.buildTypes.contains(UnitType.Zerg_Hydralisk_Den)) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Hydralisk, 1, false));
            } else if (hydralisks > lurkers && info.pcb.tech.contains(TechType.Lurker_Aspect)) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Lurker, UnitType.Zerg_Hydralisk, 1, false));
            }
        }
        selector.addRoutine(new MidgameBuilds(info));


        selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 1, false));
        selector.act(info);
        if(selector.isFailure() || selector.isSuccess()){
            reset();
        }
    }
}

import bwapi.*;

public class ZergStrat extends Routine {
    private final ChalkBoard info;
    private final Game game;
    private final Player self;
    private final enemyChalkBoard enemy;
    private Selector selector;

    public void start(ChalkBoard info){
        if(selector == null){
            selector = new Selector();
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

        if(info.pcb.larva >= 2) {
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

            if(self.supplyTotal() - self.supplyUsed() < 2){
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Overlord, 1, true));
            }
            if((drones < 14 && self.supplyTotal() >= 80) || (drones < 12 && self.supplyTotal() >= 50) || (drones < 9 && self.supplyTotal() >= 34)){
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 1, false));
            }


            if (zerglings < 8) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 1, false));
            } else if (zerglings > hydralisks * 2 && info.pcb.buildTypes.contains(UnitType.Zerg_Hydralisk_Den)) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Hydralisk, 1, false));
            } else if (hydralisks > lurkers * 2 && info.pcb.tech.contains(TechType.Lurker_Aspect)) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Lurker, UnitType.Zerg_Hydralisk, 1, false));
            }
            else if(zerglings * 2 < hydralisks)
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 1, false));
        }

        int creeps = 0;
        int spores = 0;
        int sunken = 0;
        for(UnitType builds: info.pcb.buildTypes){
            if(builds == UnitType.Zerg_Sunken_Colony){
                sunken++;
            }
            else if(builds == UnitType.Zerg_Spore_Colony){
                spores++;
            }
            else if(builds == UnitType.Zerg_Creep_Colony){
                creeps++;
            }
        }

        if(creeps > 0 && sunken < 4){
            System.out.println("BASE: Turning creep colony into Sunken Colony");
            this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Sunken_Colony, 1, true));
        }
        else if(creeps + sunken + spores < 4 && info.pcb.playstyle == Playstyle.DEFENSIVE){
            System.out.println("BASE: On the defensive, constructing creep colony");
            this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Creep_Colony, true));
        }


        selector.addRoutine(new MidgameBuilds(info));

        if(info.pcb.goLate){
            selector.addRoutine(new LateGameBuilds(info));
        }


        this.state = RoutineState.Running;

    }

    public void reset() {
        selector = null;
        this.state = null;
        start();
    }

    public ZergStrat(ChalkBoard info){
        super();
        this.info = info;
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.selector = null;
    }

    public void act(ChalkBoard info) {
        if (enemy.race != Race.Zerg) {
            System.out.println("VS ZERG FAIL");
            fail();
            return;
        }
        if(selector == null){
            this.start(info);
            selector.start();
            return;
        }
        if (selector.isRunning()) {
            if (!game.isInGame()) {
                fail();
                return;
            }
            selector.act(info);
        }
        else if(selector.isSuccess()){
            succeed();
            System.out.println("BASE: ZergStrat: Success");
            this.reset();
        }
        else if(selector.isFailure()){
            fail();
            System.out.println("BASE: ZergStrat: Failure");
            this.reset();
        }
        else{
            selector.start();
        }
    }
}

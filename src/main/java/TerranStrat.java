import bwapi.*;

public class TerranStrat extends Routine {
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
            int mutalisks = 0;
            for (UnitType unit : info.pcb.armyTypes) {
                if (unit == UnitType.Zerg_Zergling)
                    zerglings++;
                else if (unit == UnitType.Zerg_Hydralisk)
                    hydralisks++;
                else if (unit == UnitType.Zerg_Lurker)
                    lurkers++;
                else if (unit == UnitType.Zerg_Mutalisk)
                    mutalisks++;
            }

            if(self.supplyTotal() - self.supplyUsed() < 2){
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Overlord, 1, true));
            }
            if((drones < 14 && self.supplyTotal() >= 80) || (drones < 12 && self.supplyTotal() >= 50) || (drones < 9 && self.supplyTotal() >= 34)){
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Drone, 1, false));
            }


            if (zerglings < 6) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 1, false));
            } else if (zerglings > hydralisks && info.pcb.buildTypes.contains(UnitType.Zerg_Hydralisk_Den)) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Hydralisk, 1, false));
            }
            else if(mutalisks*2 < zerglings && info.pcb.buildTypes.contains(UnitType.Zerg_Spire))
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Mutalisk, 1, false));
            else if (hydralisks > lurkers * 2 && info.pcb.tech.contains(TechType.Lurker_Aspect)) {
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Lurker, UnitType.Zerg_Hydralisk, 1, false));
            }
            else if(zerglings * 3 < hydralisks)
                selector.addRoutine(new MorphUnit(info, UnitType.Zerg_Zergling, 1, false));
        }


        if(info.pcb.playstyle == Playstyle.DEFENSIVE){
            System.out.println("BASE: Terran Strategy defensive");
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
            if(sunken < 4 && creeps > 0) {
                System.out.println("BASE: Turning creep colony into Sunken Colony");
                this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Sunken_Colony, 1, true));
            }
            if(creeps + sunken + spores < 4) {
                System.out.println("BASE: On the defensive, constructing creep colony");
                this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Creep_Colony, true));
            }
        }
        else if (info.pcb.playstyle == Playstyle.OFFENSIVE) {
            System.out.println("BASE: Terran strategy offensive");
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
            if(creeps + sunken + spores < 2){
                this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Creep_Colony, false));
            }
            if(sunken < 2 && creeps > 0 ) {
                this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Sunken_Colony, 1));
            }
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

    public TerranStrat(ChalkBoard info){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.selector = null;
    }


    public void act(ChalkBoard info) {
        if (info.ecb.race != Race.Terran) {
            System.out.println("BASE: VS TERRAN FAIL");
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
            System.out.println("BASE: TerranStrat: Success");
            this.reset();
        }
        else if(selector.isFailure()){
            fail();
            System.out.println("BASE: TerranStrat: Failure");
            this.reset();
        }
        else{
            selector.start();
        }
        //game.drawTextScreen(10, 70, selector.routineQueue.toString());
    }
}

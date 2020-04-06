import bwapi.*;

public class TerranStrat extends Routine {
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

    public TerranStrat(ChalkBoard info, Selector selector){
        super();
        this.game = info.game;
        this.self = info.pcb.self;
        this.enemy = info.ecb;
        this.selector = selector;
    }


    public void act(ChalkBoard info) {
        if (info.ecb.race != Race.Terran) {
            System.out.println("BASE: VS TERRAN FAIL");
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

        if(info.pcb.playstyle == Playstyle.DEFENSIVE){
            int spores = 0;
            int sunken = 0;
            for(UnitType builds: info.pcb.buildTypes){
                if(builds == UnitType.Zerg_Sunken_Colony){
                    sunken++;
                }
                else if(builds == UnitType.Zerg_Spore_Colony){
                    spores++;
                }
            }
            if(sunken < 2) {
                if (!info.pcb.buildTypes.contains(UnitType.Zerg_Creep_Colony)) {
                    System.out.println("BASE: On the defensive, constructing creep colony");
                    this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Creep_Colony, false));
                } else {
                    System.out.println("BASE: Turning creep colony into Sunken Colony");
                    this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Sunken_Colony, 1));
                }
            }
            else if(spores < 2){
                if (!info.pcb.buildTypes.contains(UnitType.Zerg_Creep_Colony)) {
                    System.out.println("BASE: On the defensive, constructing creep colony");
                    this.selector.addRoutine(new BuildStructure(info, UnitType.Zerg_Creep_Colony, true));
                } else {
                    System.out.println("BASE: Turning creep colony into Spore Colony");
                    this.selector.addRoutine(new MorphStructure(info, UnitType.Zerg_Spore_Colony, 1));
                }
            }
        }

        selector.addRoutine(new MidgameBuilds(info));

        selector.act(info);
        if(selector.isFailure() || selector.isSuccess()){
            reset();
        }
        //game.drawTextScreen(10, 70, selector.routineQueue.toString());
    }
}

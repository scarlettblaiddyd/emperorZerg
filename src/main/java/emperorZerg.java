import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;
import bwapi.*;

import java.util.*;

class scoutChalkBoard{
    Unit scout;
    int dist[];
    int parent[];
    Region src;
    Region closest_to_start;
    int scoutInd;
    int finalPath[];
    int currDest;
    String context;
    List<TilePosition> startPos;
}

class ChalkBoard {
    Game game;
    BWClient bwClient;
    enemyChalkBoard ecb;
    playerChalkBoard pcb;
}

enum Playstyle {
    OFFENSIVE, DEFENSIVE;
}

enum UnitClass{
    enemyCombatant,
    playerCombatant,
    enemyBuilding,
    enemyCombatBuilding,
    enemyBase,
    playerBuilding,
    enemyWorker,
    playerWorker,
    unimportant
}

class playerChalkBoard{
    Player self;
    LinkedList<Unit> army;
    LinkedList<UnitType> armyTypes;
    LinkedList<Unit> buildings;
    LinkedList<UnitType> buildTypes;
    LinkedList<UnitType> morphingUnits;
    Boolean buildOrderComplete;
    Boolean expansionSecured;
    Position expansion;
    Unit scout;
    Position scoutRally;
    Region scoutDest;
    Position scoutTarget;
    LinkedList<Region> unexplored;
    LinkedList<Region> expansionUnexplored;
    Position battleField;
    Hashtable<UpgradeType, Integer> upgrades;
    LinkedList<TechType> tech;
    Playstyle playstyle;
    int strength;
    int larva;
    int scoutCD;
}

class enemyChalkBoard{
    Player self;
    Race race;
    LinkedList<Unit> army;
    LinkedList<UnitType> armyTypes;
    LinkedList<Unit> buildings;
    LinkedList<UnitType> buildTypes;
    Unit startBase;
    Boolean destroyedBase;
    LinkedList<Position> basePos;
    int strength;
}


public class emperorZerg extends DefaultBWListener {
    BWClient bwClient;
    Game game;
    Player self;
    List<Region> MapRegions;
    scoutChalkBoard sChalk = new scoutChalkBoard();
    enemyChalkBoard enemy  = new enemyChalkBoard();
    playerChalkBoard player = new playerChalkBoard();
    ChalkBoard info = new ChalkBoard();
    LinkedList<UnitType> morphingUnits = new LinkedList<UnitType>();
    Routine buildRepeater;
    Routine armyRepeater;
    boolean skipFrame = false;
    //LinkedList<UnitType> enemyBuildings     = new LinkedList<UnitType>();

    // TESTING STUFF
    Routine testRepeater;

    void newScoutPath(){
        // Get a list of all the starting positions on the map
        System.out.println("Start position list before removing my start location" + sChalk.startPos);
        // Remove the starting position that matches the player
        Iterator<TilePosition> it = sChalk.startPos.iterator();
        while(it.hasNext()){
            if (self.getStartLocation().equals(it.next())){
                System.out.println("Removing my start location");
                it.remove();
            }
        }
        System.out.println("Start position list after removing my start location" + sChalk.startPos);

        // Find the region that corresponds to a possible enemy starting location
        // This region will be passed on to dijkstras
        sChalk.closest_to_start = null;
        for(Region region : MapRegions){
            if (sChalk.closest_to_start != null){
                if( sChalk.startPos.get(0).toPosition().getDistance(region.getCenter()) < sChalk.startPos.get(0).toPosition().getDistance(sChalk.closest_to_start.getCenter())){
                    sChalk.closest_to_start = region;
                }
            }
            else{
                sChalk.closest_to_start = region;
            }
        }
        System.out.println("Start position list before removing new goal" + sChalk.startPos);
        // We just used startPos of 0, must remove it from the list of start locations
        // So that if our scout doesn't find the final position, we can get a new one
        sChalk.startPos.remove(0);
        System.out.println("Start position list after removing new goal" + sChalk.startPos);

        // Where the scout wants to go, a potential start location for the enemy
        sChalk.scoutInd = MapRegions.indexOf(sChalk.closest_to_start);
        sChalk.context = "Start New Path";
        System.out.println("Scout index is: " + sChalk.scoutInd);

        // Call djikstra's the make the path
        dijkstra();
    }


    @Override
    public void onStart() {
        // Setup those nice global variables that we use all the time
        info.bwClient = bwClient;
        game = bwClient.getGame();
        info.game = game;
        self = game.self();
        player.self = self;

        // Player attributes
        info.ecb = enemy;
        info.pcb = player;
        info.pcb.morphingUnits = morphingUnits;
        info.pcb.buildOrderComplete = false;
        info.pcb.expansionSecured = false;
        info.pcb.expansion = null;
        info.pcb.scout = null;
        info.pcb.scoutRally = null;
        info.pcb.army = new LinkedList<Unit>();
        info.pcb.armyTypes = new LinkedList<UnitType>();
        // A dictionary to tell us which upgrades we have researched
        info.pcb.upgrades = new Hashtable<UpgradeType, Integer>();
        info.pcb.upgrades.put(UpgradeType.Metabolic_Boost, 0);
        info.pcb.buildings = new LinkedList<Unit>();
        info.pcb.buildTypes = new LinkedList<UnitType>();
        info.pcb.tech = new LinkedList<TechType>();
        info.pcb.playstyle = Playstyle.OFFENSIVE;
        info.pcb.strength = 0;
        info.pcb.larva = 0;
        info.pcb.scoutCD = 0;
        info.pcb.scoutRally = self.getStartLocation().toPosition();
        info.pcb.scoutDest = null;
        info.pcb.scoutTarget = null;
        info.pcb.unexplored = new LinkedList<Region>(game.getAllRegions());
        info.pcb.expansionUnexplored = new LinkedList<Region>(game.getAllRegions());


        MapRegions = game.getAllRegions();
        sChalk.scout = null;

        sChalk.startPos = new LinkedList<TilePosition>(game.getStartLocations());

        // Set known enemy attributes
        enemy.self = game.enemies().get(0);
        enemy.race  = enemy.self.getRace();
        enemy.buildings = new LinkedList<Unit>();
        enemy.buildTypes = new LinkedList<UnitType>();
        enemy.army = new LinkedList<Unit>();
        enemy.armyTypes = new LinkedList<UnitType>();
        enemy.basePos = new LinkedList<Position>();
        enemy.startBase = null;
        enemy.destroyedBase = false;
        enemy.strength = 0;
        //newScoutPath();

        buildRepeater = new Repeat(new BaseRepeat(info));
        armyRepeater = new Repeat(new ArmyRepeat(info));

        // TESTING STUFF
        testRepeater = new Repeat(new TestRepeat(info));
    }

    public void compareStrength(ChalkBoard info){
        int selfStrength = 0;
        int enemyStrength = 0;
        for(UnitType unit : info.pcb.armyTypes){
            selfStrength += unit.mineralPrice();
        }

        for(UnitType unit: info.ecb.armyTypes){
            enemyStrength += unit.mineralPrice();
        }

        if(info.pcb.playstyle == Playstyle.OFFENSIVE && enemyStrength >= selfStrength + 500){
            info.pcb.playstyle = Playstyle.DEFENSIVE;
            System.out.println("META: Switching to defensive playstyle");
        }
        else if(info.pcb.playstyle == Playstyle.DEFENSIVE && selfStrength >= enemyStrength + 500){
            info.pcb.playstyle = Playstyle.OFFENSIVE;
            System.out.println("META: Switching to offensive playstyle");
        }
        info.pcb.strength = selfStrength;
        info.ecb.strength = enemyStrength;
    }

    public UnitClass identifyUnit(Unit unit){
        UnitType type = unit.getType();
        if(type.isBuilding()){
            if(unit.getPlayer() == self)
                return UnitClass.playerBuilding;
            else if(self.isEnemy(unit.getPlayer()) ) {
                if(type ==UnitType.Terran_Command_Center ||
                type == UnitType.Zerg_Hatchery ||
                type == UnitType.Protoss_Nexus)
                    return UnitClass.enemyBase;
                if(type == UnitType.Protoss_Photon_Cannon)
                    return UnitClass.enemyCombatBuilding;
                return UnitClass.enemyBuilding;
            }
            else
                return UnitClass.unimportant;
        }
        else{
            if(unit.getPlayer() == self) {
                if(type.isWorker())
                    return UnitClass.playerWorker;
                else if(type == UnitType.Zerg_Zergling || type == UnitType.Zerg_Hydralisk || type == UnitType.Zerg_Lurker)
                    return UnitClass.playerCombatant;
                else
                    return UnitClass.unimportant;
            }
            if(self.isEnemy(unit.getPlayer()) && (unit.getPlayer().getType() == PlayerType.Player) || (unit.getPlayer().getType() == PlayerType.Computer)){
                if(type.isWorker())
                    return UnitClass.enemyWorker;
                else
                    return UnitClass.enemyCombatant;
            }
            return UnitClass.unimportant;
        }
    }

    @Override
    public void onFrame() {
        // Temporary fix to make us go back on the offensive without a scout
        //compareStrength(info);
        info.pcb.buildings = new LinkedList<Unit>();
        info.pcb.buildTypes = new LinkedList<UnitType>();
        info.pcb.armyTypes = new LinkedList<UnitType>();
        info.pcb.army = new LinkedList<Unit>();
        info.pcb.larva = 0;
        int zerglings = 0;
        int hydralisks = 0;
        int lurkers = 0;
        for(Unit unit: self.getUnits()){
            if(unit.getType().isBuilding() && unit.getType() != UnitType.Zerg_Larva){
                info.pcb.buildings.add(unit);
                info.pcb.buildTypes.add(unit.getType());
            }
            else if(unit.getType() == UnitType.Zerg_Larva){
                info.pcb.larva++;
            }
            else if(identifyUnit(unit) == UnitClass.playerCombatant){
                if(unit.getType() == UnitType.Zerg_Zergling)
                    zerglings++;
                else if(unit.getType() == UnitType.Zerg_Hydralisk)
                    hydralisks++;
                else if(unit.getType() == UnitType.Zerg_Lurker)
                    lurkers++;
                info.pcb.army.add(unit);
                info.pcb.armyTypes.add(unit.getType());
            }
        }


        if(buildRepeater.getState() == null){
            buildRepeater.start();
        }
        if(armyRepeater.getState() == null){
            armyRepeater.start();
        }
        if(skipFrame) {
            armyRepeater.act(info);
            skipFrame = false;
        }
        else{
            buildRepeater.act(info);
            skipFrame = true;
        }

/*
        if(testRepeater.getState() == null){
            testRepeater.start();
        }
        else if(skipFrame){
            testRepeater.act(info);
            skipFrame = false;
            Routine manage = new ManageDrones(info);
            manage.start();
            manage.act(info);
        }
        else{
            skipFrame = true;
        }
*/
        // General info for keeping track of AI behavior
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + "-" + self.getRace());
        game.drawTextScreen(10, 30, "Morphing units:" + morphingUnits);
        game.drawTextScreen(10 , 40, "Zerglings: " + zerglings +", Hydralisks: " + hydralisks + ", Lurkers: " + lurkers);
        //game.drawTextScreen(10, 40, "Army: " + info.pcb.armyTypes);
        game.drawTextScreen(10, 50, "Building types: " + info.pcb.buildTypes);
        game.drawTextScreen(10, 60, "Enemy army: " + info.ecb.armyTypes);
        game.drawTextScreen(10, 70, "Total player army mineral cost: " + info.pcb.strength);
        game.drawTextScreen(10, 80, "Total enemy army mineral cost: " + info.ecb.strength);
        game.drawTextScreen(10, 200,"Behavior: " + info.pcb.playstyle);


        game.drawCircleMap(self.getStartLocation().toPosition(), 500, Color.Yellow);

        if(info.pcb.scout != null) {
            game.drawTextMap(info.pcb.scoutRally, "SCOUT RALLY POSITION");
            game.drawCircleMap(info.pcb.scout.getPosition(), 400, Color.Red);
            game.drawCircleMap(info.pcb.scout.getPosition(), 150, Color.Purple);
            game.drawCircleMap(info.pcb.scoutRally, 250, Color.Red);
        }
        if(info.pcb.expansionSecured){
            game.drawTextMap(info.pcb.expansion, "Expansion center here");
            game.drawCircleMap(info.pcb.expansion, 500, Color.Yellow);
        }
        if(info.pcb.battleField != null){
            game.drawTextMap(info.pcb.battleField, "Battlefield center");
            game.drawCircleMap(info.pcb.battleField, 600, Color.White);
        }
        if(info.pcb.scoutDest != null){
            game.drawTextMap(info.pcb.scoutDest.getCenter(), "Scout's destination");
        }

        //game.drawTextScreen(10, 60,"Enemy units: " + enemy.buildings);
        int cnt = 0;
        for(Unit unit : self.getUnits()){
            UnitType unitType = unit.getType();
            if(unitType.isWorker()) {
                cnt++;
            }
        }
        game.drawTextScreen(10, 90,"Drones: " + cnt);



        // Print all starting positions for reference
        List<TilePosition> startPos = game.getStartLocations();
        for (TilePosition pos : startPos){
            game.drawTextMap(pos.toPosition(), "Starting Location");
        }

        // Draw an X on the center of each region
        if(info.pcb.unexplored.size() > 0) {
            for (Region region : info.pcb.unexplored) {
                Position center = region.getCenter();
                game.drawTextMap(center, "x");
            }
        }

        /****************************SCOUT BEHAVIOR***************************************/
        /*
        // If there is no scout, find a worker to turn into a scout
        if (sChalk.scout == null){
            System.out.println("Looking for a unit to turn into a scout");
            for(Unit unit : self.getUnits()){
                UnitType unitType = unit.getType();
                if(unitType.isWorker()){
                    System.out.println("Found a unit to turn into scout");
                    sChalk.scout = unit;
                    sChalk.scout.stop();
                    // Once we have the unit, run djikstra's to find it a path
                    newScoutPath();
                    break;
                }
            }

        }

        // As long as we have a scout, print out the path it is following on the map
        if( sChalk.scout != null ){
            printSolution();
        }

        // For debugging, print out the scout's current state
        game.drawTextScreen( 10, 150, "Context is: " + sChalk.context);

        // If the scout exists, is idle, and has not just received an order this past frame
        if ( (sChalk.scout != null) && (sChalk.scout.isIdle()) && !sChalk.context.equals("Just Received Orders")){
            if(sChalk.currDest < sChalk.finalPath.length) {
                sChalk.scout.move(MapRegions.get(sChalk.finalPath[sChalk.currDest]).getCenter());
                //System.out.println("Moving scout to: " + sChalk.finalPath[sChalk.currDest]);
                sChalk.currDest++;
                sChalk.context = "Just Received Orders";
            }
        } // If the scout has just received orders, change its context identifier
        else if(sChalk.context.equals("Just Received Orders")){
            sChalk.context = "Heading to Destination";
        }
        // Check if the scout has reached it's destination region
        if(sChalk.scout.getRegion() == MapRegions.get(sChalk.finalPath[sChalk.finalPath.length-1])){
            sChalk.context = "Arrived at destination";
            if(enemy.buildings.contains(UnitType.Terran_Command_Center) || enemy.buildings.contains(UnitType.Zerg_Hatchery) || enemy.buildings.contains(UnitType.Protoss_Nexus)){
                sChalk.context = "Found enemy base";
            }
            else if(sChalk.startPos.size() > 0){
                System.out.println("Scout arrived at start location. Looking for new destination");
                newScoutPath();
                sChalk.context = "Ready to start";
            }
        }

        */

        /****************** TRAINING UNITS ************************/
        // Train new overlords when supply is low
        /*
        if (self.supplyTotal() - self.supplyUsed() <= 2 && self.supplyTotal() <= 400 && !morphingUnits.contains(UnitType.Zerg_Overlord)){
            game.drawTextScreen(20 , 20, "Trying to make overlord");
            Unit morpher = null;
            for (Unit unit: self.getUnits()){
                if (unit.getType().isBuilding() && !unit.getType().buildsWhat().isEmpty()) {
                    morpher = unit;
                    break;
                }
            }
            morpher.morph(UnitType.Zerg_Overlord);
        }
        else {
            // Train drones when we can, but only if we have the supply for it

            for (Unit trainer : self.getUnits()) {
                UnitType unitType = trainer.getType();
                if (unitType.isBuilding() && !unitType.buildsWhat().isEmpty()) {
                    UnitType toTrain = unitType.buildsWhat().get(0);
                    if (trainer.canMorph()) {
                        trainer.morph(UnitType.Zerg_Drone);
                    }
                }
            }
        }*/
    }

    /*****************When we start morphing a unit, add it to the list*******************************/
    public void onUnitMorph(Unit unit){
        if(unit.getPlayer() == self) {
            System.out.println("Unit morphing: " + unit.getType());
            morphingUnits.add(unit.getBuildType());
            if(unit.getBuildType() == UnitType.Zerg_Zergling){
                morphingUnits.add(unit.getBuildType());
            }
        }
    }

    /*****************When we discover an enemy unit***********************************************/
    public void onUnitDiscover(Unit unit){
        UnitClass uClass = identifyUnit(unit);
        //if(unit.getType().isBuilding() && self.isEnemy(unit.getPlayer()) && !enemy.buildings.contains(unit)) {
        if(uClass == UnitClass.enemyBuilding || uClass == UnitClass.enemyBase){
            if(!enemy.buildings.contains(unit) && uClass == UnitClass.enemyBase){
                System.out.println("META: Adding enemy base location to list");
                enemy.basePos.add(unit.getPosition());
            }
            if(!enemy.buildings.contains(unit)) {
                System.out.println("META: Discovered building of type: " + unit.getType());
                enemy.buildings.add(unit);
                enemy.buildTypes.add(unit.getType());
            }
        }
        else if (uClass == UnitClass.enemyCombatant || uClass == UnitClass.enemyCombatBuilding){
        //else if( self.isEnemy(unit.getPlayer()) && !(enemy.army.contains(unit))  && (unit.getPlayer().getType() == PlayerType.Player) || (unit.getPlayer().getType() == PlayerType.Computer)) {
            if(!enemy.army.contains(unit)) {
                System.out.println("META: Enemy combat discovered: " + unit.getType());
                enemy.army.add(unit);
                enemy.armyTypes.add(unit.getType());
            }
        }
        else if (uClass == UnitClass.enemyWorker){
            // Do nothing?
        }
        /* COMPARING STRENGTH // SIMULATION START */
        AssSimulator simulator = null;
        for(Unit enemy: info.ecb.army){
            if(enemy.isVisible(info.pcb.self)){
                simulator = new AssSimulator(info);
                break;
            }
        }
        if(simulator != null)
            simulator.act(info);
        /* COMPARING STRENGTH // SIMULATION END */

    }

    public void onUnitComplete(Unit unit) {
        if (unit.getType().isWorker() ){
            // If a new worker spawns, find the closest mineral to it
            Unit closestMineral = null;
            int closestDistance = Integer.MAX_VALUE;
            for(Unit mineral : game.getMinerals()) {
                int distance = unit.getDistance(mineral);
                if ( distance < closestDistance) {
                    closestMineral = mineral;
                    closestDistance = distance;
                }
            }
            // Gather the closest
            unit.gather(closestMineral);
        }
        if ( morphingUnits.contains(unit.getType())){
            morphingUnits.remove(unit.getType());
        }
        if (morphingUnits.contains(UnitType.None)) {
            morphingUnits.remove(UnitType.None);
        }
        if(identifyUnit(unit) == UnitClass.playerCombatant){
            //info.pcb.army.add(unit);
            //info.pcb.armyTypes.add(unit.getType());
        }

        /* COMPARING STRENGTH // SIMULATION START */
        if(identifyUnit(unit) == UnitClass.playerCombatant) {
            AssSimulator simulator = null;
            simulator = new AssSimulator(info);
            if (simulator != null)
                simulator.act(info);
        }
        /* COMPARING STRENGTH // SIMULATION END */

    }

    public void onUnitDestroy(Unit unit){
        if(unit == info.pcb.scout){
            info.pcb.scoutCD = 40;
        }
        if(info.pcb.army.contains(unit)){
            //info.pcb.army.remove(unit);
            //info.pcb.armyTypes.remove(unit.getType());
        }
        else if(info.ecb.army.contains(unit)){
            info.ecb.army.remove(unit);
            info.ecb.armyTypes.remove(unit.getType());
        }
        else if(info.ecb.buildings.contains(unit)){
            info.ecb.buildings.remove(unit);
            info.ecb.buildTypes.remove(unit);
        }
        if(identifyUnit(unit) == UnitClass.enemyBase){
            if(enemy.startBase == unit) {
                enemy.destroyedBase = true;
                System.out.println("META: Enemy's start base destroyed");
            }
            if(enemy.basePos.contains(unit.getPosition())) {
                System.out.println("META: Removing enemy base location from list");
                enemy.basePos.remove(unit.getPosition());
            }
        }
        /* COMPARING STRENGTH // SIMULATION START */
        AssSimulator simulator = null;
        simulator = new AssSimulator(info);
        if(simulator != null)
            simulator.act(info);
        /* COMPARING STRENGTH // SIMULATION END */

    }

    public int minDistance(int dist[], boolean visited[]){
        int min = Integer.MAX_VALUE;
        int min_index = 0;

        for(int v = 0; v < MapRegions.size(); v++){
            if (visited[v] == false && dist[v] <= min){
                min = dist[v];
                min_index = v;
            }
        }
        return min_index;
    }

    public void printSolution(){
        for(int i = 0; i < sChalk.finalPath.length - 1; i++){
            game.drawLineMap(MapRegions.get(sChalk.finalPath[i]).getCenter(), MapRegions.get(sChalk.finalPath[i+1]).getCenter(), Color.Cyan);
        }
    }

    public void dijkstra(){
        System.out.println("Heyo starting disjtea");
        //System.out.print(MapRegions.size());
        // Get the region for the starting vertex
        sChalk.src = sChalk.scout.getRegion();

        // Translate the region to an index of our list
        int start = MapRegions.indexOf(sChalk.src);

        sChalk.dist = new int[MapRegions.size()];
        sChalk.parent = new int[MapRegions.size()];

        // Initialize array of values indicating we have searched
        // at this node already
        boolean visited[] = new boolean[MapRegions.size()];
        for(int i = 0; i < MapRegions.size(); i++){
            //System.out.println("Initializing Region " + i );
            visited[i] = false;
            sChalk.parent[0] = -1;
            sChalk.dist[i]  = Integer.MAX_VALUE;
        }

        sChalk.dist[MapRegions.indexOf(sChalk.src)] = 0;

        // Main for loop
        for(int count = 0; count < MapRegions.size() -1; count++){
            //System.out.println("Count: " + count);
            int u = minDistance(sChalk.dist, visited);

            visited[u] = true;

            for(int v = 0; v < MapRegions.size(); v++){
                if( MapRegions.get(u).getNeighbors().contains((MapRegions.get(v))) && !visited[v] && MapRegions.get(v).isAccessible() && sChalk.dist[u] != Integer.MAX_VALUE && sChalk.dist[u] + MapRegions.get(u).getDistance(MapRegions.get(v)) < sChalk.dist[v]){
                    sChalk.dist[v] = sChalk.dist[u] + MapRegions.get(u).getDistance(MapRegions.get(v));
                    sChalk.parent[v] = u;
                }
            }
        }

        // Get the path to the enemy base, reverse it, then store it to the chalkboard
        ArrayList<Integer> scoutPath = new ArrayList<Integer>();
        while(sChalk.parent[sChalk.scoutInd] != -1){
            scoutPath.add(sChalk.scoutInd);
            sChalk.scoutInd = sChalk.parent[sChalk.scoutInd];
        }
        sChalk.finalPath = new int[scoutPath.size()];
        for(int i = 0; i < scoutPath.size(); i++){
            sChalk.finalPath[scoutPath.size()-i-1] = scoutPath.get(i).intValue();
        }
        System.out.println("Final path: " + Arrays.toString(sChalk.finalPath));
        // Set the scouts current goal to index 0 of the final path
        sChalk.currDest = 0;
    }


    void run() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public static void main(String[] args) {
        new emperorZerg().run();
    }
}
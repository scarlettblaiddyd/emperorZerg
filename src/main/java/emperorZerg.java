import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;
import bwapi.*;
import sun.nio.cs.ext.JISAutoDetect;

import javax.sound.midi.SysexMessage;
import java.rmi.MarshalException;
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

class enemyChalkBoard{
    Player enemy;
    Race race;
    LinkedList<UnitType> army;
    LinkedList<UnitType> buildings;
    LinkedList<Position> basePos;
}

public class emperorZerg extends DefaultBWListener {
    BWClient bwClient;
    Game game;
    Player self;
    List<Region> MapRegions;
    scoutChalkBoard sChalk = new scoutChalkBoard();
    enemyChalkBoard enemy  = new enemyChalkBoard();
    LinkedList<UnitType> morphingUnits = new LinkedList<UnitType>();
    //LinkedList<UnitType> enemyBuildings     = new LinkedList<UnitType>();

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
        game = bwClient.getGame();
        self = game.self();
        MapRegions = game.getAllRegions();
        sChalk.scout = null;

        sChalk.startPos = new LinkedList<TilePosition>(game.getStartLocations());

        // Set known enemy attributes
        enemy.enemy = game.enemies().get(0);
        enemy.race  = enemy.enemy.getRace();
        enemy.buildings = new LinkedList<UnitType>();
        //newScoutPath();
    }

    @Override
    public void onFrame() {
        // General info for keeping track of AI behavior
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + "-" + self.getRace());
        game.drawTextScreen( 10, 230, "Resources: " + self.minerals() + " minerals, " + self.gas() + " gas");
        game.drawTextScreen(10, 30, "Morphing units:" + morphingUnits);
        game.drawTextScreen(10, 40, "Enemy is playing as " + enemy.race);
        game.drawTextScreen(10, 50,"Enemy units: " + enemy.buildings);


        // Print all starting positions for reference
        List<TilePosition> startPos = game.getStartLocations();
        for (TilePosition pos : startPos){
            game.drawTextMap(pos.toPosition(), "Starting Location");
        }

        // Draw an X on the center of each region
        for(int i = 0; i < MapRegions.size(); i++){
            Position center = MapRegions.get(i).getCenter();
            game.drawTextMap(center, "x");
        }

        /****************************SCOUT BEHAVIOR***************************************/
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


        /****************** TRAINING UNITS ************************/
        // Train new overlords when supply is low
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
        }
    }

    /*****************When we start morphing a unit, add it to the list*******************************/
    public void onUnitMorph(Unit unit){
        if(unit.getPlayer() == self) {
            System.out.println("Unit morphing: " + unit.getType());
            morphingUnits.add(unit.getBuildType());
        }
    }

    /*****************When we discover an enemy unit***********************************************/
    public void onUnitDiscover(Unit unit){
        if(unit.getType().isBuilding() && self.isEnemy(unit.getPlayer())) {
            System.out.println("Discovered building of type: " + unit.getType());
        }
        if(self.isEnemy(unit.getPlayer()) && (unit.getPlayer().getType() == PlayerType.Player) || (unit.getPlayer().getType() == PlayerType.Computer)) {
            System.out.println("Enemy unit discovered: " + unit.getType());
            enemy.buildings.add(unit.getType());
        }
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
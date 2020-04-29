# starcraft-bot-emperorZerg

## Bot Milestone 2 Writeup

### High-Level Strategy:

Our bot's current high level strategy employs a classic early-game "build order" known as 9 Pool. This build order focuses on getting early Zerglings and sending them to harass and slow the enemies build progress. It even has the potential to overwhelm the enemy and end the game early, but even if it fails to do so, it will hopefully slow their own strategy enough to give us an edge in the mid game. Ramping up into the mid game, we start building new structures and researching new upgrades, as Zerglings are not as powerful in the mid game.

This strategy is implemented via a behavior tree mechanism. We chose a behavior tree because of its modularity. This modularity allows us to not only re-use entire behaviors when desired, but also to easily replace or modify entire behaviors. For example, if we wished to use a different build order in the early game, we need only change the contents of a single branch in our tree.

### Control Mechanism Implementation:

The behavior tree is implemented through a generic "Routine" class, which describes methods to start itself, take action on a given frame, and indicate success or failure to any parent Routines. Derived from the generic Routine class are our three main node types: Repeaters, Sequencers, and Selectors. These all act in the behavior tree as their namesakes insinuate. A repeater will repeat a routine until it recieves a "fail" signal. A selector will be given routines and put them into a queue, which it will, in the given order, run each routine until it recieves a 'successful' signal. A sequencer will run every routine it is given, sending up a 'successful' signal after it has completed all of the routines in its queue.

The higherarchy looks something like this:

- Repeat
  - Selector
    - TerranStrat
    - ZergStrat
      - Sequencer
        - MorphUnit
        - ScoutEnemy
    - ProtossStrat

  We use two trees, each with their own base Repeat routine and unique sub-routines. They alternate operation to avoid race conditions, such as both of them issuing a different command to the same unit on the same frame in the game.

In order to determine whether emporerZerg is in a favorable or disfavorable position for fighting, we have implemented the Agent Starcraft Simulator's 'simulator' functionality. It basically runs a simulation of a fight using our current army against the opponents currently known army from emporerZerg's scouted knowledge / real time knowledge.
https://github.com/JavaBWAPI/ass

## Installation

After ensuring that you have all the dependencies listed below, clone this repository, and run the command from within it:
```
make build
```

    
## Dependencies

### Docker for Ubuntu

Must have docker for Ubuntu installed.

Make sure you can run docker without sudo (based on https://docs.docker.com/engine/installation/linux/linux-postinstall/#manage-docker-as-a-non-root-user)
```
sudo usermod -aG docker $USER
```

Log out and log back in so that your group membership is re-evaluated (close and open terminal window).


### VNC Viewer

VNC https://www.realvnc.com/en/

Install RealVNC viewer for viewing GUI headful modes from the docker images.

Save the executable in PATH so that it can be launched as vnc-viewer

Something like

```
sudo ln -s [where-you-put-vnc] /usr/bin/vnc-viewer
```


## Extending this project

First things first, you'll want to follow this tutorial to set up an IDE and download Stracraft with Chaoslauncher, doing so makes it much easier to test and debug the bot: https://www.sscaitournament.com/index.php?action=tutorial

Documentation for BWAPI can be found here: https://javabwapi.github.io/JBWAPI/
You will likely need to reference these pages a LOT while you're figuring out how to write routines, at least at first. The most important classes to have a good understanding of are Unit, UnitType, Player, and Game. Also! In case it wasn't clear, this bot is written in java.

This bot's behavior is defined by three behavior trees. Two of them run at all times (ArmyRepeat and BaseRepeat), while one of them only runs when the bot is trying to create an expansion (BuildExpansion). However, if you're just starting with the bot, it would be a good idea to go into the main class, emperorZerg.java, and comment out lines 320-344. Then, uncomment lines 346-360. This makes it so that the bot uses the "TestRepeat" behavior tree.

In TestRepeat, the building blocks for any sort of behavior are in place. You can see that currently, in the "start" function, it adds 5 different routines to the selector (see control mechanism implementation for details on selectors). If you run TestRepeat right out of the box, the bot will attempt to
1) Scout the enemy base
2) Build an expansion
3) Balance drones harvesting minerals and vespene gas at home base
4) Balance drones harvesting at the expansion
5) Execute the midgamebuilds routine

Many routines print statements to the console telling the user what they are currently executing. For example, if you open up ScoutEnemy, you can see that it prints out when it is looking for a unit to turn int oa scout, when it finds a scout, when it finds an enemy base, and pretty much anytime an important command is sent to Brood War API.

If you would like to implement new behavior, the first thing to do is check if a generic routine exists for it. For example, if you want to implement behavior to make a Zerg Queen defend the base, you could simply use the UnitDefendBase routine and specify in the constructor that you want it to control Zerg Queens.

If there is no generic routine, you must create a new class that extends the Routine superclass. Each Routine subclass should implement a start and an act function. The start function will be called by a Selector, Sequencer, or Repeater, so you should use it to set up variables that you're going to use in your behavior that were not set in the constructor. For example, in the OverLordBehavior routine, we find a freindly unit to move the OverLord to depending on whether our playstyle is set to DEFENSIVE or OFFENSIVE. Keep in mind, it is good practice to manually call the start function from inside the first few lines of the act function if you detect that any of your important variables are not set. For example, in OverLordBehavior, we check to see if the friendly variable is null. Null indicates that we haven't found a friendly unit to send our overlords to, so we cann the start function if it is null.

The act function will be called every frame by a Repeater, Selector, or Sequencer until it either calls the fail() or succeed() function. This is where you want to send commands to BWAPI or manage meta data about your behvaior. For example, in OverLordBehavior, we try to move overlords toward friendly units. The boolean ordered starts out as false, but if we send a move command to any overlords in the course of the act function, we set ordered to true. At the end of the function, if ordered is true, we call succeed, but if it is false we call fail, before returning. This tells the Sequencer, Selector, or Repeater that caleld this behavior whether or not it succeeded by setting a the ROUTINE_STATE variable. These control mechanisms all check the ROUTINE_STATE variable before calling act on a routine.

Once you have written a routine, it's a good idea to test in out with TestRepeat before inserting it into the main behavior trees. Choose a control mechanism for test repeat, either a repeater, selector, or sequencer, and in the start function, make sure that if the control mechanism is null (see line 34 of the current TestRepeat class), create a new object of that type. If you're using a selector or sequencer, you'll want to use the addRoutine function to add your routine to it. If you're using a repeater, you'll want to declare the routine inside the Repeater constructor.

Then, you can build and start running your bot. In the console, it will print "Game table mapping not found" if no errors were encountered. If you then launch Starcraft through the Chaoslauncher with the BWAPI Injector enabled, the bot should connect and immediately start executing the TestRepeat routine.

There are also a few notable functions in the emperorZerg class. Brood War API calls onUnitMorph, onUnitDiscover, onUnitComplete, and onUnitDestroy every time a unit morphs, is discovered, is completed, or is destroyed in the game. These functions do some bookkeeping, such as tracking the enemy's army units or calling the AssSimulator routine. 

The AssSimulator routine is the only routine that currently is called outside of a behavior tree. It is programmed to fail or succeed with a single call to act, and is run every time we want to re-evaluate our army's standing vs our opponents army.

Finally, there are a few "Chalkboard" style classes that emperorZerg uses to share data inside the behavior trees. There is the overall Chalkboard class, which contains an instance of enemyChalkboard and playerChalkboard. enemyChalkboard keeps track of things like the enemies race, their playerID, what buildings or army units they have, and a list of the positions of their bases. playerChalkboard contains information about our own buildings and army units, and contains several flags for important milestones in play, such as completing our basic build order or starting our lategame routines.

If you have any specific questions about anything relating to this bot, please feel free to reach out to me at thomas.eckburg@gmail.com. You can also check out the official SSCAIT Discord Server to chat with other people who write starcraft brood war bots!


## Known Bugs

For some reason, if our units exceed our control because a Dark Archon steals an Overlord, the bot will not attempt to morph any more overlords. Cause unknown.

If the expansion scout is killed before turning into a hatchery but after finding a suitable expansion location, the bot will never morph a hatchery at the expansion because there will be no Drones deemed "close enough" to do so. Cause known, need to fiddle with BuildStructure and BuildExpansion to let it grab any drone for use in building the hatchery.

If a building is suddenly unable to be constructed when a buildstructure routine has been called with the "wait" variable set to true (such as being unable to construct an Ultralisk Den because our Hive was destroyed), the bot will sometimes never fail out of that routine. Cause unknown.

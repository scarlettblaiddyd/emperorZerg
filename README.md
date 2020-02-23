# starcraft-bot-emperorZerg

## Bot Milestone 2 Writeup

### High-Level Strategy:

Our bot's current high level strategy employs a classic early-game "build order" known as 9 Pool. This build order focuses on getting early Zerglings and sending them to harass and slow the enemies build progress. It even has the potential to overwhelm the enemy and end the game early, but even if it fails to do so, it will hopefully slow their own strategy enough to give us an edge in the mid game.

This strategy is implemented via a behavior tree mechanism. We chose a behavior tree because of its modularity. This modularity allows us to not only re-use entire behaviors when desired, but also to easily replace or modify entire behaviors. For example, if we wished to use a different build order in the early game, we need only change the contents of a single branch in our tree.

Control Mechanism Implementation:

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

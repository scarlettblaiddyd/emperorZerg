# starcraft-bot-emperorZerg

## Bot Milestone 2 Writeup

This bot implements a behavior tree which sets up an early-game strategy (known as 9 Pool), then sends units at the enemy base to attack it and potentially take it down before they can set up their strategy. 

This high-level strategy works by implementing our behavior tree control mechanism, using a selector to determine the enemy race the bot is facing, and with that information implements a set strategy. Within the specific strategy, a sequencer is used to loop through the strategy implementation in order to get the correct build order so that the troops can be sent to correctly harass the enemy base.

Control Mechanism Implementation:

The behavior tree is implemented through classes called routines. These all act in the behavior tree as their namesakes insinuate. A repeater will repeat a routine until it recieves a "fail" signal. A selector will be given routines and put them into a queue, which it will, in the given order, run each routine until it recieves a 'successful' signal. A sequencer will run every routine it is given, sending up a 'successful' signal after it has completed all of the routines in its queue.

The higherarchy looks something like this:

- Repeat
  - Selector
    - TerranStrat
    - ZergStrat
      - Sequencer
        - MorphUnit
        - ScoutEnemy
    - ProtossStrat
    
## Dependencies

Must have docker for Ubuntu installed.

Make sure you can run docker without sudo (based on https://docs.docker.com/engine/installation/linux/linux-postinstall/#manage-docker-as-a-non-root-user)
```
sudo usermod -aG docker $USER
```

Log out and log back in so that your group membership is re-evaluated (close and open terminal window).

VNC https://www.realvnc.com/en/

Install RealVNC viewer for viewing GUI headful modes from the docker images.

Save the executable in PATH so that it can be launched as vnc-viewer

Something like

```
sudo ln -s [where-you-put-vnc] /usr/bin/vnc-viewer
```

Clone this repository, and run our makefile with "make build".

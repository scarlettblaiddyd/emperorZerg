# starcraft-bot-emperorZerg

## Bot Milestone 2 Writeup

This bot implements a behavior tree which sets up an early-game strategy (known as 9 Pool), then sends units at the enemy base to attack it and potentially take it down before they can set up their strategy. 

This high-level strategy works by implementing our behavior tree control mechanism, using a selector to determine the enemy race the bot is facing, and with that information implements a set strategy. Within the specific strategy, a sequencer is used to loop through the strategy implementation in order to get the correct build order so that the troops can be sent to correctly harass the enemy base.

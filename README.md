# starcraft-bot-zergtastic

## Functionality/HLS

    This bot implements a behavior tree which sets up an early-game strategy (known as 9 Pool), the sends units at the enemy base to attack it and potentially take it down before they can set up their strategy. 

    This works by implementing a selector to determine which enemy the bot is facing, then to implement a strategy based on the opponent. Once it determines its opponent, it uses a sequencer to loop through the strategy implementation in order to get the correct build order so that the troops can be sent to correctly harass the enemy base. 

    

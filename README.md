Forked from AddstarMC/Minigames

This fork allows for the creation of Memory (Match Match, Match Up etc.) style of games. It does this by providing an additional Regional
Action that swaps all specified blocks in that region with random blocks in a way that the blocks always have paires.

To use this action, built a gameboard that has a way to conceal and reviel blocks. All blocks that will be placed by this action can
pushed and pulled by pistons. In this game board have marker stones that mark the postion of the blockes that will be replaced with the
game blocks. Define a region around that gameboard.

Next set the MemorySwapBlockAction for that region and in the menu, define the matchblock to the marker block inside your gameboard.
Optional you can also define a blacklist of blocks that should not appear on the gameboard (to prevent clashes with your gameboard
design). The format is of this blacklist is entry,entry,entry ... with every entry either being a simple block name like STONE or WOOL or
a pair of name and data field like STONE:0 or WOOL:12. If only a block name is provided, all blocks with the same name will not be placed.
So WOOL would prevent all WOOL blocks to be placed, WOOL:15 on the other hand would only prevent BLACK WOOL.

After setting up this action, be sure to trigger it only once when the game starts. This provides a new, random playing field for every
game.

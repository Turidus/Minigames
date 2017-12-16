Forked from AddstarMC/Minigames

# Additions to Minigame Region:

## Actions:
### MemorySwapBlogAction
A Regional Action that swaps all specified blocks in that region with random blocks in a way that the blocks always have paires.

To use this action, built a gameboard that has a way to conceal and reveal blocks. All blocks that will be placed by this action can
be pushed and pulled by pistons. In this game board have marker stones that mark the postion of the blockes that can be revealed and
concealed. Define a region around that gameboard.

Next set the MemorySwapBlockAction for that region and in the menu, define the matchblock to the marker block inside your gameboard.
Optional you can also define a blacklist of blocks that should not appear on the gameboard (to prevent clashes with your gameboard
design). The format is of this blacklist is entry,entry,entry ... with every entry either being a simple block name like STONE or WOOL or
a pair of name and data field like STONE:0 or WOOL:12. If only a block name is provided, all blocks with the same name will not be placed.
So WOOL would prevent all WOOL blocks to be placed, WOOL:15 on the other hand would only prevent BLACK WOOL.

After setting up this action, be sure to trigger it only once when the game starts. This provides a new, random playing field for every
game.

The maximum gameboard size is 120 gameblocks. This action will warn if the gameboard is to big and skip surplus blocks.

### RegionSwapAction in MinigamesRegion
This action swaps or replaces all blocks in on region with the blocks of a different region of equal size

### TimedTriggerNodeAction
This Action triggers a target node after a certain amount of time. If the action wants to execute after the minigame has already ended
a NullPointerExeption occurs.

### TimedTriggerRegionAction
This Action triggers a target region after a certain amount of time. If the action wants to execute after the minigame has already ended
a NullPointerExeption occurs.

## Triggers:
### Killer Trigger
This trigger fires on player death but targets the killer instead of the dead player

### TimedRemoteTrigger
This trigger gets executed if a **TimedTriggerNodeAction** or **TimedTriggerRegionAction** targets this node/region.

## Conditions
### BlockHeldAndOnCondition
This condition gives true if the player has the same block in their main hand then the block the player is standing on

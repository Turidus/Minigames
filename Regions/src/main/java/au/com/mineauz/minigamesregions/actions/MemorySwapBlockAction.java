package au.com.mineauz.minigamesregions.actions;


import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.actions.MemorySwapBlockAction.PhantomBlock;

/**
 * @author Turidus  https://github.com/Turidus/Minigames
 * 
 * This class provides the methods necessary to fill a gameboard with pairs of blocks randomly placed.
 * Its a region action and can as such only run inside a region.
 * 
 * The user can define two options in the menu.
 * A) the matchBlock, the block that is the placeholder in the game and which will be replaced by the random blocks
 * B) the blacklist, which removes blocks from the given blockPool to provide a free choice in gameboard design.
 * 	Removed blocks will not appear on the gameboard.
 * 	The blacklist follows this format: block:data,block2,block3,block4:data4 (no spaces)
 * 	If the block data is provided, only precise matches with that block will be removed.
 * 	If the block data is not provided, all blocks with that name will be removed. For example,
 * 	if the entry is WOOL, all WOOL blocks, no matter the color will be removed, but if the entry
 * 	is WOOL:1, only orange WOOL will be removed.
 *
 */
public class MemorySwapBlockAction extends AbstractAction {
	
	/**
	 * 
	 * @author Turidus
	 * This helper class plays the role of a struct like object and 
	 * represents an abstract block with a name, an data field and an boolean
	 * that indicates if this block should be precisely matched if compared 
	 * to other blocks, which defaults to true.
	 *
	 */
	static class PhantomBlock {
		
		public String blockName = "Stone";
		public boolean blockMatchData = true;
		public int blockDataValue = 0;
		
		public PhantomBlock(String name) {
			this.blockName = name;
			
		}
		
		public PhantomBlock(String name, int dataValue) {
			this.blockName = name;
			this.blockDataValue = dataValue;
		}
		
		public PhantomBlock(String name,boolean bool) {
			this.blockName = name;
			this.blockMatchData = bool;
		}
		
		public PhantomBlock(String name,boolean bool, int dataValue) {
			this.blockName = name;
			this.blockMatchData = bool;
			this.blockDataValue = dataValue;
		}
	}
	
	private StringFlag matchType = new StringFlag("WOOL", "matchtype");
	private BooleanFlag matchData = new BooleanFlag(true, "matchdata");
	private IntegerFlag matchDataValue = new IntegerFlag(2, "matchdatavalue");
	private StringFlag blacklist = new StringFlag("", "blacklist");
	
	/*
	 * Building a blockPool to provide the blocks that could be used in the game.
	 */
	private static ArrayList<PhantomBlock> blockPool = new ArrayList<PhantomBlock>();
	
	/**
	 * Helper function to fill the blockPool with PhantomBlocks
	 * @param phantomBlock
	 */
	private static void addToBlockPool(PhantomBlock phantomBlock){
		blockPool.add(phantomBlock);
	}

	/*
	 * Filling the block pool with blocks than can be pulled and pushed by pistons manually
	 */
	static {
		/* TODO Maybe an automatic way of dealing with this. Problem: some curation is necessary
		 * to prevent blocks that are to visual similar to appear, for example quartz and white 
		 * concrete. Letting the user sort this out with the blacklist results in a very long
		 * blacklist string, which is annoying for the user.
		 */
		
		
		//Resource blocks
		addToBlockPool(new PhantomBlock("DIAMOND_BLOCK"));
		addToBlockPool(new PhantomBlock("IRON_BLOCK"));
		addToBlockPool(new PhantomBlock("EMERALD_BLOCK"));
		addToBlockPool(new PhantomBlock("GOLD_BLOCK"));
		addToBlockPool(new PhantomBlock("LAPIS_BLOCK"));
		
		//Concrete
		addToBlockPool(new PhantomBlock("CONCRETE",0));
		addToBlockPool(new PhantomBlock("CONCRETE",1));
		addToBlockPool(new PhantomBlock("CONCRETE",2));
		addToBlockPool(new PhantomBlock("CONCRETE",3));
		addToBlockPool(new PhantomBlock("CONCRETE",4));
		addToBlockPool(new PhantomBlock("CONCRETE",5));
		addToBlockPool(new PhantomBlock("CONCRETE",6));
		addToBlockPool(new PhantomBlock("CONCRETE",7));
		addToBlockPool(new PhantomBlock("CONCRETE",8));
		addToBlockPool(new PhantomBlock("CONCRETE",9));
		addToBlockPool(new PhantomBlock("CONCRETE",10));
		addToBlockPool(new PhantomBlock("CONCRETE",11));
		addToBlockPool(new PhantomBlock("CONCRETE",12));
		addToBlockPool(new PhantomBlock("CONCRETE",13));
		addToBlockPool(new PhantomBlock("CONCRETE",14));
		addToBlockPool(new PhantomBlock("CONCRETE",15));
		
		//Ore blocks
		addToBlockPool(new PhantomBlock("COAL_ORE"));
		addToBlockPool(new PhantomBlock("DIAMOND_ORE"));
		addToBlockPool(new PhantomBlock("IRON_ORE"));
		addToBlockPool(new PhantomBlock("REDSTONE_ORE"));
		addToBlockPool(new PhantomBlock("EMERALD_ORE"));
		addToBlockPool(new PhantomBlock("GOLD_ORE"));
		addToBlockPool(new PhantomBlock("LAPIS_ORE"));
		addToBlockPool(new PhantomBlock("QUARTZ_ORE"));		
		
		//Wool blocks
		addToBlockPool(new PhantomBlock("WOOL",0));
		addToBlockPool(new PhantomBlock("WOOL",1));
		addToBlockPool(new PhantomBlock("WOOL",2));
		addToBlockPool(new PhantomBlock("WOOL",3));
		addToBlockPool(new PhantomBlock("WOOL",4));
		addToBlockPool(new PhantomBlock("WOOL",5));
		addToBlockPool(new PhantomBlock("WOOL",6));
		addToBlockPool(new PhantomBlock("WOOL",7));
		addToBlockPool(new PhantomBlock("WOOL",8));
		addToBlockPool(new PhantomBlock("WOOL",9));
		addToBlockPool(new PhantomBlock("WOOL",10));
		addToBlockPool(new PhantomBlock("WOOL",11));
		addToBlockPool(new PhantomBlock("WOOL",12));
		addToBlockPool(new PhantomBlock("WOOL",13));
		addToBlockPool(new PhantomBlock("WOOL",14));
		addToBlockPool(new PhantomBlock("WOOL",15));
		
		//Logs
		addToBlockPool(new PhantomBlock("LOG",0));
		addToBlockPool(new PhantomBlock("LOG",1));
		addToBlockPool(new PhantomBlock("LOG",2));
		addToBlockPool(new PhantomBlock("LOG",3));
		
		//Planks
		addToBlockPool(new PhantomBlock("WOOD",0));
		addToBlockPool(new PhantomBlock("WOOD",1));
		addToBlockPool(new PhantomBlock("WOOD",2));
		addToBlockPool(new PhantomBlock("WOOD",4));
		addToBlockPool(new PhantomBlock("WOOD",5));		
		
		//Misc
		addToBlockPool(new PhantomBlock("BRICK"));
		addToBlockPool(new PhantomBlock("PRISMARINE"));
		addToBlockPool(new PhantomBlock("SEA_LANTERN"));
		addToBlockPool(new PhantomBlock("SANDSTONE",1));
		addToBlockPool(new PhantomBlock("SMOOTH_BRICK",5));
		addToBlockPool(new PhantomBlock("NETHER_BRICK"));
		addToBlockPool(new PhantomBlock("STONE"));
		addToBlockPool(new PhantomBlock("DIRT"));
		
		
		
	}
	
	/**
	 * Returns a array of PhantomBlock that will consists of all blocks of the block pool minus the ones on the blacklist.
	 * The blacklist string format is block1:data1,block2,block3,block4:data4. If the data field is missing, all blocks with the same
	 * name will be removed (for example all WOOL blocks if WOOL is the entry, but only WHITE WOLL if WOOL:0 is the entry.
	 * 
	 * @param player
	 * @return ArrayList<PhantomBlock>
	 */
	private ArrayList<PhantomBlock> cleanUpBlockPool(MinigamePlayer player) {
		
		
		ArrayList<String> blackListEntries = new ArrayList<String>();
		ArrayList<PhantomBlock> blocksToRemove = new ArrayList<PhantomBlock>();
		ArrayList<PhantomBlock> finalBlockList = new ArrayList<PhantomBlock>(); 
		String blackListString = blacklist.getFlag();
		
		if (blackListString.isEmpty()) {
			return blockPool;
		}
		
		//Parses the blacklist string into strings describing blocks.
		blackListString.trim();
		
		while (blackListString.contains(",")) {
			int commaIndex = blackListString.indexOf(",");
			blackListEntries.add(blackListString.substring(0,commaIndex));
			if (blackListString.length() > commaIndex + 1) {
				blackListString = blackListString.substring(commaIndex + 1);
			}else {
				blackListString = "";
			}
		}
		
		if (!blackListString.isEmpty()) {
			blackListEntries.add(blackListString);
		}
		
				
		//Gets the PhantomBlocks out of the provided blacklist string
		for (String blockString : blackListEntries) {
			
			if (!blockString.contains(":")) {
				blocksToRemove.add(new PhantomBlock(blockString,false)); //Generate blocks that will match all blocks with the same name, regardless of dataValue
			
			}else {
			
				try {
					int colonIndex = blockString.indexOf(":"); //Generates blocks that will be precise matches
					
					
					blocksToRemove.add(new PhantomBlock(blockString.substring(0,colonIndex),Integer.parseInt(blockString.substring(colonIndex + 1))));
				}
				catch(NumberFormatException e){
					player.sendMessage("There was a wrong (non integer) data field in your blacklist");
				}
			}
			
		}
		
		//Cloning blockpool to prevent issues with the block pool
		for (PhantomBlock block : blockPool) {
			finalBlockList.add(new PhantomBlock(block.blockName,block.blockMatchData,block.blockDataValue));
		}
		
		//Removing the blocks from the final block list
		for(PhantomBlock block : blocksToRemove) {
			
			boolean notFound = true;
			
			if(block.blockMatchData) { //Precisely removing the block
				
				for (ListIterator<PhantomBlock> poolIter = finalBlockList.listIterator(); poolIter.hasNext();) {
					
					PhantomBlock poolblock = poolIter.next();
					
					if (block.blockName.equalsIgnoreCase(poolblock.blockName) && block.blockDataValue == poolblock.blockDataValue) {
						poolIter.remove();
						notFound = false;
					}
				}	
			} else {	//Removing all blocks with the same name, not matching the data field
			
				for (ListIterator<PhantomBlock> poolIter = finalBlockList.listIterator(); poolIter.hasNext();) {
					
					PhantomBlock poolblock = poolIter.next();
					
					if (block.blockName.equalsIgnoreCase(poolblock.blockName)) {
						poolIter.remove();
						notFound = false;
					}
				}
			}
			
			if(notFound) {
				player.sendMessage(block.blockName + " was not in the block pool");
			}
		}
		return finalBlockList;
	}
	
	

	@Override
	public String getName() {
		return "MEMORY_SWAP_BLOCK";
	}

	@Override
	public String getCategory() {
		return "Block Actions";
	}

	@Override
	public void describe(Map<String, Object> out) {
		
		if (matchData.getFlag()) {
			out.put("From", matchType.getFlag() + ":" + matchDataValue.getFlag());
		} else {
			out.put("From", matchType.getFlag() + ":all");
		}
		
		out.put("Block pool size", blockPool.size());
		out.put("Blacklist", blacklist.getFlag());
	}
	

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	/**
	 *This will search for a certain type of block (user definable over the menu) and replaced with a random block
	 *from the block pool minus the blacklisted blocks (also user definable over the menu). 
	 *
	 *The block will always have a pair unless there is an odd number of blocks to replace. If there is an odd numbered
	 *amount of blocks there will be one unmatched block and player will be warned.
	 *If there are more blocks to replace than it is possible, the surplus blocks will be skipped and the player will
	 *be warned.
	 */
	public void executeRegionAction(MinigamePlayer player, Region region) {
		
		debug(player,region);
		ArrayList<PhantomBlock> localBlockPool = cleanUpBlockPool(player);
		

		
		ArrayList<Block> blocksToSwap = new ArrayList<Block>();
		ArrayList<PhantomBlock> targetBlocks = new ArrayList<PhantomBlock>();
		
		
		
		 //Collects all blocks to be swapped
		for (int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++) {
			for (int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++) {
				for (int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++) {
					Block block = region.getFirstPoint().getWorld().getBlockAt(x, y, z);
					
					if (block.getType() == Material.getMaterial(matchType.getFlag())) {
						if (matchData.getFlag() && block.getData() != matchDataValue.getFlag().byteValue()) {
							continue;
						}
						
						blocksToSwap.add(block);
					}
				}
			}
		}
		
		
		//Sanity checks that can be handled without throwing a exception but need a warning to player
		if (blocksToSwap.size() % 2 != 0) {
			player.sendMessage("This gameboard has an odd amount of playing fields, there will be unmatched blocks");
		}
		if (blocksToSwap.size() > 2 * localBlockPool.size()) {
			player.sendMessage("This gameboard has more fields then supported by the pool of available blocks (2 * " + localBlockPool.size() + "), there will be unswapt blocks");
		}
		
		//Fill the target list with blocks to swap to
		//TODO removing index and finding a better way to do this loop
		int index = 0;
		while (targetBlocks.size() < blocksToSwap.size()) {
			PhantomBlock tempBlock;
			
			if (index < localBlockPool.size()) {	
				tempBlock = localBlockPool.get(index);
			}else {
				tempBlock = new PhantomBlock("noBlock"); //If there are to few blocks in the block Pool, the list gets filled up with a "noBlock"
			}
			
			targetBlocks.add(tempBlock);
			targetBlocks.add(tempBlock);
			
			index++;
		}
		
		//Shuffle the target list to produce a random game field
		Collections.shuffle(targetBlocks);
		
		
		//Replacing the blocks
		for (int i = 0; i < blocksToSwap.size();i++) {
			
			if (targetBlocks.get(i).blockName == "noBlock") {//Missing blocks in the block pool will be caught here and lead to unswapped blocks
				continue;
			}
			
			Block fromBlock = blocksToSwap.get(i);
			PhantomBlock toBlock = targetBlocks.get(i);
			
			fromBlock.setType(Material.getMaterial(toBlock.blockName));
			fromBlock.setData((byte)toBlock.blockDataValue);
		}
		
		
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		debug(player,node);
	}
	

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		matchType.saveValue(path, config);
		matchData.saveValue(path, config);
		matchDataValue.saveValue(path, config);
		blacklist.saveValue(path, config);
		
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		matchType.loadValue(path, config);
		matchData.loadValue(path, config);
		matchDataValue.loadValue(path, config);
		blacklist.loadValue(path, config);
		
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		 
		Menu m = new Menu(3, "Memory Swap Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final MinigamePlayer fply = player;
		
		//The menu entry for the from block, aka the block that will be replaced
		m.addItem(new MenuItemString("Match Block", Material.COBBLESTONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					matchType.setFlag(value.toUpperCase());
				else
					fply.sendMessage("Invalid block type!", "error");
			}
			
			@Override
			public String getValue() {
				return matchType.getFlag();
			}
		}));
		m.addItem(matchData.getMenuItem("Match Block Use Data?", Material.ENDER_PEARL));
		m.addItem(matchDataValue.getMenuItem("Match Block Data Value", Material.EYE_OF_ENDER, 0, 15));
		
		//Menu entry for the blacklist entry, aka the blocks that will be removed from the block pool
		m.addItem(new MenuItemNewLine());
		m.addItem(new MenuItemString("Blacklist", MinigameUtils.stringToList("Format: WOOL:15,LOG"), Material.BOOK, new Callback<String>() {
			
			
			@Override
			public void setValue(String value) {
				blacklist.setFlag(value);
			}
			
			@Override
			public String getValue() {
				return blacklist.getFlag();
			}
		}));
		
		m.displayMenu(player); 
		
		return false;
	}
	
}
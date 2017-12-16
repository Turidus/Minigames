package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

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
import au.com.mineauz.minigames.config.FloatFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class RegionRandomFillAction extends ActionInterface {

	private StringFlag toType = new StringFlag("COBBLESTONE", "totype");
	private BooleanFlag keepAttachment = new BooleanFlag(false, "keepattachment");
	private BooleanFlag toData = new BooleanFlag(false, "todata");
	private IntegerFlag toDataValue = new IntegerFlag(0, "todatavalue");
	private FloatFlag rndChance = new FloatFlag((float) 50, "rndChance");
	private BooleanFlag useChance = new BooleanFlag(true, "useChance");
	private IntegerFlag preciseNumber = new IntegerFlag(1, "preciseNumber");
	@Override
	public String getName() {
		return "RANDOM_FILL_REGION";
	}

	@Override
	public String getCategory() {
		return "Block Actions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
				
		if (toData.getFlag()) {
			out.put("To", toType.getFlag() + ":" + toDataValue.getFlag());
		} else {
			out.put("To", toType.getFlag());
		}
		
		out.put("Keep Attachment", keepAttachment.getFlag());
		
		if(useChance.getFlag()) {
			out.put("Random Chance", rndChance.getFlag());
			out.put("Use Chance", useChance.getFlag());
		}else {
			out.put("Blocks changed", preciseNumber.getFlag());
			out.put("Use Chance", useChance.getFlag());
		}
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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		
		if (useChance.getFlag()) {
			executeRandomChance(region);
		}else {
			executePreciseNumber(region);
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		
	}
	
	@SuppressWarnings("deprecation")
	public void executeRandomChance(Region region) {
		
		double rndWeight = rndChance.getFlag() / 100;
		
		for (int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++) {
			for (int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++) {
				for (int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++) {
					
					Block block = region.getFirstPoint().getWorld().getBlockAt(x, y, z);
					
					if (Math.random() >= rndWeight) {
						
						BlockState blockState = block.getState();
						blockState.setRawData((byte)0);;
						blockState.setType(Material.AIR);
						blockState.update(true);
						
						continue;
					}
									
										
						// Block matches, now replace it
						byte data = 0;
						BlockFace facing = null;
						if (toData.getFlag()) {
							// Replace data
							data = toDataValue.getFlag().byteValue();
						} else if (keepAttachment.getFlag()) {
							// Keep attachments if possible
							MaterialData mat = block.getState().getData();
							if (mat instanceof Directional) {
								facing = ((Directional)mat).getFacing();
							}
						}
						
						// Update block type
						block.setType(Material.getMaterial(toType.getFlag()), false);
						if (facing != null) {
							BlockState state = block.getState();
							MaterialData mat = block.getState().getData();
							if (mat instanceof Directional) {
								((Directional)mat).setFacingDirection(facing);
							}
							state.setData(mat);
							state.update(true, false);
						} else {
							block.setData(data, false);
						}
					}
				}
			}
	}
	
	public void executePreciseNumber(Region region) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		Random rndNumber = new Random();
		
		for (int y = region.getFirstPoint().getBlockY(); y <= region.getSecondPoint().getBlockY(); y++) {
			for (int x = region.getFirstPoint().getBlockX(); x <= region.getSecondPoint().getBlockX(); x++) {
				for (int z = region.getFirstPoint().getBlockZ(); z <= region.getSecondPoint().getBlockZ(); z++) {
					
										
					blocks.add(region.getFirstPoint().getWorld().getBlockAt(x, y, z));
				}
			}
		}
		
		if (preciseNumber.getFlag() > blocks.size()) {
			return;
		}
		
		for (int i = 0; i < blocks.size(); i++) {
			indexes.add(i);
		}
		
		for (int i = 0; i < preciseNumber.getFlag(); i++) {
			int tempIndex = rndNumber.nextInt(indexes.size());
			Block block = blocks.get(indexes.get(tempIndex));
			indexes.remove(tempIndex);
			
			
			// Block matches, now replace it
			byte data = 0;
			BlockFace facing = null;
			if (toData.getFlag()) {
				// Replace data
				data = toDataValue.getFlag().byteValue();
			} else if (keepAttachment.getFlag()) {
				// Keep attachments if possible
				MaterialData mat = block.getState().getData();
				if (mat instanceof Directional) {
					facing = ((Directional)mat).getFacing();
				}
			}
			
			// Update block type
			block.setType(Material.getMaterial(toType.getFlag()), false);
			if (facing != null) {
				BlockState state = block.getState();
				MaterialData mat = block.getState().getData();
				if (mat instanceof Directional) {
					((Directional)mat).setFacingDirection(facing);
				}
				state.setData(mat);
				state.update(true, false);
			} else {
				block.setData(data, false);
			}					
		}
		
		for (int i : indexes) {
			
			BlockState blockState = blocks.get(i).getState();
			blockState.setRawData((byte)0);;
			blockState.setType(Material.AIR);
			blockState.update(true);			
		}
	}
	
	
	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		toType.saveValue(path, config);
		toData.saveValue(path, config);
		toDataValue.saveValue(path, config);
		keepAttachment.saveValue(path, config);
		useChance.saveValue(path, config);
		rndChance.saveValue(path, config);
		preciseNumber.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		toType.loadValue(path, config);
		toData.loadValue(path, config);
		toDataValue.loadValue(path, config);
		keepAttachment.loadValue(path, config);
		useChance.loadValue(path, config);
		rndChance.loadValue(path, config);
		preciseNumber.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(4, "RandomFillRegion", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final MinigamePlayer fply = player;
			
		m.addItem(new MenuItemString("To Block", Material.STONE, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				if(Material.matchMaterial(value.toUpperCase()) != null)
					toType.setFlag(value.toUpperCase());
				else
					fply.sendMessage("Invalid block type!", "error");
			}
			
			@Override
			public String getValue() {
				return toType.getFlag();
			}
		}));
		m.addItem(toData.getMenuItem("To Block Use Data?", Material.ENDER_PEARL));
		m.addItem(toDataValue.getMenuItem("To Block Data Value", Material.EYE_OF_ENDER, 0, 15));
		m.addItem(keepAttachment.getMenuItem("Keep Attachment", Material.PISTON_BASE, MinigameUtils.stringToList("When on, and To Block Use Data is off;If the source and target block;types are both blocks that;attach to surfaces, this;attachment will be preserved")));
		m.addItem(new MenuItemNewLine());
		m.addItem(rndChance.getMenuItem("Random chance (0.0 - 100.0)", Material.REDSTONE_BLOCK, (double)1, (double)10, (double)0, (double)100));
		m.addItem(useChance.getMenuItem("By Chance?", Material.STONE_BUTTON, MinigameUtils.stringToList("When set to true every Block has the;set random Chance to be switched.;If set to false the precise number will be used.")));
		m.addItem(new MenuItemNewLine());
		m.addItem(preciseNumber.getMenuItem("Precise Number", Material.REDSTONE_COMPARATOR));
		m.displayMenu(player);
		return true;
	}

}

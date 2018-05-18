package au.com.mineauz.minigamesregions.actions;

import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

/***
 * This action fills a region randomly with a new block. There are two modes. Either "replace all",
 * where every block in that region is either replaced by air or the chosen block, 
 * or "replace selective" where blocks in the region are only replaced by the chosen block. 
 * @author Turidus https://github.com/Turidus/Minigames
 *
 */

public class RandomFillingAction extends AbstractAction {
	
	private StringFlag toType = new StringFlag("WOOL", "totype");
	private BooleanFlag toData = new BooleanFlag(true, "todata");
	private IntegerFlag toDataValue = new IntegerFlag(2, "todatavalue");
	private IntegerFlag percentageChance = new IntegerFlag(50, "percentagechance");
	private BooleanFlag replaceAll = new BooleanFlag(true,"replaceAll");

	@Override
	public String getName() {
		return "RANDOM_FILLING";
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
			out.put("To", toType.getFlag() + ":all");
		}
		
		out.put("Chance", percentageChance.getFlag());
		out.put("Replace misses with air", replaceAll.getFlag());
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
	public void executeRegionAction(MinigamePlayer player,
			Region region) {
		debug(player,region);
		Location temp = region.getFirstPoint();
		Random rndGen = new Random();
		for(int y = region.getFirstPoint().getBlockY(); 
				y <= region.getSecondPoint().getBlockY();
				y++){
			temp.setY(y);
			for(int x = region.getFirstPoint().getBlockX();
					x <= region.getSecondPoint().getBlockX();
					x++){
				temp.setX(x);
				for(int z = region.getFirstPoint().getBlockZ();
						z <= region.getSecondPoint().getBlockZ();
						z++){
					temp.setZ(z);
					Integer randomDraw = rndGen.nextInt(100);	//Generating a number between [0-99]
					randomDraw++;								//Adding one to handle edge cases (0 %, 100 %) correctly.
					
					if(randomDraw <= percentageChance.getFlag()){
								
						temp.getBlock().setType(Material.getMaterial(toType.getFlag()), false);
						if (toData.getFlag()) {
							temp.getBlock().setData(toDataValue.getFlag().byteValue(), false);
						}
						
					} else if (replaceAll.getFlag()) {
						temp.getBlock().setType(Material.AIR);
					}
				}
			}
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node) {
		debug(player,node);
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		toType.saveValue(path, config);
		toData.saveValue(path, config);
		toDataValue.saveValue(path, config);
		percentageChance.saveValue(path, config);
		replaceAll.saveValue(path, config);
		
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		toType.loadValue(path, config);
		toData.loadValue(path, config);
		toDataValue.loadValue(path, config);
		percentageChance.loadValue(path, config);
		replaceAll.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		
		Menu m = new Menu(4, "Random Filling", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		final MinigamePlayer fply = player;
		
		//The menu entry for the block that will be placed
		m.addItem(new MenuItemString("To Block", Material.COBBLESTONE, new Callback<String>() {
			
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
		m.addItem(toData.getMenuItem("Match Block Use Data?", Material.ENDER_PEARL));
		m.addItem(toDataValue.getMenuItem("To Block Data Value", Material.EYE_OF_ENDER, 0, 15));
		
		//Percentage of blocks that will replaced
		m.addItem(new MenuItemNewLine());
		m.addItem(new MenuItemInteger("Chance in integer percentage (0-100)", MinigameUtils.stringToList(""), Material.BOOK, new Callback<Integer>() {
			
			
			@Override
			public void setValue(Integer value) {
				percentageChance.setFlag(value);
			}
			
			@Override
			public Integer getValue() {
				return percentageChance.getFlag();
			}
		}, 0, 100));
		
		//Replace all or replace selectively
		m.addItem(new MenuItemNewLine());
		m.addItem(replaceAll.getMenuItem("Replace misses with air?", Material.ENDER_PEARL));
		
		m.displayMenu(player); 
		
		return false;
	}

}

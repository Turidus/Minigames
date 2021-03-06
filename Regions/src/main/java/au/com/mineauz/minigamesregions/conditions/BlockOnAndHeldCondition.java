package au.com.mineauz.minigamesregions.conditions;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemInteger;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

/**
 * 
 * @author Turidus https://github.com/Turidus/Minigames
 *
 *This class allows a check if the first solid block under a player is equal
 *to the block that player holds in his hand.
 */
public class BlockOnAndHeldCondition extends ConditionInterface {

	@Override
	public String getName() {
		return "BLOCK_ON_AMD_HELD";
	}
	
	@Override
	public String getCategory(){
		return "World Conditions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
		
	}

	@Override
	public boolean useInRegions() {
		return false;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public boolean checkRegionCondition(MinigamePlayer player, Region region) {
		return false;
	}

	@Override
	public boolean checkNodeCondition(MinigamePlayer player, Node node) {
		return check(player);
	}
	
	
	private boolean check(MinigamePlayer player){
		
		if(player.equals(null)) return false;
		ItemStack heldItem = player.getPlayer().getInventory().getItemInMainHand();
		if(heldItem.equals(null)) return false;		
		
		Location plyLoc = player.getPlayer().getLocation();
		int plyY = plyLoc.getBlockY();
		//In case that the player is in the air, this searches for the first solid block and checks if it equal
		while(plyY >= 0) {
			plyY -= 1;
			Block tempBlock = player.getPlayer().getWorld().getBlockAt(plyLoc.getBlockX(), plyY, plyLoc.getBlockZ());
			if(tempBlock.getType().equals(heldItem.getType()) && tempBlock.getState().getData().equals(heldItem.getData())){
				return true;
			} else if(!tempBlock.getType().equals(Material.AIR)) { 
				return false;
			}
			
		}
		
		return false;
    }

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu prev) {
		Menu m = new Menu(3, "Match Block", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, prev), m.getSize() - 9);
		addInvertMenuItem(m);
		m.displayMenu(player);
		return true;
	}

}

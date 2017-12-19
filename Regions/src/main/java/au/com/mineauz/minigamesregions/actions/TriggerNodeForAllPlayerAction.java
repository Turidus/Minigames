package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.triggers.Triggers;

/**
 * 
 * @author Turidus https://github.com/Turidus/Minigames
 * 
 * This action triggers a remote node once for every player in the minigame.
 * This is used for allowing a single event to reach all players in a minigame,
 * for example to check all player inventories after one player used a button.
 *
 */
public class TriggerNodeForAllPlayerAction extends ActionInterface {

	private StringFlag node = new StringFlag("None", "node");
	
	@Override
	public String getName() {
		return "TRIGGER_NODE_ALL_PLAYERS";
	}

	@Override
	public String getCategory() {
		return "Remote Trigger Actions";
	}

	@Override
	public void describe(Map<String, Object> out) {
		out.put("Node", node.getFlag());

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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		debug(player,region);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		// TODO Auto-generated method stub
		debug(player,node);
		if (player == null || !player.isInMinigame()) return;
		Minigame mg = player.getMinigame();
		if(mg != null){
			RegionModule rmod = RegionModule.getMinigameModule(mg);
			if(rmod.hasNode(this.node.getFlag())) {
				for (MinigamePlayer targetPlayer : mg.getPlayers()) {
					rmod.getNode(this.node.getFlag()).execute(Triggers.getTrigger("REMOTE"), targetPlayer);
				}
			}
		}

	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		node.saveValue(path, config);

	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		node.loadValue(path, config);

	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Trigger Node", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.addItem(node.getMenuItem("Node Name", Material.EYE_OF_ENDER));
		m.displayMenu(player);
		return true;
	}

}

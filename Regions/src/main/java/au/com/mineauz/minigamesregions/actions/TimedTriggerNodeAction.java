package au.com.mineauz.minigamesregions.actions;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionModule;
import au.com.mineauz.minigamesregions.triggers.Triggers;

public class TimedTriggerNodeAction extends ActionInterface {
	
	private StringFlag node = new StringFlag("None", "node");
	private IntegerFlag delay = new IntegerFlag(20,"delay");

	@Override
	public String getName() {
		return "TIMED_TRIGGER_NODE";
	}

	@Override
	public String getCategory() {
		return "Remote Trigger Actions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
		out.put("Node", node.getFlag());
		out.put("Delay in s", delay.getFlag());
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeRegionAction(MinigamePlayer player,
			Region region) {
		if(player == null || !player.isInMinigame()) return;
		Minigame mg = player.getMinigame();
		if(mg != null){
			RegionModule rmod = RegionModule.getMinigameModule(mg);
			if(rmod.hasNode(node.getFlag()))
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
					
					@Override
					public void run() {
						rmod.getNode(node.getFlag()).execute(Triggers.getTrigger("TIMED_REMOTE"), player);
					}
				}, delay.getFlag());
				
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node2) {
		if(player == null || !player.isInMinigame()) return;
		Minigame mg = player.getMinigame();
		if(mg != null){
			RegionModule rmod = RegionModule.getMinigameModule(mg);
			if(rmod.hasNode(node.getFlag()))
					Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
					
					@Override
					public void run() {
						rmod.getNode(node.getFlag()).execute(Triggers.getTrigger("TIMED_REMOTE"), player);
					}
				}, delay.getFlag());
				
		}
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		node.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		node.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Trigger Node", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.addItem(node.getMenuItem("Node Name", Material.EYE_OF_ENDER));
		m.addItem(delay.getMenuItem("Delay in ticks", Material.ENDER_PEARL));
		m.displayMenu(player);
		return true;
	}

}

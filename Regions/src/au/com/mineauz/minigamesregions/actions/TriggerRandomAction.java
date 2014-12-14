package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.BooleanFlag;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.NodeExecutor;
import au.com.mineauz.minigamesregions.Region;
import au.com.mineauz.minigamesregions.RegionExecutor;

public class TriggerRandomAction extends ActionInterface{
	
	private IntegerFlag timesTriggered = new IntegerFlag(1, "timesTriggered");
	private BooleanFlag randomPerTrigger = new BooleanFlag(false, "randomPerTrigger");

	@Override
	public String getName() {
		return "TRIGGER_RANDOM";
	}

	@Override
	public String getCategory() {
		return "Region/Node Actions";
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
	public void executeRegionAction(MinigamePlayer player, Region region) {
		List<RegionExecutor> exs = new ArrayList<RegionExecutor>();
		for(RegionExecutor ex : region.getExecutors()){
			if(ex.getTrigger().getName().equalsIgnoreCase("RANDOM"))
				exs.add(ex);
		}
		Collections.shuffle(exs);
		if(timesTriggered.getFlag() == 1){
			if(region.checkConditions(exs.get(0), player) && exs.get(0).canBeTriggered(player))
				region.execute(exs.get(0), player);
		}
		else{
			for(int i = 0; i < timesTriggered.getFlag(); i++){
				if(!randomPerTrigger.getFlag()){
					if(i == timesTriggered.getFlag()) break;
					if(region.checkConditions(exs.get(i), player) && exs.get(i).canBeTriggered(player))
						region.execute(exs.get(i), player);
				}
				else{
					if(region.checkConditions(exs.get(0), player) && exs.get(0).canBeTriggered(player))
						region.execute(exs.get(0), player);
					Collections.shuffle(exs);
				}
			}
		}
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		List<NodeExecutor> exs = new ArrayList<NodeExecutor>();
		for(NodeExecutor ex : node.getExecutors()){
			if(ex.getTrigger().getName().equalsIgnoreCase("RANDOM"))
				exs.add(ex);
		}
		Collections.shuffle(exs);
		if(timesTriggered.getFlag() == 1){
			if(node.checkConditions(exs.get(0), player) && exs.get(0).canBeTriggered(player))
				node.execute(exs.get(0), player);
		}
		else{
			for(int i = 0; i < timesTriggered.getFlag(); i++){
				if(!randomPerTrigger.getFlag()){
					if(i == timesTriggered.getFlag()) break;
					if(node.checkConditions(exs.get(i), player) && exs.get(i).canBeTriggered(player))
						node.execute(exs.get(i), player);
				}
				else{
					if(node.checkConditions(exs.get(0), player) && exs.get(0).canBeTriggered(player))
						node.execute(exs.get(0), player);
					Collections.shuffle(exs);
				}
			}
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
		timesTriggered.saveValue(path, config);
		randomPerTrigger.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
		timesTriggered.loadValue(path, config);
		randomPerTrigger.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Trigger Random", player);
		m.addItem(new MenuItemBack(previous), m.getSize() - 9);
		m.addItem(timesTriggered.getMenuItem("Times to Trigger Random", Material.COMMAND, 1, null));
		m.addItem(randomPerTrigger.getMenuItem("Allow Same Executor", Material.ENDER_PEARL, 
				MinigameUtils.stringToList("Should there be a chance;that the same execeutor;can be triggered more?")));
		m.displayMenu(player);
		return true;
	}

}

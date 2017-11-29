package au.com.mineauz.minigamesregions;

import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.set.SetCommand;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.tool.ToolModes;
import au.com.mineauz.minigamesregions.commands.SetNodeCommand;
import au.com.mineauz.minigamesregions.commands.SetRegionCommand;

/**
 * 
 * @author Forked from https://github.com/AddstarMC/Minigames
 *
 */
public class Main extends JavaPlugin{
	
	private static Minigames minigames;
	private static Main plugin;
	
	private RegionDisplayManager display;
	
	@Override
	public void onEnable(){
		try {
			plugin = this;
			Plugin mgPlugin = getServer().getPluginManager().getPlugin("Minigames");
			if(mgPlugin != null && mgPlugin.isEnabled()){
				minigames = (Minigames)mgPlugin;
			} else {
				getLogger().severe("Minigames plugin not found! You must have the plugin to use Regions!");
				plugin = null;
				minigames = null;
				this.getPluginLoader().disablePlugin(this);
				return;
			}
			
			display = new RegionDisplayManager();
			
			minigames.mdata.addModule(RegionModule.class);
			
			SetCommand.registerSetCommand(new SetNodeCommand());
			SetCommand.registerSetCommand(new SetRegionCommand());
			
			getServer().getPluginManager().registerEvents(new RegionEvents(), this);
			
			ToolModes.addToolMode(new RegionToolMode());
			ToolModes.addToolMode(new NodeToolMode());
			ToolModes.addToolMode(new RegionNodeEditToolMode());
			
			getLogger().info("Minigames Regions successfully enabled!");
		} catch (Throwable e) {
			plugin = null;
			minigames = null;
			getLogger().log(Level.SEVERE, "Failed to enable Minigames Regions " + getDescription().getVersion() + ": ", e);
			getPluginLoader().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable(){
		if (plugin == null) {
			return;
		}
		for(Minigame mg : minigames.mdata.getAllMinigames().values()){
			mg.saveMinigame();
		}
		minigames.mdata.removeModule("Regions", RegionModule.class);
		
		ToolModes.removeToolMode("REGION");
		ToolModes.removeToolMode("NODE");
		ToolModes.removeToolMode("REGION_AND_NODE_EDITOR");
		
		display.shutdown();
		
		getLogger().info("Minigames Regions disabled");
	}
	
	public static Minigames getMinigames(){
		return minigames;
	}
	
	public static Main getPlugin(){
		return plugin;
	}
	
	public RegionDisplayManager getDisplayManager() {
		return display;
	}
}

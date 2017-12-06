package au.com.mineauz.minigames.commands.set;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.commands.ICommand;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemDisplayLoadout;
import au.com.mineauz.minigames.menu.MenuItemLoadoutAdd;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.modules.LoadoutModule;

public class SetLoadoutCommand implements ICommand {

	@Override
	public String getName() {
		return "loadout";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Opens the Loadout edit window for the desired Minigames Loadouts.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {
				"/minigame set <Minigame> loadout"
		};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to edit a Minigames loadouts!";
	}

	@Override
	public String getPermission() {
		return "minigame.set.loadout";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {

		MinigamePlayer player = Minigames.plugin.pdata.getMinigamePlayer((Player)sender);
		Menu loadouts = new Menu(6, getName(), player);
		List<MenuItem> mi = new ArrayList<MenuItem>();
		LoadoutModule mod = LoadoutModule.getMinigameModule(minigame);
		
		List<String> des = new ArrayList<String>();
		des.add("Shift + Right Click to Delete");
		
		Material item = Material.THIN_GLASS;
		
		for(String ld : mod.getLoadouts()){
			item = Material.THIN_GLASS;
			if(mod.getLoadout(ld).getItems().size() != 0){
				item = mod.getLoadout(ld).getItem((Integer)mod.getLoadout(ld).getItems().toArray()[0]).getType();
			}
			MenuItemDisplayLoadout mil = new MenuItemDisplayLoadout(ld, des, item, mod.getLoadout(ld), minigame);
			mil.setAllowDelete(mod.getLoadout(ld).isDeleteable());
			mi.add(mil);
		}
		loadouts.addItem(new MenuItemLoadoutAdd("Add Loadout", Material.ITEM_FRAME, mod.getLoadoutMap(), minigame), 53);
		loadouts.addItems(mi);
		
		loadouts.displayMenu(player);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		return null;
	}

}

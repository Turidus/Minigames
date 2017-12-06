package au.com.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.minigame.Minigame;

public class EditCommand implements ICommand {

	@Override
	public String getName() {
		return "edit";
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
		return "Lets you edit a Minigame using a neat menu. Clicking on the menu items will allow you to change the settings of the Minigame.";
	}

	@Override
	public String[] getParameters() {
		return null;
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame edit <Minigame>"};
	}

	@Override
	public String getPermissionMessage() {
		return "You do not have permission to use the Minigame edit menu.";
	}

	@Override
	public String getPermission() {
		return "minigame.edit";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame,
			String label, String[] args) {
		
		if(args != null){
			if(plugin.mdata.hasMinigame(args[0])){
				Minigame mgm = plugin.mdata.getMinigame(args[0]);
				mgm.displayMenu(plugin.pdata.getMinigamePlayer((Player)sender));
//				Menu menu = new Menu(6, "Edit: " + mgm.getName(), plugin.pdata.getMinigamePlayer((Player)sender));
//				int slot = 0;
//				for(MenuItem item : mgm.getMenuItems()){
//					menu.addItem(item, slot);
//					slot++;
//				}
//				menu.displayMenu(plugin.pdata.getMinigamePlayer((Player)sender));
			}
			else{
				sender.sendMessage(ChatColor.RED + "There is no Minigame by the name " + args[0]);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args != null && args.length == 1){
			List<String> mgs = new ArrayList<String>(plugin.mdata.getAllMinigames().keySet());
			return MinigameUtils.tabCompleteMatch(mgs, args[0]);
		}
		return null;
	}

}

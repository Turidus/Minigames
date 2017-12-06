package au.com.mineauz.minigames.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.minigame.Minigame;

public class CreateCommand implements ICommand{
	
	@Override
	public String getName() {
		return "create";
	}
	
	@Override
	public String[] getAliases(){
		return null;
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public String getDescription() {
		return "Creates a Minigame using the specified name. Optionally, adding the type at the end will " +
				"set the type of minigame straight up. (Type defaults to SINGLEPLAYER)";
	}

	@Override
	public String[] getUsage() {
		return new String[] {"/minigame create <Minigame> [type]"};
	}
	
	@Override
	public String[] getParameters() {
		return null;
	}
	
	@Override
	public String getPermissionMessage(){
		return "You do not have permission to create Minigames!";
	}
	
	@Override
	public String getPermission(){
		return "minigame.create";
	}

	@Override
	public boolean onCommand(CommandSender sender, Minigame minigame, String label, String[] args) {
		if(args != null){
			Player player = (Player)sender;
			if(!plugin.mdata.hasMinigame(args[0])){
				String mgmName = args[0];
				MinigameType type = MinigameType.SINGLEPLAYER;
				if(args.length >= 2){
					if(MinigameType.hasValue(args[1].toUpperCase())){
						type = MinigameType.valueOf(args[1].toUpperCase());
					}
					else{
						player.sendMessage(ChatColor.RED + "There is no Minigame type by the name \"" + args[1] + "\"!");
					}
				}
				Minigame mgm = new Minigame(mgmName, type, player.getLocation());
				
				player.sendMessage(ChatColor.GRAY + "The Minigame " + args[0] + " has been created.");
				
				List<String> mgs = null;
				if(plugin.getConfig().contains("minigames")){
					mgs = plugin.getConfig().getStringList("minigames");
				}
				else{
					mgs = new ArrayList<String>();
				}
				mgs.add(mgmName);
				plugin.getConfig().set("minigames", mgs);
				plugin.saveConfig();
				
				mgm.saveMinigame();
				plugin.mdata.addMinigame(mgm);
			}else{
				sender.sendMessage(ChatColor.RED + "This Minigame already exists!");
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Minigame minigame,
			String alias, String[] args) {
		if(args.length == 2){
			List<String> types = new ArrayList<String>(MinigameType.values().length);
			for(MinigameType type : MinigameType.values()){
				types.add(type.toString().toLowerCase());
			}
			return MinigameUtils.tabCompleteMatch(types, args[1]);
		}
		return null;
	}
}

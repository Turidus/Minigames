package au.com.mineauz.minigames.tool;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;

public class EndPositionMode implements ToolMode {

	@Override
	public String getName() {
		return "END";
	}

	@Override
	public String getDisplayName() {
		return "End Position";
	}

	@Override
	public String getDescription() {
		return "Sets the end;position";
	}

	@Override
	public Material getIcon() {
		return Material.GOLD_BLOCK;
	}

	@Override
	public void onLeftClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		
	}

	@Override
	public void onRightClick(MinigamePlayer player, Minigame minigame,
			Team team, PlayerInteractEvent event) {
		minigame.setEndPosition(player.getLocation());
		player.sendMessage("Set end position.", null);
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void select(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getEndPosition() != null){
			player.getPlayer().sendBlockChange(minigame.getEndPosition(), Material.SKULL, (byte)1);
			player.sendMessage("Selected end position (marked with skull)", null);
		}
		else{
			player.sendMessage("No end position set!", "error");
		}
	}

	@SuppressWarnings("deprecation") //TODO: Use alternate method once available
	@Override
	public void deselect(MinigamePlayer player, Minigame minigame, Team team) {
		if(minigame.getEndPosition() != null){
			player.getPlayer().sendBlockChange(minigame.getEndPosition(), 
					minigame.getEndPosition().getBlock().getType(), 
					minigame.getEndPosition().getBlock().getData());
			player.sendMessage("Deselected end position", null);
		}
		else{
			player.sendMessage("No end position set!", "error");
		}
	}

	@Override
	public void onSetMode(MinigamePlayer player, MinigameTool tool) {
	}

	@Override
	public void onUnsetMode(MinigamePlayer player, MinigameTool tool) {
	}

}

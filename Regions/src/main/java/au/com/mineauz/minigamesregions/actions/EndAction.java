package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.gametypes.MinigameType;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class EndAction extends ActionInterface {

	@Override
	public String getName() {
		return "END";
	}

	@Override
	public String getCategory() {
		return "Minigame Actions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
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
	public void executeNodeAction(MinigamePlayer player, Node node, Minigame mgm) {
		debug(player,node);
		execute(player, mgm);
	}

	@Override
	public void executeRegionAction(MinigamePlayer player, Region region, Minigame mgm) {
		debug(player,region);
		execute(player, mgm);
	}
	
	private void execute(MinigamePlayer player,Minigame mgm){
		//TODO test, find a better way
		if(player == null) {
			Minigames.plugin.pdata.endMinigame(mgm.getPlayers().get(0));
			}else if(!player.isInMinigame()) {
				return;
		}
		if(player.getMinigame().getType() != MinigameType.SINGLEPLAYER){
			List<MinigamePlayer> w = null;
			List<MinigamePlayer> l = null;
			if(player.getMinigame().isTeamGame()){
				w = new ArrayList<>(player.getTeam().getPlayers());
				l = new ArrayList<>(player.getMinigame().getPlayers().size() - player.getTeam().getPlayers().size());
				for(Team t : TeamsModule.getMinigameModule(player.getMinigame()).getTeams()){
					if(t != player.getTeam())
						l.addAll(t.getPlayers());
				}
			}
			else{
				w = new ArrayList<>(1);
				l = new ArrayList<>(player.getMinigame().getPlayers().size());
				w.add(player);
				l.addAll(player.getMinigame().getPlayers());
				l.remove(player);
			}
			Minigames.plugin.pdata.endMinigame(player.getMinigame(), w, l);
		} else{
			Minigames.plugin.pdata.endMinigame(player.getMinigame().getPlayers().get(0));
		}
	}

	@Override
	public void saveArguments(FileConfiguration config, String path) {
	}

	@Override
	public void loadArguments(FileConfiguration config, String path) {
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		return false;
	}

}

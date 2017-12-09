package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.config.IntegerFlag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.minigame.TeamColor;
import au.com.mineauz.minigames.minigame.modules.TeamsModule;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SetTeamScoreAction extends ActionInterface {
	
	private IntegerFlag score = new IntegerFlag(1, "amount");
	private StringFlag team = new StringFlag("NONE", "team");

	@Override
	public String getName() {
		return "SET_TEAM_SCORE";
	}

	@Override
	public String getCategory() {
		return "Team Actions";
	}
	
	@Override
	public void describe(Map<String, Object> out) {
		out.put("Score", score.getFlag());
		out.put("Team", team.getFlag());
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
			Region region, Minigame mgm) {
		debug(player,region);
		executeAction(player);
	}

	@Override
	public void executeNodeAction(MinigamePlayer player,
			Node node, Minigame mgm) {
		debug(player,node);
		executeAction(player);
	}
	
	private void executeAction(MinigamePlayer player){
		if(player == null || !player.isInMinigame()) return;
		if(player.getTeam() != null && team.getFlag().equals("NONE")){
			player.getTeam().setScore(score.getFlag());
		}
		else if(!team.getFlag().equals("NONE")){
			TeamsModule tm = TeamsModule.getMinigameModule(player.getMinigame());
			if(tm.hasTeam(TeamColor.valueOf(team.getFlag()))){
				tm.getTeam(TeamColor.valueOf(team.getFlag())).setScore(score.getFlag());
			}
		}
		checkScore(player);
	}
	private void checkScore(MinigamePlayer player){
		if(player.getTeam().getScore() >= player.getMinigame().getMaxScore()){
			if(player.getMinigame().isTeamGame()){
				List<MinigamePlayer> w;
				List<MinigamePlayer> l;
				w = new ArrayList<>(player.getTeam().getPlayers());
				l = new ArrayList<>(player.getMinigame().getPlayers().size() - player.getTeam().getPlayers().size());
				for(Team t : TeamsModule.getMinigameModule(player.getMinigame()).getTeams()){
					if(t != player.getTeam())
						l.addAll(t.getPlayers());
				}
				Minigames.plugin.pdata.endMinigame(player.getMinigame(), w, l);
			}
		}
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		score.saveValue(path, config);
		team.saveValue(path, config);
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		score.loadValue(path, config);
		team.loadValue(path, config);
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Set Team Score", player);
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, previous), m.getSize() - 9);
		m.addItem(score.getMenuItem("Set Score Amount", Material.STONE, null, null));
		
		List<String> teams = new ArrayList<>();
		teams.add("None");
		for(TeamColor team : TeamColor.values()){
			teams.add(MinigameUtils.capitalize(team.toString()));
		}
		m.addItem(new MenuItemList("Specific Team", MinigameUtils.stringToList("If 'None', the players;team will be used"), Material.PAPER, new Callback<String>() {

			@Override
			public void setValue(String value) {
				team.setFlag(value.toUpperCase());
            }

			@Override
			public String getValue() {
				return MinigameUtils.capitalize(team.getFlag());
			}
		}, teams));
		m.displayMenu(player);
		return true;
	}

}

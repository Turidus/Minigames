package au.com.mineauz.minigames.minigame.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.ImmutableMap;

import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.config.TeamSetFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemAddTeam;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemPage;
import au.com.mineauz.minigames.menu.MenuItemTeam;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.minigame.Team;
import au.com.mineauz.minigames.minigame.TeamColor;

public class TeamsModule extends MinigameModule {
	private Map<TeamColor, Team> teams = new HashMap<TeamColor, Team>();
	private TeamSetFlag teamsFlag;
	private StringFlag defaultWinner = new StringFlag(null, "defaultwinner");
	
	public TeamsModule(Minigame mgm){
		super(mgm);
		teamsFlag = new TeamSetFlag(null, "teams", getMinigame());
		teamsFlag.setFlag(teams);
	}

	@Override
	public String getName() {
		return "Teams";
	}
	
	@Override
	public Map<String, Flag<?>> getFlags(){
		Map<String, Flag<?>> flags = new HashMap<String, Flag<?>>();
		flags.put(teamsFlag.getName(), teamsFlag);
		flags.put(defaultWinner.getName(), defaultWinner);
		return flags;
	}
	
	@Override
	public boolean useSeparateConfig(){
		return false;
	}

	@Override
	public void save(FileConfiguration config) {
	}

	@Override
	public void load(FileConfiguration config) {
		if(config.contains(getMinigame() + ".startposred")){ //TODO: Remove after 1.7
			if(!hasTeam(TeamColor.RED))
				addTeam(TeamColor.RED);
			Set<String> locs = config.getConfigurationSection(getMinigame() + ".startposred").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				getTeam(TeamColor.RED).addStartLocation(Minigames.plugin.mdata.minigameLocations(getMinigame().getName(false), 
						"startposred." + String.valueOf(i), config));
			}
		}
		if(config.contains(getMinigame() + ".startposblue")){ //TODO: Remove after 1.7
			if(!hasTeam(TeamColor.BLUE))
				addTeam(TeamColor.BLUE);
			Set<String> locs = config.getConfigurationSection(getMinigame() + ".startposblue").getKeys(false);
			
			for(int i = 0; i < locs.size(); i++){
				getTeam(TeamColor.BLUE).addStartLocation(Minigames.plugin.mdata.minigameLocations(getMinigame().getName(false), 
						"startposblue." + String.valueOf(i), config));
			}
		}
	}
	
	public static TeamsModule getMinigameModule(Minigame minigame){
		return (TeamsModule) minigame.getModule("Teams");
	}
	
	public Team getTeam(TeamColor color){
		return teams.get(color);
	}
	
	public List<Team> getTeams(){
		return new ArrayList<>(teams.values());
	}
	
	public Map<String, Team> getTeamsNameMap() {
		ImmutableMap.Builder<String, Team> builder = ImmutableMap.builder();
		
		for (Team team : teams.values()) {
			builder.put(team.getColor().name().toLowerCase(), team);
		}
		
		return builder.build();
	}
	
	public Team addTeam(TeamColor color){
		return addTeam(color, "");
	}
	
	public Team addTeam(TeamColor color, String name){
		if(!teams.containsKey(color)){
			teams.put(color, new Team(color, getMinigame()));
			String sbTeam = color.toString().toLowerCase();
			getMinigame().getScoreboardManager().registerNewTeam(sbTeam);
			getMinigame().getScoreboardManager().getTeam(sbTeam).setPrefix(color.getColor().toString());
			getMinigame().getScoreboardManager().getTeam(sbTeam).setAllowFriendlyFire(false);
			getMinigame().getScoreboardManager().getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
		}
		if(!name.equals(""))
			teams.get(color).setDisplayName(name);
		return teams.get(color);
	}
	
	public void addTeam(TeamColor color, Team team){
		teams.put(color, team);
		String sbTeam = color.toString().toLowerCase();
		getMinigame().getScoreboardManager().registerNewTeam(sbTeam);
		getMinigame().getScoreboardManager().getTeam(sbTeam).setPrefix(color.getColor().toString());
		getMinigame().getScoreboardManager().getTeam(sbTeam).setAllowFriendlyFire(false);
		getMinigame().getScoreboardManager().getTeam(sbTeam).setCanSeeFriendlyInvisibles(true);
	}
	
	public boolean hasTeam(TeamColor color){
        return teams.containsKey(color);
    }
	
	public void removeTeam(TeamColor color){
		if(teams.containsKey(color)){
			teams.remove(color);
			getMinigame().getScoreboardManager().getTeam(color.toString().toLowerCase()).unregister();
		}
	}
	
	public boolean hasTeamStartLocations(){
		for(Team t : teams.values()){
			if(!t.hasStartLocations())
				return false;
		}
		return true;
	}
	
	public Callback<String> getDefaultWinnerCallback(){
		return new Callback<String>() {

			@Override
			public void setValue(String value) {
				if(!value.equals("None"))
					defaultWinner.setFlag(TeamColor.matchColor(value.replace(" ", "_")).toString());
				else
					defaultWinner.setFlag(null);
			}

			@Override
			public String getValue() {
				if(defaultWinner.getFlag() != null) {
					if (!teams.containsKey(TeamColor.matchColor(defaultWinner.getFlag()))) {
						return "None";
					}
					
					return MinigameUtils.capitalize(defaultWinner.getFlag().replace("_", " "));
				}
				return "None";
			}
		};
	}

	public void setDefaultWinner(TeamColor defaultWinner) {
		this.defaultWinner.setFlag(defaultWinner.toString());
	}
	
	public TeamColor getDefaultWinner() {
		if(defaultWinner.getFlag() != null) {
			TeamColor team = TeamColor.matchColor(defaultWinner.getFlag());
			if (!teams.containsKey(team)) {
				return null;
			} else {
				return team;
			}
		}
		return null;
	}
	
	public void clearTeams(){
		teams.clear();
		defaultWinner = null;
	}

	@Override
	public void addEditMenuOptions(Menu menu) {
		Menu m = new Menu(6, "Teams", menu.getViewer());
		m.setPreviousPage(menu);
		List<MenuItem> items = new ArrayList<MenuItem>();
		List<String> teams = new ArrayList<String>(this.teams.size() + 1);
		for(TeamColor t : this.teams.keySet()){
			teams.add(MinigameUtils.capitalize(t.toString().replace("_", " ")));
		}
		teams.add("None");
		items.add(new MenuItemList("Default Winning Team", Material.PAPER, getDefaultWinnerCallback(), teams));
		items.add(new MenuItemNewLine());
		for(Team t : this.teams.values()){
			items.add(new MenuItemTeam(t.getChatColor() + t.getDisplayName(), t));
		}
		
		m.addItem(new MenuItemAddTeam("Add Team", getMinigame()), m.getSize() - 1);
		
		m.addItems(items);
		
		m.addItem(new MenuItemPage("Back", Material.REDSTONE_TORCH_ON, menu), m.getSize() - 9);
		
		MenuItemPage p = new MenuItemPage("Team Options", Material.CHEST, m);
		menu.addItem(p);
	}

	@Override
	public boolean displayMechanicSettings(Menu previous) {
		return false;
	}
}

package au.com.mineauz.minigamesregions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.common.collect.ImmutableSet;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.script.ScriptCollection;
import au.com.mineauz.minigames.script.ScriptObject;
import au.com.mineauz.minigames.script.ScriptReference;
import au.com.mineauz.minigames.script.ScriptValue;
import au.com.mineauz.minigames.script.ScriptWrapper;
import au.com.mineauz.minigamesregions.actions.ActionInterface;
import au.com.mineauz.minigamesregions.conditions.ConditionInterface;
import au.com.mineauz.minigamesregions.triggers.Trigger;
import au.com.mineauz.minigamesregions.triggers.Triggers;
import au.com.mineauz.minigamesregions.executors.RegionExecutor;
import au.com.mineauz.minigamesregions.executors.NodeExecutor;

public class Region implements ScriptObject {
	private String name;
	private Location point1;
	private Location point2;
	private List<RegionExecutor> executors = new ArrayList<RegionExecutor>();
	private List<MinigamePlayer> players = new ArrayList<MinigamePlayer>();
	private long taskDelay = 20;
	private int taskID;
	private boolean enabled = true;
	
	public Region(String name, Location point1, Location point2){
		Location[] locs = MinigameUtils.getMinMaxSelection(point1, point2);
		this.point1 = locs[0].clone();
		this.point2 = locs[1].clone();
		this.name = name;
	}
	
	public boolean playerInRegion(MinigamePlayer player){
		if(player.getLocation().getWorld() == point1.getWorld()){
			int minx = point1.getBlockX();
			int maxx = point2.getBlockX();
			int plyx = player.getLocation().getBlockX();
			
			if(plyx >= minx && plyx <= maxx){
				int miny = point1.getBlockY();
				int maxy = point2.getBlockY();
				int plyy = player.getLocation().getBlockY();
				
				if(plyy >= miny && plyy <= maxy){
					int minz = point1.getBlockZ();
					int maxz = point2.getBlockZ();
					int plyz = player.getLocation().getBlockZ();
					
					if(plyz >= minz && plyz <= maxz){
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	public boolean locationInRegion(Location loc){
		if(loc.getWorld() == point1.getWorld()){
			int minx = point1.getBlockX();
			int maxx = point2.getBlockX();
			int plyx = loc.getBlockX();
			
			if(plyx >= minx && plyx <= maxx){
				int miny = point1.getBlockY();
				int maxy = point2.getBlockY();
				int plyy = loc.getBlockY();
				
				if(plyy >= miny && plyy <= maxy){
					int minz = point1.getBlockZ();
					int maxz = point2.getBlockZ();
					int plyz = loc.getBlockZ();
					
					if(plyz >= minz && plyz <= maxz){
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	public String getName(){
		return name;
	}
	
	public Location getFirstPoint(){
		return point1.clone();
	}
	
	public Location getSecondPoint(){
		return point2.clone();
	}
	
	public void updateRegion(Location point1, Location point2) {
		Location[] locs = MinigameUtils.getMinMaxSelection(point1, point2);
		this.point1 = locs[0];
		this.point2 = locs[1];
	}
	
	public boolean hasPlayer(MinigamePlayer player){
		return players.contains(player);
	}
	
	public void addPlayer(MinigamePlayer player){
		players.add(player);
	}
	
	public void removePlayer(MinigamePlayer player){
		players.remove(player);
	}
	
	public List<MinigamePlayer> getPlayers(){
		return players;
	}
	
	public int addExecutor(Trigger trigger){
		executors.add(new RegionExecutor(trigger));
		return executors.size();
	}
	
	public int addExecutor(RegionExecutor exec){
		executors.add(exec);
		return executors.size();
	}
	
	public List<RegionExecutor> getExecutors(){
		return executors;
	}
	
	public void removeExecutor(int id){
		if(executors.size() <= id){
			executors.remove(id - 1);
		}
	}
	
	public void removeExecutor(RegionExecutor executor){
		if(executors.contains(executor)){
			executors.remove(executor);
		}
	}
	
	public void changeTickDelay(long delay){
		removeTickTask();
		taskDelay = delay;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.plugin, new Runnable() {
			
			@Override
			public void run() {
				List<MinigamePlayer> plys = new ArrayList<MinigamePlayer>(players);
				for(MinigamePlayer player : plys){
					execute(Triggers.getTrigger("TICK"), player, player.getMinigame());
				}
			}
		}, 0, delay);
	}
	
	public long getTickDelay(){
		return taskDelay;
	}
	
	public void startTickTask(){
		if(taskID != -1)
			removeTickTask();
		
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Minigames.plugin, new Runnable() {
			
			@Override
			public void run() {
				List<MinigamePlayer> plys = new ArrayList<MinigamePlayer>(players);
				for(MinigamePlayer player : plys){
					execute(Triggers.getTrigger("TICK"), player, player.getMinigame());
				}
			}
		}, 0, taskDelay);
	}
	
	public void removeTickTask(){
		Bukkit.getScheduler().cancelTask(taskID);
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public boolean getEnabled(){
		return enabled;
	}
	
	public void execute(Trigger trigger, MinigamePlayer player, Minigame mgm){
		if(player != null && mgm != null && player.getMinigame().isSpectator(player)) return;
		List<RegionExecutor> toExecute = new ArrayList<RegionExecutor>();
		for(RegionExecutor exec : executors){
			if(exec.getTrigger() == trigger){
				if(checkConditions(exec, player, mgm) && exec.canBeTriggered(player))
					toExecute.add(exec);
			}
		}
		for(RegionExecutor exec : toExecute){
			execute(exec, player, mgm);
		}
	}
	
	public boolean checkConditions(RegionExecutor exec, MinigamePlayer player, Minigame mgm){
		for(ConditionInterface con : exec.getConditions()){
			boolean c = con.checkRegionCondition(player, this, mgm);
			if(con.isInverted())
				c = !c;
			if(!c){
				return false;
			}
		}
		return true;
	}
	
	public void execute(RegionExecutor exec, MinigamePlayer player, Minigame mgm){
		for(ActionInterface act : exec.getActions()){
			if(!enabled && !act.getName().equalsIgnoreCase("SET_ENABLED")) continue;
			act.executeRegionAction(player, this, mgm);
			if(!exec.isTriggerPerPlayer())
				exec.addPublicTrigger();
			else
				exec.addPlayerTrigger(player);
		}
	}
	
	@Override
	public ScriptReference get(String name) {
		if (name.equalsIgnoreCase("name")) {
			return ScriptValue.of(name);
		} else if (name.equalsIgnoreCase("players")) {
			return ScriptCollection.of(players);
		} else if (name.equalsIgnoreCase("min")) {
			return ScriptWrapper.wrap(point1);
		} else if (name.equalsIgnoreCase("max")) {
			return ScriptWrapper.wrap(point2);
		}
		
		return null;
	}
	
	@Override
	public String getAsString() {
		return name;
	}
	
	@Override
	public Set<String> getKeys() {
		return ImmutableSet.of("name", "players", "min", "max");
	}
}

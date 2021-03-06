package au.com.mineauz.minigames;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import au.com.mineauz.minigames.events.MinigameTimerTickEvent;
import au.com.mineauz.minigames.events.TimerExpireEvent;
import au.com.mineauz.minigames.minigame.Minigame;
import au.com.mineauz.minigames.sounds.MGSounds;
import au.com.mineauz.minigames.sounds.PlayMGSound;

public class MinigameTimer{
	private int time = 0;
	private int otime = 0;
	private Minigame minigame;
	private List<Integer> timeMsg = new ArrayList<Integer>();
	private static Minigames plugin = Minigames.plugin;
	private int taskID = -1;
	private boolean broadcastTime = true;
	
	public MinigameTimer(Minigame minigame, int time){
		this.time = time;
		otime = time;
		this.minigame = minigame;
		timeMsg.addAll(plugin.getConfig().getIntegerList("multiplayer.timerMessageInterval"));
		startTimer();
	}
	
	public boolean isBroadcastingTime(){
		return broadcastTime;
	}
	
	public void setBroadcastTime(boolean bool){
		broadcastTime = bool;
	}
	
	public void startTimer(){
		if(taskID != -1)
			stopTimer();
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				time -= 1;
				if(minigame.isUsingXPBarTimer()){
					float timeper = ((Integer)time).floatValue() / ((Integer)otime).floatValue();
					int level = 0;
					if(time / 60 > 0)
						level = time / 60;
					else
						level = time;
					
					for(MinigamePlayer ply : minigame.getPlayers()){
						if(timeper < 0 ){
							ply.getPlayer().setExp(0);
							ply.getPlayer().setLevel(0);
						}else {
							ply.getPlayer().setExp(timeper);
							ply.getPlayer().setLevel(level);
						}
					}
				}
				if(timeMsg.contains(time) && broadcastTime){
					PlayMGSound.playSound(minigame, MGSounds.getSound("timerTick"));
					plugin.mdata.sendMinigameMessage(minigame, MinigameUtils.formStr("minigame.timeLeft", MinigameUtils.convertTime(time)), null, null);
				}

				if(time <= 0){
					Bukkit.getServer().getPluginManager().callEvent(new TimerExpireEvent(minigame));
					stopTimer();
				}
				
				if(time > 0)
					Bukkit.getPluginManager().callEvent(new MinigameTimerTickEvent(minigame, minigame.getMinigameTimer()));
			}
		}, 0, 20);
	}
	
	public void stopTimer(){
		if(taskID != -1){
			Bukkit.getScheduler().cancelTask(taskID);
		}
	}
	
	public int getTimeLeft(){
		return time;
	}
	
	public void setTimeLeft(int time){
		this.time = time;
	}
}

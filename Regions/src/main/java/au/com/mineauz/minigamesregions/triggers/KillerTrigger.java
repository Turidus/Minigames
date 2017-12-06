package au.com.mineauz.minigamesregions.triggers;

/**
 * This trigger fires every time a player dies 
 * and has a killer. Actions will act on that player.
 * 
 * See {@link RegionEvents}
 *  
 * @author Turidus https://github.com/Turidus/Minigames
 *
 */
public class KillerTrigger implements Trigger {

	@Override
	public String getName() {
		return "KILLER";
	}

	@Override
	public boolean useInRegions() {
		return true;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

}
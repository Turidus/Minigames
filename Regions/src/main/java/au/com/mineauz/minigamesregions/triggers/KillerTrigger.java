package au.com.mineauz.minigamesregions.triggers;

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
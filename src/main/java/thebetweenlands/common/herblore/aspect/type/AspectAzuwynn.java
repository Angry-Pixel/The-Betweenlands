package thebetweenlands.common.herblore.aspect.type;

public class AspectAzuwynn implements IAspectType {
	@Override
	public String getName() {
		return "Azuwynn";
	}

	@Override
	public String getType() {
		return "Muscle";
	}

	@Override
	public String getDescription() {
		return "Has effect on the muscles, could either result in more damage, speed or maybe rapid fire and all stuff in that regard.";
	}
	
	@Override
	public int getIconIndex() {
		return 1;
	}
}

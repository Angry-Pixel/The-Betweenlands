package thebetweenlands.herblore.aspects.list;

import thebetweenlands.herblore.aspects.IAspect;

public class AspectYunugaz implements IAspect {
	@Override
	public String getName() {
		return "Yunugaz";
	}

	@Override
	public String getType() {
		return "Wind";
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to wind. Any combination with this effect can be related to the element wind.";
	}

	@Override
	public int getIconIndex() {
		return 13;
	}
}

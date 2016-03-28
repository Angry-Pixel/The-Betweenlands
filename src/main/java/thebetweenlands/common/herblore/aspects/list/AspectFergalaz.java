package thebetweenlands.common.herblore.aspects.list;

import thebetweenlands.common.herblore.aspects.IAspectType;

public class AspectFergalaz implements IAspectType {
	@Override
	public String getName() {
		return "Fergalaz";
	}

	@Override
	public String getType() {
		return "Earth";
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to earth. Any combination with this effect can be related to the element earth.";
	}

	@Override
	public int getIconIndex() {
		return 6;
	}
}

package thebetweenlands.common.herblore.aspects;

import thebetweenlands.common.herblore.aspect.IAspectType;

public class AspectByrginaz implements IAspectType {
	@Override
	public String getName() {
		return "Byrginaz";
	}

	@Override
	public String getType() {
		return "Water";
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to water. Any combination with this effect can be related to water.";
	}

	@Override
	public int getIconIndex() {
		return 3;
	}
}

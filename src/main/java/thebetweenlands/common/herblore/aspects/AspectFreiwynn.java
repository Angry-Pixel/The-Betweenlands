package thebetweenlands.common.herblore.aspects;

import thebetweenlands.common.herblore.aspect.IAspectType;

public class AspectFreiwynn implements IAspectType {
	@Override
	public String getName() {
		return "Freiwynn";
	}

	@Override
	public String getType() {
		return "Vision";
	}

	@Override
	public String getDescription() {
		return "Alters the player's vision. (In combination with other properties. So for example when you combine health with vision, you would be able to spot mobs their health.)";
	}

	@Override
	public int getIconIndex() {
		return 8;
	}
}

package thebetweenlands.common.herblore.aspects.list;

import thebetweenlands.common.herblore.aspects.IAspectType;

public class AspectDayuniis implements IAspectType {
	@Override
	public String getName() {
		return "Dayuniis";
	}

	@Override
	public String getType() {
		return "Mind";
	}

	@Override
	public String getDescription() {
		return "Has effect on the player's mind and on how senses work. Could be positive, or negative (think nausea/schizophrenia).";
	}

	@Override
	public int getIconIndex() {
		return 5;
	}
}

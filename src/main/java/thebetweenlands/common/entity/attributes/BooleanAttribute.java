package thebetweenlands.common.entity.attributes;

import javax.annotation.Nullable;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

/**
 * This boolean attribute has either the value 0 or 1. If a value other than 0 or 1 is assigned it is clamped to [0, 1] and then rounded up to either 0 or 1.
 */
public class BooleanAttribute extends RangedAttribute {
	public BooleanAttribute(@Nullable IAttribute parent, String unlocalizedName, boolean defaultValue) {
		super(parent, unlocalizedName, defaultValue ? 1 : 0, 0, 1);
	}

	@Override
	public double clampValue(double val) {
		return val > 0 ? 1 : 0;
	}
}

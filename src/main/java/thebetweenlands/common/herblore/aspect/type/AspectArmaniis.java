package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectArmaniis implements IAspectType {
	@Override
	public String getName() {
		return "Armaniis";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.desire");
	}

	@Override
	public String getDescription() {
		return "Has effect on the desires of a mob or the player. Could be useful for food, but also things like trading or corrupting the desire.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_armaniis.png");
	}

	@Override
	public int getColor() {
		return 0xFFFFCC00;
	}
}

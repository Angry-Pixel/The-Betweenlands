package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectYeowynn implements IAspectType {
	@Override
	public String getName() {
		return "Yeowynn";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.health");
	}

	@Override
	public String getDescription() {
		return "Has effect on the health bar, could be both negative or positive, depending on the combination.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_yeowynn.png");
	}

	@Override
	public int getColor() {
		return 0xFFFC0069;
	}
}

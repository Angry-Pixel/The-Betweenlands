package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectYunugaz implements IAspectType {
	@Override
	public String getName() {
		return "Yunugaz";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.wind");
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to wind. Any combination with this effect can be related to the element wind.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_yunugaz.png");
	}

	@Override
	public int getColor() {
		return 0xFF00FFBB;
	}
}

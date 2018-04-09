package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectByrginaz implements IAspectType {
	@Override
	public String getName() {
		return "Byrginaz";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.water");
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to water. Any combination with this effect can be related to water.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_byrginaz.png");
	}

	@Override
	public int getColor() {
		return 0xFF1EBBDB;
	}
}

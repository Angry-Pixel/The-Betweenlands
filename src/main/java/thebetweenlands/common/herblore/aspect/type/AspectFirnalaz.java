package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class AspectFirnalaz implements IAspectType {
	@Override
	public String getName() {
		return "Firnalaz";
	}

	@Override
	public String getType() {
		return "Fire";
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to fire. Any combination with this effect can be related to fire.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_firnalaz.png");
	}
}

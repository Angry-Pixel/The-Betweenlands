package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class AspectOrdaniis implements IAspectType {
	@Override
	public String getName() {
		return "Ordaniis";
	}

	@Override
	public String getType() {
		return "Enhance";
	}

	@Override
	public String getDescription() {
		return "Needs new decription";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_ordaniis.png");
	}
}

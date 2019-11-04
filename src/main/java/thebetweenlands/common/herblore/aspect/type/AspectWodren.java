package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectWodren implements IAspectType {
	@Override
	public String getName() {
		return "Wodren";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.soul");
	}

	@Override
	public String getDescription() {
		return "The aspect that affects the spiritual state and life force of a being.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_wodren.png");
	}

	@Override
	public int getColor() {
		return 0xFF63C2AF;
	}
}

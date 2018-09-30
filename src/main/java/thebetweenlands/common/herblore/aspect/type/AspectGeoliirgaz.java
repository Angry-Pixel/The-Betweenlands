package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectGeoliirgaz implements IAspectType {
	@Override
	public String getName() {
		return "Geoliirgaz";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.void");
	}

	@Override
	public String getDescription() {
		return "Magical property which relates to the void or ether. Any combination with this effect can be related to void or darkness.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_geoliirgaz.png");
	}

	@Override
	public int getColor() {
		return 0xFF222228;
	}
}

package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectAzuwynn implements IAspectType {
	@Override
	public String getName() {
		return "Azuwynn";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.muscle");
	}

	@Override
	public String getDescription() {
		return "Has effect on the muscles, could either result in more damage, speed or maybe rapid fire and all stuff in that regard.";
	}
	
	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_azuwynn.png");
	}

	@Override
	public int getColor() {
		return 0xFFE01414;
	}
}

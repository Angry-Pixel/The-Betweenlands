package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectFreiwynn implements IAspectType {
	@Override
	public String getName() {
		return "Freiwynn";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.vision");
	}

	@Override
	public String getDescription() {
		return "Alters the player's vision. (In combination with other properties. So for example when you combine health with vision, you would be able to spot mobs their health.)";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_freiwynn.png");
	}

	@Override
	public int getColor() {
		return 0xFFC1D8F4;
	}
}

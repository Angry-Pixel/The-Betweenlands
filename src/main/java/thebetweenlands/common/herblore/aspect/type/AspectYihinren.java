package thebetweenlands.common.herblore.aspect.type;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.lib.ModInfo;

public class AspectYihinren implements IAspectType {
	@Override
	public String getName() {
		return "Yihinren";
	}

	@Override
	public String getType() {
		return I18n.translateToLocal("manual.form");
	}

	@Override
	public String getDescription() {
		return "This effect has influence on the form of things, both physical and psychological. A very rare aspect that is mainly used in the special potions.";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/misc/aspect_yihinren.png");
	}

	@Override
	public int getColor() {
		return 0xFFFFFFFF;
	}
}

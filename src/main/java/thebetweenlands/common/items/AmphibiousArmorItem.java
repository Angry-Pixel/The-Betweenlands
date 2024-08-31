package thebetweenlands.common.items;

import net.minecraft.world.item.ArmorItem;
import thebetweenlands.common.registries.ArmorMaterialRegistry;

public class AmphibiousArmorItem extends ArmorItem {
	public AmphibiousArmorItem(Type type, Properties properties) {
		super(ArmorMaterialRegistry.AMPHIBIOUS, type, properties);
	}
}

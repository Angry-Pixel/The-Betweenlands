package thebetweenlands.api.item.amphibious;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

public interface AmphibiousArmorAttributeUpgrade {
	void applyAttributeModifiers(ArmorItem.Type armorType, ItemStack stack, int count, List<ItemAttributeModifiers.Entry> modifiers);
}

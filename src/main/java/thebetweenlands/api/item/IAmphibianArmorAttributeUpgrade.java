package thebetweenlands.api.item;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public interface IAmphibianArmorAttributeUpgrade {
	public void applyAttributeModifiers(EntityEquipmentSlot armorType, ItemStack stack, int count, Multimap<String, AttributeModifier> modifiers);
}

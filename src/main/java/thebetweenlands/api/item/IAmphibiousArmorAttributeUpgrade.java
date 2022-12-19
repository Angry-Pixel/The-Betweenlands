package thebetweenlands.api.item;

import java.util.Map;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public interface IAmphibiousArmorAttributeUpgrade {
	public void applyAttributeModifiers(EntityEquipmentSlot armorType, ItemStack armor, int count, Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> counts, Multimap<String, AttributeModifier> modifiers);
}

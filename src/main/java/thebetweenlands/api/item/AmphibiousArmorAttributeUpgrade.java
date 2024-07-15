package thebetweenlands.api.item;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public interface AmphibiousArmorAttributeUpgrade {
	void applyAttributeModifiers(ArmorItem.Type armorType, ItemStack armor, int count, Map<AmphibiousArmorUpgrade, Map<EquipmentSlot, Integer>> counts, Multimap<String, AttributeModifier> modifiers);
}

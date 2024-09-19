package thebetweenlands.api.item.amphibious;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Map;

public interface AmphibiousArmorAttributeUpgrade {
	void applyAttributeModifiers(ArmorItem.Type armorType, ItemStack stack, int count, List<ItemAttributeModifiers.Entry> modifiers);
}

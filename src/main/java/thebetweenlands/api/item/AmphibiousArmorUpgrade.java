package thebetweenlands.api.item;

import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;

public interface AmphibiousArmorUpgrade extends AmphibiousArmorAttributeUpgrade {
	enum DamageEvent {
		ALL,
		ON_DAMAGE,
		ON_USE,
		NONE
	}

	ResourceLocation getId();

	boolean matches(EquipmentSlot armorType, ItemStack stack);

	Set<EquipmentSlot> getArmorTypes();

	@Override
	default void applyAttributeModifiers(ArmorItem.Type armorType, ItemStack armor, int count, Map<AmphibiousArmorUpgrade, Map<EquipmentSlot, Integer>> counts, Multimap<String, AttributeModifier> modifiers) { }

	default void onChanged(EquipmentSlot armorType, ItemStack armor, ItemStack stack) { }

	default boolean isBlacklisted(AmphibiousArmorUpgrade other) {
		return false;
	}

	int getMaxDamage();

	boolean isApplicableDamageEvent(DamageEvent event);

	boolean canBreak();
}

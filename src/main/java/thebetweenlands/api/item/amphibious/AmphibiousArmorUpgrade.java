package thebetweenlands.api.item.amphibious;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Set;

public interface AmphibiousArmorUpgrade extends AmphibiousArmorAttributeUpgrade {
	enum DamageEvent {
		ALL,
		ON_DAMAGE,
		ON_USE,
		NONE
	}

	boolean matches(EquipmentSlot armorType, ItemStack stack);

	Set<EquipmentSlot> getArmorTypes();

	@Override
	default void applyAttributeModifiers(ArmorItem.Type armorType, ItemStack stack, int count, List<ItemAttributeModifiers.Entry> modifiers) { }

	default void onChanged(EquipmentSlot armorType, ItemStack armor, ItemStack stack) { }

	default boolean isBlacklisted(Holder<AmphibiousArmorUpgrade> other) {
		return false;
	}

	int getMaxDamage();

	boolean isApplicableDamageEvent(DamageEvent event);

	boolean canBreak();
}

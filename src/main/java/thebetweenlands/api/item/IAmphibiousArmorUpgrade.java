package thebetweenlands.api.item;

import java.util.Set;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IAmphibiousArmorUpgrade extends IAmphibiousArmorAttributeUpgrade {
	public static enum DamageEvent {
		ALL,
		ON_DAMAGE,
		ON_USE,
		NONE
	}

	public ResourceLocation getId();

	public boolean matches(EntityEquipmentSlot armorType, ItemStack stack);

	public Set<EntityEquipmentSlot> getArmorTypes();

	@Override
	public default void applyAttributeModifiers(EntityEquipmentSlot armorType, ItemStack armor, int count, Multimap<String, AttributeModifier> modifiers) { }

	public default void onChanged(EntityEquipmentSlot armorType, ItemStack armor, ItemStack stack) { }

	public default boolean isBlacklisted(IAmphibiousArmorUpgrade other) {
		return false;
	}

	public int getMaxDamage();

	public boolean isApplicableDamageEvent(DamageEvent event);
	
	public boolean canBreak();
}

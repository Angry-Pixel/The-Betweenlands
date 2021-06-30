package thebetweenlands.api.item;

import java.util.Set;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IAmphibianArmorUpgrade extends IAmphibianArmorAttributeUpgrade {
	public ResourceLocation getId();

	public boolean matches(EntityEquipmentSlot armorType, ItemStack stack);

	public Set<EntityEquipmentSlot> getArmorTypes();
	
	@Override
	public default void applyAttributeModifiers(EntityEquipmentSlot armorType, ItemStack stack, int count, Multimap<String, AttributeModifier> modifiers) { }
	
	public int getMaxDamage();
}

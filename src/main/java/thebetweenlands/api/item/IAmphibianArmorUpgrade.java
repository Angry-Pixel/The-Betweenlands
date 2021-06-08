package thebetweenlands.api.item;

import java.util.Set;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IAmphibianArmorUpgrade {
	public ResourceLocation getId();

	public boolean matches(ItemStack stack);

	public Set<EntityEquipmentSlot> getArmorTypes();
}

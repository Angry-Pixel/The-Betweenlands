package thebetweenlands.api.item;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.registries.CapabilityRegistry;

public interface IEquippable {
	/**
	 * Returns the equipment category
	 * @param stack Item
	 * @return
	 */
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack);

	/**
	 * Returns whether this item should be equipped on right click
	 * @param stack Item to equip
	 * @param player Player that is interacting
	 * @param target Entity that will be equipped
	 * @return
	 */
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity target);

	/**
	 * Returns whether this item can be equipped
	 * @param stack Item to equip
	 * @param player Player that is interacting
	 * @param target Entity that will be equipped
	 * @return
	 */
	public boolean canEquip(ItemStack stack, @Nullable EntityPlayer player, Entity target);

	/**
	 * Returns whether this item can be unequipped by a player
	 * @param stack Item to unequip
	 * @param player Player that is interacting
	 * @param target Entity that will be unequipped
	 * @param inventory Equipment inventory 
	 * @return
	 */
	public boolean canUnequip(ItemStack stack, @Nullable EntityPlayer player, Entity target, IInventory inventory);

	/**
	 * Returns whether this item can drop on death
	 * @param stack Equipped item
	 * @param entity Entity that died
	 * @param inventory Equipment inventory 
	 * @return
	 */
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory);

	/**
	 * Called when an item is equipped
	 * @param stack Equipped item
	 * @param entity Entity that is being equipped
	 * @param inventory Equipment inventory
	 */
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory);

	/**
	 * Called when an item is unequipped
	 * @param stack Equipped item
	 * @param entity Entity that is being unequipped
	 * @param inventory Equipment inventory
	 */
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory);

	/**
	 * Called when the equipment is ticked
	 * @param stack
	 * @param entity
	 */
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory);

	/**
	 * Adds the equipped property overrides to the specified item
	 * @param item
	 */
	public static void addEquippedPropertyOverrides(Item item) {
		item.addPropertyOverride(new ResourceLocation("equipped"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
				if(stack.getItem() instanceof IEquippable && entity != null && entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
					IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
					IInventory inv = cap.getInventoryIfPresent(((IEquippable) stack.getItem()).getEquipmentCategory(stack));
					if (inv != null) {
						for(int i = 0; i < inv.getSizeInventory(); i++) {
							if(stack == inv.getStackInSlot(i)) {
								return 1;
							}
						}
					}
				}
				return 0;
			}
		});
	}
}

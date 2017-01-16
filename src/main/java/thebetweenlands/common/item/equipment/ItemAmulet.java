package thebetweenlands.common.item.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.entity.mobs.EntityGiantToad;

public class ItemAmulet extends Item implements IEquippable {
	public ItemAmulet() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);

		//TODO Implement Amulet
	}

	@Override
	public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentInventory.AMULET;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity entity, IInventory inventory) {
		return entity instanceof EntityGiantToad;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity entity, IInventory inventory) {
		return entity instanceof EntityGiantToad;
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity entity, IInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
		System.out.println("EQUIP ON " + entity);
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
		System.out.println("UNEQUIP FROM " + entity);
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity) {
		System.out.println("EQUIPMENT TICK " + entity);
	}

}

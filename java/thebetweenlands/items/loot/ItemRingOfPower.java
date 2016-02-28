package thebetweenlands.items.loot;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.IEquippable;
import thebetweenlands.manual.IManualEntryItem;

/**
 * Created by Bart on 8-7-2015.
 */
public class ItemRingOfPower extends Item implements IEquippable, IManualEntryItem {
	public ItemRingOfPower() {
		this.maxStackSize = 1;
		this.setUnlocalizedName("thebetweenlands.ringOfPower");
		setTextureName("thebetweenlands:ringOfPower");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("power.ring.bonus"));
	}

	@Override
	public String manualName(int meta) {
		return "ringOfPower";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{6};
	}

	@Override
	public int metas() {
		return 0;
	}

	@Override
	public EnumEquipmentCategory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentCategory.RING;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return entity instanceof EntityPlayer && inventory.getEquipment(EnumEquipmentCategory.RING).size() == 0;
	}

	@Override
	public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, EquipmentInventory inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, EquipmentInventory inventory) { }

	@Override
	public void onUnequip(ItemStack stack, Entity entity, EquipmentInventory inventory) { }
}

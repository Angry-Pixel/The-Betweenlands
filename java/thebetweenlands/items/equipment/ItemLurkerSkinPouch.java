package thebetweenlands.items.equipment;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.IEquippable;
import thebetweenlands.proxy.CommonProxy;

public class ItemLurkerSkinPouch extends Item implements IEquippable {

	public ItemLurkerSkinPouch() {
		super();
		setMaxStackSize(1);
		setUnlocalizedName("thebetweenlands.lurkerSkinPouch");
		setTextureName("thebetweenlands:lurkerSkinPouch");
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean showAdvancedInfo) {
		int slots = 9 + (stack.getItemDamage() * 9);
		list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("lurkerSkinPouch.size") + " " + slots);
		list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("lurkerSkinPouch.info"));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				int meta = stack.getItemDamage();
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH, world, meta, 0, 0);
			}
			else {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_LURKER_POUCH_NAMING, world, 0, 0, 0);
			}
		}
		return stack;
	}

	@Override
	public EnumEquipmentCategory getEquipmentCategory(ItemStack stack) {
		return EnumEquipmentCategory.POUCH;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return false;
	}

	@Override
	public boolean canEquip(ItemStack stack, EntityPlayer player, Entity entity, EquipmentInventory inventory) {
		return entity instanceof EntityPlayer && inventory.getEquipment(EnumEquipmentCategory.POUCH).size() == 0;
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

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity) { }

	/**
	 * Returns the first accessible pouch of the players equipment (first priority) or hotbar
	 * @param player
	 * @return
	 */
	public static ItemStack getFirstPouch(EntityPlayer player) {
		EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(player);
		if(equipmentInventory != null && !equipmentInventory.getEquipment(EnumEquipmentCategory.POUCH).isEmpty()) {
			Equipment pouch = equipmentInventory.getEquipment(EnumEquipmentCategory.POUCH).get(0);
			return pouch.item;
		}
		InventoryPlayer playerInventory = player.inventory;
		for(int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
			ItemStack stack = playerInventory.getStackInSlot(i);
			if(stack != null && stack.getItem() == BLItemRegistry.lurkerSkinPouch) {
				return stack;
			}
		}
		return null;
	}
}

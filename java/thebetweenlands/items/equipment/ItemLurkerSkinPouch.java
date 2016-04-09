package thebetweenlands.items.equipment;

import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.IEquippable;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.utils.ItemRenderHelper;

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

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Specials.Post event) {
		if(event.entityPlayer != null) {
			GL11.glPushMatrix();
			if(event.entityPlayer == TheBetweenlands.proxy.getClientPlayer()) {
				GL11.glTranslated(0, 1.65D, 0);
			}
			double rx = event.entityPlayer.prevPosX + (event.entityPlayer.posX - event.entityPlayer.prevPosX) * event.partialRenderTick;
			double ry = event.entityPlayer.prevPosY + (event.entityPlayer.posY - event.entityPlayer.prevPosY) * event.partialRenderTick;
			double rz = event.entityPlayer.prevPosZ + (event.entityPlayer.posZ - event.entityPlayer.prevPosZ) * event.partialRenderTick;
			this.renderPouch(event.entityPlayer, rx, ry, rz, (float)event.partialRenderTick);
			GL11.glPopMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	private void renderPouch(EntityLivingBase entity, double x, double y, double z, float partialTicks) {
		EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(entity);
		List<Equipment> equippedPouches = equipmentInventory.getEquipment(EnumEquipmentCategory.POUCH);
		if(!equippedPouches.isEmpty()) {
			Equipment pouch = equippedPouches.get(0);
			ItemStack stack = pouch.item;
			if(stack != null) {
				GL11.glPushMatrix();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glRotated(180, 1, 0, 0);
				GL11.glTranslated(0, 1.25D, 0);
				GL11.glRotated(entity.isSneaking() ? 28 : 0, 1, 0, 0);
				GL11.glTranslated(0, entity.isSneaking() ? -0.05D : 0, entity.isSneaking() ? -0.37D : -0.18D);
				GL11.glPushMatrix();
				GL11.glTranslated(0, 0, 0.02D);
				GL11.glScaled(0.33D, 0.33D, 0.5D);
				ItemRenderHelper.renderItem(stack, 0);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glScaled(0.3D, 0.3D, 0.5D);
				ItemRenderHelper.renderItem(stack, 0);
				GL11.glPopMatrix();
				GL11.glPopMatrix();
			}
		}
	}
}

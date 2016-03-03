package thebetweenlands.event.input.radialmenu;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.event.item.ItemEquipmentHandler;
import thebetweenlands.items.IEquippable;
import thebetweenlands.network.packet.client.PacketEquipment;
import thebetweenlands.utils.ItemRenderHelper;

public class Categories {
	public static class EquipCategory extends thebetweenlands.event.input.radialmenu.RadialMenuHandler.Category {
		protected final int slot;
		protected final ItemStack item;

		/**
		 * Adds an equip category.
		 * @param name
		 * @param color
		 * @param highlightColor
		 * @param item
		 * @param slot Inventory slot of the item to equip
		 */
		public EquipCategory(String name, int color, int highlightColor, ItemStack item, int slot) {
			super(name, color, highlightColor);
			this.item = item;
			this.slot = slot;
		}

		public ItemStack getItem() {
			return this.item;
		}

		@Override
		public void renderCategory(double centerX, double centerY, double dirX, double dirY, double radius, double startX, double startY, double angle, double segmentAngle) {
			if(this.item != null) {
				GL11.glPushMatrix();
				double posX = centerX + startX + dirX * radius / 2.0D - 8;
				double posY = centerY + startY + dirY * radius / 2.0D - 8;
				GL11.glTranslated(posX, posY, 0);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				ItemRenderHelper.drawItemStack(this.getItem(), 0, 0, null, true);
				GL11.glPopMatrix();
			}
		}

		@Override
		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
			if(this.slot < sender.inventory.getSizeInventory()) {
				ItemStack item = sender.inventory.getStackInSlot(this.slot);
				if(item != null) {
					if(item.getItem() instanceof IEquippable) {
						IEquippable equippable = (IEquippable) item.getItem();
						EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(sender);
						if(equippable.canEquip(item, sender, sender, equipmentInventory)) {
							TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketEquipment(0, this.slot)));
							ItemEquipmentHandler.tryPlayerEquip(sender, sender, item);
							if(item.stackSize <= 0)
								sender.inventory.setInventorySlotContents(this.slot, null);
						}
					}
				}
			}
			return mouseButton == 0;
		}
	}

	public static class UnequipCategory extends EquipCategory {
		/**
		 * Adds an unequip category
		 * @param name
		 * @param color
		 * @param highlightColor
		 * @param item
		 * @param slot Equipment slot of the equipment to drop
		 */
		public UnequipCategory(String name, int color, int highlightColor, ItemStack item, int slot) {
			super(name, color, highlightColor, item, slot);
		}

		@Override
		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
			EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(sender);
			if(this.slot < equipmentInventory.getEquipment().size()) {
				Equipment equipment = equipmentInventory.getEquipment().get(this.slot);
				if(equipment != null && equipment.equippable.canUnequip(equipment.item, sender, sender, equipmentInventory)) {
					TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketEquipment(1, this.slot)));
					EquipmentInventory.unequipItem(sender, equipment);
					if(!sender.inventory.addItemStackToInventory(equipment.item))
						sender.entityDropItem(equipment.item, sender.getEyeHeight());
				}
			}
			return mouseButton == 0;
		}
	}
}

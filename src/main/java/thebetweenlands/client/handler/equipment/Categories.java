package thebetweenlands.client.handler.equipment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.handler.equipment.RadialMenuHandler.Category;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.network.serverbound.MessageEquipItem;
import thebetweenlands.common.registries.CapabilityRegistry;

public class Categories {
	public static class EquipCategory extends Category {
		protected final int slot;
		protected final ItemStack item;
		protected final EnumEquipmentInventory inventory;

		/**
		 * Adds an equip category.
		 * @param name
		 * @param color
		 * @param highlightColor
		 * @param item
		 * @param slot Inventory slot of the item to equip
		 */
		public EquipCategory(String name, int color, int highlightColor, ItemStack item, EnumEquipmentInventory inventory, int slot) {
			super(name, color, highlightColor);
			this.item = item;
			this.slot = slot;
			this.inventory = inventory;
		}

		public ItemStack getItem() {
			return this.item;
		}

		@Override
		public void renderCategory(double centerX, double centerY, double dirX, double dirY, double radius, double startX, double startY, double angle, double segmentAngle) {
			if(!this.item.isEmpty()) {
				GlStateManager.pushMatrix();
				double posX = centerX + startX + dirX * radius / 2.0D - 8;
				double posY = centerY + startY + dirY * radius / 2.0D - 8;
				GlStateManager.translate(posX, posY, 0);
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

				Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(this.getItem(), 0, 0);

				GlStateManager.popMatrix();
			}
		}

		@Override
		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			EntityPlayer sender = Minecraft.getMinecraft().player;

			if(sender.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				ItemStack res = EquipmentHelper.equipItem(sender, sender, item, false);

				if(res.isEmpty() || res.getCount() != item.getCount()) {
					TheBetweenlands.networkWrapper.sendToServer(new MessageEquipItem(this.slot, sender));

					if(!sender.capabilities.isCreativeMode) {
						sender.inventory.setInventorySlotContents(this.slot, res);
					}

					RadialMenuHandler.INSTANCE.updateMenu();
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
		public UnequipCategory(String name, int color, int highlightColor, ItemStack item, EnumEquipmentInventory inventory, int slot) {
			super(name, color, highlightColor, item, inventory, slot);
		}

		@Override
		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			EntityPlayer sender = Minecraft.getMinecraft().player;
			ItemStack unequipped = EquipmentHelper.unequipItem(sender, sender, this.inventory, this.slot, false);

			if(!unequipped.isEmpty()) {
				TheBetweenlands.networkWrapper.sendToServer(new MessageEquipItem(sender, this.inventory, this.slot));

				sender.inventory.addItemStackToInventory(unequipped);

				RadialMenuHandler.INSTANCE.updateMenu();
			}

			return mouseButton == 0;
		}
	}
}

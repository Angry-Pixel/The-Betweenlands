package thebetweenlands.client.handler.equipment;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.network.serverbound.EquipItemPacket;
import thebetweenlands.common.registries.AttachmentRegistry;

public class Categories {

	public static class EquipCategory extends RadialMenuHandler.Category {
		protected final int slot;
		protected final ItemStack item;
		protected final EquipmentInventoryType inventory;

		/**
		 * Adds an equip category.
		 *
		 * @param name
		 * @param color
		 * @param highlightColor
		 * @param item
		 * @param slot           Inventory slot of the item to equip
		 */
		public EquipCategory(Component name, int color, int highlightColor, ItemStack item, EquipmentInventoryType inventory, int slot) {
			super(name, color, highlightColor);
			this.item = item;
			this.slot = slot;
			this.inventory = inventory;
		}

		public ItemStack getItem() {
			return this.item;
		}

		@Override
		public void renderCategory(GuiGraphics graphics, double centerX, double centerY, double dirX, double dirY, double radius, double startX, double startY, double angle, double segmentAngle) {
			if (!this.item.isEmpty()) {
				graphics.pose().pushPose();
				double posX = centerX + startX + dirX * radius / 2.0D - 8;
				double posY = centerY + startY + dirY * radius / 2.0D - 8;
				graphics.pose().translate(posX, posY, 0);
				graphics.renderFakeItem(this.getItem(), 0, 0);
				graphics.pose().popPose();
			}
		}

		@Override
		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			Player sender = BetweenlandsClient.getClientPlayer();

			if (sender.hasData(AttachmentRegistry.EQUIPMENT)) {
				ItemStack res = EquipmentHelper.equipItem(sender, sender, this.item, false);

				if (res.isEmpty() || res.getCount() != this.item.getCount()) {
					PacketDistributor.sendToServer(new EquipItemPacket(sender.getId(), this.slot, EquipItemPacket.EQUIP_MODE, this.inventory));

					if (!sender.isCreative()) {
						sender.getInventory().setItem(this.slot, res);
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
		 *
		 * @param name
		 * @param color
		 * @param highlightColor
		 * @param item
		 * @param slot           Equipment slot of the equipment to drop
		 */
		public UnequipCategory(Component name, int color, int highlightColor, ItemStack item, EquipmentInventoryType inventory, int slot) {
			super(name, color, highlightColor, item, inventory, slot);
		}

		@Override
		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			Player sender = BetweenlandsClient.getClientPlayer();
			ItemStack unequipped = EquipmentHelper.unequipItem(sender, sender, this.inventory, this.slot, false);

			if (!unequipped.isEmpty()) {
				PacketDistributor.sendToServer(new EquipItemPacket(sender.getId(), this.slot, EquipItemPacket.UNEQUIP_MODE, this.inventory));

				sender.getInventory().add(unequipped);

				RadialMenuHandler.INSTANCE.updateMenu();
			}

			return mouseButton == 0;
		}
	}
}

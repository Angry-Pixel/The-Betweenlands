package thebetweenlands.event.input.radialmenu;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import thebetweenlands.TheBetweenlands;
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
				double posX = centerX + startX + dirX * radius / 2.0D - 1;
				double posY = centerY + startY + dirY * radius / 2.0D + 4;
				GL11.glTranslated(posX, posY, 0);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glScaled(-10, -10, 0);
				for(int i = 0; i < this.item.getItem().getRenderPasses(this.item.getItemDamage()); i++) {
					ItemRenderHelper.renderItem(this.item, i);
				}
				GL11.glPopMatrix();
			}
		}

		@Override
		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketEquipment(0, this.slot)));
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
			TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketEquipment(1, this.slot)));
			return mouseButton == 0;
		}
	}
}

package thebetweenlands.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.InventoryItem;
import thebetweenlands.common.inventory.container.ContainerAmphibiousArmour;
import thebetweenlands.common.item.armor.amphibian.ItemAmphibianArmor;

@SideOnly(Side.CLIENT)
public class GuiAmphibiousArmour extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/amphibious_armour.png");
	private final InventoryItem inventory;

	public GuiAmphibiousArmour(ContainerAmphibiousArmour armour) {
		super(armour);
		this.inventory = armour.getItemInventory();
		ySize = 182;
		xSize = 174;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		String s = I18n.format(inventory.getName(), new Object[0]);
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		ItemStack invItem = this.inventory.getInventoryItemStack();

		if(invItem.getItem() instanceof ItemAmphibianArmor) {
			for(Slot slot : this.inventorySlots.inventorySlots) {
				if(slot instanceof ContainerAmphibiousArmour.SlotUpgrade && slot.getHasStack()) {
					int damage = ((ItemAmphibianArmor) invItem.getItem()).getUpgradeDamage(invItem, slot.getSlotIndex());

					if(damage > 0) {
						int maxDamage = ((ItemAmphibianArmor) invItem.getItem()).getUpgradeMaxDamage(invItem, slot.getSlotIndex());

						GlStateManager.disableTexture2D();

						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder buffer = tessellator.getBuffer();

						double durability = damage / (double)maxDamage;
						int bar = Math.round(16.0F - (float)durability * 16.0F);

						int color = MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - durability)) / 3.0F, 1.0F, 1.0F);

						this.draw(buffer, this.guiLeft + slot.xPos, this.guiTop + slot.yPos - 4, 16, 2, 0, 0, 0, 255);
						this.draw(buffer, this.guiLeft + slot.xPos, this.guiTop + slot.yPos - 4, bar, 1, color >> 16 & 255, color >> 8 & 255, color & 255, 255);

						GlStateManager.enableTexture2D();
					}
				}
			}
		}
	}

	private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}

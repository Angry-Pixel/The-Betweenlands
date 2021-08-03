package thebetweenlands.client.gui.inventory;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
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
import thebetweenlands.common.inventory.container.ContainerAmphibiousArmor;
import thebetweenlands.common.item.armor.amphibious.ItemAmphibiousArmor;

@SideOnly(Side.CLIENT)
public class GuiAmphibiousArmor extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/amphibious_armor.png");
	private final InventoryItem inventory;

	private static final int ANIMATION_FRAMES_X = 39;
	private static final int ANIMATION_FRAMES_Y = 55;
	private static final int[][] ANIMATION_FRAMES = {
			{1, 222},
			{1, 281},
			{1, 340},
			{1, 399},
			{127, 222},
			{127, 281},
			{127, 340},
			{127, 399}
	};
	private static final int[][] SLOT_FRAMES = {
			{253, 163},
			{253, 222},
			{253, 281},
			{253, 340},
			{253, 399}
	};
	
	private int pulseTimer = 0;
	private int pulseAnimationTicks = 0;
	
	public GuiAmphibiousArmor(ContainerAmphibiousArmor armour) {
		super(armour);
		this.inventory = armour.getItemInventory();
		this.ySize = 221;
		this.xSize = 202;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		
		if(this.pulseAnimationTicks >= 0) {
			this.pulseAnimationTicks++;
			
			if(this.pulseAnimationTicks >= (ANIMATION_FRAMES.length + 1) * 5) {
				this.pulseAnimationTicks = -1;
			}
		} else {
			this.pulseTimer++;
			
			if(this.pulseTimer > 5) {
				this.pulseAnimationTicks = 0;
				this.pulseTimer = 0;
			}
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 20, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);

		this.mc.getTextureManager().bindTexture(GUI_TEXTURE);

		this.drawTexturedModalRect512(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		
        GlStateManager.disableAlpha();
		
		if(this.pulseAnimationTicks >= 0) {
			float animationCounter = (this.pulseAnimationTicks + partialTickTime) / 5.0f;
			int currentFrame = MathHelper.floor(animationCounter);
			
			float frame1alpha;
			int frame1;
			
			float frame2alpha;
			int frame2;
			
			if(currentFrame == 0) {
				frame1 = frame2 = 0;
				frame1alpha = animationCounter;
				frame2alpha = 0;
			} else if(currentFrame >= ANIMATION_FRAMES.length - 1) {
				frame1 = frame2 = ANIMATION_FRAMES.length - 1;
				frame1alpha = 0;
				frame2alpha = Math.max(0, 1.0f - (animationCounter - ANIMATION_FRAMES.length + 1));
			} else {
				frame1 = currentFrame;
				frame1alpha = 1.0f - (animationCounter - currentFrame);
				frame2 = currentFrame + 1;
				frame2alpha = 1.0f - frame1alpha;
			}

			GlStateManager.color(1, 1, 1, frame1alpha);
			this.drawTexturedModalRect512(this.guiLeft + ANIMATION_FRAMES_X, this.guiTop + ANIMATION_FRAMES_Y, ANIMATION_FRAMES[frame1][0], ANIMATION_FRAMES[frame1][1], 124, 57);
			
			GlStateManager.color(1, 1, 1, frame2alpha);
			this.drawTexturedModalRect512(this.guiLeft + ANIMATION_FRAMES_X, this.guiTop + ANIMATION_FRAMES_Y, ANIMATION_FRAMES[frame2][0], ANIMATION_FRAMES[frame2][1], 124, 57);
		}

		GlStateManager.color(1, 1, 1, 1);
		
		for(int i = 0; i < 5; i++) {
			if(this.inventory.getSizeInventory() > i && this.inventorySlots.getSlot(i).getHasStack()) {
				this.drawTexturedModalRect512(this.guiLeft + ANIMATION_FRAMES_X, this.guiTop + ANIMATION_FRAMES_Y, SLOT_FRAMES[i][0], SLOT_FRAMES[i][1], 124, 57);
			}
		}
        
		if(this.inventory.getSizeInventory() < 4) {
			this.drawTexturedModalRect512(this.guiLeft + 67, this.guiTop + 81, 222, 0, 34, 35);
		}
		
		if(this.inventory.getSizeInventory() < 5) {
			this.drawTexturedModalRect512(this.guiLeft + 101, this.guiTop + 81, 256, 0, 34, 35);
		}
		
		GlStateManager.enableAlpha();
		
		ItemStack invItem = this.inventory.getInventoryItemStack();

		if(invItem.getItem() instanceof ItemAmphibiousArmor) {
			for(Slot slot : this.inventorySlots.inventorySlots) {
				if(slot instanceof ContainerAmphibiousArmor.SlotUpgrade) {
					if(slot.getHasStack()) {
						int damage = ((ItemAmphibiousArmor) invItem.getItem()).getUpgradeDamage(invItem, slot.getSlotIndex());

						if(damage > 0) {
							int maxDamage = ((ItemAmphibiousArmor) invItem.getItem()).getUpgradeMaxDamage(invItem, slot.getSlotIndex());

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
					} else if(isShiftKeyDown()) {
						ItemStack filter = ((ItemAmphibiousArmor) invItem.getItem()).getUpgradeFilter(invItem, slot.getSlotIndex());

						if(!filter.isEmpty()) {
							this.renderSlot(filter, this.guiLeft + slot.xPos, this.guiTop + slot.yPos);
						}
					}
				}
			}
		}
	}

	private void renderSlot(ItemStack stack, int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GL14.glBlendColor(0, 0, 0, 0.35f);
		GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA); //ugly hack
		GlStateManager.pushMatrix();
		this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		GlStateManager.popMatrix();
		GL14.glBlendColor(1, 1, 1, 1);
		GlStateManager.blendFunc(SourceFactor.CONSTANT_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA); //ugly
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
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

	/**
	 * Same as {@link #drawTexturedModalRect(int, int, int, int, int, int)} but for 512x512 textures
	 */
	protected void drawTexturedModalRect512(float x, float y, int minU, int minV, int width, int height) {
		float scale = 0.001953125F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + height) * scale)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + height) * scale)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + 0) * scale)).endVertex();
		bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + 0) * scale)).endVertex();
		tessellator.draw();
	}
}

package thebetweenlands.client.gui.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.common.inventory.container.ContainerCenser;
import thebetweenlands.common.tile.TileEntityCenser;

@SideOnly(Side.CLIENT)
public class GuiCenser extends GuiContainer {
	private TileEntityCenser censer;
	private static final ResourceLocation CENSER_GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/censer.png");

	public GuiCenser(InventoryPlayer inv, TileEntityCenser tile) {
		super(new ContainerCenser(inv, tile));
		this.ySize = 256;
		this.censer = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(CENSER_GUI_TEXTURE);
		int xx = (width - xSize) / 2;
		int yy = (height - ySize) / 2;
		drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		super.drawScreen(mouseX, mouseY, partialTicks);

		Slot internalSlot = this.inventorySlots.getSlot(ContainerCenser.SLOT_INTERNAL);

		List<String> effectTooltip = null;

		ICenserRecipe<Object> currentRecipe = this.censer.getCurrentRecipe();

		if(currentRecipe != null) {
			List<String> effect = new ArrayList<>();
			currentRecipe.getLocalizedEffectText(this.censer.getCurrentRecipeContext(), this.censer.getCurrentRecipeInputAmount(), this.censer, effect);
			if(!effect.isEmpty()) {
				effectTooltip = new ArrayList<>();
				effectTooltip.add("");
				effectTooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocalFormatted("tooltip.bl.censer_effect"));
				effectTooltip.addAll(effect);
			}

			int amount = this.censer.getCurrentRecipeInputAmount();
			if(amount > 0) {
				int maxAmount = this.censer.getMaxCurrentRecipeInputAmount();
				int effectColor = currentRecipe.getEffectColor(this.censer.getCurrentRecipeContext(), this.censer.getCurrentRecipeInputAmount(), this.censer);

				float percent = (float)amount / (float)maxAmount;

				this.zLevel = 100.0F;
				this.itemRender.zLevel = 100.0F;

				GlStateManager.disableLighting();

				GlStateManager.enableRescaleNormal();
				GlStateManager.enableAlpha();
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableTexture2D();

				int maxBarHeight = 84;
				int barHeight = MathHelper.ceil(percent * maxBarHeight);
				int barHeightBackground = Math.min(barHeight + 1, maxBarHeight);
				int barHeightBackground2 = Math.min(barHeight + 2, maxBarHeight);
				int barHeightBackground3 = Math.min(barHeight + 3, maxBarHeight);

				this.mc.getTextureManager().bindTexture(CENSER_GUI_TEXTURE);

				this.drawTexturedModalRectWithGradient(this.guiLeft + 67, this.guiTop + 18 + maxBarHeight - barHeightBackground3, 195, 1 + maxBarHeight - barHeightBackground3, 47, barHeightBackground3, 0xFF202020, 0xFF202020);
				this.drawTexturedModalRectWithGradient(this.guiLeft + 67, this.guiTop + 18 + maxBarHeight - barHeightBackground2, 195, 1 + maxBarHeight - barHeightBackground2, 47, barHeightBackground2, 0xFF505050, 0xFF505050);
				this.drawTexturedModalRectWithGradient(this.guiLeft + 67, this.guiTop + 18 + maxBarHeight - barHeightBackground, 195, 1 + maxBarHeight - barHeightBackground, 47, barHeightBackground, 0xFF808080, 0xFF808080);
				this.drawTexturedModalRectWithGradient(this.guiLeft + 67, this.guiTop + 18 + maxBarHeight - barHeight, 195, 1 + maxBarHeight - barHeight, 47, barHeight, effectColor, 0xFFFFFFFF);

				GlStateManager.disableAlpha();
				GlStateManager.disableRescaleNormal();
				GlStateManager.enableLighting();

				this.itemRender.zLevel = 0.0F;
				this.zLevel = 0.0F;
			}
		}

		int fuelTicks = this.censer.getFuelTicks();
		if(fuelTicks > 0) {
			int maxFuelTicks = this.censer.getMaxFuelTicks();

			float percent = (float)fuelTicks / (float)maxFuelTicks;

			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;

			GlStateManager.disableLighting();

			GlStateManager.enableRescaleNormal();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableTexture2D();

			int maxBarHeight = 14;
			int barHeight = MathHelper.ceil(percent * maxBarHeight);

			this.mc.getTextureManager().bindTexture(CENSER_GUI_TEXTURE);

			this.drawTexturedModalRect(this.guiLeft + 81, this.guiTop + 122 + maxBarHeight - barHeight, 177, 20 + maxBarHeight - barHeight, 14, barHeight);

			GlStateManager.disableAlpha();
			GlStateManager.disableRescaleNormal();
			GlStateManager.enableLighting();

			this.itemRender.zLevel = 0.0F;
			this.zLevel = 0.0F;
		}

		ItemStack internalStack = internalSlot.getStack();
		if(!internalStack.isEmpty()) {
			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;

			GlStateManager.enableDepth();

			this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, internalStack, this.guiLeft + internalSlot.xPos, this.guiTop + internalSlot.yPos);
			this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, internalStack, this.guiLeft + internalSlot.xPos, this.guiTop + internalSlot.yPos, null);

			this.itemRender.zLevel = 0.0F;
			this.zLevel = 0.0F;

			if(this.isPointInRegion(internalSlot.xPos, internalSlot.yPos, 16, 16, mouseX, mouseY)) {
				this.renderToolTipWithMore(internalStack, mouseX, mouseY, effectTooltip);
			}
		} else {
			//Render internal slot cover

			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;

			GlStateManager.disableLighting();

			GlStateManager.enableRescaleNormal();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableTexture2D();

			this.mc.getTextureManager().bindTexture(CENSER_GUI_TEXTURE);
			this.drawTexturedModalRect(this.guiLeft + internalSlot.xPos - 1, this.guiTop + internalSlot.yPos - 1, 176, 1, 18, 18);

			GlStateManager.disableAlpha();
			GlStateManager.disableRescaleNormal();
			GlStateManager.enableLighting();

			this.itemRender.zLevel = 0.0F;
			this.zLevel = 0.0F;
		}

		FluidStack fluidStack = this.censer.getTankProperties()[0].getContents();
		if(fluidStack != null) {
			ResourceLocation fluidTexture = fluidStack.getFluid().getStill();

			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;

			GlStateManager.disableLighting();

			GlStateManager.enableRescaleNormal();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableTexture2D();

			this.zLevel = 90.0F;

			int color = fluidStack.getFluid().getColor(fluidStack);

			if(fluidTexture != null) {
				TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidTexture.toString());

				this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				float a = ((color >> 24) & 0xFF) / 255f;

				GlStateManager.color(r, g, b, a);

				this.drawTexturedModalRect(this.guiLeft + internalSlot.xPos - 3, this.guiTop + internalSlot.yPos - 3, sprite, 20, 20);
			} else {
				GlStateManager.disableTexture2D();

				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, this.zLevel);
				drawRect(this.guiLeft + internalSlot.xPos - 3, this.guiTop + internalSlot.yPos - 3, this.guiLeft + internalSlot.xPos - 3 + 20, this.guiTop + internalSlot.yPos - 3 + 20, color);
				GlStateManager.popMatrix();
			}

			GlStateManager.enableTexture2D();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			//Render cover
			this.mc.getTextureManager().bindTexture(CENSER_GUI_TEXTURE);
			this.drawTexturedModalRect(this.guiLeft + internalSlot.xPos - 3, this.guiTop + internalSlot.yPos - 3, 176, 100, 20, 20);

			GlStateManager.disableAlpha();
			GlStateManager.disableRescaleNormal();
			GlStateManager.enableLighting();

			this.itemRender.zLevel = 0.0F;
			this.zLevel = 0.0F;

			if(this.isPointInRegion(internalSlot.xPos, internalSlot.yPos, 16, 16, mouseX, mouseY)) {
				List<String> tooltip = new ArrayList<>();
				tooltip.add(fluidStack.getLocalizedName() + " (" + fluidStack.amount + "mb)");
				if(effectTooltip != null) {
					tooltip.addAll(effectTooltip);
				}
				this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRenderer);
			}
		}

		this.renderHoveredToolTip(mouseX, mouseY);
	}

	protected void renderToolTipWithMore(ItemStack stack, int x, int y, @Nullable List<String> more) {
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
		List<String> tooltip = this.getItemToolTip(stack);
		if(more != null) {
			tooltip.addAll(more);
		}
		this.drawHoveringText(tooltip, x, y, (font == null ? fontRenderer : font));
		net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
	}

	protected void drawTexturedModalRectWithGradient(int x, int y, int textureX, int textureY, int width, int height, int colorTop, int colorBottom) {
		float r1 = ((colorTop >> 16) & 0xFF) / 255f;
		float g1 = ((colorTop >> 8) & 0xFF) / 255f;
		float b1 = ((colorTop >> 0) & 0xFF) / 255f;
		float a1 = ((colorTop >> 24) & 0xFF) / 255f;

		float r2 = ((colorBottom >> 16) & 0xFF) / 255f;
		float g2 = ((colorBottom >> 8) & 0xFF) / 255f;
		float b2 = ((colorBottom >> 0) & 0xFF) / 255f;
		float a2 = ((colorBottom >> 24) & 0xFF) / 255f;

		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).color(r2, g2, b2, a2).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).color(r2, g2, b2, a2).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).color(r1, g1, b1, a1).endVertex();
		bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).color(r1, g1, b1, a1).endVertex();
		tessellator.draw();

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
	}
}

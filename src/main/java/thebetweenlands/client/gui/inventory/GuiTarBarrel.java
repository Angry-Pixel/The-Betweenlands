package thebetweenlands.client.gui.inventory;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.container.ContainerBarrel;
import thebetweenlands.common.tile.TileEntityBarrel;

@SideOnly(Side.CLIENT)
public class GuiTarBarrel extends GuiContainer {
	private TileEntityBarrel barrel;
	private static final ResourceLocation TAR_BARREL_GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/tar_barrel.png");

	private DecimalFormat numberFormat;

	public GuiTarBarrel(InventoryPlayer inv, TileEntityBarrel tile) {
		super(new ContainerBarrel(inv, tile));

		this.barrel = tile;

		this.numberFormat = new DecimalFormat();
		this.numberFormat.setMinimumFractionDigits(1);
		this.numberFormat.setMaximumFractionDigits(1);
		this.numberFormat.setRoundingMode(RoundingMode.DOWN);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);

		mc.renderEngine.bindTexture(TAR_BARREL_GUI_TEXTURE);

		int xx = this.guiLeft;
		int yy = this.guiTop;

		drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);

		//bucket base
		this.drawTexturedModalRect(xx + 140, yy + 16, 176, 64, 16, 16);

		IFluidTankProperties props = this.barrel.getTankProperties()[0];
		FluidStack fluidStack = props.getContents();

		if(fluidStack != null) {
			ResourceLocation fluidTexture = fluidStack.getFluid().getStill();

			int color = fluidStack.getFluid().getColor(fluidStack);

			int barHeight = MathHelper.ceil(57 * fluidStack.amount / (float)props.getCapacity());

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();

			if(fluidTexture != null) {
				TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidTexture.toString());

				this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				float a = ((color >> 24) & 0xFF) / 255f;

				GlStateManager.color(r, g, b, a);

				//barrel
				this.drawTexturedBar(xx + 64, yy + 71, sprite, 47, 57, barHeight);

				//bucket
				this.drawTexturedBar(xx + 140, yy + 32, sprite, 16, 16, 16);
			} else {
				GlStateManager.disableTexture2D();

				//barrel
				drawRect(xx + 64, yy + 71 - barHeight, xx + 64 + 47, yy + 71, color);

				//bucket
				drawRect(xx + 140, yy + 16, xx + 140 + 16, yy + 16 + 16, color);
			}

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.enableTexture2D();
			GlStateManager.color(1, 1, 1, 1);

			this.mc.renderEngine.bindTexture(TAR_BARREL_GUI_TEXTURE);

			//barrel cover
			this.drawTexturedModalRect(xx + 64, yy + 12, 176, 1, 47, 62);

			//bucket cover
			this.drawTexturedModalRect(xx + 140, yy + 16, 176 + 17, 64, 16, 16);

			this.fontRenderer.drawString(this.numberFormat.format(fluidStack != null ? fluidStack.amount / 1000.0f : 0.0f), xx + 127, yy + 26, 0xFFFFFFFF);

			GlStateManager.disableBlend();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		super.drawScreen(mouseX, mouseY, partialTicks);

		this.renderHoveredToolTip(mouseX, mouseY);

		if(this.isPointInRegion(69, 16, 37, 54, mouseX, mouseY)) {
			IFluidTankProperties props = this.barrel.getTankProperties()[0];
			FluidStack fluidStack = props.getContents();

			if(fluidStack != null) {
				List<String> tooltip = new ArrayList<>();
				tooltip.add(fluidStack.getLocalizedName() + " (" + fluidStack.amount + "mb)");
				this.drawHoveringText(tooltip, mouseX, mouseY, this.fontRenderer);
			}
		}
	}

	protected void drawTexturedBar(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn, int fullHeight) {
		if(heightIn <= 0) {
			return;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

		int yStart = 0;
		while(yStart < fullHeight) {
			int segmentHeight = Math.min(yStart + heightIn, fullHeight) - yStart;

			float fraction = segmentHeight / (float)heightIn;

			float minU = textureSprite.getMinU();
			float minV = textureSprite.getMinV();
			float maxU = textureSprite.getMaxU();
			float maxV = textureSprite.getMaxV();

			maxV = minV + (maxV - minV) * fraction;

			int yOff = -yStart - (int)(heightIn * fraction);

			bufferbuilder.pos((double)(xCoord + widthIn), (double)(yOff + yCoord + 0), (double)this.zLevel).tex(maxU, maxV).endVertex();
			bufferbuilder.pos((double)(xCoord + 0), (double)(yOff + yCoord + 0), (double)this.zLevel).tex(minU, maxV).endVertex();
			bufferbuilder.pos((double)(xCoord + 0), (double)(yOff + yCoord + heightIn * fraction), (double)this.zLevel).tex(minU, minV).endVertex();
			bufferbuilder.pos((double)(xCoord + widthIn), (double)(yOff + yCoord + heightIn * fraction), (double)this.zLevel).tex(maxU, minV).endVertex();

			yStart += heightIn;
		}

		tessellator.draw();
	}
}

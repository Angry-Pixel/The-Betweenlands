package thebetweenlands.client.gui.inventory;

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
import thebetweenlands.common.inventory.container.ContainerTarBarrel;
import thebetweenlands.common.tile.TileEntityTarBarrel;

@SideOnly(Side.CLIENT)
public class GuiTarBarrel extends GuiContainer {
	private TileEntityTarBarrel barrel;
	private static final ResourceLocation TAR_BARREL_GUI_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/tar_barrel.png");

	public GuiTarBarrel(InventoryPlayer inv, TileEntityTarBarrel tile) {
		super(new ContainerTarBarrel(inv, tile));
		this.ySize = 256;
		this.barrel = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		GlStateManager.color(1, 1, 1, 1);

		mc.renderEngine.bindTexture(TAR_BARREL_GUI_TEXTURE);

		int xx = this.guiLeft;
		int yy = this.guiTop;

		drawTexturedModalRect(xx, yy, 0, 0, xSize, ySize);

		IFluidTankProperties props = this.barrel.getTankProperties()[0];
		FluidStack fluidStack = props.getContents();

		if(fluidStack != null) {
			ResourceLocation fluidTexture = fluidStack.getFluid().getStill();

			int color = fluidStack.getFluid().getColor(fluidStack);

			int barHeight = MathHelper.ceil(63 * fluidStack.amount / (float)props.getCapacity());

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

				this.drawTexturedBar(xx + 83, yy + 74, sprite, 10, 10, barHeight);
			} else {
				GlStateManager.disableTexture2D();
				drawRect(xx + 83, yy + 74 - barHeight, xx + 83 + 10, yy + 74, color);
			}

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.enableTexture2D();
			GlStateManager.color(1, 1, 1, 1);

			mc.renderEngine.bindTexture(TAR_BARREL_GUI_TEXTURE);

			int barForegroundHeight = barHeight + 2;

			drawTexturedModalRect(xx + 82, yy + 10 + 65 - barForegroundHeight, 206, 65 - barForegroundHeight, 12, barForegroundHeight);

			GlStateManager.disableBlend();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		super.drawScreen(mouseX, mouseY, partialTicks);

		this.renderHoveredToolTip(mouseX, mouseY);
		
		if(this.isPointInRegion(82, 12, 12, 63, mouseX, mouseY)) {
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

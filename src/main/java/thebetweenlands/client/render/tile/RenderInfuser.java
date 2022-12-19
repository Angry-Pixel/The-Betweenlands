package thebetweenlands.client.render.tile;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelInfuser;
import thebetweenlands.common.block.container.BlockInfuser;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.tile.TileEntityInfuser;
import thebetweenlands.util.StatePropertyHelper;

@SideOnly(Side.CLIENT)
public class RenderInfuser extends TileEntitySpecialRenderer<TileEntityInfuser> {
	private final ModelInfuser model = new ModelInfuser();
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/infuser.png");

	@Override
	public void render(TileEntityInfuser infuser, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		bindTexture(TEXTURE);

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		if(infuser == null || !infuser.hasWorld()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) 0.5F, (float) 1.5F, (float) 0.5F);
			GlStateManager.scale(1F, -1F, -1F);
			model.render();
			GlStateManager.popMatrix();
			return;
		}

		BlockPos pos = infuser.getPos();
		EnumFacing facing = StatePropertyHelper.getStatePropertySafely(infuser, BlockInfuser.class, BlockInfuser.FACING, EnumFacing.NORTH);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		switch (facing) {
		default:
		case NORTH:
			GlStateManager.rotate(180F, 0.0F, 1F, 0F);
			break;
		case WEST:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			break;
		case EAST:
			GlStateManager.rotate(90F, 0.0F, 1F, 0F);
			break;
		case SOUTH:
			GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
			break;
		}
		model.render();
		GlStateManager.pushMatrix();
		GlStateManager.rotate(infuser.getStirProgress() * 4, 0.0F, 1F, 0F);
		model.renderSpoon();
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();

		ElixirRecipe recipe = infuser.getInfusingRecipe();

		if(BetweenlandsConfig.DEBUG.debug) {
			String elixirName = recipe != null ? recipe.name : " N/A";
			renderStirCount("Evap: " + infuser.getEvaporation() + " Temp: "+ infuser.getTemperature() + " Time: " + infuser.getInfusionTime() + " Recipe: " + elixirName, x, y, z);
		}

		int amount = infuser.waterTank.getFluidAmount();
		int capacity = infuser.waterTank.getCapacity();
		float size = 1F / capacity * amount;

		int itemBob = infuser.getItemBob();
		int stirProgress = infuser.getStirProgress();
		float crystalRotation = infuser.getCrystalRotation();
		double itemY = y + 0.3D + size * 0.5D;
		Random rand = new Random();
		rand.setSeed((long) (pos.getX() + pos.getY() + pos.getZ()));
		for(int i = 0; i <= TileEntityInfuser.MAX_INGREDIENTS; i++) {
			float randRot = rand.nextFloat() * 360.0F;
			double xo = -0.2D + rand.nextFloat() * 0.4D;
			double zo = -0.2D + rand.nextFloat() * 0.4D;
			double rot = (stirProgress < 90 && amount >= 100 ? stirProgress * 4D + 45D : 45D) + randRot;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, 0, z + 0.5D);
			GlStateManager.rotate((float)-rot, 0, 1, 0);
			GlStateManager.translate(xo, 0, zo);
			renderItemInSlot(infuser, i, 0, itemY, 0, amount >= 100 ? (i % 2 == 0 ? (itemBob * 0.01D) : ((-itemBob + 20) * 0.01D)) : 0.0D, -rot);
			GlStateManager.popMatrix();
		}

		renderItemInSlot(infuser, TileEntityInfuser.MAX_INGREDIENTS + 1, x + 0.5, y + 1.43D, z + 0.5D, itemBob * 0.01D, crystalRotation);

		if (amount >= 100) {
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder vb = tess.getBuffer();
			TextureAtlasSprite waterSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(FluidRegistry.SWAMP_WATER.getStill().toString());
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			float tx = (float) x + 0.0F;
			float ty = (float) (y + 0.35F + size * 0.5F);
			float tz = (float) z + 0.0F;
			vb.setTranslation(tx, ty, tz);
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			float[] targetColor;
			if(infuser.getInfusionColorGradientTicks() > 0) {
				targetColor = new float[] {
						infuser.prevInfusionColor[0] + (infuser.currentInfusionColor[0] - infuser.prevInfusionColor[0]) / 30.0F * infuser.getInfusionColorGradientTicks(),
						infuser.prevInfusionColor[1] + (infuser.currentInfusionColor[1] - infuser.prevInfusionColor[1]) / 30.0F * infuser.getInfusionColorGradientTicks(),
						infuser.prevInfusionColor[2] + (infuser.currentInfusionColor[2] - infuser.prevInfusionColor[2]) / 30.0F * infuser.getInfusionColorGradientTicks(),
						infuser.prevInfusionColor[3] + (infuser.currentInfusionColor[3] - infuser.prevInfusionColor[3]) / 30.0F * infuser.getInfusionColorGradientTicks()
				};
			} else {
				targetColor = infuser.currentInfusionColor;
			}
			vb.pos(0.1875, 0, 0.1875).tex(waterSprite.getMinU(), waterSprite.getMinV()).color(targetColor[0], targetColor[1], targetColor[2], targetColor[3]).endVertex();
			vb.pos(0.1875, 0, 0.8125).tex(waterSprite.getMinU(), waterSprite.getMaxV()).color(targetColor[0], targetColor[1], targetColor[2], targetColor[3]).endVertex();
			vb.pos(0.8125, 0, 0.8125).tex(waterSprite.getMaxU(), waterSprite.getMaxV()).color(targetColor[0], targetColor[1], targetColor[2], targetColor[3]).endVertex();
			vb.pos(0.8125, 0, 0.1875).tex(waterSprite.getMaxU(), waterSprite.getMinV()).color(targetColor[0], targetColor[1], targetColor[2], targetColor[3]).endVertex();
			vb.setTranslation(0, 0, 0);
			tess.draw();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	private void renderItemInSlot(TileEntityInfuser infuser, int slotIndex, double x, double y, double z, double itemBob, double rotation) {
		if (!infuser.getStackInSlot(slotIndex).isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(0.25D, 0.25D, 0.25D);
			GlStateManager.translate(0D, itemBob, 0D);
			GlStateManager.rotate((float) rotation, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(infuser.getStackInSlot(slotIndex), TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}

	private void renderStirCount(String count, double x, double y, double z) {
		float scale = 0.02666667F;
		float height = 0.8F;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + height + 0.75F, z + 0.5F);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-scale, -scale, scale);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();

		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
		int width = fontrenderer.getStringWidth(count) / 2;
		vb.pos(-width - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vb.pos(-width - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vb.pos(width + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vb.pos(width + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		tessellator.draw();

		GlStateManager.enableTexture2D();
		fontrenderer.drawString(count, -fontrenderer.getStringWidth(count) / 2, 0, 553648127);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		fontrenderer.drawString(count, -fontrenderer.getStringWidth(count) / 2, 0, -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}

package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelAlembic;
import thebetweenlands.common.block.container.BlockAspectVial;
import thebetweenlands.common.block.terrain.BlockDentrothyst;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.tile.TileEntityAspectVial;
import thebetweenlands.util.ColorUtils;
import thebetweenlands.util.LightingUtil;
import thebetweenlands.util.TileEntityHelper;

@SideOnly(Side.CLIENT)
public class RenderAspectVial extends TileEntitySpecialRenderer<TileEntityAspectVial> {

	private static final ModelAlembic MODEL = new ModelAlembic();
	public static final ResourceLocation TEXTURE1 = new ResourceLocation("thebetweenlands:textures/tiles/vial_block_green.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation("thebetweenlands:textures/tiles/vial_block_orange.png");

	@Override
	public void render(TileEntityAspectVial te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		BlockDentrothyst.EnumDentrothyst type = TileEntityHelper.getStatePropertySafely(te, BlockAspectVial.class, BlockAspectVial.TYPE, EnumDentrothyst.GREEN);
		switch(type) {
		default:
		case GREEN:
			bindTexture(TEXTURE1);
			break;
		case ORANGE:
			bindTexture(TEXTURE2);
			break;
		}

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.scale(2, 2, 2);
		GlStateManager.translate(-0.3F, -0.75F, 0.25F);

		if(te.getAspect() != null) {
			GlStateManager.disableTexture2D();
			float[] aspectRGBA = ColorUtils.getRGBA(te.getAspect().type.getColor());
			GlStateManager.color(aspectRGBA[0] * 2, aspectRGBA[1] * 2, aspectRGBA[2] * 2, 0.98F);

			float filled = te.getAspect().amount / TileEntityAspectVial.MAX_AMOUNT;

			if(filled != 0.0F) {
				LightingUtil.INSTANCE.setLighting(255);
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, -(23.5F * 0.0625F) * filled + (23.5F * 0.0625F), 0);
				GlStateManager.scale(1, filled, 1);
				GL11.glFrontFace(GL11.GL_CW);
				MODEL.jar_liquid.render(0.0625F);
				GL11.glFrontFace(GL11.GL_CCW);
				GlStateManager.popMatrix();
				LightingUtil.INSTANCE.revert();
			}

			GlStateManager.enableTexture2D();
			GlStateManager.color(1, 1, 1, 1.0F);
		}

		GlStateManager.disableCull();
		MODEL.davids_jar.render(0.0625F);
		GlStateManager.enableCull();

		GlStateManager.popMatrix();
	}
}

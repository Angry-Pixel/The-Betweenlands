package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
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
import thebetweenlands.util.StatePropertyHelper;

@SideOnly(Side.CLIENT)
public class RenderAspectVial extends TileEntitySpecialRenderer<TileEntityAspectVial> {

	private static final ModelAlembic MODEL = new ModelAlembic();
	public static final ResourceLocation TEXTURE1 = new ResourceLocation("thebetweenlands:textures/tiles/vial_block_green.png");
	public static final ResourceLocation TEXTURE2 = new ResourceLocation("thebetweenlands:textures/tiles/vial_block_orange.png");

	@Override
	public void render(TileEntityAspectVial te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		BlockDentrothyst.EnumDentrothyst type = StatePropertyHelper.getStatePropertySafely(te, BlockAspectVial.class, BlockAspectVial.TYPE, EnumDentrothyst.GREEN);
		switch(type) {
		default:
		case GREEN:
			bindTexture(TEXTURE1);
			break;
		case ORANGE:
			bindTexture(TEXTURE2);
			break;
		}

		float randX = 0;
		float randZ = 0;
		
		if(StatePropertyHelper.getStatePropertySafely(te, BlockAspectVial.class, BlockAspectVial.RANDOM_POSITION, true)) {
			long posRand = (long)(te.getPos().getY() * 224856) ^ (te.getPos().getX() * 3129871) ^ (long)te.getPos().getZ() * 116129781L;
			posRand = posRand * posRand * 42317861L + posRand * 11L;
			randX = (((float)(posRand >> 16 & 15L) / 15.0F) - 0.5F) * 0.45F;
			randZ = (((float)(posRand >> 24 & 15L) / 15.0F) - 0.5F) * 0.45F;
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.translate(-0.3F + randX, -0.001F, 0.25F + randZ);

		GlStateManager.depthMask(false);
		MODEL.davids_jar.render(0.0625F);
		GlStateManager.depthMask(true);
		
		if(te.getAspect() != null) {
			GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
			
			GlStateManager.disableTexture2D();
			float[] aspectRGBA = ColorUtils.getRGBA(te.getAspect().type.getColor());
			GlStateManager.color(aspectRGBA[0], aspectRGBA[1], aspectRGBA[2], 0.98F);

			float filled = te.getAspect().amount / TileEntityAspectVial.MAX_AMOUNT;

			if(filled != 0.0F) {
				LightingUtil.INSTANCE.setLighting(255);
				GlStateManager.enableNormalize();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0, -(23.5F * 0.0625F) * filled + (23.5F * 0.0625F), 0);
				GlStateManager.scale(1, filled, 1);
				MODEL.jar_liquid.render(0.0625F);
				GlStateManager.popMatrix();
				GlStateManager.disableNormalize();
				LightingUtil.INSTANCE.revert();
			}

			GlStateManager.enableTexture2D();
			GlStateManager.color(1, 1, 1, 1.0F);
			
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		}

		GlStateManager.colorMask(false, false, false, false);
		MODEL.davids_jar.render(0.0625F);
		GlStateManager.colorMask(true, true, true, true);
		
		GlStateManager.cullFace(CullFace.FRONT);
		MODEL.davids_jar.render(0.0625F);
		GlStateManager.cullFace(CullFace.BACK);
		
		GlStateManager.popMatrix();
	}
}

package thebetweenlands.client.render.tile;

import java.util.Random;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.client.render.model.tile.ModelAspectrusCrop1;
import thebetweenlands.client.render.model.tile.ModelAspectrusCrop2;
import thebetweenlands.client.render.model.tile.ModelAspectrusCrop3;
import thebetweenlands.client.render.model.tile.ModelAspectrusCrop4;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.block.farming.BlockAspectrusCrop;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityAspectrusCrop;
import thebetweenlands.util.ColorUtils;
import thebetweenlands.util.LightingUtil;
import thebetweenlands.util.StatePropertyHelper;

public class RenderAspectrusCrop extends TileEntitySpecialRenderer<TileEntityAspectrusCrop> {
	protected static final ModelAspectrusCrop1 MODEL1 = new ModelAspectrusCrop1();
	protected static final ModelAspectrusCrop2 MODEL2 = new ModelAspectrusCrop2();
	protected static final ModelAspectrusCrop3 MODEL3 = new ModelAspectrusCrop3();
	protected static final ModelAspectrusCrop4 MODEL4 = new ModelAspectrusCrop4();

	protected static final ResourceLocation TEXTURE_0 = new ResourceLocation("thebetweenlands:textures/tiles/aspectrus_crop_0.png");
	protected static final ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/tiles/aspectrus_crop_1.png");
	protected static final ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/tiles/aspectrus_crop_2.png");
	protected static final ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/tiles/aspectrus_crop_3.png");
	protected static final ResourceLocation TEXTURE_4 = new ResourceLocation("thebetweenlands:textures/tiles/aspectrus_crop_4.png");
	protected static final ResourceLocation TEXTURE_5 = new ResourceLocation("thebetweenlands:textures/tiles/aspectrus_crop_5.png");
	protected static final ResourceLocation TEXTURE_6 = new ResourceLocation("thebetweenlands:textures/tiles/aspectrus_crop_6.png");

	@Override
	public void render(TileEntityAspectrusCrop tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		Random rnd = new Random();
		long seed = tile.getPos().getX() * 0x2FC20FL ^ tile.getPos().getY() * 0x6EBFFF5L ^ tile.getPos().getZ();
		rnd.setSeed(seed * seed * 0x285B825L + seed * 11L);
		int rndRot = rnd.nextInt(4);

		Aspect aspect = tile.getAspect();

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.rotate(rndRot * 90.0F, 0, 1, 0);
		GlStateManager.disableCull();

		int index = StatePropertyHelper.getStatePropertySafely(tile, BlockAspectrusCrop.class, BlockRegistry.ASPECTRUS_CROP.getStageProperty(), 0, true, false) / 2;
		if(index <= 4) {
			index = index / 2;
		} else {
			index = index - 2;
		}
		if(index >= 5 && StatePropertyHelper.getStatePropertySafely(tile, BlockAspectrusCrop.class, BlockGenericCrop.DECAYED, false, true, false)) {
			index = 6;
		}

		if(index >= 4 && ShaderHelper.INSTANCE.isWorldShaderActive() && aspect != null) {
			float[] rgba = ColorUtils.getRGBA(aspect.type.getColor());
			ShaderHelper.INSTANCE.require();
			float brightness = ((float)Math.sin((tile.glowTicks + partialTicks) / 15.0F) * (float)Math.cos((tile.glowTicks + partialTicks + 4) / 80.0F) + 1.0F) / 2.0F;
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(tile.getPos().getX() + 0.5D, tile.getPos().getY() + 0.5D, tile.getPos().getZ() + 0.5D, 
					brightness * brightness * 4.0F,
					rgba[0] * brightness * brightness * 2.5F,
					rgba[1] * brightness * brightness * 2.5F,
					rgba[2] * brightness * brightness * 2.5F));
		}

		switch(index) {
		case 0:
			this.bindTexture(TEXTURE_0);
			break;
		case 1:
			this.bindTexture(TEXTURE_1);
			break;
		case 2:
			this.bindTexture(TEXTURE_2);
			break;
		case 3:
			this.bindTexture(TEXTURE_3);
			break;
		case 4:
			this.bindTexture(TEXTURE_4);
			break;
		case 5:
			this.bindTexture(TEXTURE_5);
			break;
		case 6:
			this.bindTexture(TEXTURE_6);
			break;
		}

		switch(index) {
		case 0:
		case 1:
			MODEL1.render();
			break;
		case 2:
		case 3:
			MODEL2.render();
			break;
		case 4:
			if(aspect != null) {
				float[] rgba = ColorUtils.getRGBA(aspect.type.getColor());
				GlStateManager.color(rgba[0], rgba[1], rgba[2], 0.85F);
				LightingUtil.INSTANCE.setLighting(255);
			}
			MODEL3.renderFruitAspects();
			if(aspect != null) {
				LightingUtil.INSTANCE.revert();
			}
			GlStateManager.color(1, 1, 1, 1);
			MODEL3.renderPlant();
			break;
		case 5:
		default:
			if(aspect != null) {
				float[] rgba = ColorUtils.getRGBA(aspect.type.getColor());
				GlStateManager.color(rgba[0], rgba[1], rgba[2], 0.85F);
				LightingUtil.INSTANCE.setLighting(255);
			}
			MODEL4.renderFruitAspects();
			if(aspect != null) {
				LightingUtil.INSTANCE.revert();
			}
			GlStateManager.color(1, 1, 1, 1);
			MODEL4.renderPlant();
			break;
		case 6:
			//Decay
			MODEL4.renderPlant();
			break;
		}

		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}
}

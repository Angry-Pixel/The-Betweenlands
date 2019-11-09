package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.client.render.model.tile.ModelGeckoCage;
import thebetweenlands.client.render.model.tile.ModelGeckoCorrupted;
import thebetweenlands.client.render.model.tile.ModelGeckoGreen;
import thebetweenlands.client.render.model.tile.ModelGeckoNormal;
import thebetweenlands.common.block.container.BlockGeckoCage;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.tile.TileEntityGeckoCage;
import thebetweenlands.util.StatePropertyHelper;

public class RenderGeckoCage extends TileEntitySpecialRenderer<TileEntityGeckoCage> {
	private static final ModelGeckoCage MODEL = new ModelGeckoCage();
	private static final ModelGeckoNormal MODEL_GECKO_NORMAL = new ModelGeckoNormal();
	private static final ModelGeckoCorrupted MODEL_GECKO_CORRUPTED = new ModelGeckoCorrupted();
	private static final ModelGeckoGreen MODEL_GECKO_GREEN = new ModelGeckoGreen();

	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/gecko_cage.png");
	private static final ResourceLocation GECKO_TEXTURE_NORMAL = new ResourceLocation("thebetweenlands:textures/entity/gecko.png");
	private static final ResourceLocation GECKO_TEXTURE_CORRUPTED = new ResourceLocation("thebetweenlands:textures/entity/gecko_corrupted.png");
	private static final ResourceLocation GECKO_TEXTURE_GREEN = new ResourceLocation("thebetweenlands:textures/entity/gecko_green.png");
	private static final ResourceLocation GECKO_TEXTURE_PALE = new ResourceLocation("thebetweenlands:textures/entity/gecko_pale.png");
	private static final ResourceLocation GECKO_TEXTURE_RED = new ResourceLocation("thebetweenlands:textures/entity/gecko_red.png");
	private static final ResourceLocation GECKO_TEXTURE_TAN = new ResourceLocation("thebetweenlands:textures/entity/gecko_tan.png");

	@Override
	public void render(TileEntityGeckoCage cage, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (destroyStage < 0) {
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);

		EnumFacing facing = StatePropertyHelper.getStatePropertySafely(cage, BlockGeckoCage.class, BlockGeckoCage.FACING, EnumFacing.NORTH);

		GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1F, 0F);

		if (destroyStage >= 0) {
			this.bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 4.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			bindTexture(TEXTURE);
		}

		GlStateManager.disableCull();
		MODEL.render();
		GlStateManager.enableCull();

		if (destroyStage >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		/*GL11.glPushMatrix();
		GL11.glTranslatef(-1.0F, 0.5F, -0.5F);
		ItemRenderHelper.renderItem(new ItemStack(BLBlockRegistry.weedwoodBush), 0);
		GL11.glPopMatrix();*/

		if(cage != null && cage.hasGecko()) {
			//float animationTicks = cage.getInterpolatedTicks(partialTickTime);

			GlStateManager.translate(0, 0.3F, 0);

			//GL11.glRotated(animationTicks * 3.0F, 0, 1, 0);
			//GL11.glTranslated(0.2D, 0, 0.07D);

			GlStateManager.scale(0.6F, 0.6F, 0.6F);

			//float speed = 0.15F;
			//float swing = animationTicks / 2.0F;
			float speed = 0;
			float swing = 0;

			IAspectType aspect = cage.getAspectType();
			if(aspect == AspectRegistry.FERGALAZ) {
				bindTexture(GECKO_TEXTURE_TAN);
				MODEL_GECKO_NORMAL.render(cage.getTicks(), partialTicks, swing, speed);
			} else if(aspect == AspectRegistry.FIRNALAZ) {
				bindTexture(GECKO_TEXTURE_RED);
				MODEL_GECKO_NORMAL.render(cage.getTicks(), partialTicks, swing, speed);
			} else if(aspect == AspectRegistry.GEOLIIRGAZ) {
				bindTexture(GECKO_TEXTURE_PALE);
				MODEL_GECKO_NORMAL.render(cage.getTicks(), partialTicks, swing, speed);
			} else if(aspect == AspectRegistry.YIHINREN) {
				bindTexture(GECKO_TEXTURE_GREEN);
				MODEL_GECKO_GREEN.render(cage.getTicks(), partialTicks, swing, speed);
			} else if(aspect == AspectRegistry.BYARIIS) {
				bindTexture(GECKO_TEXTURE_CORRUPTED);
				MODEL_GECKO_CORRUPTED.render(cage.getTicks(), partialTicks);
			} else {
				bindTexture(GECKO_TEXTURE_NORMAL);
				MODEL_GECKO_NORMAL.render(cage.getTicks(), partialTicks, swing, speed);
			}
		}

		GlStateManager.popMatrix();
	}
}


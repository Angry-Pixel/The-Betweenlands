package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.EntityRootGrabber.RootPart;

@OnlyIn(Dist.CLIENT)
public class RenderRootGrabber extends Render<EntityRootGrabber> {
	protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] {new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};

	public RenderRootGrabber(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntityRootGrabber entity, double x, double y, double z, float yaw, float partialTicks) {
		renderRoots(entity, x, y, z, yaw, partialTicks);
	}

	public void renderRoots(EntityRootGrabber entity, double x, double y, double z, float yaw, float partialTicks) {
		entity.initRootModels();

		if(entity.modelParts != null) {
			GlStateManager.pushMatrix();

			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.translated(x, y + 1, z);

			for(RootPart part : entity.modelParts) {
				GlStateManager.pushMatrix();

				this.bindEntityTexture(entity);

				GlStateManager.translatef(part.x, part.y, part.z);

				GlStateManager.rotatef(part.yaw - 90, 0, 1, 0);
				GlStateManager.rotatef(part.pitch, 1, 0, 0);

				GlStateManager.translatef(0, entity.getRootYOffset(partialTicks), 0);

				float animationTicks = entity.ticksExisted + partialTicks;
				GlStateManager.rotatef((float)Math.cos(animationTicks / 4) * 0.5F, 0, 0, 1);
				GlStateManager.rotatef((float)Math.sin(animationTicks / 5) * 0.8F, 1, 0, 0);

				part.renderer.render();

				int damage = MathHelper.ceil(entity.getDamage() * 10.0F);
				if(damage > 0 && damage <= 10) {
					this.bindTexture(DESTROY_STAGES[damage - 1]);

					TextureAtlasSprite sprite = part.renderer.getSprite();

					this.preRenderDamagedBlocks();

					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.pushMatrix();
					GlStateManager.loadIdentity();
					GlStateManager.scalef(1.0F / (sprite.getMaxU() - sprite.getMinU()), 1.0F / (sprite.getMaxV() - sprite.getMinV()), 0);
					GlStateManager.translatef(-sprite.getMinU(), -sprite.getMinV(), 0);
					GlStateManager.matrixMode(GL11.GL_MODELVIEW);

					part.renderer.render();

					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.popMatrix();
					GlStateManager.matrixMode(GL11.GL_MODELVIEW);

					this.postRenderDamagedBlocks();
				}

				GlStateManager.popMatrix();
			}

			GlStateManager.enableCull();
			GlStateManager.popMatrix();
		}
	}

	private void preRenderDamagedBlocks() {
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.polygonOffset(-3.0F, -3.0F);
		GlStateManager.enablePolygonOffset();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableAlphaTest();
	}

	private void postRenderDamagedBlocks() {
		GlStateManager.disableAlphaTest();
		GlStateManager.polygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRootGrabber entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}
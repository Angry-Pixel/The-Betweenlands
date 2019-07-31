package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.EntityRootGrabber.RootPart;

@SideOnly(Side.CLIENT)
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
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.translate(x, y + 1, z);

			for(RootPart part : entity.modelParts) {
				GlStateManager.pushMatrix();

				this.bindTexture(part.texture);

				GlStateManager.translate(part.x, part.y, part.z);

				GlStateManager.rotate(part.yaw - 90, 0, 1, 0);
				GlStateManager.rotate(part.pitch, 1, 0, 0);

				GlStateManager.translate(0, entity.getRootYOffset(partialTicks), 0);

				if(!entity.isChains()) {
					float animationTicks = entity.ticksExisted + partialTicks;
					GlStateManager.rotate((float)Math.cos(animationTicks / 4) * 0.5F, 0, 0, 1);
					GlStateManager.rotate((float)Math.sin(animationTicks / 5) * 0.8F, 1, 0, 0);
				}

				part.render();

				int damage = MathHelper.ceil(entity.getDamage() * 10.0F);
				if(damage > 0 && damage <= 10) {
					this.bindTexture(DESTROY_STAGES[damage - 1]);

					this.preRenderDamagedBlocks();

					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.pushMatrix();
					GlStateManager.loadIdentity();
					GlStateManager.scale(1.0F / part.texWidth, 1.0F / part.texHeight, 0);
					GlStateManager.translate(-part.texU, -part.texV, 0);
					GlStateManager.matrixMode(GL11.GL_MODELVIEW);

					part.render();

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
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.doPolygonOffset(-3.0F, -3.0F);
		GlStateManager.enablePolygonOffset();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableAlpha();
	}

	private void postRenderDamagedBlocks() {
		GlStateManager.disableAlpha();
		GlStateManager.doPolygonOffset(0.0F, 0.0F);
		GlStateManager.disablePolygonOffset();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRootGrabber entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}
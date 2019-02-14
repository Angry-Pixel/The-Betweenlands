package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelWallLamprey;
import thebetweenlands.common.entity.mobs.EntityWallLamprey;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderWallLamprey extends RenderWallFace<EntityWallLamprey> {
	protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] {new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};

	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/blocks/mud_bricks.png");

	private final ModelWallLamprey model;

	public RenderWallLamprey(RenderManager renderManager) {
		super(renderManager, new ModelWallLamprey(), 0);

		this.model = (ModelWallLamprey) this.mainModel;

		this.addLayer(new LayerOverlay<EntityWallLamprey>(this) {
			@Override
			protected ResourceLocation getTexture(EntityWallLamprey entity, int index) {
				int damage = MathHelper.ceil((1.0F - entity.getHealth() / entity.getMaxHealth()) * 10.0F);
				if(damage > 0 && damage <= 10) {
					return DESTROY_STAGES[damage - 1];
				}
				return null;
			}

			@Override
			protected void renderOverlay(EntityWallLamprey entity, ModelBase model, float limbSwing,
					float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
					float scale) {
				this.preRenderDamagedBlocks();
				super.renderOverlay(entity, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
				this.postRenderDamagedBlocks();
			}

			private void preRenderDamagedBlocks() {
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.enableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.enableAlpha();
			}

			private void postRenderDamagedBlocks() {
				GlStateManager.disableAlpha();
				GlStateManager.enableAlpha();
				GlStateManager.depthMask(true);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
		});
	}

	@Override
	protected void preRenderCallback(EntityWallLamprey entity, float partialTickTime) {
		super.preRenderCallback(entity, partialTickTime);
		GlStateManager.translate(0, 0.55D, 0.3D);
	}

	@Override
	public void doRender(EntityWallLamprey entity, double x, double y, double z, float entityYaw, float partialTicks) {
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

		boolean useStencil = false;
		int stencilBit = MinecraftForgeClient.reserveStencilBit();
		int stencilMask = 1 << stencilBit;

		if(stencilBit >= 0) {
			useStencil = fbo.isStencilEnabled() ? true : fbo.enableStencil();
		}

		if(useStencil) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);

			//Clear our stencil bit to 0
			GL11.glStencilMask(stencilMask);
			GL11.glClearStencil(0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
			GL11.glStencilMask(~0);

			GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_REPLACE);

			GlStateManager.depthMask(false);
			GlStateManager.colorMask(false, false, false, false);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.disableTexture2D();

			//Polygon offset required so that there's no z fighting with the window and background wall
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(-5.0F, -5.0F);

			//Render window through which the hole will be visible
			this.model.frontPiece1.showModel = false;
			this.model.window.showModel = true;
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
			this.model.frontPiece1.showModel = true;
			this.model.window.showModel = false;

			GlStateManager.disablePolygonOffset();

			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.enableTexture2D();

			GlStateManager.depthMask(true);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

			GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		}

		//Render to depth only with reversed depth test such that in the next pass it can be rendered normally
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		GlStateManager.colorMask(false, false, false, false);

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);

		//Render visible pass
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if(stencilBit >= 0) {
			MinecraftForgeClient.releaseStencilBit(stencilBit);
		}
		if(useStencil) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
	}

	/*@Override
	protected void renderModel(EntityWallLamprey entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

		boolean useStencil = false;
		int stencilBit = MinecraftForgeClient.reserveStencilBit();
		int stencilMask = 1 << stencilBit;

		if(stencilBit >= 0) {
			useStencil = fbo.isStencilEnabled() ? true : fbo.enableStencil();
		}

		if(useStencil) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);

			//Clear our stencil bit to 0
			GL11.glStencilMask(stencilMask);
			GL11.glClearStencil(0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
			GL11.glStencilMask(~0);

			GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_REPLACE);

			GlStateManager.depthMask(false);
			GlStateManager.colorMask(false, false, false, false);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

			GlStateManager.disableAlpha();
			GlStateManager.disableBlend();
			GlStateManager.disableTexture2D();

			//Polygon offset required so that there's no z fighting with the window and background wall
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(-5.0F, -5.0F);

			//Render window through which the hole will be visible
			this.model.window.render(scaleFactor);

			GlStateManager.disablePolygonOffset();

			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.enableTexture2D();

			GlStateManager.depthMask(true);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

			GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		}

		//Render to depth only with reversed depth test such that in the next pass it can be rendered normally
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		GlStateManager.colorMask(false, false, false, false);

		super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);

		//Render visible pass
		super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

		if(stencilBit >= 0) {
			MinecraftForgeClient.releaseStencilBit(stencilBit);
		}
		if(useStencil) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
	}*/

	@Override
	protected ResourceLocation getEntityTexture(EntityWallLamprey entity) {
		return TEXTURE;
	}
}

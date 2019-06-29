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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelWallLampreyHole;
import thebetweenlands.common.entity.mobs.EntityWallLamprey;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.Stencil;

@SideOnly(Side.CLIENT)
public class RenderWallLamprey extends RenderWallFace<EntityWallLamprey> {
	protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] {new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};

	private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation(ModInfo.ID, "textures/entity/wall_lamprey_hole_overlay.png");

	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/blocks/mud_bricks.png");

	private final ModelWallLampreyHole modelBlockTextured;
	private final ModelWallLampreyHole modelNormal;

	public RenderWallLamprey(RenderManager renderManager) {
		super(renderManager, new ModelWallLampreyHole(true), 0);

		this.modelBlockTextured = (ModelWallLampreyHole) this.mainModel;
		this.modelNormal = new ModelWallLampreyHole(false);

		this.addLayer(new LayerOverlay<EntityWallLamprey>(this, TEXTURE_OVERLAY) {
			@Override
			protected ModelBase[] getModels(EntityWallLamprey entity) {
				return new ModelBase[] { RenderWallLamprey.this.modelNormal };
			}
		});

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

		try(Stencil stencil = Stencil.reserve(fbo)) {
			if(stencil.valid()) {
				GL11.glEnable(GL11.GL_STENCIL_TEST);

				stencil.clear(false);

				stencil.func(GL11.GL_ALWAYS, true);
				stencil.op(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_REPLACE);

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
				this.modelBlockTextured.frontPiece1.showModel = false;
				this.modelBlockTextured.window.showModel = true;
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
				this.modelBlockTextured.frontPiece1.showModel = true;
				this.modelBlockTextured.window.showModel = false;

				GlStateManager.disablePolygonOffset();

				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.enableTexture2D();

				GlStateManager.depthMask(true);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

				stencil.func(GL11.GL_EQUAL, true);
				stencil.op(GL11.GL_KEEP);
			}

			//Render to depth only with reversed depth test such that in the next pass it can be rendered normally
			GlStateManager.depthFunc(GL11.GL_GEQUAL);
			GlStateManager.colorMask(false, false, false, false);

			super.doRender(entity, x, y, z, entityYaw, partialTicks);

			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.depthFunc(GL11.GL_LEQUAL);

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
		
		//Render visible pass
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWallLamprey entity) {
		return TEXTURE;
	}
}

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
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelWallLamprey;
import thebetweenlands.client.render.model.entity.ModelWallLampreyHole;
import thebetweenlands.common.entity.mobs.EntityWallLamprey;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.Stencil;

@SideOnly(Side.CLIENT)
public class RenderWallLamprey extends RenderWallFace<EntityWallLamprey> {
	protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] {new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};

	private static final ResourceLocation WALL_TEXTURE_OVERLAY = new ResourceLocation(ModInfo.ID, "textures/entity/wall_lamprey_hole_overlay.png");

	private static final ResourceLocation WALL_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/blocks/mud_bricks.png");
	private static final ResourceLocation MODEL_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/wall_lamprey.png");

	private final ModelWallLampreyHole modelBlockTextured;
	private final ModelWallLampreyHole modelNormal;
	private final ModelWallLamprey model;

	private boolean renderWall = false;
	private int renderPass = 0;

	private float partialTicks;

	public RenderWallLamprey(RenderManager renderManager) {
		super(renderManager, new ModelWallLamprey(), 0);

		this.model = (ModelWallLamprey) this.mainModel;
		this.modelBlockTextured = new ModelWallLampreyHole(true);
		this.modelNormal = new ModelWallLampreyHole(false);

		this.addLayer(new LayerOverlay<EntityWallLamprey>(this, WALL_TEXTURE_OVERLAY) {
			@Override
			protected ModelBase[] getModels(EntityWallLamprey entity) {
				return new ModelBase[] { RenderWallLamprey.this.modelNormal };
			}
		});

		this.addLayer(new LayerOverlay<EntityWallLamprey>(this) {
			@Override
			protected ModelBase[] getModels(EntityWallLamprey entity) {
				return new ModelBase[] { RenderWallLamprey.this.modelNormal };
			}

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

				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				GlStateManager.scale(4f, 4f, 0);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			}

			private void postRenderDamagedBlocks() {
				GlStateManager.disableAlpha();
				GlStateManager.enableAlpha();
				GlStateManager.depthMask(true);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
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
		this.partialTicks = partialTicks;

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
				this.modelBlockTextured.setWindowZOffsetPercent(-0.001F);
				this.renderPass = 0;
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

			this.renderPass = 1;
			super.doRender(entity, x, y, z, entityYaw, partialTicks);

			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.depthFunc(GL11.GL_LEQUAL);

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		//Render visible pass
		this.renderPass = 2;

		this.modelBlockTextured.window.showModel = true;
		this.modelBlockTextured.setWindowZOffsetPercent(entity.getHoleDepthPercent(partialTicks));

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		this.modelBlockTextured.window.showModel = false;
	}

	@Override
	protected void renderModel(EntityWallLamprey entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		this.mainModel = this.modelBlockTextured;
		this.renderWall = true;

		super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

		this.mainModel = this.model;
		this.renderWall = false;

		if(this.renderPass == 2 && entity.getLampreyHiddenPercent(this.partialTicks) < 0.99F) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, -0.55D, -0.3D);
			this.applyDeathAndEasterEggRotations(entity);
			super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
			GlStateManager.popMatrix();
		}
	}

	protected void applyDeathAndEasterEggRotations(EntityWallLamprey entity) {
		float yOffset = 1.1F;

		GlStateManager.translate(0, yOffset, 0);

		if (entity.deathTime > 0) {
			float deathPercent = ((float)entity.deathTime + this.partialTicks - 1.0F) / 20.0F * 1.6F;
			deathPercent = MathHelper.sqrt(deathPercent);

			if (deathPercent > 1.0F) {
				deathPercent = 1.0F;
			}

			GlStateManager.rotate(deathPercent * this.getDeathMaxRotation(entity) * (1 - entity.getLampreyHiddenPercent(this.partialTicks)), 0.0F, 0.0F, 1.0F);
		} else {
			String s = TextFormatting.getTextWithoutFormattingCodes(entity.getName());

			if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s))) {
				GlStateManager.translate(0.0F, entity.height + 0.1F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			}
		}

		GlStateManager.translate(0, -yOffset, 0);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWallLamprey entity) {
		return this.renderWall ? WALL_TEXTURE : MODEL_TEXTURE;
	}
}

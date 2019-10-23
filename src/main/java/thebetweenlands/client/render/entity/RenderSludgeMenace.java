package thebetweenlands.client.render.entity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.tile.RenderDecayPitControl;
import thebetweenlands.common.entity.mobs.EntityMultipartDummy;
import thebetweenlands.common.entity.mobs.EntitySludgeMenace;
import thebetweenlands.common.entity.mobs.EntitySludgeMenace.Bulge;
import thebetweenlands.common.entity.mobs.EntityWallLivingRoot;
import thebetweenlands.util.RenderUtils;

@SideOnly(Side.CLIENT)
public class RenderSludgeMenace extends RenderWallLivingRoot implements IMultipartDummyRendererDelegate<EntitySludgeMenace> {
	public static final ResourceLocation HULL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/sludge_menace_hull.png");

	public static final ResourceLocation DECAY_HOLE_TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_hole_1.png");
	public static final ResourceLocation DECAY_HOLE_TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_hole_2.png");
	public static final ResourceLocation DECAY_HOLE_TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_hole_3.png");

	public RenderSludgeMenace(RenderManager renderManager) {
		super(renderManager, HULL_TEXTURE);
	}

	@Override
	public void doRender(EntityWallLivingRoot entity, double x, double y, double z, float entityYaw, float partialTicks) {
		EntitySludgeMenace sludge = (EntitySludgeMenace) entity;

		int frameCounter = RenderUtils.getFrameCounter();

		if(sludge.renderedFrame != frameCounter) {
			sludge.renderedFrame = frameCounter;

			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	@Override
	protected void renderEntityModel(EntityWallLivingRoot entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, float partialTicks) {
		super.renderEntityModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, partialTicks);

		if(this.mainModel == BLANK_MODEL) {
			//Unset hurt color
			this.unsetBrightness();

			float ringRotation = entity.ticksExisted + partialTicks;

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 1.0F, 0);
			GlStateManager.rotate(-90, 1, 0, 0);

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

			GlStateManager.color(1, 1, 1, entity.getArmSize(partialTicks));

			GlStateManager.depthMask(false);

			bindTexture(DECAY_HOLE_TEXTURE_1);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for (int part = 0; part < 24; part++) {
				RenderDecayPitControl.buildRingQuads(buffer, 0, -0.8f + 1F + 0.003F, 0, 15F * part, 2.25D, 2.25D, 0D, 0D, false);
			}
			tessellator.draw();

			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.5, 0.5, 0);
			GlStateManager.rotate(ringRotation, 0, 0, 1);
			GlStateManager.translate(-0.5, -0.5, 0);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			bindTexture(DECAY_HOLE_TEXTURE_2);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for (int part = 0; part < 24; part++) {
				RenderDecayPitControl.buildRingQuads(buffer, 0, -0.8f + 1.5f + 0.003F, 0, 15F * part, 2D, 2D, 0D, 0D, false);
			}
			tessellator.draw();

			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.5, 0.5, 0);
			GlStateManager.rotate(ringRotation * 2F, 0, 0, 1);
			GlStateManager.translate(-0.5, -0.5, 0);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			bindTexture(DECAY_HOLE_TEXTURE_3);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for (int part = 0; part < 24; part++) {
				RenderDecayPitControl.buildRingQuads(buffer, 0, -0.8f + 1.75F + 0.003F, 0, 15F * part, 2.25D, 2.25D, 0D, 0D, false);
			}
			tessellator.draw();

			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);

			GlStateManager.depthMask(true);

			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.disableBlend();

			GlStateManager.popMatrix();

			//Restore hurt color
			this.setDoRenderBrightness(entity, partialTicks);
		}
	}

	@Override
	protected float getUvScale(EntityWallLivingRoot entity, float partialTicks) {
		return 1;
	}

	@Override
	protected float calculateHullContraction(EntityWallLivingRoot entity, int i, float armSize, float partialTicks) {
		List<Bulge> bulges = ((EntitySludgeMenace) entity).getBulges(partialTicks);

		float bulgeSize = 0.0f;

		if(bulges != null) {
			for(Bulge bulge : bulges) {
				float bulgePos = bulge.renderPosition * entity.armSegments.size();
				bulgeSize += (float) Math.cos(Math.min(Math.abs(bulgePos - i) / bulge.type.length, Math.PI / 2)) * bulge.renderSize;
			}
		}

		float baseSize = (1 - i / (float)(entity.armSegments.size() - 1) + 0.2f) * armSize;
		float animation = ((float)Math.sin(-(entity.ticksExisted + partialTicks) * 0.2f + i) + 0.5f) * 0.5f * 0.2f + 0.8f;

		return Math.max(Math.min(bulgeSize, 1.2f), baseSize * animation);
	}

	@Override
	protected void renderBodyHull(EntityWallLivingRoot entity, float partialTicks) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);

		//GlStateManager.enableCull();
		//GlStateManager.cullFace(CullFace.FRONT); //oops hull seems to be inside-out

		super.renderBodyHull(entity, partialTicks);

		//GlStateManager.disableCull();
		//GlStateManager.cullFace(CullFace.BACK);

		GlStateManager.disableBlend();
	}

	@Override
	public void setRenderFromMultipart(EntityMultipartDummy dummy) {

	}
}

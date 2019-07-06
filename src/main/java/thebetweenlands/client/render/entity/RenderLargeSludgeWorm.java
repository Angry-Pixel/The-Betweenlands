package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelLargeSludgeWorm;
import thebetweenlands.client.render.model.entity.ModelTinyWormEggSac;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderLargeSludgeWorm extends RenderLiving<EntityLargeSludgeWorm> {
	public static final ResourceLocation MODEL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/large_sludge_worm.png");
	public static final ResourceLocation HULL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/large_sludge_worm_hull.png");
	public static final ResourceLocation EGG_SAC_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/worm_egg_sac.png");

	protected static final float HULL_OUTER_WIDTH = 0.58F;
	protected static final float HULL_INNER_WIDTH = 0.44F;
	protected static final float[][] HULL_CROSS_SECTION = new float[][] {
		{-HULL_OUTER_WIDTH, HULL_INNER_WIDTH},
		{-HULL_OUTER_WIDTH, -HULL_INNER_WIDTH},
		{-HULL_INNER_WIDTH, -HULL_INNER_WIDTH},
		{-HULL_INNER_WIDTH, -HULL_OUTER_WIDTH},
		{HULL_INNER_WIDTH, -HULL_OUTER_WIDTH},
		{HULL_INNER_WIDTH, -HULL_INNER_WIDTH},
		{HULL_OUTER_WIDTH, -HULL_INNER_WIDTH},
		{HULL_OUTER_WIDTH, HULL_INNER_WIDTH},
		{HULL_INNER_WIDTH, HULL_INNER_WIDTH},
		{HULL_INNER_WIDTH, HULL_OUTER_WIDTH},
		{-HULL_INNER_WIDTH, HULL_OUTER_WIDTH},
		{-HULL_INNER_WIDTH, HULL_INNER_WIDTH},
	};

	private final ModelLargeSludgeWorm model;
	private final ModelTinyWormEggSac modelEggSac = new ModelTinyWormEggSac();

	public RenderLargeSludgeWorm(RenderManager manager) {
		super(manager, new ModelLargeSludgeWorm(), 0.0F);
		this.model = (ModelLargeSludgeWorm) this.mainModel;
	}

	@Override
	public void doRender(EntityLargeSludgeWorm entity, double x, double y, double z, float yaw, float partialTicks) {
		if(!entity.segmentsAvailable) {
			return;
		}

		boolean isVisible = this.isVisible(entity);
		boolean isTranslucentToPlayer = !isVisible && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);

		if(!isVisible && !isTranslucentToPlayer) {
			return;
		}

		boolean useBrightness = this.setDoRenderBrightness(entity, partialTicks);

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1, 1);

		if(isTranslucentToPlayer) {
			GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.55D, z);

		if(!this.renderOutlines) {
			GlStateManager.enableCull();
			GlStateManager.cullFace(CullFace.FRONT);
			GlStateManager.depthMask(false);

			this.renderPass(entity, false, partialTicks);
		}

		GlStateManager.disableCull();
		GlStateManager.cullFace(CullFace.BACK);
		GlStateManager.depthMask(true);

		boolean useTeamColors = false;

		if(this.renderOutlines) {
			useTeamColors = this.setScoreTeamColor(entity);
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		this.renderPass(entity, true, partialTicks);

		if(this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		if(useTeamColors) {
			this.unsetScoreTeamColor();
		}

		GlStateManager.enableCull();
		if(!this.renderOutlines) {
			this.renderPass(entity, false, partialTicks);
		}

		GlStateManager.popMatrix();

		if(isTranslucentToPlayer) {
			GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
		}
		GlStateManager.disableBlend();

		if(useBrightness) {
			this.unsetBrightness();
		}

		if(!this.renderOutlines) {
			this.renderLeash(entity, x, y, z, yaw, partialTicks);
		}
	}

	protected void renderPass(EntityLargeSludgeWorm entity, boolean renderSolids, float partialTicks) {
		this.renderHead(entity, renderSolids, partialTicks);

		this.renderTail(entity, renderSolids, partialTicks);

		if(!renderSolids) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.055D, 0);

			this.renderBodyHull(entity, partialTicks);

			GlStateManager.popMatrix();
		}

		if(renderSolids) {
			this.renderSpine(entity, partialTicks);

			this.renderEggSac(entity, partialTicks);
		}
	}

	protected void renderEggSac(EntityLargeSludgeWorm entity, float partialTicks) {
		if(entity.eggSacPosition != null && entity.prevEggSacPosition != null) {
			this.bindTexture(EGG_SAC_TEXTURE);

			Vec3d pos = lerp(entity.prevEggSacPosition, entity.eggSacPosition, partialTicks);

			GlStateManager.pushMatrix();
			GlStateManager.translate(pos.x, pos.y - 0.4D + Math.sin((entity.ticksExisted + partialTicks) * 0.25D) * 0.025D, pos.z);
			GlStateManager.scale(-1, -1, 1);

			float scale = Math.max(0, Math.min(1, entity.getEggSacPercentage()));

			GlStateManager.scale(scale, scale, scale);

			GlStateManager.translate(0, -1.5D, 0);

			this.modelEggSac.render(0.0625F);

			GlStateManager.popMatrix();
		}
	}

	protected void renderBodyHull(EntityLargeSludgeWorm entity, float partialTicks) {
		this.bindTexture(HULL_TEXTURE);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		Vec3d worldUp = new Vec3d(0, 1, 0);

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

		float uOffset = 0;

		for(int i = 0; i < entity.prevSegmentPositions.length - 1; i++) {
			Vec3d pos1 = lerp(entity.prevSegmentPositions[i], entity.segmentPositions[i], partialTicks);
			Vec3d dir1 = lerp(entity.prevSegmentDirs[i], entity.segmentDirs[i], partialTicks);

			Vec3d pos2 = lerp(entity.prevSegmentPositions[i + 1], entity.segmentPositions[i + 1], partialTicks);
			Vec3d dir2 = lerp(entity.prevSegmentDirs[i + 1], entity.segmentDirs[i + 1], partialTicks);

			Vec3d right1 = dir1.crossProduct(worldUp).normalize();
			Vec3d up1 = right1.crossProduct(dir1).normalize();

			Vec3d right2 = dir2.crossProduct(worldUp).normalize();
			Vec3d up2 = right2.crossProduct(dir2).normalize();

			Vec3d[] s1 = new Vec3d[HULL_CROSS_SECTION.length];
			Vec3d[] s2 = new Vec3d[HULL_CROSS_SECTION.length];

			float contraction1 = this.calculateHullContraction(entity, i / (float)(entity.prevSegmentPositions.length - 2), partialTicks);
			float contraction2 = this.calculateHullContraction(entity, (i + 1) / (float)(entity.prevSegmentPositions.length - 2), partialTicks);

			for(int j = 0; j < HULL_CROSS_SECTION.length; j++) {
				s1[j] = getModelVertex(pos1, right1, up1, HULL_CROSS_SECTION[j][0] * contraction1, HULL_CROSS_SECTION[j][1] * contraction1);
				s2[j] = getModelVertex(pos2, right2, up2, HULL_CROSS_SECTION[j][0] * contraction2, HULL_CROSS_SECTION[j][1] * contraction2);
			}

			float maxUW = 0;

			for(int j = 0; j < HULL_CROSS_SECTION.length; j++) {
				Vec3d v11 = s1[j];
				Vec3d v12 = s1[(j + 1) % HULL_CROSS_SECTION.length];
				Vec3d v21 = s2[j];
				Vec3d v22 = s2[(j + 1) % HULL_CROSS_SECTION.length];

				float uw1 = (float)v12.subtract(v11).length() * 0.5F;
				float vw1 = (float)v21.subtract(v11).length();

				float uw2 = (float)v22.subtract(v21).length() * 0.5F;
				float vw2 = (float)v22.subtract(v12).length();

				float uw = Math.max(uw1, uw2);
				float vw = Math.max(vw1, vw2);

				Vec3d normal = v21.subtract(v12).crossProduct(v22.subtract(v11)).normalize();

				float us = uOffset;
				float vs = 0;

				bufferBuilder.pos(v11.x, v11.y, v11.z).tex(us, vs).normal((float)normal.x, (float)normal.y, (float)normal.z).endVertex();
				bufferBuilder.pos(v21.x, v21.y, v21.z).tex(us, vs + vw).normal((float)normal.x, (float)normal.y, (float)normal.z).endVertex();
				bufferBuilder.pos(v22.x, v22.y, v22.z).tex(us + uw, vs + vw).normal((float)normal.x, (float)normal.y, (float)normal.z).endVertex();
				bufferBuilder.pos(v12.x, v12.y, v12.z).tex(us + uw, vs).normal((float)normal.x, (float)normal.y, (float)normal.z).endVertex();

				maxUW = Math.max(maxUW, uw);
			}

			uOffset += maxUW;
		}

		tessellator.draw();
	}

	protected float calculateHullContraction(EntityLargeSludgeWorm entity, float percent, float partialTicks) {
		double arcLength = entity.spineySpliney.getArcLength();
		float minBound = 1.8F / (float)arcLength;
		float maxBound = 1.0F - minBound;
		float lerp = 1;
		if(percent < minBound) {
			lerp = percent / minBound;
		} else if(percent > maxBound) {
			lerp = 1 - (percent - maxBound) / minBound;
		}
		float contraction = ((float)Math.sin(percent * entity.spineySpliney.getArcLength() * 4 - (entity.ticksExisted + partialTicks) * 0.25F) + 1.0F) / 2.0F * 0.2F + 0.8F;
		return 1 + (contraction - 1) * lerp;
	}

	protected void renderSpine(EntityLargeSludgeWorm entity, float partialTicks) {
		this.bindTexture(MODEL_TEXTURE);

		for(int i = 0; i < Math.min(entity.spinePositions.size(), entity.prevSpinePositions.size()); i++) {
			Vec3d bonePos = lerp(entity.prevSpinePositions.get(i), entity.spinePositions.get(i), partialTicks);
			Vec3d boneDir = lerp(entity.prevSpineDirs.get(i), entity.spineDirs.get(i), partialTicks);

			float boneYaw = -(float)Math.toDegrees(Math.atan2(boneDir.z, boneDir.x)) + 90;

			GlStateManager.pushMatrix();
			GlStateManager.translate(bonePos.x, bonePos.y + Math.sin(-(entity.ticksExisted + partialTicks) * 0.25F + i * 0.2F) * 0.05F, bonePos.z);
			this.model.renderSpinePiece(i % 6, boneYaw);
			GlStateManager.popMatrix();
		}
	}

	protected void renderHead(EntityLargeSludgeWorm entity, boolean renderSolids, float partialTicks) {
		this.bindTexture(MODEL_TEXTURE);

		GlStateManager.pushMatrix();

		GlStateManager.scale(-1, -1, 1);

		Vec3d headDir = lerp(entity.prevSegmentDirs[0], entity.segmentDirs[0], partialTicks);

		float headYaw = (float)Math.toDegrees(Math.atan2(headDir.z, headDir.x)) - 90;

		GlStateManager.rotate(headYaw, 0, 1, 0);

		GlStateManager.translate(0, -1, 0);

		this.model.renderHead(entity, 0, 0, partialTicks, renderSolids);

		GlStateManager.popMatrix();
	}

	protected void renderTail(EntityLargeSludgeWorm entity, boolean renderSolids, float partialTicks) {
		this.bindTexture(MODEL_TEXTURE);

		GlStateManager.pushMatrix();

		Vec3d tailPos = lerp(entity.prevSegmentPositions[entity.segmentDirs.length - 1], entity.segmentPositions[entity.segmentDirs.length - 1], partialTicks);
		Vec3d tailDir = lerp(entity.prevSegmentDirs[entity.segmentDirs.length - 1], entity.segmentDirs[entity.segmentDirs.length - 1], partialTicks);

		GlStateManager.translate(tailPos.x, tailPos.y, tailPos.z);

		GlStateManager.scale(-1, -1, 1);

		float tailYaw = (float)Math.toDegrees(Math.atan2(tailDir.z, tailDir.x)) - 90;

		GlStateManager.rotate(tailYaw, 0, 1, 0);

		GlStateManager.translate(0, -1, -1.8D);

		this.model.renderTail(entity, 0, 0, partialTicks, renderSolids);

		GlStateManager.popMatrix();
	}

	protected static Vec3d getModelVertex(Vec3d pos, Vec3d rightVec, Vec3d upVec, float right, float up) {
		return new Vec3d(pos.x + rightVec.x * right + upVec.x * up, pos.y + rightVec.y * right + upVec.y * up, pos.z + rightVec.z * right + upVec.z * up);
	}

	protected static Vec3d lerp(Vec3d start, Vec3d end, float delta) {
		return new Vec3d(start.x + (end.x - start.x) * delta, start.y + (end.y - start.y) * delta, start.z + (end.z - start.z) * delta);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLargeSludgeWorm entity) {
		return MODEL_TEXTURE;
	}
}
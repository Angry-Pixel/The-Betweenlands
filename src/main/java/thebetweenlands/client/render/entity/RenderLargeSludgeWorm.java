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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelLargeSludgeWorm;
import thebetweenlands.client.render.model.entity.ModelTinyWormEggSac;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm.HullSegment;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm.SpineBone;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderLargeSludgeWorm extends RenderLiving<EntityLargeSludgeWorm> {
	public static final ResourceLocation MODEL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/large_sludge_worm.png");
	public static final ResourceLocation HULL_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/large_sludge_worm_hull.png");
	public static final ResourceLocation EGG_SAC_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/worm_egg_sac.png");

	private final ModelLargeSludgeWorm model;
	private final ModelTinyWormEggSac modelEggSac = new ModelTinyWormEggSac();

	public RenderLargeSludgeWorm(RenderManager manager) {
		super(manager, new ModelLargeSludgeWorm(), 0.0F);
		this.model = (ModelLargeSludgeWorm) this.mainModel;
	}

	@Override
	public void doRender(EntityLargeSludgeWorm entity, double x, double y, double z, float yaw, float partialTicks) {
		this.renderPass(entity, x, y, z, yaw, partialTicks, false);
	}

	@Override
	public void renderMultipass(EntityLargeSludgeWorm entity, double x, double y, double z, float yaw, float partialTicks) {
		this.renderPass(entity, x, y, z, yaw, partialTicks, true);
	}

	protected void renderPass(EntityLargeSludgeWorm entity, double x, double y, double z, float yaw, float partialTicks, boolean isMultiPass) {
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

		if(isMultiPass && !this.renderOutlines) {
			GlStateManager.enableCull();
			GlStateManager.cullFace(CullFace.FRONT);
			GlStateManager.depthMask(false);

			this.renderParts(entity, false, partialTicks);
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

		if(!isMultiPass) {
			this.renderParts(entity, true, partialTicks);
		}

		if(this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		if(useTeamColors) {
			this.unsetScoreTeamColor();
		}

		GlStateManager.enableCull();
		if(isMultiPass && !this.renderOutlines) {
			this.renderParts(entity, false, partialTicks);
		}

		GlStateManager.popMatrix();

		if(isTranslucentToPlayer) {
			GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
		}
		GlStateManager.disableBlend();

		if(useBrightness) {
			this.unsetBrightness();
		}

		if(!isMultiPass && !this.renderOutlines) {
			this.renderLeash(entity, x, y, z, yaw, partialTicks);
		}
	}

	protected void renderParts(EntityLargeSludgeWorm entity, boolean renderSolids, float partialTicks) {
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

			Vec3d pos = entity.eggSacPosition;
			Vec3d prevPos = entity.prevEggSacPosition;

			double x = lerp(prevPos.x, pos.x, partialTicks);
			double y = lerp(prevPos.y, pos.y, partialTicks);
			double z = lerp(prevPos.z, pos.z, partialTicks);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y - 0.4D + Math.sin((entity.ticksExisted + partialTicks) * 0.25D) * 0.025D, z);
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

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

		float uOffset = 0;

		int i = 0;

		double pos1X = 0, pos1Y = 0, pos1Z = 0;
		HullSegment segment1 = null;
		for(HullSegment segment2 : entity.segments) {
			Vec3d pos = segment2.pos;
			Vec3d prevPos = segment2.prevPos;

			double pos2X = lerp(prevPos.x, pos.x, partialTicks);
			double pos2Y = lerp(prevPos.y, pos.y, partialTicks);
			double pos2Z = lerp(prevPos.z, pos.z, partialTicks);

			if(segment1 != null) {
				float maxUW = 0;

				int hullVerts = Math.min(segment1.offsetX.length, segment2.offsetX.length);

				for(int vertIndex = 0; vertIndex < hullVerts; vertIndex++) {
					int nextVertIndex = (vertIndex + 1) % hullVerts;

					float contraction1 = this.calculateHullContraction(entity, (i - 1) / (float)(entity.segments.length - 1), partialTicks);
					float contraction2 = this.calculateHullContraction(entity, i / (float)(entity.segments.length - 1), partialTicks);

					double v11x = pos1X + segment1.offsetX[vertIndex] * contraction1;
					double v11y = pos1Y + segment1.offsetY[vertIndex] * contraction1;
					double v11z = pos1Z + segment1.offsetZ[vertIndex] * contraction1;

					double v12x = pos1X + segment1.offsetX[nextVertIndex] * contraction1;
					double v12y = pos1Y + segment1.offsetY[nextVertIndex] * contraction1;
					double v12z = pos1Z + segment1.offsetZ[nextVertIndex] * contraction1;

					double v21x = pos2X + segment2.offsetX[vertIndex] * contraction2;
					double v21y = pos2Y + segment2.offsetY[vertIndex] * contraction2;
					double v21z = pos2Z + segment2.offsetZ[vertIndex] * contraction2;

					double v22x = pos2X + segment2.offsetX[nextVertIndex] * contraction2;
					double v22y = pos2Y + segment2.offsetY[nextVertIndex] * contraction2;
					double v22z = pos2Z + segment2.offsetZ[nextVertIndex] * contraction2;

					float uw1 = dist(v12x, v12y, v12z, v11x, v11y, v11z) * 0.5F;
					float vw1 = dist(v21x, v21y, v21z, v11x, v11y, v11z);

					float uw2 = dist(v22x, v22y, v22z, v21x, v21y, v21z) * 0.5F;
					float vw2 = dist(v22x, v22y, v22z, v12x, v12y, v12z);

					float uw = Math.max(uw1, uw2);
					float vw = Math.max(vw1, vw2);

					float d1x = (float) (v21x - v12x);
					float d1y = (float) (v21y - v12y);
					float d1z = (float) (v21z - v12z);

					float d2x = (float) (v22x - v11x);
					float d2y = (float) (v22y - v11y);
					float d2z = (float) (v22z - v11z);

					float nx = d1y * d2z - d1z * d2y;
					float ny = d1z * d2x - d1x * d2z;
					float nz = d1x * d2y - d1y * d2x;

					float len = len(nx, ny, nz);

					nx /= len;
					ny /= len;
					nz /= len;

					float us = uOffset;
					float vs = 0;

					bufferBuilder.pos(v11x, v11y, v11z).tex(us, vs).normal(nx, ny, nz).endVertex();
					bufferBuilder.pos(v21x, v21y, v21z).tex(us, vs + vw).normal(nx, ny, nz).endVertex();
					bufferBuilder.pos(v22x, v22y, v22z).tex(us + uw, vs + vw).normal(nx, ny, nz).endVertex();
					bufferBuilder.pos(v12x, v12y, v12z).tex(us + uw, vs).normal(nx, ny, nz).endVertex();

					maxUW = Math.max(maxUW, uw);
				}

				uOffset += maxUW;
			}

			segment1 = segment2;
			pos1X = pos2X;
			pos1Y = pos2Y;
			pos1Z = pos2Z;

			i++;
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
		return 0.99F + (contraction - 0.99F) * lerp;
	}

	protected void renderSpine(EntityLargeSludgeWorm entity, float partialTicks) {
		this.bindTexture(MODEL_TEXTURE);

		int i = 0;
		for(SpineBone bone : entity.bones) {
			Vec3d pos = bone.pos;
			Vec3d prevPos = bone.prevPos;

			double x = lerp(prevPos.x, pos.x, partialTicks);
			double y = lerp(prevPos.y, pos.y, partialTicks);
			double z = lerp(prevPos.z, pos.z, partialTicks);

			float boneYaw = lerp(bone.prevYaw, bone.yaw, partialTicks);

			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + Math.sin(-(entity.ticksExisted + partialTicks) * 0.25F + i * 0.2F) * 0.05F, z);
			this.model.renderSpinePiece(i % 6, boneYaw);
			GlStateManager.popMatrix();

			i++;
		}
	}

	protected void renderHead(EntityLargeSludgeWorm entity, boolean renderSolids, float partialTicks) {
		this.bindTexture(MODEL_TEXTURE);

		GlStateManager.pushMatrix();

		HullSegment headSegment = entity.segments[0];

		float headYaw = lerp(headSegment.prevYaw, headSegment.yaw, partialTicks);

		GlStateManager.scale(-1, -1, 1);

		GlStateManager.rotate(headYaw, 0, 1, 0);

		GlStateManager.translate(0, -1, 0);

		this.model.renderHead(entity, 0, 0, partialTicks, renderSolids);

		GlStateManager.popMatrix();
	}

	protected void renderTail(EntityLargeSludgeWorm entity, boolean renderSolids, float partialTicks) {
		this.bindTexture(MODEL_TEXTURE);

		GlStateManager.pushMatrix();

		HullSegment tailSegment = entity.segments[entity.segments.length - 1];

		Vec3d pos = tailSegment.pos;
		Vec3d prevPos = tailSegment.prevPos;

		double x = lerp(prevPos.x, pos.x, partialTicks);
		double y = lerp(prevPos.y, pos.y, partialTicks);
		double z = lerp(prevPos.z, pos.z, partialTicks);

		float tailYaw = lerp(tailSegment.prevYaw, tailSegment.yaw, partialTicks);

		GlStateManager.translate(x, y, z);

		GlStateManager.scale(-1, -1, 1);

		GlStateManager.rotate(tailYaw, 0, 1, 0);

		GlStateManager.translate(0, -1, -1.8D);

		this.model.renderTail(entity, 0, 0, partialTicks, renderSolids);

		GlStateManager.popMatrix();
	}

	protected static float dist(double x1, double y1, double z1, double x2, double y2, double z2) {
		return len(x2 - x1, y2 - y1, z2 - z1);
	}

	protected static float len(double x, double y, double z) {
		return MathHelper.sqrt(x * x + y * y + z * z);
	}

	protected static float lerp(float start, float end, float delta) {
		return start + (end - start) * delta;
	}

	protected static double lerp(double start, double end, float delta) {
		return start + (end - start) * delta;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLargeSludgeWorm entity) {
		return MODEL_TEXTURE;
	}

	@Override
	public boolean isMultipass() {
		return true;
	}
}
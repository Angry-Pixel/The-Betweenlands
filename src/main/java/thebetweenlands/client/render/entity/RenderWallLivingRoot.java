package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelBlank;
import thebetweenlands.common.entity.mobs.EntityWallLivingRoot;
import thebetweenlands.common.entity.mobs.EntityWallLivingRoot.ArmSegment;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderWallLivingRoot extends RenderWallHole<EntityWallLivingRoot> {
	private static final ResourceLocation ROOT_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/blocks/root_middle.png");

	private static final ModelBase BLANK_MODEL = new ModelBlank();

	public RenderWallLivingRoot(RenderManager renderManager) {
		super(renderManager, BLANK_MODEL, ROOT_TEXTURE);
	}

	@Override
	protected void renderEntityModel(EntityWallLivingRoot entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, float partialTicks) {
		if(this.mainModel == BLANK_MODEL) {
			this.renderRootModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, partialTicks);
		} else {
			super.renderEntityModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, partialTicks);
		}
	}

	protected void renderRootModel(EntityWallLivingRoot entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, float partialTicks) {
		this.bindEntityTexture(entity);

		GlStateManager.pushMatrix();
		GlStateManager.translate(0.0F, 1.501F, 0.0F);	
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);

		GlStateManager.color(1, 1, 1, 1);

		this.renderBodyHull(entity, partialTicks);

		GlStateManager.popMatrix();
	}

	protected void renderBodyHull(EntityWallLivingRoot entity, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);

		float uOffset = 0;

		int i = 0;

		float uvScale = 2.0f;

		double pos1X = 0, pos1Y = 0, pos1Z = 0;
		ArmSegment segment1 = null;
		for(ArmSegment segment2 : entity.armSegments) {
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

					float contraction1 = 1 - (i - 1) / (float)(entity.armSegments.size() - 1);
					float contraction2 = 1 - (i) / (float)(entity.armSegments.size() - 1);

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

					float uw1 = dist(v12x, v12y, v12z, v11x, v11y, v11z) * 0.5F * uvScale;
					float vw1 = dist(v21x, v21y, v21z, v11x, v11y, v11z) * uvScale;

					float uw2 = dist(v22x, v22y, v22z, v21x, v21y, v21z) * 0.5F * uvScale;
					float vw2 = dist(v22x, v22y, v22z, v12x, v12y, v12z) * uvScale;

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
	protected TextureAtlasSprite getWallSprite(EntityWallLivingRoot entity) {
		return entity.getWallSprite();
	}

	@Override
	protected float getHoleDepthPercent(EntityWallLivingRoot entity, float partialTicks) {
		return entity.getHoleDepthPercent(partialTicks);
	}

	@Override
	protected float getMainModelVisibilityPercent(EntityWallLivingRoot entity, float partialTicks) {
		return 1.0F;
	}
}

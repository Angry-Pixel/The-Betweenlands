package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelLargeSludgeWorm;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;
import thebetweenlands.util.CatmullRomSpline;
import thebetweenlands.util.ISpline;

@SideOnly(Side.CLIENT)
public class RenderLargeSludgeWorm extends RenderSludgeWorm<EntityLargeSludgeWorm> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/large_sludge_worm.png");

	private final ModelLargeSludgeWorm model;

	private int renderPass = 0;

	public RenderLargeSludgeWorm(RenderManager manager) {
		super(manager, new ModelLargeSludgeWorm(), 0.0F);
		this.model = (ModelLargeSludgeWorm) this.mainModel;
	}

	@Override
	public void doRender(EntityLargeSludgeWorm entity, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();

		/*for(int i = 0; i < 4; i++) {
			this.renderPass = i;
			super.doRender(entity, x, y, z, yaw, partialTicks);
		}*/

		Vec3d[] points = new Vec3d[entity.parts.length + 2];

		points[0] = entity.parts[0].getPositionVector().add(0, -1, 0);
		for(int i = 0; i < entity.parts.length; i++) {
			points[i + 1] = entity.parts[i].getPositionVector();
		}
		points[entity.parts.length + 1] = entity.parts[entity.parts.length - 1].getPositionVector().add(0, -1, 0);

		ISpline spline = new CatmullRomSpline(points);

		GlStateManager.glLineWidth(4);

		GlStateManager.disableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1, 1, 1, 1);
		
		RenderHelper.enableGUIStandardItemLighting();

		GlStateManager.pushMatrix();
		GlStateManager.translate(x - (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks), y - (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks) + 0.5D, z - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks));

		GlStateManager.disableCull();

		//GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
		this.bindTexture(TEXTURE);
		
		GlStateManager.glBegin(GL11.GL_QUADS);

		int segments = 32;

		Vec3d worldUp = new Vec3d(0, 1, 0);

		final float[][] crossSection = new float[][] {
			{-0.5F, 0.4F},
			{-0.5F, -0.4F},
			{-0.4F, -0.4F},
			{-0.4F, -0.5F},
			{0.4F, -0.5F},
			{0.4F, -0.4F},
			{0.5F, -0.4F},
			{0.5F, 0.4F},
			{0.4F, 0.4F},
			{0.4F, 0.5F},
			{-0.4F, 0.5F},
			{-0.4F, 0.4F},
		};
		
		for(int i = 1; i < segments - 2; i++) {
			Vec3d pos1 = spline.interpolate(i / (float)(segments - 1));
			Vec3d dir1 = spline.derivative(i / (float)(segments - 1));

			Vec3d pos2 = spline.interpolate((i + 1) / (float)(segments - 1));
			Vec3d dir2 = spline.derivative((i + 1) / (float)(segments - 1));

			Vec3d right1 = dir1.crossProduct(worldUp).normalize();
			Vec3d up1 = right1.crossProduct(dir1).normalize();

			Vec3d right2 = dir2.crossProduct(worldUp).normalize();
			Vec3d up2 = right2.crossProduct(dir2).normalize();

			Vec3d[] s1 = new Vec3d[crossSection.length];
			Vec3d[] s2 = new Vec3d[crossSection.length];
			
			for(int j = 0; j < crossSection.length; j++) {
				s1[j] = getVertex(pos1, right1, up1, crossSection[j][0], crossSection[j][1]);
				s2[j] = getVertex(pos2, right2, up2, crossSection[j][0], crossSection[j][1]);
			}

			for(int j = 0; j < crossSection.length; j++) {
				Vec3d v11 = s1[j];
				Vec3d v12 = s1[(j + 1) % crossSection.length];
				Vec3d v21 = s2[j];
				Vec3d v22 = s2[(j + 1) % crossSection.length];
				
				float uw = (float)v12.subtract(v11).length() * 0.0625F;
				float vw = (float)v21.subtract(v11).length() * 0.125F;
				
				float us = 0.078125F;
				float vs = 0.140625F;
				
				glVert(v11, us, vs);
				glVert(v21, us, vs + vw);
				glVert(v22, us + uw, vs + vw);
				glVert(v12, us + uw, vs);
			}
		}

		GlStateManager.glEnd();

		GlStateManager.popMatrix();
		
		GlStateManager.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	protected static Vec3d getVertex(Vec3d pos, Vec3d rightVec, Vec3d upVec, float right, float up) {
		return new Vec3d(pos.x + rightVec.x * right + upVec.x * up, pos.y + rightVec.y * right + upVec.y * up, pos.z + rightVec.z * right + upVec.z * up);
	}

	protected static void glVert(Vec3d pos, float u, float v) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex3d(pos.x, pos.y, pos.z);
	}
	
	protected static void glVert(Vec3d pos, Vec3d rightVec, Vec3d upVec, float right, float up) {
		GL11.glVertex3d(pos.x + rightVec.x * right + upVec.x * up, pos.y + rightVec.y * right + upVec.y * up, pos.z + rightVec.z * right + upVec.z * up);
	}

	@Override
	protected void renderHeadPartModel(EntityLargeSludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);

		GlStateManager.enableCull();

		this.prePass();
		model.renderHead(entity, frame, wibbleStrength, partialTicks, this.renderPass == 1);
		this.postPass();
	}

	@Override
	protected void renderBodyPartModel(EntityLargeSludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);

		GlStateManager.enableCull();

		this.prePass();
		model.renderBody(entity, frame, wibbleStrength, partialTicks, this.renderPass == 1);
		this.postPass();
	}

	@Override
	protected void renderTailPartModel(EntityLargeSludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);

		GlStateManager.enableCull();

		this.prePass();
		model.renderTail(entity, frame, wibbleStrength, partialTicks, this.renderPass == 1);
		this.postPass();
	}

	protected void prePass() {
		switch(this.renderPass) {
		case 0:
			GlStateManager.enableCull();
			GlStateManager.depthMask(false);
			GlStateManager.cullFace(CullFace.FRONT);
			break;
		case 2:
			GlStateManager.enableCull();
			GlStateManager.depthMask(false);
			GlStateManager.cullFace(CullFace.BACK);
			break;
		case 3:
			GlStateManager.enableCull();
			GlStateManager.depthMask(true);
			GlStateManager.colorMask(false, false, false, false);
			break;
		default:
			GlStateManager.disableCull();
			GlStateManager.cullFace(CullFace.BACK);
			break;
		}
	}

	protected void postPass() {
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);

		switch(this.renderPass) {
		case 0:
			GlStateManager.cullFace(CullFace.BACK);
			break;
		case 2:
			GlStateManager.cullFace(CullFace.BACK);
			break;
		case 3:
			GlStateManager.colorMask(true, true, true, true);
			break;
		default:
			break;
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLargeSludgeWorm entity) {
		return TEXTURE;
	}

}
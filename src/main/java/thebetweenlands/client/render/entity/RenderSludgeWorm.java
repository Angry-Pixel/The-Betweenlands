package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSmolSludgeWorm;
import thebetweenlands.common.entity.mobs.EntitySludgeWorm;

@SideOnly(Side.CLIENT)
public class RenderSludgeWorm<T extends EntitySludgeWorm> extends RenderLiving<T> {
	public static final ResourceLocation TEXTURE_HEAD = new ResourceLocation("thebetweenlands:textures/entity/smol_sludge_worm_head.png");
	public static final ResourceLocation TEXTURE_BODY = new ResourceLocation("thebetweenlands:textures/entity/smol_sludge_worm_body.png");

	private final ModelSmolSludgeWorm model;

	public RenderSludgeWorm(RenderManager manager) {
		super(manager, new ModelSmolSludgeWorm(), 0.0F);
		this.model = (ModelSmolSludgeWorm) this.mainModel;
	}

	public RenderSludgeWorm(RenderManager manager, ModelBase model, float shadow) {
		super(manager, model, shadow);
		this.model = null;
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);

		boolean isVisible = this.isVisible(entity);
		boolean isTranslucentToPlayer = !isVisible && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);

		if(!isVisible && !isTranslucentToPlayer) {
			return;
		}

		boolean useBrightness = this.setDoRenderBrightness(entity, partialTicks);

		if(isTranslucentToPlayer) {
			GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
		}
		
		boolean useTeamColors = false;

		if(this.renderOutlines) {
			useTeamColors = this.setScoreTeamColor(entity);
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}
		
		GlStateManager.pushMatrix();

		float totalAngleDiff = 0.0f;

		for(int i = 1; i < entity.parts.length; i++) {
			MultiPartEntityPart prevPart = entity.parts[i - 1];
			MultiPartEntityPart part = entity.parts[i];

			double yawDiff = (prevPart.rotationYaw - part.rotationYaw) % 360.0F;
			double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;

			totalAngleDiff += (float) Math.abs(yawInterpolant);
		}

		float avgAngleDiff = totalAngleDiff;
		if(entity.parts.length > 1) {
			avgAngleDiff /= (entity.parts.length - 1);
		}

		float avgWibbleStrength = MathHelper.clamp(1.0F - avgAngleDiff / 60.0F, 0, 1);

		renderHead(entity, 1, x, y + 1.5F, z, entity.parts[0].rotationYaw, avgWibbleStrength, partialTicks);

		double ex = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
		double ey = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
		double ez = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;

		double rx = ex - x;
		double ry = ey - y;
		double rz = ez - z;

		float zOffset = 0;

		for(int i = 1; i < entity.parts.length - 1; i++) {
			renderBodyPart(entity, entity.parts[i], entity.parts[i - 1], rx, ry, rz, i, avgWibbleStrength, zOffset -= 0.001F, partialTicks);
		}

		renderTailPart(entity, entity.parts[entity.parts.length - 1], entity.parts[entity.parts.length - 2], rx, ry, rz, entity.parts.length - 1, avgWibbleStrength, partialTicks);

		GlStateManager.popMatrix();
		
		if(this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		if(useTeamColors) {
			this.unsetScoreTeamColor();
		}

		if(isTranslucentToPlayer) {
			GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
		}

		if(useBrightness) {
			this.unsetBrightness();
		}
	}

	private void renderHead(T entity, int frame, double x, double y, double z, float yaw, float avgWibbleStrength, float partialTicks) {
		double yawDiff = (yaw - entity.parts[1].rotationYaw) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		float wibbleStrength = Math.min(avgWibbleStrength, MathHelper.clamp(1.0F - (float)Math.abs(yawInterpolant) / 60.0F, 0, 1));

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(180F + yaw, 0, 1F, 0);

		this.renderHeadPartModel(entity, frame, wibbleStrength, partialTicks);

		GlStateManager.popMatrix();
	}

	protected void renderHeadPartModel(T entity, int frame, float wibbleStrength, float partialTicks) {
		bindTexture(TEXTURE_HEAD);
		model.renderHead(entity, frame, wibbleStrength, partialTicks);
	}

	protected void renderBodyPart(T entity, MultiPartEntityPart part, MultiPartEntityPart prevPart, double rx, double ry, double rz, int frame, float avgWibbleStrength, float zOffset, float partialTicks) {
		double x = part.lastTickPosX + (part.posX - part.lastTickPosX) * (double)partialTicks - rx;
		double y = part.lastTickPosY + (part.posY - part.lastTickPosY) * (double)partialTicks - ry;
		double z = part.lastTickPosZ + (part.posZ - part.lastTickPosZ) * (double)partialTicks - rz;

		float yaw = part.prevRotationYaw + (part.rotationYaw - part.prevRotationYaw) * partialTicks;

		double yawDiff = (prevPart.rotationYaw - part.rotationYaw) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		float wibbleStrength = Math.min(avgWibbleStrength, MathHelper.clamp(1.0F - (float)Math.abs(yawInterpolant) / 60.0F, 0, 1));

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y - 1.125f + zOffset, z);// GlStateManager.translate(x, y, z);
		//GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(-yaw, 0, 1F, 0);

		this.renderBodyPartModel(entity, frame, wibbleStrength, partialTicks);

		GlStateManager.popMatrix();
	}

	protected void renderBodyPartModel(T entity, int frame, float wibbleStrength, float partialTicks) {
		bindTexture(TEXTURE_BODY);
		model.renderBody(entity, frame, wibbleStrength, partialTicks);
	}

	protected void renderTailPart(T entity, MultiPartEntityPart part, MultiPartEntityPart prevPart, double rx, double ry, double rz, int frame, float avgWibbleStrength, float partialTicks) {
		double x = part.lastTickPosX + (part.posX - part.lastTickPosX) * (double)partialTicks - rx;
		double y = part.lastTickPosY + (part.posY - part.lastTickPosY) * (double)partialTicks - ry;
		double z = part.lastTickPosZ + (part.posZ - part.lastTickPosZ) * (double)partialTicks - rz;

		float yaw = part.prevRotationYaw + (part.rotationYaw - part.prevRotationYaw) * partialTicks;

		double yawDiff = (prevPart.rotationYaw - part.rotationYaw) % 360.0F;
		double yawInterpolant = 2 * yawDiff % 360.0F - yawDiff;
		float wibbleStrength = Math.min(avgWibbleStrength, MathHelper.clamp(1.0F - (float)Math.abs(yawInterpolant) / 60.0F, 0, 1));

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.525f, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(180F + yaw, 0, 1F, 0);
		GlStateManager.disableCull();

		this.renderTailPartModel(entity, frame, wibbleStrength, partialTicks);

		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	protected void renderTailPartModel(T entity, int frame, float wibbleStrength, float partialTicks) {
		bindTexture(TEXTURE_BODY);
		model.renderTail(entity, frame, wibbleStrength, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySludgeWorm entity) {
		return TEXTURE_HEAD;
	}
}
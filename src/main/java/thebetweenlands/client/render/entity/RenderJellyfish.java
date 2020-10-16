package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelJellyfish;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityJellyfish;
import thebetweenlands.util.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderJellyfish extends RenderLiving<EntityJellyfish> {
	private static final ResourceLocation[] TEXTURE = new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_1.png"),
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_2.png"),
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_3.png"),
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_4.png"),
			new ResourceLocation("thebetweenlands:textures/entity/jellyfish_5.png")
	};

	private static final float[][] GLOW_COLORS = {
			{238 / 255.0f, 173 / 255.0f, 114 / 255.0f},
			{180 / 255.0f, 42 / 255.0f, 42 / 255.0f},
			{224 / 255.0f, 83 / 255.0f, 108 / 255.0f},
			{90 / 255.0f, 170 / 255.0f, 145 / 255.0f},
			{215 / 255.0f, 176 / 255.0f, 195 / 255.0f},
	};

	public final static ModelJellyfish MODEL = new ModelJellyfish();

	public RenderJellyfish(RenderManager manager) {
		super(manager, MODEL, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityJellyfish entity) {
		return TEXTURE[entity.getJellyfishColour()];
	}

	@Override
	public void doRender(EntityJellyfish jellyfish, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(jellyfish, x, y + jellyfish.height * 0.5f, z, entityYaw, partialTicks);

		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double interpX = jellyfish.lastTickPosX + (jellyfish.posX - jellyfish.lastTickPosX) * partialTicks;
			double interpY = jellyfish.lastTickPosY + (jellyfish.posY - jellyfish.lastTickPosY) * partialTicks;
			double interpZ = jellyfish.lastTickPosZ + (jellyfish.posZ - jellyfish.lastTickPosZ) * partialTicks;

			float str = 5.0f * jellyfish.getJellyfishSize();

			float[] colors = GLOW_COLORS[jellyfish.getJellyfishColour()];

			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(interpX, interpY, interpZ, 8.0f, str * colors[0], str * colors[1], str * colors[2]));
		}
	}

	@Override
	public float prepareScale(EntityJellyfish entitylivingbaseIn, float partialTicks) {
		float scale = entitylivingbaseIn.getJellyfishSize();

		GlStateManager.scale(scale, scale * entitylivingbaseIn.getJellyfishLength(), scale);

		float limbSwingAmount = entitylivingbaseIn.prevLimbSwingAmount + (entitylivingbaseIn.limbSwingAmount - entitylivingbaseIn.prevLimbSwingAmount) * partialTicks;
		float limbSwing = entitylivingbaseIn.limbSwing - entitylivingbaseIn.limbSwingAmount * (1.0F - partialTicks);

		GlStateManager.scale(
				1.0f + MathHelper.sin(limbSwing) * Math.min(limbSwingAmount, 0.2f) * 2.0f,
				1.0f - MathHelper.sin(limbSwing) * Math.min(limbSwingAmount, 0.2f),
				1.0f + MathHelper.sin(limbSwing) * Math.min(limbSwingAmount, 0.2f) * 2.0f
				);

		return super.prepareScale(entitylivingbaseIn, partialTicks);
	}

	@Override
	protected void renderModel(EntityJellyfish entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.enableCull();

		LightingUtil.INSTANCE.setLighting(255);

		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		GlStateManager.depthMask(false);
		Minecraft.getMinecraft().entityRenderer.setupFogColor(true);

		super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

		Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
		GlStateManager.depthMask(true);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		super.renderModel(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

		LightingUtil.INSTANCE.revert();

		GlStateManager.disableCull();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
	}

	protected static double interpolate(double prev, double now, double partialTicks) {
		return prev + (now - prev) * partialTicks;
	}

	@Override
	protected void applyRotations(EntityJellyfish jellofooosh, float ageInTicks, float rotationYaw, float partialTicks) {
		Vec3d weightPos = jellofooosh.getOrientationPos(partialTicks);

		double dx = interpolate(jellofooosh.lastTickPosX, jellofooosh.posX, partialTicks) - weightPos.x;
		double dy = interpolate(jellofooosh.lastTickPosY, jellofooosh.posY, partialTicks) - weightPos.y;
		double dz = interpolate(jellofooosh.lastTickPosZ, jellofooosh.posZ, partialTicks) - weightPos.z;

		float yaw = -(float)Math.toDegrees(MathHelper.atan2(dz, dx));
		float pitch = (float)Math.toDegrees(MathHelper.atan2(Math.sqrt(dx * dx + dz * dz), -dy)) - 180;

		GlStateManager.rotate(yaw, 0, 1, 0);
		GlStateManager.rotate(pitch, 0, 0, 1);
		GlStateManager.rotate(-yaw, 0, 1, 0);
	}
}

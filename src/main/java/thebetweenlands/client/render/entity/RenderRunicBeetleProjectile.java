package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.runechain.modifier.RenderProperties;
import thebetweenlands.api.runechain.modifier.RuneEffectModifier;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelRunicBeetleFlying;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.EntityRunicBeetleProjectile;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderRunicBeetleProjectile extends Render<EntityRunicBeetleProjectile> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/runic_beetle.png");
	private static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation(ModInfo.ID, "textures/entity/runic_beetle_overlay.png");
	private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(ModInfo.ID, "textures/entity/runic_beetle_glow.png");

	private static final ModelRunicBeetleFlying MODEL = new ModelRunicBeetleFlying();

	public RenderRunicBeetleProjectile(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityRunicBeetleProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180.0f + entityYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1, -1, 1);
		GlStateManager.translate(0, -1.5f, 0);

		GlStateManager.color(1, 1, 1, 1);

		this.bindEntityTexture(entity);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		GlStateManager.disableCull();

		GlStateManager.translate(0, 0.125f, 0);

		float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

		MODEL.setRotationAngles(0, 0, entity.ticksExisted + partialTicks, 0, pitch, 0.0625f, entity);

		MODEL.render(entity, 0, 0, 0, 0, 0, 0.0625F);

		float r = 1f;
		float g = 0.1f;
		float b = 0.1f;
		float a = 0.5f;

		RuneEffectModifier modifier = entity.getRuneEffectModifier();
		Subject subject = entity.getRuneEffectModifierSubject();

		if(modifier != null && subject != null) {
			int count = modifier.getColorModifierCount(subject);

			if(count > 0) {
				int color = modifier.getColorModifier(subject, 0);

				a = (float)(color >> 24 & 255) / 255.0F;
				r = (float)(color >> 16 & 255) / 255.0F;
				g = (float)(color >> 8 & 255) / 255.0F;
				b = (float)(color & 255) / 255.0F;

				this.bindTexture(TEXTURE_OVERLAY);

				LayerOverlay.renderOverlay(entity, () -> {
					MODEL.render(entity, 0, 0, 0, 0, 0, 0.0625F);
				}, false, r, g, b, a);
			}
		}

		this.bindTexture(TEXTURE_GLOW);

		LayerOverlay.renderOverlay(entity, () -> {
			MODEL.render(entity, 0, 0, 0, 0, 0, 0.0625F);
		}, true, 1, 1, 1, 1);

		if(modifier != null && subject != null) {
			GlStateManager.pushMatrix();

			GlStateManager.translate(0, 0.75f, 0);
			GlStateManager.scale(1, -1, 1);

			RenderProperties properties = new RenderProperties();
			properties.emissive = true;
			properties.sizeX = properties.sizeY = properties.sizeZ = 0.35f;
			properties.alpha = 0.3f;

			for(int i = 0; i < Math.min(3, modifier.getRendererCount(subject)); i++) {
				modifier.render(subject, i, properties, entity.getRenderState(), partialTicks);

				GlStateManager.translate(0, 0.5f, 0);
			}

			GlStateManager.popMatrix();
		}

		GlStateManager.enableCull();

		GlStateManager.disableBlend();

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		
		float glow = a * 0.1f;
		
		float ox = (float)x - (float)ActiveRenderInfo.getCameraPosition().x;
		float oy = (float)y + 0.175f - (float)ActiveRenderInfo.getCameraPosition().y;
		float oz = (float)z - (float)ActiveRenderInfo.getCameraPosition().z;
		float len = MathHelper.sqrt(ox * ox + oy * oy + oz * oz);
		ox /= len;
		oy /= len;
		oz /= len;
		
		float o = -0.25f;

		LightingUtil.INSTANCE.setLighting(255);
		
		GlStateManager.disableLighting();
		RenderFirefly.renderFireflyGlow(x - ox * o, y - oy * o + 0.175f, z - oz * o, 1.0f, r * glow, g * glow, b * glow, a, false, 5);
		GlStateManager.enableLighting();
		
		LightingUtil.INSTANCE.revert();
		
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
        	ShaderHelper.INSTANCE.require();
        	double rx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
			double ry = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
			double rz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry + 0.15f, rz, 2.5f, r * a * 4, g * a * 4, b * a * 4));
        }
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRunicBeetleProjectile entity) {
		return TEXTURE;
	}
}
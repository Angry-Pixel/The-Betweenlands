package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelWormArrow;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.item.tools.bow.EnumArrowType;

@SideOnly(Side.CLIENT)
public class RenderBLArrow extends RenderArrow<EntityBLArrow> {
	private static final ResourceLocation TEXTURE_ANGLER_TOOTH = new ResourceLocation("thebetweenlands:textures/entity/angler_tooth_arrow.png");
	private static final ResourceLocation TEXTURE_POISONED_ANGLER_TOOTH = new ResourceLocation("thebetweenlands:textures/entity/poisoned_angler_tooth_arrow.png");
	private static final ResourceLocation TEXTURE_OCTINE = new ResourceLocation("thebetweenlands:textures/entity/octine_arrow.png");
	private static final ResourceLocation TEXTURE_BASILISK = new ResourceLocation("thebetweenlands:textures/entity/basilisk_arrow.png");
	public static final ResourceLocation TEXTURE_WORM = new ResourceLocation("thebetweenlands:textures/entity/tiny_sludge_worm.png");
	private static final ResourceLocation TEXTURE_CHIROMAW_BARB = new ResourceLocation("thebetweenlands:textures/entity/chiromaw_barb.png");
	public static final ModelWormArrow WORM_MODEL = new ModelWormArrow();

	public RenderBLArrow(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityBLArrow entity, double x, double y, double z, float entityYaw, float partialTicks) { 
		if(entity.getArrowType() == EnumArrowType.WORM) {
			renderWormArrow(entity, x, y, z, entityYaw, partialTicks);
		}
		else {
			if (entity.getArrowType() == EnumArrowType.OCTINE && ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();
				double rx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
				double ry = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
				double rz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry, rz, 3.0F, 2.3F, 0.5F, 0));
			}
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	private void renderWormArrow(EntityBLArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.bindEntityTexture(entity);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        float shake = (float)entity.arrowShake - partialTicks;

        if (shake > 0.0F) {
            float f10 = -MathHelper.sin(shake * 3.0F) * shake;
            GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-1F, -1F, 1F);
        GlStateManager.translate(0F, -1.5F, 0F);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        //render the worm arrow model
        WORM_MODEL.render();

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBLArrow entity) {
		switch (entity.getArrowType()) {
		case ANGLER_POISON:
			return TEXTURE_POISONED_ANGLER_TOOTH;
		case OCTINE:
			return TEXTURE_OCTINE;
		case BASILISK:
			return TEXTURE_BASILISK;
		case WORM:
			return TEXTURE_WORM;
		case CHIROMAW_BARB:
			return TEXTURE_CHIROMAW_BARB;
		default:
			return TEXTURE_ANGLER_TOOTH;
		}
	}
}

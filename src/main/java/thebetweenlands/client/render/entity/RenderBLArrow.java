package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

	public RenderBLArrow(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityBLArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if(entity.getArrowType() == EnumArrowType.OCTINE && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double rx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
			double ry = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
			double rz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry, rz, 3.0F, 2.3F, 0.5F, 0));
		}
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
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
		default:
			return TEXTURE_ANGLER_TOOTH;
		}
	}
}

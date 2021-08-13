package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityAnimalBurrow;
@SideOnly(Side.CLIENT)
public class RenderAnimalBurrow extends Render<EntityAnimalBurrow> {

	public RenderAnimalBurrow(RenderManager renderManager) {
		super(renderManager);
	}
/*
	@Override
	public void doRender(EntityAnimalBurrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
		
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
			if (!entity.getBurrowItem().isEmpty()) {
				if (shouldRenderAsEntity(entity) && entity.getEntity() != null)
					renderMobInSlot(entity.getEntity(), 0F, 0.0625F, 0F, 0F);
				else
					renderItemInSlot(entity.getBurrowItem(), 0F, 0.25F, 0F, 0.5F);
			}
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	public boolean shouldRenderAsEntity(EntityAnimalBurrow  entity) {
		return entity.getBurrowItem().getItem() instanceof ItemMob && ((ItemMob) entity.getBurrowItem().getItem()).hasEntityData(entity.getBurrowItem());
	}

	public void renderMobInSlot(Entity entity, float x, float y, float z, float rotation) {
		if (entity != null) {
			
			float scale2 = 1F / ((Entity) entity).width * 0.75F;
			float offsetRotation = 180F;
			float offsetY = 0F;

			GlStateManager.pushMatrix();
			if(entity instanceof EntitySiltCrab) {
				offsetY = 0.0625F;
				scale2 = 0.95F;
				offsetRotation = 90F;
			}
			GlStateManager.translate(x, y + offsetY, z);
			GlStateManager.scale(scale2, scale2, scale2);
			GlStateManager.rotate(0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(offsetRotation - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			Render<Entity> renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
			renderer.doRender(entity, 0, 0, 0, 0, 0);
			GlStateManager.popMatrix();
		}
	}

	public void renderItemInSlot(ItemStack stack, float x, float y, float z, float scale) {
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}
	*/
	@Override
	protected ResourceLocation getEntityTexture(EntityAnimalBurrow entity) {
		return null;
	}
}

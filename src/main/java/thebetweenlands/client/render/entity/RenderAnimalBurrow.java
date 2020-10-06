package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelWallHole;
import thebetweenlands.common.entity.EntityAnimalBurrow;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.util.Stencil;
@SideOnly(Side.CLIENT)
public class RenderAnimalBurrow extends Render<EntityAnimalBurrow> {
	private final ModelWallHole modelBlockTextured;
	public RenderAnimalBurrow(RenderManager renderManager) {
		super(renderManager);
		modelBlockTextured = new ModelWallHole(false);
	}

	@Override
	public void doRender(EntityAnimalBurrow entity, double x, double y, double z, float entityYaw, float partialTicks) {

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.pushMatrix();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0F, 0F, 0F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.popMatrix();
		Entity entityb = entity.getCachedEntity();
		//System.out.println("EH?" + entityb);
		if (entityb != null) {
			System.out.println("HELLO?");
			renderMobInSlot(entityb, 0F, 0.0625F, 0F, 0F);
		}

		GlStateManager.popMatrix();

	/*	GlStateManager.pushMatrix();

		//GlStateManager.enableCull();
		GlStateManager.translate((float) x, (float) y - 0.25F, (float) z);
		//GlStateManager.translate(0F, 0F, 0F);
		//GlStateManager.disableCull();
		bindTexture(getBurrowTexture(entity));
		modelBlockTextured.render(entity, 0F, 0F, 0F, 0F, 0F, 0.0625F);

		GlStateManager.popMatrix();
	*/	
		
	}

	public void renderMobInSlot(Entity entity, float x, float y, float z, float rotation) {
			float scale2 = 1F / ((Entity) entity).width * 0.5F;
			float offsetRotation = 180F;
			float offsetY = 0F;
			GlStateManager.translate(x, y + offsetY, z);
			GlStateManager.scale(scale2, scale2, scale2);
			GlStateManager.rotate(0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(offsetRotation - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			Render renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
			renderer.doRender(entity, 0, 0, 0, 0, 0);
			GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityAnimalBurrow entity) {
		return null;
	}
	
	protected ResourceLocation getBurrowTexture(EntityAnimalBurrow entity) {
		String blockPath = entity.getWallSprite().getIconName();
		String modName = "minecraft";
		if (blockPath.contains(":")) {
			modName = blockPath.split(":")[0];
			blockPath = blockPath.split(":")[1];
		}
		return new ResourceLocation(modName, "textures/" + blockPath + ".png");
	}
}

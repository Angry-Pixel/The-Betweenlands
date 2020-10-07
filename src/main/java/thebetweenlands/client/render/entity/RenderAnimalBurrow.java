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
			if (!entity.getBurrowItem().isEmpty()) {
				if (isSafeMobItem(entity) && entity.getEntity() != null)
					renderMobInSlot(entity.getEntity(), 0F, 0.0625F, 0F, 0F);
				else
					renderItemInSlot(entity.getBurrowItem(), 0F, 0.25F, 0F, 0.5F);
			}
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();

		//GlStateManager.enableCull();
		GlStateManager.translate((float) x, (float) y - 0.25F, (float) z);
		//GlStateManager.translate(0F, 0F, 0F);
		//GlStateManager.disableCull();
		bindTexture(getBurrowTexture(entity));
		modelBlockTextured.render(entity, 0F, 0F, 0F, 0F, 0F, 0.0625F);

		GlStateManager.popMatrix();
		
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

		try(Stencil stencil = Stencil.reserve(fbo)) {
			if(stencil.valid()) {
				GL11.glEnable(GL11.GL_STENCIL_TEST);

				stencil.clear(false);

				stencil.func(GL11.GL_ALWAYS, true);
				stencil.op(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_REPLACE);

				GlStateManager.depthMask(false);
				GlStateManager.colorMask(false, false, false, false);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				GlStateManager.disableTexture2D();

				//Polygon offset required so that there's no z fighting with the window and background wall
				GlStateManager.enablePolygonOffset();
				GlStateManager.doPolygonOffset(-5.0F, -5.0F);

				//Render window through which the hole will be visible
				this.modelBlockTextured.frontPiece1.showModel = false;
				this.modelBlockTextured.window.showModel = true;
				this.modelBlockTextured.setWindowZOffsetPercent(-0.001F);
			//	this.renderPass = 0;
				super.doRender(entity, x, y, z, entityYaw, partialTicks);
				this.modelBlockTextured.frontPiece1.showModel = true;
				this.modelBlockTextured.window.showModel = false;

				GlStateManager.disablePolygonOffset();

				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.enableTexture2D();

				GlStateManager.depthMask(true);
				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

				stencil.func(GL11.GL_EQUAL, true);
				stencil.op(GL11.GL_KEEP);
			}

			//Render to depth only with reversed depth test such that in the next pass it can be rendered normally
			GlStateManager.depthFunc(GL11.GL_GEQUAL);
			GlStateManager.colorMask(false, false, false, false);

			//this.renderPass = 1;
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.depthFunc(GL11.GL_LEQUAL);

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		//Render visible pass
		//this.renderPass = 2;

		this.modelBlockTextured.window.showModel = true;
		this.modelBlockTextured.setWindowZOffsetPercent(100F);

		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		this.modelBlockTextured.window.showModel = false;
		

	}

	public boolean isSafeMobItem(EntityAnimalBurrow  entity) {
		return entity.getBurrowItem().getItem() instanceof ItemMob && entity.getBurrowItem().getTagCompound() != null && entity.getBurrowItem().getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND);
	}

	public void renderMobInSlot(Entity entity, float x, float y, float z, float rotation) {
		if (entity != null) {
			float scale2 = 1F / ((Entity) entity).width * 0.5F;
			float tumble = rotation * 11.25F;
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
			if(tumble > 0F)
				GlStateManager.rotate(tumble, 1.0F, 0.0F, 0.0F);
			else
				GlStateManager.rotate(0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(offsetRotation - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			Render renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
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

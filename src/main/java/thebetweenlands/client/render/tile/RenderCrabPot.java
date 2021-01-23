package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelBubblerCrab;
import thebetweenlands.client.render.model.entity.ModelSiltCrab;
import thebetweenlands.client.render.model.tile.ModelCrabPot;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.tile.TileEntityCrabPot;

public class RenderCrabPot extends TileEntitySpecialRenderer<TileEntityCrabPot> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/crab_pot.png");
	public static final ResourceLocation TEXTURE_SILT = new ResourceLocation("thebetweenlands:textures/entity/silt_crab.png");
	public static final ResourceLocation TEXTURE_BUBBLER = new ResourceLocation("thebetweenlands:textures/entity/bubbler_crab.png");
	public static final ModelCrabPot MODEL = new ModelCrabPot();
	public static final ModelSiltCrab MODEL_SILT = new ModelSiltCrab();
	public static final ModelBubblerCrab MODEL_BUBBLER = new ModelBubblerCrab();

	@Override
	public void render(TileEntityCrabPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		int index = te != null ? te.getRotation() : 0;
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5f, (float) y, (float) z + 0.5f);
		GlStateManager.pushMatrix();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 1.5f, 0);
		GlStateManager.rotate(getRotation(index), 0.0F, 1F, 0F);
		GlStateManager.scale(1F, -1F, -1F);
		bindTexture(TEXTURE);
		MODEL.render();
		GlStateManager.popMatrix();

		if (te != null) {
			// inputs
			if (!te.getStackInSlot(0).isEmpty()) {
				if (isSafeMobItem(te) && te.getEntity() != null) {
					if (te.fallCounter > 0) {
						float smoothed = (float) te.fallCounter * 0.03125F + ((float) te.fallCounter * 0.03125F - (float) te.fallCounterPrev * 0.03125F) * partialTicks;
						float smoothedTumble = (float) te.fallCounter + ((float) te.fallCounter - (float) te.fallCounterPrev) * partialTicks;
						renderMobInSlot(te.getEntity(), 0F, 0.0625F + smoothed, 0F, smoothedTumble);
					}
					if (te.fallCounter <= 0) {
						GlStateManager.pushMatrix();
						GlStateManager.translate(0F, 1.5F, 0F);
						if(!te.animate)
							GlStateManager.rotate(90F - Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
						else
							GlStateManager.rotate(-90F + getRotation(index), 0.0F, 1.0F, 0.0F);

						if (te.getEntity() instanceof EntitySiltCrab) {
							GlStateManager.pushMatrix();
							bindTexture(TEXTURE_SILT);
							GlStateManager.scale(0.95F, -0.95F, -0.95F);
							if(te.animate)
								MODEL_SILT.renderEating(te.animationTicks, 0.0625F);
							else
								MODEL_SILT.render(te.getEntity(), 0F, 0F, 0F, 0F, 0F, 0.0625F);
							GlStateManager.popMatrix();
						}
						if (te.getEntity() instanceof EntityBubblerCrab) {
							GlStateManager.pushMatrix();
							bindTexture(TEXTURE_BUBBLER);
							GlStateManager.scale(0.95F, -0.95F, -0.95F);
							if(te.animate)
								MODEL_BUBBLER.renderEating(te.animationTicks, 0.0625F);
							else
								MODEL_BUBBLER.render(te.getEntity(), 0F, 0F, 0F, 0F, 0F, 0.0625F);
							GlStateManager.popMatrix();
						}
						GlStateManager.popMatrix();
					}
				}
				else
					renderItemInSlot(te.getStackInSlot(0), 0F, 0.5F, 0F, 0.5F);
			}
		}
		GlStateManager.popMatrix();
	}

	public static float getRotation(int index) {
		switch (index) {
		case 3:
			return 180F;
		case 2:
		default:
			return -90F;
		case 1:
			return 0F;
		case 0:
			return 90F;
		}
	}

	public boolean isSafeMobItem(TileEntityCrabPot te) {
		return te.getStackInSlot(0).getItem() instanceof ItemMob && (te.getEntity() instanceof EntitySiltCrab || te.getEntity() instanceof EntityBubblerCrab);
	}

	public void renderMobInSlot(Entity entity, float x, float y, float z, float rotation) {
		if (entity != null) {
			float scale = 0.95F;
			float tumble = rotation * 11.25F;
			float offsetRotation = 90F;
			float offsetY = 0.0625F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + offsetY, z);
			GlStateManager.scale(scale, scale, scale);
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
}
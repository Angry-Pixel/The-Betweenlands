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
import net.minecraftforge.common.util.Constants;
import thebetweenlands.client.render.model.tile.ModelCrabPot;
import thebetweenlands.common.entity.mobs.EntityBubblerCrab;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.tile.TileEntityCrabPot;

public class RenderCrabPot extends TileEntitySpecialRenderer<TileEntityCrabPot> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/crab_pot.png");
	public static final ModelCrabPot MODEL = new ModelCrabPot();

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
					float smoothed = (float)te.fallCounter * 0.03125F + ((float)te.fallCounter * 0.03125F - (float)te.fallCounterPrev * 0.03125F) * partialTicks;
					float smoothedTumble = (float)te.fallCounter + ((float)te.fallCounter  - (float)te.fallCounterPrev) * partialTicks;
					renderMobInSlot(te.getEntity(), 0F, 0.0625F + smoothed, 0F, smoothedTumble);
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
			return 90F;
		case 1:
			return 0F;
		case 0:
			return -90F;
		}
	}

	public boolean isSafeMobItem(TileEntityCrabPot te) {
		return te.getStackInSlot(0).getItem() instanceof ItemMob && te.getStackInSlot(0).getTagCompound() != null && te.getStackInSlot(0).getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND);
	}

	public void renderMobInSlot(Entity entity, float x, float y, float z, float rotation) {
		if (entity != null) {
			float scale2 = 1F / ((Entity) entity).width * 0.5F;
			float tumble = rotation * 11.25F;
			float offsetRotation = 180F;
			float offsetY = 0F;

			GlStateManager.pushMatrix();
			if(entity instanceof EntitySiltCrab || entity instanceof EntityBubblerCrab) {
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
}
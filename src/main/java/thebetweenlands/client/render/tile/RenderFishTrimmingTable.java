package thebetweenlands.client.render.tile;

import java.util.SplittableRandom;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.model.tile.ModelFishTrimmingTable;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.mobs.EntityAnadia.EnumAnadiaColor;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.item.misc.ItemMobAnadia;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityFishTrimmingTable;

public class RenderFishTrimmingTable extends TileEntitySpecialRenderer<TileEntityFishTrimmingTable> {
	public static final ResourceLocation TEXTURE_BASE = new ResourceLocation("thebetweenlands:textures/tiles/fish_trimming_table.png");
	public static final ResourceLocation TEXTURE_USED = new ResourceLocation("thebetweenlands:textures/tiles/fish_trimming_table_used.png");
	public static final ModelFishTrimmingTable MODEL = new ModelFishTrimmingTable();

	@Override
	public void render(TileEntityFishTrimmingTable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		int meta = te != null ? te.getBlockMetadata() : 0;

		GlStateManager.pushMatrix();

		GlStateManager.translate((float) x + 0.5f, (float) y, (float) z + 0.5f);
		GlStateManager.rotate(getRotation(meta) - 90F, 0.0F, 1F, 0F);

		GlStateManager.pushMatrix();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 1.5f, 0);
		GlStateManager.scale(1F, -1F, -1F);
	//	GlStateManager.disableCull();

		bindTexture(TEXTURE_BASE);
		if (te != null) {
			if (!te.getStackInSlot(4).isEmpty())
				bindTexture(TEXTURE_USED);
			if (!te.getStackInSlot(5).isEmpty())
				MODEL.renderParts(te);
		}
		MODEL.render();

	//	GlStateManager.enableCull();
		GlStateManager.popMatrix();

		if (te != null) {
			// inputs
			SplittableRandom rand = new SplittableRandom((long) (te.getPos().getX() + te.getPos().getY() + te.getPos().getZ()));
			if (!te.getStackInSlot(0).isEmpty()) {
				if (shouldRenderAsEntity(te, 0) && te.getInputEntity() != null)
					renderAnadiaInSlot(te.getStackInSlot(0), te.getInputEntity(), 0, 0.8F, 0, 1F);
				else
					renderItemInSlot(te.getStackInSlot(0), 0F, 0.75F, 0F, 0.5F, 0F);
			}

			if (!te.getStackInSlot(1).isEmpty())
				renderItemInSlot(te.getStackInSlot(1), -0.25F, 0.75F, 0F, 0.25F, (float)rand.nextDouble() * 60F - 30F);

			if (!te.getStackInSlot(2).isEmpty())
				renderItemInSlot(te.getStackInSlot(2), 0F, 0.75F, -0.125F, 0.25F, (float)rand.nextDouble() * 60F - 30F);

			if (!te.getStackInSlot(3).isEmpty())
				renderItemInSlot(te.getStackInSlot(3), 0.25F, 0.75F, 0F, 0.25F, (float)rand.nextDouble() * 60F - 30F);

			if (!te.getStackInSlot(4).isEmpty())
				renderItemInSlot(te.getStackInSlot(4), -0.25F, 0.75F, 0.25F, 0.25F, (float)rand.nextDouble() * 60F - 30F);

		}

		GlStateManager.popMatrix();
	}

	public boolean shouldRenderAsEntity(TileEntityFishTrimmingTable te, int slot) {
		if(te.getStackInSlot(slot).getItem() == ItemRegistry.ANADIA && ((ItemMob) te.getStackInSlot(slot).getItem()).hasEntityData(te.getStackInSlot(slot)))
			return true;
		if(te.getStackInSlot(slot).getItem() == ItemRegistry.SILT_CRAB || te.getStackInSlot(slot).getItem() == ItemRegistry.BUBBLER_CRAB)
			return true;
		return false;
	}

	public void renderAnadiaInSlot(ItemStack stack, Entity entity, float x, float y, float z, float scale) {
		if (entity != null) {
			if(stack.getItem() instanceof ItemMobAnadia && ((ItemMobAnadia)stack.getItem()).isRotten(Minecraft.getMinecraft().world, stack))
				((EntityAnadia) entity).setFishColour(EnumAnadiaColor.ROTTEN);
			entity.prevRotationPitch = entity.rotationPitch = 0;
			entity.prevRotationYaw = entity.rotationYaw = 0;
			float scale2 = 1F / entity.width * 0.5F;
			if(stack.getItem() instanceof ItemMobAnadia)
				scale2 = 1F / ((EntityAnadia) entity).getFishSize() * 0.5F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);		
			GlStateManager.rotate(-90F, 0F, 1F, 0F);
			if(stack.getItem() instanceof ItemMobAnadia) {
				GlStateManager.translate(0.1875F, 0F, -0.0625F);
				GlStateManager.rotate(90F, 0F, 0F, 1F);
				GlStateManager.rotate(45F, 1F, 0F, 0F);
			}
			else {
				GlStateManager.translate(0F, 0.1875F, 0F);
				GlStateManager.rotate(180F, 0F, 0F, 1F);
			}
			GlStateManager.scale(scale2, scale2, scale2);
			Render<Entity> renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
			renderer.doRender(entity, 0, 0, 0, 0, 0);
			GlStateManager.popMatrix();
		}
	}

	public void renderItemInSlot(ItemStack stack, float x, float y, float z, float scale, float rotate) {
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(scale, scale, scale);

			if(stack.getItem() != ItemRegistry.BONE_AXE) {
				GlStateManager.rotate(-90F, 1F, 0F, 0F);
				GlStateManager.rotate(0F, 0F, 1F, 0F);
				GlStateManager.rotate(rotate, 0F, 0F, 1F);
			}
			else
			{
				GlStateManager.rotate(180F, 1F, 0F, 0F);
				GlStateManager.rotate(-70F, 0F, 1F, 0F);
				GlStateManager.rotate(0F, 0F, 0F, 1F);
			}

			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			tex.setBlurMipmap(false, false);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, (World) null, (EntityLivingBase) null));
			RenderHelper.enableStandardItemLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			tex.restoreLastBlurMipmap();
		}
	}

	public static float getRotation(int meta) {
		switch (meta) {
		case 5:
			return 180F;
		case 4:
		default:
			return 0F;
		case 3:
			return 90F;
		case 2:
			return -90F;
		}
	}
}
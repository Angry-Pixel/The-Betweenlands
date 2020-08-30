package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.client.render.model.tile.ModelSmokingRack;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntitySmokingRack;

public class RenderSmokingRack extends TileEntitySpecialRenderer<TileEntitySmokingRack> {
	public static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("thebetweenlands:textures/tiles/smoking_rack.png");
	public static final ResourceLocation TEXTURE_SMOKED = new ResourceLocation("thebetweenlands:textures/tiles/smoking_rack_smoked.png");
	public static final ModelSmokingRack MODEL = new ModelSmokingRack();

	@Override
	public void render(TileEntitySmokingRack te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
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
		this.bindTexture(TEXTURE_NORMAL);
		MODEL.render();
	//	GlStateManager.enableCull();
		GlStateManager.popMatrix();

		if (te != null) {
			//inputs
			if (!te.getStackInSlot(0).isEmpty()) {
				
				renderItemInSlot(te, te.getStackInSlot(0), 0F, 0.0F, 0F, 1F);
			}

			if (!te.getStackInSlot(1).isEmpty() && te.getStackInSlot(4).isEmpty()) {
				if(isSafeAnadiaMobItem(te, 1) && te.getRenderEntity(1) != null)
						renderAnadiaInSlot(te, te.getRenderEntity(1), 0.525F, 0.5F, 0.0F, 0.5F);
				else
					renderItemInSlot(te, te.getStackInSlot(1), 0F, 1.525F, 0F, 0.5F);
			}

			if (!te.getStackInSlot(2).isEmpty() && te.getStackInSlot(5).isEmpty()) {
				if(isSafeAnadiaMobItem(te, 2) && te.getRenderEntity(2) != null)
					renderAnadiaInSlot(te, te.getRenderEntity(2), 0.525F, -0.4F, 0.4F, 0.5F);
				else
					renderItemInSlot(te, te.getStackInSlot(2), 0F, 0.55F, 0.4F, 0.5F);
			}

			if (!te.getStackInSlot(3).isEmpty() && te.getStackInSlot(6).isEmpty()) {
				if(isSafeAnadiaMobItem(te, 3) && te.getRenderEntity(3) != null)
					renderAnadiaInSlot(te, te.getRenderEntity(3), 0.525F, -0.4F, -0.4F, 0.5F);
				else
					renderItemInSlot(te, te.getStackInSlot(3), 0F, 0.55F, -0.4F, 0.5F);
			}

			//outputs
			if (!te.getStackInSlot(4).isEmpty()) {
				if(isSafeAnadiaMobItem(te, 4)&& te.getRenderEntity(4) != null)
					renderAnadiaInSlot(te, te.getRenderEntity(4), 0.525F, 0.5F, 0.0F, 0.5F);
				else
					renderItemInSlot(te, te.getStackInSlot(4), 0F, 1.525F, 0F, 0.5F);
			}

			if (!te.getStackInSlot(5).isEmpty()) {
				if(isSafeAnadiaMobItem(te, 5) && te.getRenderEntity(5) != null)
					renderAnadiaInSlot(te, te.getRenderEntity(5), 0.525F, -0.4F, 0.4F, 0.5F);
				else
					renderItemInSlot(te, te.getStackInSlot(5), 0F, 0.55F, 0.4F, 0.5F);
			}

			if (!te.getStackInSlot(6).isEmpty()) {
				if(isSafeAnadiaMobItem(te, 6) && te.getRenderEntity(6) != null)
					renderAnadiaInSlot(te, te.getRenderEntity(6), 0.525F, -0.4F, -0.4F, 0.5F);
				else
					renderItemInSlot(te, te.getStackInSlot(6), 0F, 0.55F, -0.4F, 0.5F);
			}

		}

		GlStateManager.popMatrix();
	}

	public boolean isSafeAnadiaMobItem(TileEntitySmokingRack te, int slot) {
		return te.getStackInSlot(slot).getItem() == ItemRegistry.ANADIA && te.getStackInSlot(slot).getTagCompound() != null && te.getStackInSlot(slot).getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND);
	}

	public void renderAnadiaInSlot(TileEntitySmokingRack smoking_rack, Entity entity, float x, float y, float z, float scale) {
		if (entity != null) {
			float scale2 = 1F / ((EntityAnadia) entity).getFishSize() * 0.5F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(scale2, scale2, scale2);
			Render renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
			renderer.doRender(entity, 0, 0, 0, 0, 0);
			GlStateManager.popMatrix();
		}
	}

	public void renderItemInSlot(TileEntitySmokingRack smoking_rack, ItemStack stack, float x, float y, float z, float scale) {
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(scale, scale, scale);

			if(stack.getItem() != Item.getItemFromBlock(BlockRegistry.MOSS)) {
				GlStateManager.rotate(0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(45F, 0.0F, 0.0F, 1.0F);
			}
			else
			{
				GlStateManager.rotate(0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(90F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(0F, 0.0F, 0.0F, 1.0F);
			}
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
			//RenderHelper.disableStandardItemLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, (World) null, (EntityLivingBase) null));
			//GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
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
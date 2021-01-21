package thebetweenlands.client.render.tile;

import java.util.SplittableRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelCrabPotFilter;
import thebetweenlands.common.tile.TileEntityCrabPotFilter;

public class RenderCrabPotFilter extends TileEntitySpecialRenderer<TileEntityCrabPotFilter> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/crab_pot_filter.png");
	public static final ModelCrabPotFilter MODEL = new ModelCrabPotFilter();

	@Override
	public void render(TileEntityCrabPotFilter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
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
		GlStateManager.popMatrix();
		// TODO more things needed here for some render stuffs and whatnot ;P
		
		if(te != null) {
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			SplittableRandom rand = new SplittableRandom((long) (te.getPos().getX() + te.getPos().getY() + te.getPos().getZ()));
			
			// input rendering
			if (!te.getStackInSlot(1).isEmpty() && te.active) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x + 0.5D + getItemOffsetX(index), y + 1.25D - (double) (te.getFilteringProgressScaled(200) * 0.000625D), z + 0.5D + getItemOffsetZ(index));
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.5D - (float) (te.getFilteringProgressScaled(200) * 0.0025F), 0.5D - (float) (te.getFilteringProgressScaled(200) * 0.0025F), 0.5D - (float) (te.getFilteringProgressScaled(200) * 0.0025F));
				GlStateManager.rotate(-90F - index * 90F, 0, 1, 0);
				ItemStack stack = te.getStackInSlot(1);
				renderItem.renderItem(stack, TransformType.FIXED);
				GlStateManager.popMatrix();
				GlStateManager.popMatrix();
			}

			// result rendering
			if (!te.getStackInSlot(2).isEmpty()) {
				GlStateManager.pushMatrix();
				double yUp = 0.25D;
				if (te.getStackInSlot(2).getItem() instanceof ItemBlock)
					yUp = 0.29D;
				GlStateManager.translate(x + 0.5D, y + yUp, z + 0.5D);
				GlStateManager.rotate(180, 1, 0, 0);
				int items = te.getStackInSlot(2).getCount() / 2 + 1;
				for (int i = 0; i < items; i++) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(rand.nextDouble() / 2.0D - 1.0D / 4.0D, 0.0D, rand.nextDouble() / 2.0D - 1.0D / 4.0D);
					GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 1, 0, 0);
					GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 0, 0, 1);
					GlStateManager.scale(0.25D, 0.25D, 0.25D);
					GlStateManager.rotate(90, 1, 0, 0);
					GlStateManager.rotate((float) rand.nextDouble() * 360.0F, 0, 0, 1);
					ItemStack stack = te.getStackInSlot(2);
					renderItem.renderItem(stack, TransformType.FIXED);
					GlStateManager.popMatrix();
				}
				GlStateManager.popMatrix();
			}
		}
	}

	private double getItemOffsetX(int index) {
		switch (index) {
		case 3:
			return 0F;
		case 2:
		default:
			return -0.25F;
		case 1:
			return 0F;
		case 0:
			return 0.25F;
		}
	}
	
	private double getItemOffsetZ(int index) {
		switch (index) {
		case 3:
			return -0.25F;
		case 2:
		default:
			return 0F;
		case 1:
			return 0.25F;
		case 0:
			return 0F;
		}
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
}
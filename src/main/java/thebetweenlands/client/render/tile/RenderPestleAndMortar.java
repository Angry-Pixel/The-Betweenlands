package thebetweenlands.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelPestleAndMortar;
import thebetweenlands.common.tile.TileEntityMortar;

@SideOnly(Side.CLIENT)
public class RenderPestleAndMortar extends TileEntitySpecialRenderer<TileEntityMortar> {

	private final ModelPestleAndMortar model = new ModelPestleAndMortar();
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/pestle_and_mortar.png");

	@Override
	public void render(TileEntityMortar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		int meta = 0;
		if(te != null) {
			meta = te.getBlockMetadata();
		}

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);

		switch (meta) {
		case 2:
			GlStateManager.rotate(180F, 0.0F, 1F, 0F);
			break;
		case 3:
			GlStateManager.rotate(0F, 0.0F, 1F, 0F);
			break;
		case 4:
			GlStateManager.rotate(90F, 0.0F, 1F, 0F);
			break;
		case 5:
			GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
			break;
		}

		model.render();

		if(te != null && te.hasPestle) {
			GlStateManager.pushMatrix();
			float rise = (float)(te.progress -42F) * -0.03F;
			if(te.progress <= 42)
				GlStateManager.rotate(te.progress * 4.2857142857142857142857142857143F * 2F, 0.0F, 1F, 0F);
			if(te.progress > 42 && te.progress < 54)
				GlStateManager.translate(0F, rise, 0F);
			if(te.progress >= 54 && te.progress < 64)
				GlStateManager.translate(0F, -0.63F - rise, 0F);
			if(te.progress >= 64 && te.progress < 74)
				GlStateManager.translate(0F, 0.63F + rise, 0F);
			if(te.progress >= 74 && te.progress < 84)
				GlStateManager.translate(0F, -1.23F - rise, 0F);
			model.renderPestle();
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();

		if (te != null && !te.getStackInSlot(3).isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + 1.43D, z + 0.5D);
			GlStateManager.scale(0.15D, 0.15D, 0.15D);
			GlStateManager.translate(0D, te.itemBob * 0.01F, 0D);
			GlStateManager.rotate(te.crystalRotation, 0, 1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(te.getStackInSlot(3), ItemCameraTransforms.TransformType.GROUND);
			GlStateManager.popMatrix();
		}
	}
}

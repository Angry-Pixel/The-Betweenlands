package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelPuffshroom;
import thebetweenlands.common.tile.TileEntityPuffshroom;

@SideOnly(Side.CLIENT)
public class TileEntityPuffshroomRenderer extends TileEntitySpecialRenderer<TileEntityPuffshroom> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/puffshroom.png");

	private final ModelPuffshroom model = new ModelPuffshroom();

	@Override
	public void render(TileEntityPuffshroom tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (tile.animation_1 != 0) {
			bindTexture(TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x + 0.5F, (float) y + 1.89F, (float) z + 0.5F);
			GlStateManager.scale(-1, -1, 1);
			model.render(tile, partialTicks);
			GlStateManager.popMatrix();
		}
	}
}
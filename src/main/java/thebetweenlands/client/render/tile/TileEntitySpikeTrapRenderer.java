package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelSpikeBlock;
import thebetweenlands.common.tile.TileEntitySpikeTrap;

@SideOnly(Side.CLIENT)
public class TileEntitySpikeTrapRenderer extends TileEntitySpecialRenderer<TileEntitySpikeTrap> {
	private static final ResourceLocation ACTIVE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spikeBlockActive.png");
	private static final ResourceLocation INACTIVE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spikeBlockInactive.png");
	private final ModelSpikeBlock model = new ModelSpikeBlock();

	@Override
	public final void renderTileEntityAt(TileEntitySpikeTrap tile, double x, double y, double z, float partialTicks, int destroyStage) {
		if (tile.type == 1)
			bindTexture(ACTIVE_TEXTURE);
		else
			bindTexture(INACTIVE_TEXTURE);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);
		GlStateManager.disableCull();
		model.renderSpikes(tile, partialTicks);
		GlStateManager.enableCull();;
		GlStateManager.popMatrix();

	}
}
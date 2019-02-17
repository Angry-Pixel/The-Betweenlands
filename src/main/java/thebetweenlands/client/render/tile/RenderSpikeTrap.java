package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.tile.ModelSpikeBlock;
import thebetweenlands.common.tile.TileEntitySpikeTrap;

@OnlyIn(Dist.CLIENT)
public class RenderSpikeTrap extends TileEntitySpecialRenderer<TileEntitySpikeTrap> {
	private static final ResourceLocation ACTIVE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spike_block_active.png");
	private static final ResourceLocation INACTIVE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spike_block_inactive.png");
	private static final ModelSpikeBlock MODEL = new ModelSpikeBlock();

	@Override
	public void render(TileEntitySpikeTrap tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (tile.type == 1)
			bindTexture(ACTIVE_TEXTURE);
		else
			bindTexture(INACTIVE_TEXTURE);

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(-1, -1, 1);
		GlStateManager.disableCull();
		MODEL.renderSpikes(tile, partialTicks);
		GlStateManager.enableCull();;
		GlStateManager.popMatrix();
	}
}
package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelSpikeBlock;
import thebetweenlands.common.tile.TileEntitySpikeTrap;

@SideOnly(Side.CLIENT)
public class TileEntitySpikeTrapRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation ACTIVE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spikeBlockActive.png");
	private static final ResourceLocation INACTIVE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spikeBlockInactive.png");
	private final ModelSpikeBlock model = new ModelSpikeBlock();

	@Override
	public final void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage) {
		TileEntitySpikeTrap spikes = (TileEntitySpikeTrap) tile;
		int brightness = 0;
		brightness = spikes.getWorld().getLightFor(EnumSkyBlock.SKY, spikes.getPos());
		int lightmapX = brightness % 65536;
		int lightmapY = brightness / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX / 1.0F, (float)lightmapY / 1.0F);

		if (spikes.type == 1)
			bindTexture(ACTIVE_TEXTURE);
		else
			bindTexture(INACTIVE_TEXTURE);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScaled(-1, -1, 1);
		GL11.glDisable(GL11.GL_CULL_FACE);
		model.renderSpikes(spikes);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();

	}
}
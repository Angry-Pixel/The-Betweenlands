package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelDungeonSpoopLayer;
import thebetweenlands.common.block.structure.BlockSpikeTrap;
import thebetweenlands.common.tile.TileEntityMudBricksSpikeTrap;
import thebetweenlands.common.tile.TileEntitySpikeTrap;
import thebetweenlands.util.StatePropertyHelper;

@SideOnly(Side.CLIENT)
public class RenderMudBrickSpikeTrap extends RenderSpikeTrap {
	private static final ResourceLocation SPOOP_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/mud_brick_spike_block_spoop_layer.png");
	private static final ModelDungeonSpoopLayer MODEL_SPOOP = new ModelDungeonSpoopLayer();

	public RenderMudBrickSpikeTrap() {
		super(new ResourceLocation("thebetweenlands:mud_brick_spike_trap"), new ResourceLocation("thebetweenlands:textures/tiles/spike_block_spikes_2.png"));
	}

	@Override
	public void renderTileEntityFast(TileEntitySpikeTrap te, double x, double y, double z, float partialTicks,
			int destroyStage, float partial, BufferBuilder buffer) {
		super.renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, partial, buffer);

		TileEntityMudBricksSpikeTrap tile = (TileEntityMudBricksSpikeTrap) te;

		if (tile.spoopAnimationTicks > 0) {
			EnumFacing facing = StatePropertyHelper.getStatePropertySafely(tile, BlockSpikeTrap.class, BlockSpikeTrap.FACING, EnumFacing.UP);

			if(tile.getWorld() != null) {
				RenderHelper.enableStandardItemLighting();
				int i = tile.getWorld().getCombinedLight(tile.getPos().offset(facing), 0);
				int j = i % 65536;
				int k = i / 65536;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
			GlStateManager.scale(-1, -1, 1);

			switch (facing) {
			case UP:
				GlStateManager.rotate(180F, 0.0F, 1F, 0F);
				break;
			case DOWN:
				GlStateManager.rotate(180F, 1F, 0F, 0F);
				break;
			case NORTH:
				GlStateManager.rotate(90F, 1F, 0F, 0F);
				break;
			case SOUTH:
				GlStateManager.rotate(-90F, 1.0F, 0F, 0F);
				GlStateManager.rotate(180F, 0.0F, 1F, 0F);
				break;
			case WEST:
				GlStateManager.rotate(90F, 0.0F, 0F, 1F);
				GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
				break;
			case EAST:
				GlStateManager.rotate(-90F, 0.0F, 0F, 1F);
				GlStateManager.rotate(90F, 0.0F, 1F, 0F);
				break;
			}

			GlStateManager.translate(0F, -1F, 0F);
			GlStateManager.disableCull();
			this.bindTexture(SPOOP_TEXTURE);
			MODEL_SPOOP.renderSpoop(tile, partialTicks);
			GlStateManager.enableCull();;
			GlStateManager.popMatrix();
		}
	}
}
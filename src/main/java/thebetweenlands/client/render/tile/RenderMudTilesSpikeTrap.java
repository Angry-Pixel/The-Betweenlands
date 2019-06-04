package thebetweenlands.client.render.tile;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMudTilesSpikeTrap extends RenderSpikeTrap {
	public RenderMudTilesSpikeTrap() {
		super(new ResourceLocation("thebetweenlands:mud_tiles_spike_trap"), new ResourceLocation("thebetweenlands:textures/tiles/spike_block_spikes_2.png"));
	}
}
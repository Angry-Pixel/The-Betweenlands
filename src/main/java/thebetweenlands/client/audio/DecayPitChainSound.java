package thebetweenlands.client.audio;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityDecayPitHangingChain;

public class DecayPitChainSound extends TileEntitySound<TileEntityDecayPitHangingChain> {
	public DecayPitChainSound(TileEntity tile) {
		super(SoundRegistry.CHAIN_LONG, SoundCategory.HOSTILE, (TileEntityDecayPitHangingChain)tile, TileEntityDecayPitHangingChain::isSlow);
		repeat = false;
	}
	@Override
	public float getVolume() {
		return 1F;
	}
	
	@Override
	public float getPitch() {
		return 1.2F;
	}
}

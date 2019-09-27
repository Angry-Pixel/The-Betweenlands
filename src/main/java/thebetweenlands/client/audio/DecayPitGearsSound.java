package thebetweenlands.client.audio;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityDecayPitControl;

public class DecayPitGearsSound extends TileEntitySound<TileEntityDecayPitControl> {
	public DecayPitGearsSound(TileEntity tile) {
		super(SoundRegistry.GEARS, SoundCategory.HOSTILE, (TileEntityDecayPitControl)tile, TileEntityDecayPitControl::isUnPlugged);
		repeat = true;
	}
	@Override
	public float getVolume() {
		return 1F;
	}

	@Override
	public float getPitch() {
		return 1F;
	}
}

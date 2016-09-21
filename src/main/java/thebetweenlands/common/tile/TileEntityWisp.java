package thebetweenlands.common.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import thebetweenlands.common.TheBetweenlands;

public class TileEntityWisp extends TileEntity implements ITickable {
	public long lastSpawn = 0;
	public final List<Object> particleList = new ArrayList<Object>();

	@Override
	public void update() {
		TheBetweenlands.proxy.updateWispParticles(this);
	}
}

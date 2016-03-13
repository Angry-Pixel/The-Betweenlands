package thebetweenlands.tileentities;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import thebetweenlands.TheBetweenlands;

public class TileEntityWisp extends TileEntity { 
	public long lastSpawn = 0;
	public final ArrayList<Object> particleList = new ArrayList<Object>();

	@Override
	public void updateEntity() {
		TheBetweenlands.proxy.updateWispParticles(this);
	}
}

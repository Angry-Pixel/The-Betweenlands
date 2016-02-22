package thebetweenlands.tileentities;

import net.minecraft.tileentity.TileEntityMobSpawner;

public class TileEntityBLSpawner extends TileEntityMobSpawner {
	public float counter = 0.0F;
	public float lastCounter = 0.0F;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		this.lastCounter = this.counter;
		this.counter += 0.0085F;
    }
}

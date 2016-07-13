package thebetweenlands.common.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import thebetweenlands.common.TheBetweenlands;

import java.util.ArrayList;

public class TileEntityWisp extends TileEntity implements ITickable{
    public long lastSpawn = 0;
    public final ArrayList<Object> particleList = new ArrayList<Object>();

    @Override
    public void update() {
        TheBetweenlands.proxy.updateWispParticles(this);
    }
}

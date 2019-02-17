package thebetweenlands.common.tile;

import net.minecraft.tileentity.TileEntityBed;

public class TileEntityMossBed extends TileEntityBed {
	//TODO 1.13 Bed can't extend TileEntityBed because TileEntityType is hardcoded...
	
    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }
}

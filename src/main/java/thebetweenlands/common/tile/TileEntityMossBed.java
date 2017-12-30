package thebetweenlands.common.tile;

import net.minecraft.tileentity.TileEntityBed;

public class TileEntityMossBed extends TileEntityBed {

    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }
}

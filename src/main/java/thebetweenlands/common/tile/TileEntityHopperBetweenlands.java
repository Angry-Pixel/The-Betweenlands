package thebetweenlands.common.tile;

import net.minecraft.tileentity.TileEntityHopper;

public class TileEntityHopperBetweenlands extends TileEntityHopper {

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "tile.thebetweenlands.syrmorite_hopper.name";
    }
}

package thebetweenlands.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;

public class TileEntityHopperBetweenlands extends TileEntityHopper {
    private String customName;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.customName : "thebetweenlands.container.syrmorite_hopper";
    }

    @Override
    public String getGuiID() {
        return "thebetweenands:syrmorite_hopper";
    }
}

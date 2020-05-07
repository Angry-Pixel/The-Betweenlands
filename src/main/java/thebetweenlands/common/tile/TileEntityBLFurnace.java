package thebetweenlands.common.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thebetweenlands.common.block.container.BlockBLFurnace;

public class TileEntityBLFurnace extends TileEntityAbstractBLFurnace {

    public TileEntityBLFurnace() {
        super("container.bl.sulfur_furnace", 1);
    }
    
    protected TileEntityBLFurnace(String name, NonNullList<ItemStack> inventory) {
        super(name, inventory);
    }

    @Override
    protected void updateState(boolean active) {
        BlockBLFurnace.setState(active, world, pos);
    }
}

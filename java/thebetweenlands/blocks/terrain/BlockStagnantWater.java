package thebetweenlands.blocks.terrain;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import thebetweenlands.blocks.BLFluidRegistry;

/**
 * Created by Bart on 29-8-2015.
 */
public class BlockStagnantWater extends BlockFluidClassic {
    public BlockStagnantWater() {
        super(BLFluidRegistry.stagnantWater, Material.water);
        setBlockName("thebetweenlands.stagnantWater");
    }
}

package thebetweenlands.common.block.container;

import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityHopperBetweenlands;
import thebetweenlands.util.AdvancedStateMap;

public class BlockHopperBetweenlands extends BlockHopper implements BlockRegistry.IStateMappedBlock {
	public BlockHopperBetweenlands() {
		super();
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setHardness(3.0F);
		this.setResistance(8.0F);
		this.setSoundType(SoundType.METAL);
	}
	
    @Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHopperBetweenlands();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setStateMapper(AdvancedStateMap.Builder builder) {
        builder.ignore(ENABLED);
    }
}

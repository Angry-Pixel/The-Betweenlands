package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.TileEntitySpikeTrap;

public class BlockSpikeTrap extends BlockContainer {

	public BlockSpikeTrap() {
		super(Material.ROCK);
		setSoundType(SoundType.STONE);
		setHardness(10F);
		setResistance(2000.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntitySpikeTrap();
	}

	@Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}

}
package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getItemsToDropCount(IBlockState state, int fortune, World worldIn, BlockPos pos, Random random) {
		return 0;
	}	
}
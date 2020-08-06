package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockSulfurScrivenerMark extends BlockScrivenerMark {
	public static final PropertyBool BURNING = PropertyBool.create("burning");

	public BlockSulfurScrivenerMark() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(BURNING, false).withProperty(NORTH_SIDE, false).withProperty(EAST_SIDE, false).withProperty(SOUTH_SIDE, false).withProperty(WEST_SIDE, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { BURNING, NORTH_SIDE, EAST_SIDE, SOUTH_SIDE, WEST_SIDE }, new IUnlistedProperty[0]));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BURNING) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BURNING, meta == 1);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ItemRegistry.ITEMS_MISC;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return EnumItemMisc.SULFUR.getID();
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

		if(!worldIn.isRemote && worldIn.getBlockState(fromPos).getBlock() == Blocks.FIRE) {
			this.setOnFire(worldIn, pos, state);
		}
	}

	public void setOnFire(World world, BlockPos pos, IBlockState state) {
		world.setBlockState(pos, state.withProperty(BURNING, true));
		world.scheduleUpdate(pos, state.getBlock(), 1);
		world.addBlockEvent(pos, this, 10, 0);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(state.getValue(BURNING)) {
			for(EnumFacing offset : EnumFacing.HORIZONTALS) {
				for(int yo = -1; yo <= 1; yo++) {
					BlockPos offsetPos = pos.add(offset.getXOffset(), offset.getYOffset() + yo, offset.getZOffset());

					IBlockState offsetState = worldIn.getBlockState(offsetPos);

					if(offsetState.getBlock() instanceof BlockSulfurScrivenerMark) {
						((BlockSulfurScrivenerMark) offsetState.getBlock()).setOnFire(worldIn, offsetPos, offsetState);
					}
				}
			}

			worldIn.addBlockEvent(pos, this, 10, 1);

			worldIn.setBlockState(pos, BlockRegistry.SCRIVENER_BURNT_MARK.getDefaultState());
		}
	}

	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		if(worldIn.isRemote && id == 10) {
			for(int i = 0; i < 3; ++i) {
				double d0 = (double)pos.getX() + worldIn.rand.nextDouble();
				double d1 = (double)pos.getY() + worldIn.rand.nextDouble() * 0.5D;
				double d2 = (double)pos.getZ() + worldIn.rand.nextDouble();
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(stateIn.getValue(BURNING)) {
			for(int i = 0; i < 3; ++i) {
				double d0 = (double)pos.getX() + rand.nextDouble();
				double d1 = (double)pos.getY() + rand.nextDouble() * 0.5D;
				double d2 = (double)pos.getZ() + rand.nextDouble();
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}
}

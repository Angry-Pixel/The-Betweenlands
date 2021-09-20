package thebetweenlands.common.block.misc;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;

public class BlockMistBridge extends Block implements IStateMappedBlock {
	
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool SOLID = PropertyBool.create("solid");

	public BlockMistBridge(Material materialIn) {
		super(materialIn);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setSoundType(SoundType.STONE);
		setHardness(1.2F);
		setResistance(8.0F);
		setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, false)
				.withProperty(EAST, false)
				.withProperty(SOUTH, false)
				.withProperty(WEST, false)
				.withProperty(SOLID, true));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		if(!state.getValue(SOLID))
			return NULL_AABB;
		return FULL_BLOCK_AABB;
	}

	@Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockAccess.getBlockState(pos.offset(side)).getBlock() != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.AIR;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		PooledMutableBlockPos offset = PooledMutableBlockPos.retain();
		PooledMutableBlockPos offsetDown = PooledMutableBlockPos.retain();

		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			offset.setPos(pos.getX() + facing.getXOffset(), pos.getY(), pos.getZ() + facing.getZOffset());
			IBlockState offsetState = worldIn.getBlockState(offset);

			offsetDown.setPos(pos.getX() + facing.getXOffset(), pos.getY() - 1, pos.getZ() + facing.getZOffset());
			IBlockState offsetDownState = worldIn.getBlockState(offsetDown);

			PropertyBool prop;
			switch(facing) {
			default:
			case NORTH:
				prop = NORTH;
				break;
			case EAST:
				prop = EAST;
				break;
			case SOUTH:
				prop = SOUTH;
				break;
			case WEST:
				prop = WEST;
				break;
			}

			state = state.withProperty(prop, offsetState.getBlock() instanceof BlockMistBridge == false);// && offsetDownState.isSideSolid(worldIn, offsetDown, EnumFacing.UP) && offsetDownState.getBlockFaceShape(worldIn, offsetDown, EnumFacing.UP) == BlockFaceShape.SOLID);
		}

		offset.release();
		offsetDown.release();

		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { NORTH, EAST, SOUTH, WEST, SOLID });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(SOLID, meta == 0 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(SOLID) ? 0 : 1;
	}

	@Override
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(SOLID);
	}

	@Override
    public void onPlayerDestroy(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote)
			world.setBlockState(pos, getDefaultState().withProperty(SOLID, false), 3);
    }

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (world.isAirBlock(pos.up())) {
			for(int i = 0; i < 3 + rand.nextInt(5); i++) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(world, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F, 
						ParticleArgs.get()
						.withMotion((rand.nextFloat() - 0.5f) * 0.08f, rand.nextFloat() * 0.01F + 0.01F, (rand.nextFloat() - 0.5f) * 0.08f)
						.withScale(1f + rand.nextFloat() * 8.0F)
						.withColor(1F, 1.0F, 1.0F, 0.05f)
						.withData(80, true, 0.01F, true)));
			}
		}
	}
}

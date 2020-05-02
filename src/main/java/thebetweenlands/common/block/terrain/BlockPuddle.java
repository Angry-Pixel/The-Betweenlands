package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.farming.BlockGenericCrop;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.util.AdvancedStateMap;

public class BlockPuddle extends Block implements ITintedBlock, IStateMappedBlock {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D/16.0D, 1.0D);
	public final static PropertyInteger AMOUNT = PropertyInteger.create("amount", 0, 15);

	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");

	public BlockPuddle() {
		super(Material.GROUND);
		setHardness(0.1F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setTickRandomly(true);
		setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, false)
				.withProperty(EAST, false)
				.withProperty(SOUTH, false)
				.withProperty(WEST, false)
				.withProperty(AMOUNT, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AMOUNT, NORTH, EAST, SOUTH, WEST });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AMOUNT, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(AMOUNT);
	}

	@Override
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(AMOUNT);
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

			state = state.withProperty(prop, offsetState.getBlock() instanceof BlockPuddle == false && offsetDownState.isSideSolid(worldIn, offsetDown, EnumFacing.UP) && offsetDownState.getBlockFaceShape(worldIn, offsetDown, EnumFacing.UP) == BlockFaceShape.SOLID);
		}

		offset.release();
		offsetDown.release();

		return state;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if(!world.isRemote) {
			int amount = state.getValue(AMOUNT);
			if(!BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().heavyRain.isActive()) {
				world.setBlockToAir(pos);
				amount = 0;
			} else if(world.canBlockSeeSky(pos)) {
				amount = Math.min(amount + rand.nextInt(6), 15);
				world.setBlockState(pos, state.withProperty(AMOUNT, amount), 2);
			}
			if(amount > 2) {
				amount = Math.max(0, amount - 3);
				world.setBlockState(pos, state.withProperty(AMOUNT, amount), 2);
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						BlockPos newPos = pos.add(xo, 0, zo);
						if((xo == 0 && zo == 0) || xo*xo == zo*zo) continue;
						if((world.isAirBlock(newPos) || world.getBlockState(newPos).getBlock() instanceof BlockGenericCrop) && this.canPlaceBlockAt(world, newPos)) {
							world.setBlockState(newPos, getDefaultState());
						} else if(world.getBlockState(newPos).getBlock() == BlockRegistry.PUDDLE) {
							world.setBlockState(newPos, state.withProperty(AMOUNT, Math.min(amount + rand.nextInt(6), 15)), 2);
						}
					}
				}
			}
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (blockAccess.getBlockState(pos.offset(side)).getBlock() == this) {
			return false;
		} else {
			return side == EnumFacing.UP || side == EnumFacing.DOWN || super.shouldSideBeRendered(blockState, blockAccess, pos, side);
		}
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
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return (block.isReplaceable(world, pos) || block instanceof BlockGenericCrop) && world.isSideSolid(pos.down(), EnumFacing.UP) && world.getBlockState(pos.down()).getBlockFaceShape(world, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP)) {
			world.setBlockToAir(pos);
		}
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		if (worldIn == null || pos == null) return -1;
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;

		for (int xOff = -1; xOff <= 1; ++xOff) {
			for (int yOff = -1; yOff <= 1; ++yOff) {
				int colorMultiplier = worldIn.getBiome(pos.add(xOff, 0, yOff)).getWaterColorMultiplier();
				avgRed += (colorMultiplier & 16711680) >> 16;
			avgGreen += (colorMultiplier & 65280) >> 8;
					avgBlue += colorMultiplier & 255;
			}
		}

		return (avgRed / 9 & 255) << 16 | (avgGreen / 9 & 255) << 8 | avgBlue / 9 & 255;
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
		return false;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		IBlockState stateBelow = world.getBlockState(pos.down());
		return stateBelow.getBlock().getSoundType(stateBelow, world, pos.down(), entity);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if(entityIn.world.isRemote && entityIn instanceof EntityPlayer && entityIn.posY <= pos.getY() + 0.01f && entityIn.ticksExisted % 5 == 0) {
			float strength = MathHelper.sqrt(entityIn.motionX * entityIn.motionX * 0.2D + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ * 0.2D) * 0.2f;

			if(strength > 0.01f) {
				entityIn.playSound(SoundEvents.ENTITY_GENERIC_SWIM, strength * 8, 1.0F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.4F);

				for(int j = 0; (float)j < 10.0F + entityIn.width * 20.0F; ++j) {
					float rx = (worldIn.rand.nextFloat() * 2.0F - 1.0F) * entityIn.width;
					float rz = (worldIn.rand.nextFloat() * 2.0F - 1.0F) * entityIn.width;
					worldIn.spawnParticle(EnumParticleTypes.WATER_SPLASH, entityIn.posX + rx, pos.getY() + 0.1f, entityIn.posZ + rz, entityIn.motionX + (worldIn.rand.nextFloat() - 0.5f) * strength * 20, entityIn.motionY, entityIn.motionZ + (worldIn.rand.nextFloat() - 0.5f) * strength * 20);
				}
			}
		}
	}
}

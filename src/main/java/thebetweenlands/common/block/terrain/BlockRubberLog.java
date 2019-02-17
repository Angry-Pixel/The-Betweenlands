package thebetweenlands.common.block.terrain;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistryOld.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;

public class BlockRubberLog extends BlockLog implements IStateMappedBlock {
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

	protected static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[] {
			//CENTER
			new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D),
			//NORTH
			new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 0.25D),
			//SOUTH
			new AxisAlignedBB(0.25D, 0.25D, 0.75D, 0.75D, 0.75D, 1.0D),
			//EAST
			new AxisAlignedBB(0.75D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D),
			//WEST
			new AxisAlignedBB(0.0D, 0.25D, 0.25D, 0.25D, 0.75D, 0.75D),
			//UP
			new AxisAlignedBB(0.25D, 0.75D, 0.25D, 0.75D, 1.0D, 0.75D),
			//DOWN
			new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.25D, 0.75D),
	};
	protected static final AxisAlignedBB[] COMBINED_BOUNDING_BOXES = new AxisAlignedBB[64];

	static {
		List<AxisAlignedBB> boxes = new ArrayList<AxisAlignedBB>();
		for(int i = 0; i < 64; i++) {
			boolean north = (i & 1) == 1;
			boolean south = ((i >> 1) & 1) == 1;
			boolean east = ((i >> 2) & 1) == 1;
			boolean west = ((i >> 3) & 1) == 1;
			boolean up = ((i >> 4) & 1) == 1;
			boolean down = ((i >> 5) & 1) == 1;
			boxes.clear();
			boxes.add(BOUNDING_BOXES[0]);
			if(north)
				boxes.add(BOUNDING_BOXES[1]);
			if(south)
				boxes.add(BOUNDING_BOXES[2]);
			if(east)
				boxes.add(BOUNDING_BOXES[3]);
			if(west)
				boxes.add(BOUNDING_BOXES[4]);
			if(up)
				boxes.add(BOUNDING_BOXES[5]);
			if(down)
				boxes.add(BOUNDING_BOXES[6]);
			double minX = 1.0D;
			double minY = 1.0D;
			double minZ = 1.0D;
			double maxX = 0.0D;
			double maxY = 0.0D;
			double maxZ = 0.0D;
			for(AxisAlignedBB box : boxes) {
				if(box.minX < minX)
					minX = box.minX;
				if(box.minY < minY)
					minY = box.minY;
				if(box.minZ < minZ)
					minZ = box.minZ;
				if(box.maxX > maxX)
					maxX = box.maxX;
				if(box.maxY > maxY)
					maxY = box.maxY;
				if(box.maxZ > maxZ)
					maxZ = box.maxZ;
			}
			COMBINED_BOUNDING_BOXES[i] = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
		}
	}

	public static AxisAlignedBB getCombinedBoundingBoxForState(IBlockState state) {
		int index = 0;
		if(state.get(NORTH))
			index |= 1;
		if(state.get(SOUTH))
			index |= 2;
		if(state.get(EAST))
			index |= 4;
		if(state.get(WEST))
			index |= 8;
		if(state.get(UP))
			index |= 16;
		if(state.get(DOWN))
			index |= 32;
		return COMBINED_BOUNDING_BOXES[index];
	}

	public BlockRubberLog() {
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setHarvestLevel("axe", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().with(NATURAL, false));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().with(NATURAL, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.get(NATURAL) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { LOG_AXIS, UP, DOWN, NORTH, SOUTH, EAST, WEST, NATURAL });
	}

	@Override
	public boolean rotateBlock(net.minecraft.world.World world, BlockPos pos, EnumFacing axis) {
		return false;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IWorldReader worldIn, BlockPos pos) {
		return state
				.with(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north())))
				.with(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east())))
				.with(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south())))
				.with(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west())))
				.with(UP, Boolean.valueOf(this.canConnectTo(worldIn, pos.up())))
				.with(DOWN, Boolean.valueOf(worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) || this.canConnectTo(worldIn, pos.down())));
	}

	public boolean canConnectTo(IWorldReader worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		return block == this || block == BlockRegistry.LEAVES_RUBBER_TREE;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		switch (rot) {
		case CLOCKWISE_180:
			return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
		case COUNTERCLOCKWISE_90:
			return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
		case CLOCKWISE_90:
			return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
		default:
			return state;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		switch (mirrorIn) {
		case LEFT_RIGHT:
			return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
		case FRONT_BACK:
			return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
		default:
			return super.withMirror(state, mirrorIn);
		}
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
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		state = state.getActualState(worldIn, pos);

		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[0]);

		if(state.get(NORTH))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[1]);

		if(state.get(SOUTH))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[2]);

		if(state.get(EAST))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[3]);

		if(state.get(WEST))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[4]);

		if(state.get(UP))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[5]);

		if(state.get(DOWN))
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOXES[6]);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
		state = this.getActualState(state, source, pos);
		return getCombinedBoundingBoxForState(state);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(LOG_AXIS).ignore(NATURAL).withPropertySuffixFalse(NATURAL, "cut");
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().with(NATURAL, placer instanceof EntityPlayer && ((EntityPlayer)placer).isCreative());
	}
}

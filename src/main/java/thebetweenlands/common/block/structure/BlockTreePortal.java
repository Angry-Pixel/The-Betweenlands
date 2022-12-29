package thebetweenlands.common.block.structure;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationPortal;
import thebetweenlands.common.world.teleporter.TeleporterHandler;

public class BlockTreePortal extends BasicBlock implements ICustomItemBlock {
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis", EnumFacing.Axis.class, new EnumFacing.Axis[]{EnumFacing.Axis.X, EnumFacing.Axis.Z});

	protected static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
	protected static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);
	protected static final AxisAlignedBB Y_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);

	public BlockTreePortal() {
		super(Material.PORTAL);
		setLightLevel(1.0F);
		setBlockUnbreakable();
		setSoundType2(SoundType.GLASS);
		setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X));
		setCreativeTab(null);
	}

	public static boolean makePortalX(World world, BlockPos pos) {
		world.setBlockState(pos.add(0, 2, -1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_RIGHT).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, 2, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.TOP).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, 2, 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_LEFT).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, 1, -1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, 1, 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, 0, -1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, 0, 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, -1, -1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_RIGHT).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, -1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.BOTTOM).withProperty(BlockPortalFrame.X_AXIS, true));
		world.setBlockState(pos.add(0, -1, 1), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_LEFT).withProperty(BlockPortalFrame.X_AXIS, true));

		if (isPatternValidX(world, pos)) {
			world.setBlockState(pos, BlockRegistry.TREE_PORTAL.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Z), 2);
			world.setBlockState(pos.up(), BlockRegistry.TREE_PORTAL.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Z), 2);
			return true;
		}
		return false;
	}

	public static boolean makePortalZ(World world, BlockPos pos) {
		world.setBlockState(pos.add(-1, 2, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_RIGHT).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(0, 2, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.TOP).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(1, 2, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_TOP_LEFT).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(-1, 1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(1, 1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(-1, 0, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_RIGHT).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(1, 0, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.SIDE_LEFT).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(-1, -1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_RIGHT).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(0, -1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.BOTTOM).withProperty(BlockPortalFrame.X_AXIS, false));
		world.setBlockState(pos.add(1, -1, 0), BlockRegistry.PORTAL_FRAME.getDefaultState().withProperty(BlockPortalFrame.FRAME_POSITION, BlockPortalFrame.EnumPortalFrame.CORNER_BOTTOM_LEFT).withProperty(BlockPortalFrame.X_AXIS, false));

		if (isPatternValidZ(world, pos)) {
			world.setBlockState(pos, BlockRegistry.TREE_PORTAL.getDefaultState().withProperty(AXIS, EnumFacing.Axis.X), 2);
			world.setBlockState(pos.up(), BlockRegistry.TREE_PORTAL.getDefaultState().withProperty(AXIS, EnumFacing.Axis.X), 2);
			return true;
		}

		return false;
	}

	public static boolean isPatternValidX(IBlockAccess world, BlockPos pos) {
		// Layer 0
		if (!check(world, pos.down(), BlockRegistry.PORTAL_FRAME) && !checkPortal(world, pos.down(), BlockRegistry.TREE_PORTAL, EnumFacing.Axis.Z)) {
			return false;
		}

		// Layer 1
		if (!check(world, pos.north(), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}
		if (!check(world, pos.south(), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}

		// Layer 2
		if (!check(world, pos.up().north(), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}
		if (!check(world, pos.up().south(), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}

		// Layer 3
		if (!check(world, pos.up(2), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}

		return true;
	}

	public static boolean isPatternValidZ(IBlockAccess world, BlockPos pos) {
		// Layer 0
		if (!check(world, pos.down(), BlockRegistry.PORTAL_FRAME) && !checkPortal(world, pos.down(), BlockRegistry.TREE_PORTAL, EnumFacing.Axis.X)) {
			return false;
		}

		// Layer 1
		if (!check(world, pos.west(), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}
		if (!check(world, pos.east(), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}

		// Layer 2
		if (!check(world, pos.up().west(), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}
		if (!check(world, pos.up().east(), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}

		// Layer 3
		if (!check(world, pos.up(2), BlockRegistry.PORTAL_FRAME)) {
			return false;
		}

		return true;
	}

	private static boolean check(IBlockAccess world, BlockPos pos, Block target) {
		return world.getBlockState(pos).getBlock() == target;
	}

	private static boolean checkPortal(IBlockAccess world, BlockPos pos, Block target, EnumFacing.Axis axis) {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() == target && state.getValue(AXIS) == axis;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch ((EnumFacing.Axis) state.getValue(AXIS)) {
		case X:
			return X_AABB;
		case Y:
		default:
			return Y_AABB;
		case Z:
			return Z_AABB;
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		canBlockStay(worldIn, pos);
	}

	protected boolean canBlockStay(World world, BlockPos pos) {
		if (checkPortal(world, pos.up(), BlockRegistry.TREE_PORTAL, EnumFacing.Axis.Z) && isPatternValidX(world, pos))
			return true;
		if (checkPortal(world, pos.down(), BlockRegistry.TREE_PORTAL, EnumFacing.Axis.Z) && isPatternValidX(world, pos.down()))
			return true;
		if (checkPortal(world, pos.up(), BlockRegistry.TREE_PORTAL, EnumFacing.Axis.X) && isPatternValidZ(world, pos))
			return true;
		if (checkPortal(world, pos.down(), BlockRegistry.TREE_PORTAL, EnumFacing.Axis.X) && isPatternValidZ(world, pos.down()))
			return true;
		else {
			world.playEvent(null, 2001, pos, Block.getIdFromBlock(BlockRegistry.TREE_PORTAL));
			world.setBlockToAir(pos);
		}
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{AXIS});
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumFacing.Axis axis = state.getValue(AXIS);
		return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}


	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!entityIn.isRiding() && !entityIn.isBeingRidden() && entityIn.timeUntilPortal <= 0 && BetweenlandsConfig.WORLD_AND_DIMENSION.portalDimensionWhitelistSet.isListed(entityIn.dimension)) {
			AxisAlignedBB aabb = state.getBoundingBox(worldIn, pos);
			if (aabb != null && aabb.offset(pos).intersects(entityIn.getEntityBoundingBox())) {
				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(worldIn);
				AxisAlignedBB entityAabb = entityIn.getEntityBoundingBox();
				List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, entityAabb, loc -> loc.intersects(entityAabb));
				LocationPortal portal = null;
				if(!portals.isEmpty()) {
					portal = portals.get(0);
				}
				int targetDim = BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId;
				if(portal != null && (portal.getOtherPortalPosition() != null || portal.hasTargetDimension())) {
					//Portal already linked, teleport to linked dimension
					targetDim = portal.getOtherPortalDimension();
				} else if (entityIn.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
					targetDim = BetweenlandsConfig.WORLD_AND_DIMENSION.portalDefaultReturnDimension;
				}
				if(targetDim != entityIn.dimension) {
					IPortalCapability cap = entityIn.getCapability(CapabilityRegistry.CAPABILITY_PORTAL, null);
					if (cap != null) {
						cap.setInPortal(true);
					} else if (!worldIn.isRemote && worldIn instanceof WorldServer) {

						WorldServer otherDim = ((WorldServer) worldIn).getMinecraftServer().getWorld(targetDim);
						if(otherDim != null) {
							TeleporterHandler.transferToDim(entityIn, otherDim);
						}
						entityIn.timeUntilPortal = entityIn.getPortalCooldown();
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return side != EnumFacing.DOWN || side != EnumFacing.UP;
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for (int i = 0; i < 4; i++) {
			double particleX = pos.getX() + rand.nextFloat();
			double particleY = pos.getY() + rand.nextFloat();
			double particleZ = pos.getZ() + rand.nextFloat();
			double motionX;
			double motionY;
			double motionZ;
			float multi = (rand.nextFloat() * 2.0F - 1.0F) / 4.0F;

			motionX = (rand.nextFloat() - 0.5D) * 0.25D;
			motionY = (rand.nextFloat() - 0.5D) * 0.25D;
			motionZ = (rand.nextFloat() - 0.5D) * 0.25D;

			if (worldIn.getBlockState(pos.add(-1, 0, 0)).getBlock() != this && worldIn.getBlockState(pos.add(1, 0, 0)).getBlock() != this) {
				particleX = pos.getX() + 0.5D + 0.25D * multi;
				motionX = rand.nextFloat() * 2.0F * multi;
			} else {
				particleZ = pos.getZ() + 0.5D + 0.25D * multi;
				motionZ = rand.nextFloat() * 2.0F * multi;
			}

			BLParticles.PORTAL.spawn(worldIn, particleX, particleY, particleZ, ParticleArgs.get().withMotion(motionX, motionY, motionZ));
		}

		if (rand.nextInt(20) == 0) {
			worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundRegistry.PORTAL, SoundCategory.BLOCKS, 0.3F, rand.nextFloat() * 0.4F + 0.8F, false);
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);

		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(worldIn);
		List<LocationPortal> portals = worldStorage.getLocalStorageHandler().getLocalStorages(LocationPortal.class, new AxisAlignedBB(pos).grow(1, 1, 1), null);
		for(LocationPortal portal : portals) {
			portal.validateAndRemove();
		}
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}

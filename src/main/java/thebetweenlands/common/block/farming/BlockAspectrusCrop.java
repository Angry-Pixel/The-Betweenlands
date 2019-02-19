package thebetweenlands.common.block.farming;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockFence;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistryOld.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityAspectrusCrop;
import thebetweenlands.common.tile.TileEntityDugSoil;

public class BlockAspectrusCrop extends BlockGenericCrop implements ICustomItemBlock, ITileEntityProvider {
	protected static final float ASPECT_FRUIT_MULTIPLIER = 0.5F;
	protected static final int ASPECT_SEEDS_DEGRADATION = 180;
	protected static final int MAX_HEIGHT = 3;
	protected static final int DECAY_CHANCE = 15;

	public BlockAspectrusCrop() {
		this.setCreativeTab(null);
		this.setMaxHeight(MAX_HEIGHT);
	}

	public void setAspect(IWorldReader world, BlockPos pos, @Nullable Aspect aspect) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityAspectrusCrop) {
			((TileEntityAspectrusCrop)tile).setAspect(aspect);
		}
	}

	@Nullable
	public Aspect getAspect(IWorldReader world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityAspectrusCrop) {
			return ((TileEntityAspectrusCrop)tile).getAspect();
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if(stack.getItem() instanceof ItemAspectVial) {
			if(this.getAspect(world, pos) == null) {
				ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(stack);
				List<Aspect> aspects = aspectContainer.getAspects();
				if(!aspects.isEmpty()) {
					Aspect aspect = aspects.get(0);
					if(!world.isRemote()) {
						this.setAspect(world, pos, aspect);
						if(!player.isCreative()) {
							player.setHeldItem(hand, stack.getItem().getContainerItem(stack));
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void tick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
		super.tick(state, worldIn, pos, rand);
		Aspect aspect = this.getAspect(worldIn, pos);
		if(aspect != null && worldIn.rand.nextInt(Math.max((int)(DECAY_CHANCE - aspect.amount / 1000.0F * 15.0F), 2)) == 0) {
			MutableBlockPos checkPos = new MutableBlockPos();
			checkPos.setPos(pos.getX(), pos.getY() - 1, pos.getZ());
			for(int i = 0; i < MAX_HEIGHT; i++) {
				IBlockState offsetState = worldIn.getBlockState(checkPos);
				if(offsetState.getBlock() instanceof BlockGenericDugSoil) {
					if(!((BlockGenericDugSoil)offsetState.getBlock()).isPurified(worldIn, checkPos, offsetState)) {
						TileEntityDugSoil te = BlockGenericDugSoil.getTile(worldIn, checkPos);
						if(te != null && !te.isFullyDecayed()) {
							te.setDecay(te.getDecay() + 5);
						}
					}
					break;
				}
				checkPos.setPos(checkPos.getX(), checkPos.getY() - 1, checkPos.getZ());
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IWorldReader worldIn, BlockPos pos) {
		return BlockFence.PILLAR_AABB;
	}

	@Override
	protected boolean removePlant(World world, BlockPos pos, EntityPlayer player, boolean canHarvest) {
		return world.setBlockState(pos, BlockRegistry.RUBBER_TREE_PLANK_FENCE.getDefaultState(), world.isRemote() ? 11 : 3);
	}

	@Override
	public int getCropDrops(IWorldReader world, BlockPos pos, Random rand, int fortune) {
		IBlockState state = world.getBlockState(pos);
		if(state.get(AGE) >= 15) {
			return 1 + rand.nextInt(3 + fortune);
		}
		return 0;
	}

	@Override
	public int getSeedDrops(IWorldReader world, BlockPos pos, Random rand, int fortune) {
		IBlockState state = world.getBlockState(pos);
		if(state.get(AGE) >= 15) {
			return 1 + (rand.nextInt(8) == 0 ? 1 : 0);
		}
		return 1;
	}

	@Override
	protected boolean canGrow(World world, BlockPos pos, IBlockState state) {
		Aspect aspect = this.getAspect(world, pos);
		return aspect != null;
	}

	@Override
	protected float getGrowthChance(World world, BlockPos pos, IBlockState state, Random rand) {
		Aspect aspect = this.getAspect(world, pos);
		return 1.0F / (1.0F + aspect.amount / 1000.0F * 8.0F);
	}

	@Override
	protected boolean canGrowUp(World world, BlockPos pos, IBlockState state, int height) {
		if(this.maxHeight == -1 || height < this.maxHeight) {
			BlockPos posUp = pos.up();
			IBlockState upState = world.getBlockState(posUp);
			if(upState.getBlock() == BlockRegistry.RUBBER_TREE_PLANK_FENCE) {
				for(EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
					if(BlockRegistry.RUBBER_TREE_PLANK_FENCE.canBeConnectedTo(world, posUp, dir)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected void growUp(World world, BlockPos pos) {
		super.growUp(world, pos);

		Aspect aspect = this.getAspect(world, pos);
		if(aspect != null) {
			BlockPos posUp = pos.up();
			IBlockState stateUp = world.getBlockState(posUp);
			if(stateUp.getBlock() instanceof BlockAspectrusCrop) {
				((BlockAspectrusCrop)stateUp.getBlock()).setAspect(world, posUp, aspect);
			}
		}
	}

	@Override
	protected IntegerProperty createStageProperty() {
		return IntegerProperty.create("stage", 0, 15);
	}

	@Override
	public ItemStack getSeedDrop(IWorldReader world, BlockPos pos, Random rand) {
		ItemStack stack = new ItemStack(ItemRegistry.ASPECTRUS_SEEDS);
		Aspect aspect = this.getAspect(world, pos);
		if(aspect != null && aspect.amount - ASPECT_SEEDS_DEGRADATION > 0) {
			ItemAspectContainer.fromItem(stack).set(aspect.type, aspect.amount - ASPECT_SEEDS_DEGRADATION);
		}
		return stack;	
	}

	@Override
	public ItemStack getCropDrop(IWorldReader world, BlockPos pos, Random rand) {
		if(!this.isDecayed(world, pos)) {
			ItemStack stack = new ItemStack(ItemRegistry.ASPECTRUS_FRUIT);
			Aspect aspect = this.getAspect(world, pos);
			if(aspect != null) {
				ItemAspectContainer.fromItem(stack).set(aspect.type, MathHelper.floor(aspect.amount * ASPECT_FRUIT_MULTIPLIER));
			}
			return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ItemRegistry.ASPECTRUS_SEEDS);
	}

	@Override
	public ItemBlock getItemBlock() {
		return null;
	}

	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
		super.onReplaced(state, worldIn, pos, newState, isMoving);
		worldIn.removeTileEntity(pos);
	}

	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityAspectrusCrop();
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int age = state.get(AGE) + (rand.nextInt(2) == 0 ? 1 : 0);
		if(age > 15) {
			age = 15;
			int height;
			for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);
			if(this.canGrowUp(worldIn, pos, state, height)) {
				this.growUp(worldIn, pos);
			}
		}
		worldIn.setBlockState(pos, state.with(AGE, age));
	}
}

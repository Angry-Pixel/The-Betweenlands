package thebetweenlands.common.block.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.plant.BlockDoublePlantBL.EnumBlockHalf;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityRepeller;

public class BlockRepeller extends BlockContainer {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<EnumBlockHalf> HALF = EnumProperty.<EnumBlockHalf>create("half", EnumBlockHalf.class);

	protected static final AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0.15F, 0, 0.15F, 0.85F, 1.0F, 0.85F);
	protected static final AxisAlignedBB AABB_TOP = new AxisAlignedBB(0.15F, 0, 0.15F, 0.85F, 0.7F, 0.85F);

	public BlockRepeller() {
		super(Material.WOOD);
		setHardness(1.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().with(FACING, EnumFacing.NORTH).with(HALF, EnumBlockHalf.BOTTOM));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
		return state.get(HALF) == EnumBlockHalf.TOP ? AABB_TOP : AABB_BOTTOM;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		IBlockState state = this.getStateFromMeta(meta);
		if(state.get(HALF) == EnumBlockHalf.BOTTOM) {
			return new TileEntityRepeller();
		}
		return null;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
		return this.getDefaultState().with(HALF, EnumBlockHalf.BOTTOM).with(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.with(HALF, EnumBlockHalf.BOTTOM).with(FACING, placer.getHorizontalFacing()), 2);
		world.setBlockState(pos.up(), this.getDefaultState().with(HALF, EnumBlockHalf.TOP).with(FACING, placer.getHorizontalFacing()), 2);
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(state.get(HALF) == EnumBlockHalf.TOP && world.getBlockState(pos.down()).getBlock() == this) {
			this.onBlockActivated(world, pos.down(), world.getBlockState(pos.down()), player, hand, facing, hitX, hitY, hitZ);
		} else if(state.get(HALF) == EnumBlockHalf.BOTTOM) {
			TileEntityRepeller tile = (TileEntityRepeller) world.getTileEntity(pos);
			ItemStack held = player.getHeldItem(hand);
			if(!player.isSneaking() && !held.isEmpty()) {
				if(held.getItem() == ItemRegistry.SHIMMER_STONE) {
					if(!tile.hasShimmerstone()) {
						tile.addShimmerstone();
						if(!player.abilities.isCreativeMode) {
							held.shrink(1);
							if(held.getCount() <= 0) {
								player.setHeldItem(hand, ItemStack.EMPTY);
							}
						}
					}
					return true;
				} else if(held.getItem() == ItemRegistry.ASPECT_VIAL) {
					if(tile.hasShimmerstone()) {
						if(tile.getFuel() < tile.getMaxFuel()) {
							ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(held);
							int amount = aspectContainer.get(AspectRegistry.BYARIIS);
							int loss = 10; //Loss when adding
							if(amount >= loss) {
								if(!world.isRemote) {
									int added = tile.addFuel(amount - loss);
									if(!player.abilities.isCreativeMode) {
										int leftAmount = amount - added - loss;
										if(leftAmount > 0) {
											aspectContainer.set(AspectRegistry.BYARIIS, leftAmount);
										} else {
											player.setHeldItem(hand, held.getItem().getContainerItem(held));
										}
									}
								}
								player.swingArm(hand);
								return true;
							}
						}
					} else {
						if(!world.isRemote) {
							player.sendStatusMessage(new TextComponentTranslation("chat.repeller.shimmerstone_missing"), true);
						}
					}
				} else if(held.getItem() == ItemRegistry.DENTROTHYST_VIAL && tile.getFuel() > 0) {
					if(held.getItemDamage() == 0 || held.getItemDamage() == 2) {
						ItemStack newStack = new ItemStack(ItemRegistry.ASPECT_VIAL, 1, held.getItemDamage() == 0 ? 0 : 1);
						if(!world.isRemote) {
							ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(newStack);
							aspectContainer.set(AspectRegistry.BYARIIS, tile.removeFuel(Amounts.VIAL));
						}
						held.shrink(1);
						if(held.getCount() <= 0) {
							player.setHeldItem(hand, ItemStack.EMPTY);
						}
						if(!player.inventory.addItemStackToInventory(newStack)) {
							player.dropItem(newStack, false);
						}
						return true;
					}
				}
			} else if(player.isSneaking() && held.isEmpty() && tile.hasShimmerstone()) {
				tile.removeShimmerstone();
				ItemStack stack = new ItemStack(ItemRegistry.SHIMMER_STONE, 1);
				if(!player.inventory.addItemStackToInventory(stack)) {
					player.dropItem(stack, false);
				}
				return true;
			} else if(!player.isSneaking() && held.isEmpty()) {
				if(!world.isRemote) {
					tile.cycleRadiusState();
				}
				player.swingArm(hand);
			}
		}
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP) && worldIn.isAirBlock(pos.up());
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndBreakBlock(worldIn, pos);
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}

	protected void checkAndBreakBlock(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		EnumBlockHalf half = state.get(HALF);
		if ((half == EnumBlockHalf.BOTTOM && (!world.isSideSolid(pos.down(), EnumFacing.UP) || world.getBlockState(pos.up()).getBlock() != this))
				|| (half == EnumBlockHalf.TOP) && world.getBlockState(pos.down()).getBlock() != this) {
			this.breakBlock(world, pos, state);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
		if(!worldIn.isRemote) {
			TileEntityRepeller tile = (TileEntityRepeller) worldIn.getTileEntity(pos);
			if(tile != null) {
				float f = 0.7F;

				if(tile.hasShimmerstone()) {
					double d0 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					double d1 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, new ItemStack(ItemRegistry.SHIMMER_STONE, 1));
					entityitem.setDefaultPickupDelay();
					worldIn.spawnEntity(entityitem);
				}

				double d0 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double d1 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5D;
				EntityItem entityitem = new EntityItem(worldIn, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, new ItemStack(BlockRegistry.REPELLER, 1));
				entityitem.setDefaultPickupDelay();
				worldIn.spawnEntity(entityitem);
			}
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public List<ItemStack> getDrops(IWorldReader world, BlockPos pos, IBlockState state, int fortune) {
		return Collections.emptyList();
	}

	@Override
	public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if(rand.nextInt(6) == 0) {
			IBlockState state = worldIn.getBlockState(pos);
			if(state.get(HALF) == EnumBlockHalf.BOTTOM) {
				TileEntityRepeller tile = (TileEntityRepeller) worldIn.getTileEntity(pos);
				if(tile != null && tile.isRunning())  {
					EnumFacing facing = state.get(FACING);
					for(int i = 0; i < 60; i++) {
						float rot = (float) (Math.PI * 2.0F / 60.0F * i + Math.PI * rand.nextFloat() / 60.0F);
						double radius = Math.max(tile.getRadius(0.0F), 1.0D);
						double rotX = Math.sin(rot) * radius;
						double rotZ = Math.cos(rot) * radius;
						double xOff = -facing.getXOffset() * 0.23F;
						double zOff = facing.getZOffset() * 0.23F;
						double centerX = pos.getX() + 0.5F + xOff;
						double centerY = pos.getY() + 1.3F;
						double centerZ = pos.getZ() + 0.5F - zOff;
						List<Vec3d> points = new ArrayList<Vec3d>();
						points.add(new Vec3d(centerX, centerY, centerZ));
						points.add(new Vec3d(centerX, centerY + radius, centerZ));
						points.add(new Vec3d(centerX + rotX, centerY + radius, centerZ + rotZ));
						points.add(new Vec3d(centerX + rotX, pos.getY() + 0.1D, centerZ + rotZ));
						BLParticles.ANIMATOR.spawn(worldIn, centerX, centerY, centerZ, ParticleArgs.get().withData(points));
					}
				}
			}
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, HALF);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int facing = (meta >> 1) & 0b11;
		boolean isUpper = (meta & 1) == 1;
		return this.getDefaultState().with(HALF, isUpper ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM).with(FACING, EnumFacing.byHorizontalIndex(facing));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int facing = ((EnumFacing)state.get(FACING)).getHorizontalIndex();
		boolean isUpper = state.get(HALF) == EnumBlockHalf.TOP;
		int meta = facing << 1;
		meta |= isUpper ? 1 : 0;
		return meta;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityRepeller) {
			TileEntityRepeller repeller = (TileEntityRepeller) tile;
			return Math.round((float)repeller.getFuel() / (float)repeller.getMaxFuel() * 16.0F);
		}
		return 0;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
}
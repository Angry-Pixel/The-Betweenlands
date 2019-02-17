package thebetweenlands.common.block.misc;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import thebetweenlands.common.registries.BlockRegistryOld.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockRope extends Block implements ICustomItemBlock {
	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.4375f, 0f, 0.4375f, 0.5625f, 1f, 0.5625f);

	public static final EnumProperty<EnumRopeVariant> VARIANT = EnumProperty.<EnumRopeVariant>create("variant", EnumRopeVariant.class);

	public BlockRope() {
		super(Material.PLANTS);
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.5F);
		this.setDefaultState(this.blockState.getBaseState().with(VARIANT, EnumRopeVariant.SINGLE));
		this.setCreativeTab(null);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override 
	public boolean isLadder(IBlockState state, IWorldReader world, BlockPos pos, EntityLivingBase entity) { 
		return true; 
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if(heldItem.isEmpty() && player.isSneaking()) {
			BlockPos offsetPos = pos.down();
			while(world.getBlockState(offsetPos).getBlock() == this) {
				offsetPos = offsetPos.down();
			}
			offsetPos = offsetPos.up();
			if(offsetPos.getY() != pos.getY()) {
				if(!world.isRemote()) {
					world.removeBlock(offsetPos);

					if(!player.abilities.isCreativeMode && !player.inventory.addItemStackToInventory(new ItemStack(ItemRegistry.ROPE_ITEM))) {
						world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(ItemRegistry.ROPE_ITEM)));
					}
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return !worldIn.getBlockState(pos).getMaterial().isLiquid() && super.canPlaceBlockAt(worldIn, pos) && (worldIn.getBlockState(pos.up()).isSideSolid(worldIn, pos, EnumFacing.DOWN) || worldIn.getBlockState(pos.up()).getBlock() == this);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return ItemRegistry.ROPE_ITEM;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		if (!(worldIn.getBlockState(pos.up()).isSideSolid(worldIn, pos, EnumFacing.DOWN) || worldIn.getBlockState(pos.up()).getBlock() == this)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.removeBlock(pos);
		}
	}

	@Override
	public IBlockState getActualState(IBlockState state, IWorldReader worldIn, BlockPos pos) {
		state = state.with(VARIANT, EnumRopeVariant.MIDDLE);

		if(worldIn.getBlockState(pos.up()).getBlock() != this) {
			if(worldIn.getBlockState(pos.down()).getBlock() == this) {
				state = state.with(VARIANT, EnumRopeVariant.TOP);
			} else {
				state = state.with(VARIANT, EnumRopeVariant.SINGLE);
			}
		} else if(worldIn.getBlockState(pos.down()).getBlock() != this){
			state = state.with(VARIANT, EnumRopeVariant.BOTTOM);
		}

		return state;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		switch(meta) {
		default:
		case 0:
			return this.getDefaultState().with(VARIANT, EnumRopeVariant.SINGLE);
		case 1:
			return this.getDefaultState().with(VARIANT, EnumRopeVariant.TOP);
		case 2:
			return this.getDefaultState().with(VARIANT, EnumRopeVariant.MIDDLE);
		case 3:
			return this.getDefaultState().with(VARIANT, EnumRopeVariant.BOTTOM);
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		switch(state.get(VARIANT)) {
		default:
		case SINGLE:
			return 0;
		case TOP:
			return 1;
		case MIDDLE:
			return 2;
		case BOTTOM:
			return 3;
		}
	}

	public static enum EnumRopeVariant implements IStringSerializable {
		SINGLE,
		TOP,
		MIDDLE,
		BOTTOM;

		private final String name;

		private EnumRopeVariant() {
			this.name = name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getName() {
			return this.name;
		}
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IWorldReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}

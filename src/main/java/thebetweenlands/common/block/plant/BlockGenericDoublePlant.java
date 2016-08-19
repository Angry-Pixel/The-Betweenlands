package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.tools.ISickleHarvestable;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockGenericDoublePlant extends BlockBush implements IStateMappedBlock, IShearable, ISickleHarvestable {
	public static final PropertyEnum<BlockGenericDoublePlant.EnumBlockHalf> HALF = PropertyEnum.<BlockGenericDoublePlant.EnumBlockHalf>create("half", BlockGenericDoublePlant.EnumBlockHalf.class);
	public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;

	protected ItemStack sickleHarvestableDrop;
	
	public BlockGenericDoublePlant() {
		super(Material.PLANTS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, BlockGenericDoublePlant.EnumBlockHalf.LOWER).withProperty(FACING, EnumFacing.NORTH));
		this.setHardness(0.0F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	public BlockGenericDoublePlant setSickleDrop(ItemStack drop) {
		this.sickleHarvestableDrop = drop;
		return this;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return false;
	}

	@Override
	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			boolean isUpperHalf = state.getValue(HALF) == BlockGenericDoublePlant.EnumBlockHalf.UPPER;
			BlockPos posAboveOrHere = isUpperHalf ? pos : pos.up();
			BlockPos posBelowOrHere = isUpperHalf ? pos.down() : pos;
			Block blockAboveOrHere = (Block)(isUpperHalf ? this : worldIn.getBlockState(posAboveOrHere).getBlock());
			Block blockBelowOrHere = (Block)(isUpperHalf ? worldIn.getBlockState(posBelowOrHere).getBlock() : this);

			if (!isUpperHalf) 
				this.dropBlockAsItem(worldIn, pos, state, 0); //Forge move above the setting to air.

			if (blockAboveOrHere == this) {
				worldIn.setBlockState(posAboveOrHere, Blocks.AIR.getDefaultState(), 2);
			}

			if (blockBelowOrHere == this) {
				worldIn.setBlockState(posBelowOrHere, Blocks.AIR.getDefaultState(), 3);
			}
		}
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getBlock() != this) 
			return super.canBlockStay(worldIn, pos, state); //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
		if (state.getValue(HALF) == BlockGenericDoublePlant.EnumBlockHalf.UPPER) {
			return worldIn.getBlockState(pos.down()).getBlock() == this;
		} else {
			IBlockState stateAbove = worldIn.getBlockState(pos.up());
			return stateAbove.getBlock() == this && super.canBlockStay(worldIn, pos, stateAbove);
		}
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	public void placeAt(World worldIn, BlockPos lowerPos, int updateFlags) {
		worldIn.setBlockState(lowerPos, this.getDefaultState().withProperty(HALF, BlockGenericDoublePlant.EnumBlockHalf.LOWER), updateFlags);
		worldIn.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(HALF, BlockGenericDoublePlant.EnumBlockHalf.UPPER), updateFlags);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rot = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockGenericDoublePlant.EnumBlockHalf.UPPER).withProperty(FACING, EnumFacing.getHorizontal(rot)), 2);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (state.getValue(HALF) == BlockGenericDoublePlant.EnumBlockHalf.UPPER) {
			if (worldIn.getBlockState(pos.down()).getBlock() == this) {
				if (!player.capabilities.isCreativeMode) {
					IBlockState iblockstate = worldIn.getBlockState(pos.down());
					worldIn.destroyBlock(pos.down(), true);
				} else {
					worldIn.setBlockToAir(pos.down());
				}
			}
		} else if (worldIn.getBlockState(pos.up()).getBlock() == this) {
			worldIn.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), 2);
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int facing = (meta >> 1) & 4;
		boolean isUpper = (meta & 1) == 1;
		return this.getDefaultState().withProperty(HALF, isUpper ? EnumBlockHalf.UPPER : EnumBlockHalf.LOWER).withProperty(FACING, EnumFacing.getHorizontal(facing));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int facing = ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		boolean isUpper = state.getValue(HALF) == BlockGenericDoublePlant.EnumBlockHalf.UPPER;
		int meta = facing << 1;
		meta |= isUpper ? 1 : 0;
		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {HALF, FACING});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XYZ;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		//Forge: Break both parts on the client to prevent the top part flickering as default type for a few frames.
		if (state.getBlock() ==  this && state.getValue(HALF) == EnumBlockHalf.LOWER && world.getBlockState(pos.up()).getBlock() == this)
			world.setBlockToAir(pos.up());
		return world.setBlockToAir(pos);
	}

	public static enum EnumBlockHalf implements IStringSerializable {
		UPPER,
		LOWER;

		public String toString() {
			return this.getName();
		}

		public String getName() {
			return this == UPPER ? "upper" : "lower";
		}
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(FACING);
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		return this.sickleHarvestableDrop != null ? ImmutableList.of(this.sickleHarvestableDrop) : ImmutableList.of();
	}
}
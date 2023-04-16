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
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedStateMap;

import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import net.minecraft.util.EnumHand;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;

public class BlockDoublePlantBL extends BlockBush implements IStateMappedBlock, IShearable, ISickleHarvestable, IFarmablePlant, ITintedBlock {
	protected static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 1.0D, 0.9D);

	public static final PropertyEnum<BlockDoublePlantBL.EnumBlockHalf> HALF = PropertyEnum.<BlockDoublePlantBL.EnumBlockHalf>create("half", BlockDoublePlantBL.EnumBlockHalf.class);
	public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;

	protected ItemStack sickleHarvestableDrop;
	protected boolean replaceable;
	
	public BlockDoublePlantBL() {
		super(Material.PLANTS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, BlockDoublePlantBL.EnumBlockHalf.LOWER).withProperty(FACING, EnumFacing.NORTH));
		this.setHardness(0.0F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}

	public BlockDoublePlantBL setSickleDrop(ItemStack drop) {
		this.sickleHarvestableDrop = drop;
		return this;
	}
	
	public BlockDoublePlantBL setReplaceable(boolean replaceable) {
		this.replaceable = replaceable;
		return this;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return PLANT_AABB;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return this.replaceable;
	}

	@Override
	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			boolean isUpperHalf = state.getValue(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER;
			BlockPos posAboveOrHere = isUpperHalf ? pos : pos.up();
			BlockPos posBelowOrHere = isUpperHalf ? pos.down() : pos;
			Block blockAboveOrHere = (Block)(isUpperHalf ? this : worldIn.getBlockState(posAboveOrHere).getBlock());
			Block blockBelowOrHere = (Block)(isUpperHalf ? worldIn.getBlockState(posBelowOrHere).getBlock() : this);

			if (!isUpperHalf && blockAboveOrHere == this) 
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
		if (state.getValue(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER) {
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
		worldIn.setBlockState(lowerPos, this.getDefaultState().withProperty(HALF, BlockDoublePlantBL.EnumBlockHalf.LOWER), updateFlags);
		worldIn.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlantBL.EnumBlockHalf.UPPER), updateFlags);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rot = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlantBL.EnumBlockHalf.UPPER).withProperty(FACING, EnumFacing.byHorizontalIndex(rot)), 2);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (state.getValue(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER) {
			if (worldIn.getBlockState(pos.down()).getBlock() == this) {
				if (!player.capabilities.isCreativeMode) {
					//Stupid workarounds...
					this.harvestBlock(worldIn, player, pos.down(), worldIn.getBlockState(pos.down()), worldIn.getTileEntity(pos.down()), player.getHeldItemMainhand());
				}
				worldIn.setBlockToAir(pos.down());
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
		return this.getDefaultState().withProperty(HALF, isUpper ? EnumBlockHalf.UPPER : EnumBlockHalf.LOWER).withProperty(FACING, EnumFacing.byHorizontalIndex(facing));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int facing = state.getValue(FACING).getHorizontalIndex();
		boolean isUpper = state.getValue(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER;
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
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	public static enum EnumBlockHalf implements IStringSerializable {
		UPPER,
		LOWER;

		@Override
		public String toString() {
			return this.getName();
		}

		@Override
		public String getName() {
			return this == UPPER ? "upper" : "lower";
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(FACING);
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS || item.getItem() == ItemRegistry.SILT_CRAB_CLAW;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return this.sickleHarvestableDrop != null ? ImmutableList.of(this.sickleHarvestableDrop.copy()) : ImmutableList.of();
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || SoilHelper.canSustainPlant(state);
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		return world.isAirBlock(targetPos) && world.isAirBlock(targetPos.up()) && this.canPlaceBlockAt(world, targetPos);
	}
	
	@Override
	public float getSpreadChance(World world, BlockPos pos, IBlockState state, BlockPos taretPos, Random rand) {
		return 0.25F;
	}

	@Override
	public void spreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		world.setBlockState(targetPos, this.getDefaultState().withProperty(BlockDoublePlantBL.HALF, BlockDoublePlantBL.EnumBlockHalf.LOWER));
		world.setBlockState(targetPos.up(), this.getDefaultState().withProperty(BlockDoublePlantBL.HALF, BlockDoublePlantBL.EnumBlockHalf.UPPER));
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, IBlockState state, Random rand) {
		return 8;
	}

	@Override
	public void decayPlant(World world, BlockPos pos, IBlockState state, Random rand) {
		world.setBlockToAir(pos.up());
		world.setBlockToAir(pos);
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, IBlockState state) {
		return true;
	}
	
	@Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(!heldItem.isEmpty() && EnumItemMisc.COMPOST.isItemOf(heldItem)) {
			pos = pos.down();
			if(state.getValue(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER) {
				pos = pos.down();
			}
			state = world.getBlockState(pos);
			if(state.getBlock() instanceof BlockGenericDugSoil) {
				return state.getBlock().onBlockActivated(world, pos, state, playerIn, hand, EnumFacing.UP, 0.5f, 1.0f, 0.5f);
			}
		}
		return false;
	}
}

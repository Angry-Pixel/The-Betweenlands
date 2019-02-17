package thebetweenlands.common.block.plant;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;
import thebetweenlands.api.block.IFarmablePlant;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistryOld.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AdvancedStateMap;

public class BlockDoublePlantBL extends BlockBush implements IStateMappedBlock, IShearable, ISickleHarvestable, IFarmablePlant, ITintedBlock {
	protected static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 1.0D, 0.9D);

	public static final EnumProperty<BlockDoublePlantBL.EnumBlockHalf> HALF = EnumProperty.<BlockDoublePlantBL.EnumBlockHalf>create("half", BlockDoublePlantBL.EnumBlockHalf.class);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	protected ItemStack sickleHarvestableDrop;
	protected boolean replaceable;
	
	public BlockDoublePlantBL() {
		super(Material.PLANTS);
		this.setDefaultState(this.blockState.getBaseState().with(HALF, BlockDoublePlantBL.EnumBlockHalf.LOWER).with(FACING, EnumFacing.NORTH));
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
		return PLANT_AABB;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
	}

	@Override
	public boolean isReplaceable(IWorldReader worldIn, BlockPos pos) {
		return this.replaceable;
	}

	@Override
	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			boolean isUpperHalf = state.get(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER;
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
		if (state.get(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER) {
			return worldIn.getBlockState(pos.down()).getBlock() == this;
		} else {
			IBlockState stateAbove = worldIn.getBlockState(pos.up());
			return stateAbove.getBlock() == this && super.canBlockStay(worldIn, pos, stateAbove);
		}
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return Items.AIR;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	public void placeAt(World worldIn, BlockPos lowerPos, int updateFlags) {
		worldIn.setBlockState(lowerPos, this.getDefaultState().with(HALF, BlockDoublePlantBL.EnumBlockHalf.LOWER), updateFlags);
		worldIn.setBlockState(lowerPos.up(), this.getDefaultState().with(HALF, BlockDoublePlantBL.EnumBlockHalf.UPPER), updateFlags);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rot = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		worldIn.setBlockState(pos.up(), this.getDefaultState().with(HALF, BlockDoublePlantBL.EnumBlockHalf.UPPER).with(FACING, EnumFacing.byHorizontalIndex(rot)), 2);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (state.get(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER) {
			if (worldIn.getBlockState(pos.down()).getBlock() == this) {
				if (!player.abilities.isCreativeMode) {
					//Stupid workarounds...
					this.harvestBlock(worldIn, player, pos.down(), worldIn.getBlockState(pos.down()), worldIn.getTileEntity(pos.down()), player.getHeldItemMainhand());
				}
				worldIn.removeBlock(pos.down());
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
		return this.getDefaultState().with(HALF, isUpper ? EnumBlockHalf.UPPER : EnumBlockHalf.LOWER).with(FACING, EnumFacing.byHorizontalIndex(facing));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int facing = state.get(FACING).getHorizontalIndex();
		boolean isUpper = state.get(HALF) == BlockDoublePlantBL.EnumBlockHalf.UPPER;
		int meta = facing << 1;
		meta |= isUpper ? 1 : 0;
		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {HALF, FACING});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
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
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(FACING);
	}

	@Override
	public boolean isShearable(ItemStack item, IWorldReader world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IWorldReader world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IWorldReader world, BlockPos pos, int fortune) {
		return this.sickleHarvestableDrop != null ? ImmutableList.of(this.sickleHarvestableDrop.copy()) : ImmutableList.of();
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return super.canSustainBush(state) || SoilHelper.canSustainPlant(state);
	}

	@Override
	public boolean canSpreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		return rand.nextFloat() <= 0.25F && world.isAirBlock(targetPos) && world.isAirBlock(targetPos.up()) && this.canPlaceBlockAt(world, targetPos);
	}

	@Override
	public void spreadTo(World world, BlockPos pos, IBlockState state, BlockPos targetPos, Random rand) {
		world.setBlockState(targetPos, this.getDefaultState().with(BlockDoublePlantBL.HALF, BlockDoublePlantBL.EnumBlockHalf.LOWER));
		world.setBlockState(targetPos.up(), this.getDefaultState().with(BlockDoublePlantBL.HALF, BlockDoublePlantBL.EnumBlockHalf.UPPER));
	}

	@Override
	public int getCompostCost(World world, BlockPos pos, IBlockState state, Random rand) {
		return 8;
	}

	@Override
	public void decayPlant(World world, BlockPos pos, IBlockState state, Random rand) {
		world.removeBlock(pos.up());
		world.removeBlock(pos);
	}

	@Override
	public boolean isFarmable(World world, BlockPos pos, IBlockState state) {
		return true;
	}
	
	@Override
	public int getColorMultiplier(IBlockState state, IWorldReader worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColors.getGrassColor(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}
}
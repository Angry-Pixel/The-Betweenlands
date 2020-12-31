package thebetweenlands.common.block.structure;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ItemBlockEnum.IGenericMetaSelector;
import thebetweenlands.common.item.ItemRenamableBlockEnum;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.BlockRegistry.ISubtypeItemBlockModelDefinition;
import thebetweenlands.common.tile.TileEntitySimulacrum;
import thebetweenlands.util.AdvancedStateMap.Builder;
import thebetweenlands.util.NBTHelper;

public class BlockSimulacrum extends BlockContainer implements IStateMappedBlock, ICustomItemBlock, ISubtypeItemBlockModelDefinition {
	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.15D, 0.0D, 0.15D, 0.85D, 1.0D, 0.85D);

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	public BlockSimulacrum(Material material, SoundType soundType) {
		super(material);
		this.setHardness(10.0F);
		this.setResistance(10000.0F);
		this.setSoundType(soundType);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VARIANT);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, Variant.byMetadata(meta)).withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMetadata(state.getValue(FACING));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int rotation = MathHelper.floor(placer.rotationYaw * 4.0F / 360.0F + 0.5D + 2) & 3;
		state = state.withProperty(FACING, EnumFacing.byHorizontalIndex(rotation));
		state = state.withProperty(VARIANT, Variant.byMetadata(stack.getItemDamage()));
		worldIn.setBlockState(pos, state, 3);

		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntitySimulacrum) {
			((TileEntitySimulacrum) tile).setEffect(TileEntitySimulacrum.Effect.byId(NBTHelper.getStackNBTSafe(stack).getInteger("simulacrumEffectId")));
			((TileEntitySimulacrum) tile).setActive(true);
			if(stack.hasDisplayName()) {
				((TileEntitySimulacrum) tile).setCustomName(stack.getDisplayName());
			}
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState();
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
			this.dropAt(worldIn, pos, state);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySimulacrum();
	}

	@Override
	public void setStateMapper(Builder builder) {
		builder.ignore(VARIANT).withPropertySuffix(VARIANT, v -> v.name);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> list) {
		list.add(new ItemStack(this, 1, Variant.ONE.getMetadata(EnumFacing.NORTH)));
		list.add(new ItemStack(this, 1, Variant.TWO.getMetadata(EnumFacing.NORTH)));
		list.add(new ItemStack(this, 1, Variant.THREE.getMetadata(EnumFacing.NORTH)));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		ItemStack stack = new ItemStack(this, 1, state.getValue(VARIANT).getMetadata(EnumFacing.NORTH));

		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntitySimulacrum) {
			NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
			nbt.setInteger("simulacrumEffectId", ((TileEntitySimulacrum) tile).getEffect().id);
		}

		return stack;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Collections.emptyList();
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(!worldIn.isRemote && !player.isCreative() && worldIn.getGameRules().getBoolean("doTileDrops")) {
			this.dropAt(worldIn, pos, state);
		}
	}
	
	private void dropAt(World worldIn, BlockPos pos, IBlockState state) {
		ItemStack stack = new ItemStack(this, 1, state.getValue(VARIANT).getMetadata(EnumFacing.NORTH));

		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TileEntitySimulacrum) {
			if(!((TileEntitySimulacrum) tile).isActive()) {
				worldIn.playSound(null, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.SIMULACRUM_BREAK, SoundCategory.BLOCKS, 1, 0.95f + worldIn.rand.nextFloat() * 0.1f);
			}
			
			NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
			nbt.setInteger("simulacrumEffectId", ((TileEntitySimulacrum) tile).getEffect().id);

			String customName = ((TileEntitySimulacrum) tile).getCustomName();
			if(customName.length() > 0) {
				stack.setStackDisplayName(customName);
			}
		}

		InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
	}

	@Override
	public int getSubtypeNumber() {
		return Variant.values().length * 4;
	}

	@Override
	public String getSubtypeName(int meta) {
		return "%s_" + Variant.byMetadata(meta).getName();
	}

	@Override
	public ItemBlock getItemBlock() {
		ItemBlock item = ItemRenamableBlockEnum.create(this, Variant.class);
		item.setMaxStackSize(1);
		return item;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		if(Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.isCreative()) {
			tooltip.add(I18n.format("tooltip.bl.simulacrum.effect", I18n.format("tooltip.bl.simulacrum.effect." + TileEntitySimulacrum.Effect.byId(NBTHelper.getStackNBTSafe(stack).getInteger("simulacrumEffectId")).name)));
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(hand == EnumHand.MAIN_HAND && playerIn.isCreative() && playerIn.isSneaking()) {

			if(!worldIn.isRemote) {
				TileEntity tile = worldIn.getTileEntity(pos);

				if(tile instanceof TileEntitySimulacrum) {
					TileEntitySimulacrum simulacrum = (TileEntitySimulacrum) tile;

					TileEntitySimulacrum.Effect nextEffect = TileEntitySimulacrum.Effect.values()[(simulacrum.getEffect().ordinal() + 1) % TileEntitySimulacrum.Effect.values().length];

					simulacrum.setEffect(nextEffect);

					playerIn.sendStatusMessage(new TextComponentTranslation("chat.simulacrum.changed_effect", new TextComponentTranslation("tooltip.bl.simulacrum.effect." + nextEffect.name)), true);
				}
			}

			playerIn.swingArm(hand);

			return true;
		}

		return false;
	}

	public enum Variant implements IStringSerializable, IGenericMetaSelector {
		ONE("1"),
		TWO("2"),
		THREE("3");

		private final String name;

		private Variant(String name) {
			this.name = name.toLowerCase(Locale.ENGLISH);
		}

		public int getMetadata(EnumFacing facing) {
			return facing.getHorizontalIndex() | (this.ordinal() << 2);
		}

		@Override
		public String toString() {
			return this.name;
		}

		public static Variant byMetadata(int metadata) {
			metadata >>= 2;
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public boolean isMetadataMatching(int meta) {
			return byMetadata(meta) == this;
		}
	}
}

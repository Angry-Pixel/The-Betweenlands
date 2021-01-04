package thebetweenlands.common.block.container;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntitySiltGlassJar;

public class BlockSiltGlassJar extends BasicBlock implements ITileEntityProvider, ICustomItemBlock {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 1.0D, 0.8125D);

	public BlockSiltGlassJar() {
		super(Material.GLASS);
		setSoundType(SoundType.GLASS);
		setHardness(1.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySiltGlassJar();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byIndex(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().rotateYCCW());
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().rotateYCCW()), 2);
		if(!world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("Items")) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileEntitySiltGlassJar) {
				NBTTagList tags = stack.getTagCompound().getTagList("Items", 10);
				((TileEntitySiltGlassJar) tileentity).inventory = NonNullList.<ItemStack>withSize(((TileEntitySiltGlassJar) tileentity).getSizeInventory(), ItemStack.EMPTY);

				for (int i = 0; i < tags.tagCount(); i++) {
					NBTTagCompound data = tags.getCompoundTagAt(i);
					int j = data.getByte("Slot") & 255;

					if (j >= 0 && j < ((TileEntitySiltGlassJar) tileentity).inventory.size())
						((TileEntitySiltGlassJar) tileentity).inventory.set(j, new ItemStack(data));
				}
			}
			world.notifyBlockUpdate(pos, state, state, 3);
		}
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if (!world.isRemote) {
			if (world.getTileEntity(pos) instanceof TileEntitySiltGlassJar) {
				TileEntitySiltGlassJar tile = (TileEntitySiltGlassJar) world.getTileEntity(pos);
				if (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ItemRegistry.WEEDWOOD_FISHING_ROD) {
					ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
					for (int i = 0; i < tile.getItems().size(); i++) {
						if (!tile.getItems().get(i).isEmpty()) {
							if (stack.getItem() == ItemRegistry.WEEDWOOD_FISHING_ROD && stack.hasTagCompound()) { // no need for double item check - but may expand
								if (stack.getTagCompound().getBoolean("baited"))
									return;
								else {
									stack.getTagCompound().setBoolean("baited", true);
									tile.getItems().set(i, ItemStack.EMPTY);
									tile.markForUpdate();
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			if (world.getTileEntity(pos) instanceof TileEntitySiltGlassJar) {
				TileEntitySiltGlassJar tile = (TileEntitySiltGlassJar) world.getTileEntity(pos);
				if (!player.getHeldItem(hand).isEmpty() && (player.getHeldItem(hand).getItem() == ItemRegistry.TINY_SLUDGE_WORM || player.getHeldItem(hand).getItem() == ItemRegistry.TINY_SLUDGE_WORM_HELPER)) {
					for(int i = 0; i < tile.getItems().size(); i++) {
						if (tile.getItems().get(i).isEmpty()) {
							ItemStack stack = player.getHeldItem(hand).splitStack(1);
							if (!stack.isEmpty()) {
								tile.getItems().set(i, stack);
								tile.markForUpdate();
								return true;
							}
						}
					}
				} else if(player.isSneaking() && hand == EnumHand.MAIN_HAND) {
					for(int i = 0; i < tile.getItems().size(); i++) {
						if(!tile.getItems().get(i).isEmpty()) {
							ItemStack extracted = tile.getItems().get(i);
							EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
							item.motionX = item.motionY = item.motionZ = 0D;
							world.spawnEntity(item);
							tile.getItems().set(i, ItemStack.EMPTY);
							tile.markForUpdate();
							return true;
						}
					}
				}
				
			}
		}
		return false;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileEntitySiltGlassJar) {
				NBTTagCompound nbt = new NBTTagCompound();
				tileentity.writeToNBT(nbt);
				ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, 0);
				if(((TileEntitySiltGlassJar) tileentity).getSizeInventory() > 0)
					stack.setTagCompound(nbt);
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.removeTileEntity(pos);
			}
		}
	}

	@Nullable
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public ItemBlock getItemBlock() {
		 ItemBlock SILT_GLASS_JAR_ITEM = new ItemBlock(BlockRegistry.SILT_GLASS_JAR) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {

				if (stack.hasTagCompound() && stack.getTagCompound().getTagList("Items", 10) != null) {
					NBTTagList tags = stack.getTagCompound().getTagList("Items", 10);

					for (int i = 0; i < tags.tagCount(); i++) {
						NBTTagCompound data = tags.getCompoundTagAt(i);
						int j = data.getByte("Slot") & 255;

						if (i >= 0 && i <= 7) {
							list.add("Slot " + (j + 1) + ": " + TextFormatting.GREEN + new ItemStack(data).getDisplayName() + " x " + new ItemStack(data).getCount());

						} 
					}
				}
			}
		};
		return SILT_GLASS_JAR_ITEM;
	}

}

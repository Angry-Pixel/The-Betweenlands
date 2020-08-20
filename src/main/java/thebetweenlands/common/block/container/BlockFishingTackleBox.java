package thebetweenlands.common.block.container;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityFishingTackleBox;


public class BlockFishingTackleBox extends BlockContainer implements ICustomItemBlock {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	
	public BlockFishingTackleBox() {
		super(Material.WOOD);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
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
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().rotateYCCW()), 2);

		if(!world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("Items")) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileEntityFishingTackleBox) {
				NBTTagList tags = stack.getTagCompound().getTagList("Items", 10);
				((TileEntityFishingTackleBox) tileentity).inventory = NonNullList.<ItemStack>withSize(((TileEntityFishingTackleBox) tileentity).getSizeInventory(), ItemStack.EMPTY);

				for (int i = 0; i < tags.tagCount(); i++) {
					NBTTagCompound data = tags.getCompoundTagAt(i);
					int j = data.getByte("Slot") & 255;

					if (j >= 0 && j < ((TileEntityFishingTackleBox) tileentity).inventory.size())
						((TileEntityFishingTackleBox) tileentity).inventory.set(j, new ItemStack(data));
				}
			}
			world.notifyBlockUpdate(pos, state, state, 3);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityFishingTackleBox();
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			if (player.isSneaking()) {
				if (world.getTileEntity(pos) instanceof TileEntityFishingTackleBox) {
					TileEntityFishingTackleBox tile = (TileEntityFishingTackleBox) world.getTileEntity(pos);
					if(!tile.isOpen())
						world.playSound(null, pos, SoundRegistry.FISHING_TACKLE_BOX_OPEN, SoundCategory.BLOCKS, 1F, 1F);
					if(tile.isOpen())
						world.playSound(null, pos, SoundRegistry.FISHING_TACKLE_BOX_CLOSE, SoundCategory.BLOCKS, 1F, 1F);
					tile.setOpen(!tile.isOpen());
					world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
					tile.markDirty();
				}
			}

			if (!player.isSneaking()) {
				if (world.getTileEntity(pos) instanceof TileEntityFishingTackleBox) {
					TileEntityFishingTackleBox tile = (TileEntityFishingTackleBox) world.getTileEntity(pos);
					if (!tile.isOpen()) {
						if(world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2)))
							tile.seatPlayer(player, pos);
					}
					if (tile.isOpen())
						player.openGui(TheBetweenlands.instance, CommonProxy.GUI_FISHING_TACKLE_BOX, world, pos.getX(), pos.getY(), pos.getZ());
				}
			}
		}
		return true;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof TileEntityFishingTackleBox) {
				NBTTagCompound nbt = new NBTTagCompound();
				tileentity.writeToNBT(nbt);
				ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, 0);
				if(((TileEntityFishingTackleBox) tileentity).getSizeInventory() > 0)
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public ItemBlock getItemBlock() {
		 ItemBlock FISHING_TACKLE_BOX_ITEM = new ItemBlock(BlockRegistry.FISHING_TACKLE_BOX) {
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {

				if (stack.hasTagCompound() && stack.getTagCompound().getTagList("Items", 10) != null) {
					NBTTagList tags = stack.getTagCompound().getTagList("Items", 10);

					for (int i = 0; i < tags.tagCount(); i++) {
						NBTTagCompound data = tags.getCompoundTagAt(i);
						int j = data.getByte("Slot") & 255;

						if (i >= 0 && i <= 15) {
							list.add("Slot " + (j + 1) + ": " + TextFormatting.GREEN + new ItemStack(data).getDisplayName() + " x " + new ItemStack(data).getCount());

						} 
					}
				}
			}
		};
		return FISHING_TACKLE_BOX_ITEM;
	}

}

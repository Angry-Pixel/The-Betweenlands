package thebetweenlands.common.block.container;

import javax.annotation.Nullable;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.tile.TileEntityCompostBin;


public class BlockCompostBin extends BasicBlock implements ITileEntityProvider {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockCompostBin() {
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
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCompostBin();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getFront(meta);

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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return true;
		if (world.getTileEntity(pos) instanceof TileEntityCompostBin) {
			TileEntityCompostBin tile = (TileEntityCompostBin) world.getTileEntity(pos);

			boolean open = tile.open;

			if (!player.isSneaking() && player.getHeldItem(hand) == null && tile.getTotalCompostedAmount() == 0) {
				if (open) {
					if (tile.hasCompostableItems()) {
						player.addChatMessage(new TextComponentTranslation("chat.compost.close"));
					} else {
						player.addChatMessage(new TextComponentTranslation("chat.compost.add"));
					}
				} else {
					if (tile.hasCompostableItems()) {
						player.addChatMessage(new TextComponentTranslation("chat.compost.composting"));
					} else {
						player.addChatMessage(new TextComponentTranslation("chat.compost.add"));
					}
				}
			} else if (player.isSneaking()) {
				tile.open = !tile.open;
				world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
				tile.markDirty();
			} else {
				if (player.getHeldItem(hand) != null) {
					if (open) {
						ItemStack stack = player.getHeldItem(hand);
						CompostRecipe compostRecipe = CompostRecipe.getCompostRecipe(stack);
						if (compostRecipe != null) {
							switch (tile.addItemToBin(stack, compostRecipe.compostAmount, compostRecipe.compostTime, true)) {
							case 1:
								tile.addItemToBin(stack, compostRecipe.compostAmount, compostRecipe.compostTime, false);
								if (!player.capabilities.isCreativeMode)
									player.inventory.decrStackSize(player.inventory.currentItem, 1);
								break;
							case -1:
							default:
								player.addChatMessage(new TextComponentTranslation("chat.compost.full"));
								break;
							}
						} else {
							player.addChatMessage(new TextComponentTranslation("chat.compost.not.compostable"));
						}
					} else {
						player.addChatMessage(new TextComponentTranslation("chat.compost.open.add"));
					}
				} else if (!open && tile.getTotalCompostedAmount() != 0) {
					player.addChatMessage(new TextComponentTranslation("chat.compost.open.get"));
				} else if (open && tile.getTotalCompostedAmount() != 0) {
					if (tile.removeCompost(TileEntityCompostBin.COMPOST_PER_ITEM)) {
						world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, EnumItemMisc.COMPOST.create(1)));
					}
				}
			}
		}
		return true;
	}
}

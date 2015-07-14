package thebetweenlands.blocks.container;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.recipes.CompostRegistry;
import thebetweenlands.tileentities.TileEntityCompostBin;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCompostBin extends BlockContainer {

	public BlockCompostBin() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("thebetweenlands.compostBin");
		setCreativeTab(ModCreativeTabs.blocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.weedwood.getIcon(0, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		int rot = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rot == 0 ? 2 : rot == 1 ? 5 : rot == 2 ? 3 : 4, 3);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		if (world.getTileEntity(x, y, z) instanceof TileEntityCompostBin) {
			TileEntityCompostBin tile = (TileEntityCompostBin) world.getTileEntity(x, y, z);

			if (player.isSneaking())
				return false;

			if (player.getCurrentEquippedItem() != null) {
				ItemStack stack = player.getCurrentEquippedItem();
				int compostAmount = CompostRegistry.hasCompostValue(stack);
				if(compostAmount > 0) {
					if(tile.addItemToBin(stack, compostAmount, true)) {
						tile.addItemToBin(stack, compostAmount, false);
						player.inventory.consumeInventoryItem(stack.getItem());
					} else {
						player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.full")));
					}
				}else {
					player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.cannot")));
				}
			} else {
				int compostAmount = tile.compostedAmount;
				if(compostAmount >= 25){
					world.spawnEntityInWorld(new EntityItem(world, x, y + 2, z, new ItemStack(BLItemRegistry.compost)));
					tile.removeCompost(25);
				} else {
					player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("compost.not.enough")));
				}
			}
		}
		return true;
	}
/* TODO actually make this have an inventory
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block,
			int meta) {
		IInventory tile = (IInventory) world.getTileEntity(x, y, z);
		if (tile != null)
			for (int i = 0; i < tile.getSizeInventory(); i++) {
				ItemStack stack = tile.getStackInSlot(i);
				if (stack != null) {
					if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
						float f = 0.7F;
						double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, stack);
						entityitem.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}
		super.breakBlock(world, x, y, z, block, meta);
	}
*/
	@Override
	public int getRenderType() {
		return - 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCompostBin();
	}


}
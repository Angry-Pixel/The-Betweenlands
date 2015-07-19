package thebetweenlands.blocks.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.tileentities.TileEntityPestleAndMortar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPestleAndMortar extends BlockContainer {

	public BlockPestleAndMortar() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("thebetweenlands.pestleAndMortar");
		setCreativeTab(ModCreativeTabs.blocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.betweenstone.getIcon(0, 0);
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
		if (world.getTileEntity(x, y, z) instanceof TileEntityPestleAndMortar) {
			TileEntityPestleAndMortar tile = (TileEntityPestleAndMortar) world.getTileEntity(x, y, z);

			if (player.getCurrentEquippedItem() == null && !player.isSneaking()) {
				tile.manualGrinding = true;
				world.markBlockForUpdate(x, y, z);
				return true;
			}

			if (player.getCurrentEquippedItem() != null) {
				if(player.getCurrentEquippedItem().getItem() == BLItemRegistry.pestle) {
					if(tile.getStackInSlot(1) == null) {
						tile.setInventorySlotContents(1, player.getCurrentEquippedItem());
						tile.hasPestle = true;
						player.setCurrentItemOrArmor(0, null);
					}
					return true;
				}
				if(player.getCurrentEquippedItem().getItem() == BLItemRegistry.lifeCrystal) {
					if(tile.getStackInSlot(3) == null) {
						tile.setInventorySlotContents(3, player.getCurrentEquippedItem());
						player.setCurrentItemOrArmor(0, null);
					}
					return true;
				}
			}

			if (tile != null && player.isSneaking())
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_PESTLE_AND_MORTAR, world, x, y, z);
		}
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
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

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		TileEntityPestleAndMortar tile = (TileEntityPestleAndMortar) world.getTileEntity(x, y, z);
		if (tile.progress > 0 && rand.nextInt(3) == 0) {
			float f = x + 0.5F;
			float f1 = y + 1.1F + rand.nextFloat() * 6.0F / 16.0F;
			float f2 = z + 0.5F;
			world.spawnParticle("happyVillager", f, f1, f2, 0.0D, 0.0D, 0.0D);
		}
	}

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
		return new TileEntityPestleAndMortar();
	}
}
package thebetweenlands.blocks.container;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.tileentities.TileEntityWeedWoodChest;

public class BlockWeedWoodChest extends BlockContainer {

	public BlockWeedWoodChest() {
		super(Material.wood);
		setHardness(2.5F);
		setStepSound(soundTypeWood);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.weedwoodChest");
		setBlockTextureName("thebetweenlands:weedwoodPlanks");
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
	public int getRenderType() {
		return 22;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z - 1) == this)
			setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
		else if (world.getBlock(x, y, z + 1) == this)
			setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
		else if (world.getBlock(x - 1, y, z) == this)
			setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		else if (world.getBlock(x + 1, y, z) == this)
			setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
		else
			setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		unifyAdjacentChests(world, x, y, z);
		Block l = world.getBlock(x, y, z - 1);
		Block i1 = world.getBlock(x, y, z + 1);
		Block j1 = world.getBlock(x - 1, y, z);
		Block k1 = world.getBlock(x + 1, y, z);

		if (l == this)
			unifyAdjacentChests(world, x, y, z - 1);

		if (i1 == this)
			unifyAdjacentChests(world, x, y, z + 1);

		if (j1 == this)
			unifyAdjacentChests(world, x - 1, y, z);

		if (k1 == this)
			unifyAdjacentChests(world, x + 1, y, z);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack is) {
		Block l = world.getBlock(x, y, z - 1);
		Block i1 = world.getBlock(x, y, z + 1);
		Block j1 = world.getBlock(x - 1, y, z);
		Block k1 = world.getBlock(x + 1, y, z);
		byte b0 = 0;
		int l1 = MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (l1 == 0)
			b0 = 2;

		if (l1 == 1)
			b0 = 5;

		if (l1 == 2)
			b0 = 3;

		if (l1 == 3)
			b0 = 4;

		if (l != this && i1 != this && j1 != this && k1 != this)
			world.setBlockMetadataWithNotify(x, y, z, b0, 3);
		else {
			if ((l == this || i1 == this) && (b0 == 4 || b0 == 5)) {
				if (l == this)
					world.setBlockMetadataWithNotify(x, y, z - 1, b0, 3);
				else
					world.setBlockMetadataWithNotify(x, y, z + 1, b0, 3);

				world.setBlockMetadataWithNotify(x, y, z, b0, 3);
			}

			if ((j1 == this || k1 == this) && (b0 == 2 || b0 == 3)) {
				if (j1 == this)
					world.setBlockMetadataWithNotify(x - 1, y, z, b0, 3);
				else
					world.setBlockMetadataWithNotify(x + 1, y, z, b0, 3);

				world.setBlockMetadataWithNotify(x, y, z, b0, 3);
			}
		}
	}

	public void unifyAdjacentChests(World world, int x, int y, int z) {
		if (!world.isRemote) {
			Block l = world.getBlock(x, y, z - 1);
			Block i1 = world.getBlock(x, y, z + 1);
			Block j1 = world.getBlock(x - 1, y, z);
			Block k1 = world.getBlock(x + 1, y, z);
			Block l1;
			Block i2;
			byte b0;
			int j2;

			if (l != this && i1 != this) {
				if (j1 != this && k1 != this) {
					b0 = 3;

					if (l.func_149730_j() && !i1.func_149730_j())
						b0 = 3;

					if (i1.func_149730_j() && !l.func_149730_j())
						b0 = 2;

					if (j1.func_149730_j() && !k1.func_149730_j())
						b0 = 5;

					if (k1.func_149730_j() && !j1.func_149730_j())
						b0 = 4;
				} else {
					l1 = world.getBlock(j1 == this ? x - 1 : x + 1, y, z - 1);
					i2 = world.getBlock(j1 == this ? x - 1 : x + 1, y, z + 1);
					b0 = 3;

					if (j1 == this)
						j2 = world.getBlockMetadata(x - 1, y, z);
					else
						j2 = world.getBlockMetadata(x + 1, y, z);

					if (j2 == 2)
						b0 = 2;

					if ((l.func_149730_j() || l1.func_149730_j()) && !i1.func_149730_j() && !i2.func_149730_j())
						b0 = 3;

					if ((i1.func_149730_j() || i2.func_149730_j()) && !l.func_149730_j() && !l1.func_149730_j())
						b0 = 2;
				}
			} else {
				l1 = world.getBlock(x - 1, y, l == this ? z - 1 : z + 1);
				i2 = world.getBlock(x + 1, y, l == this ? z - 1 : z + 1);
				b0 = 5;

				if (l == this)
					j2 = world.getBlockMetadata(x, y, z - 1);
				else
					j2 = world.getBlockMetadata(x, y, z + 1);

				if (j2 == 4)
					b0 = 4;

				if ((j1.func_149730_j() || l1.func_149730_j()) && !k1.func_149730_j() && !i2.func_149730_j())
					b0 = 5;

				if ((k1.func_149730_j() || i2.func_149730_j()) && !j1.func_149730_j() && !l1.func_149730_j())
					b0 = 4;
			}

			world.setBlockMetadataWithNotify(x, y, z, b0, 3);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		int l = 0;

		if (world.getBlock(x - 1, y, z) == this)
			++l;

		if (world.getBlock(x + 1, y, z) == this)
			++l;

		if (world.getBlock(x, y, z - 1) == this)
			++l;

		if (world.getBlock(x, y, z + 1) == this)
			++l;

		return l > 1 ? false : isThereANeighborChest(world, x - 1, y, z) ? false : isThereANeighborChest(world, x + 1, y, z) ? false : isThereANeighborChest(world, x, y, z - 1) ? false : !isThereANeighborChest(world, x, y, z + 1);
	}

	private boolean isThereANeighborChest(World world, int x, int y, int z) {
		return world.getBlock(x, y, z) != this ? false : world.getBlock(x - 1, y, z) == this ? true : world.getBlock(x + 1, y, z) == this ? true : world.getBlock(x, y, z - 1) == this ? true : world.getBlock(x, y, z + 1) == this;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		super.onNeighborBlockChange(world, x, y, z, neighbour);
		TileEntityWeedWoodChest tileentitychest = (TileEntityWeedWoodChest) world.getTileEntity(x, y, z);

		if (tileentitychest != null)
			tileentitychest.updateContainingBlockInfo();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityWeedWoodChest)
			for (int i = 0; i < ((IInventory) tile).getSizeInventory(); i++) {
				ItemStack is = ((IInventory) tile).getStackInSlot(i);
				if (is != null)
					if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
						float f = 0.7F;
						double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, is);
						entityitem.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(entityitem);
					}
				world.func_147453_f(x, y, z, block);
			}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		else {
			if(getInventory(world, x, y, z) != null)
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_WEEDWOOD_CHEST, world, x, y, z);
			return true;
		}
	}

	public static IInventory getInventory(World world, int x, int y, int z) {
		Object object = world.getTileEntity(x, y, z);
		Block chest = BLBlockRegistry.weedwoodChest;

		if (object == null)
			return null;
		else if (world.isSideSolid(x, y + 1, z, DOWN))
			return null;
		else if (isOcelotBlockingChest(world, x, y, z))
			return null;
		else if (world.getBlock(x - 1, y, z) == chest && (world.isSideSolid(x - 1, y + 1, z, DOWN) || isOcelotBlockingChest(world, x - 1, y, z)))
			return null;
		else if (world.getBlock(x + 1, y, z) == chest && (world.isSideSolid(x + 1, y + 1, z, DOWN) || isOcelotBlockingChest(world, x + 1, y, z)))
			return null;
		else if (world.getBlock(x, y, z - 1) == chest && (world.isSideSolid(x, y + 1, z - 1, DOWN) || isOcelotBlockingChest(world, x, y, z - 1)))
			return null;
		else if (world.getBlock(x, y, z + 1) == chest && (world.isSideSolid(x, y + 1, z + 1, DOWN) || isOcelotBlockingChest(world, x, y, z + 1)))
			return null;
		else {
			if (world.getBlock(x - 1, y, z) == chest)
				object = new InventoryLargeChest("container.weedwoodChestDouble", (TileEntityWeedWoodChest) world.getTileEntity(x - 1, y, z), (IInventory) object);

			if (world.getBlock(x + 1, y, z) == chest)
				object = new InventoryLargeChest("container.weedwoodChestDouble", (IInventory) object, (TileEntityWeedWoodChest) world.getTileEntity(x + 1, y, z));

			if (world.getBlock(x, y, z - 1) == chest)
				object = new InventoryLargeChest("container.weedwoodChestDouble", (TileEntityWeedWoodChest) world.getTileEntity(x, y, z - 1), (IInventory) object);

			if (world.getBlock(x, y, z + 1) == chest)
				object = new InventoryLargeChest("container.weedwoodChestDouble", (IInventory) object, (TileEntityWeedWoodChest) world.getTileEntity(x, y, z + 1));

			return (IInventory) object;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWeedWoodChest();
	}

	@SuppressWarnings("unchecked")
	public static boolean isOcelotBlockingChest(World world, int x, int y, int z) {
		Iterator<EntityOcelot> iterator = world.getEntitiesWithinAABB(EntityOcelot.class, AxisAlignedBB.getBoundingBox(x, y + 1, z, x + 1, y + 2, z + 1)).iterator();

		do
			if (!iterator.hasNext())
				return false;
		while (!iterator.next().isSitting());

		return true;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
		return Container.calcRedstoneFromInventory(getInventory(world, x, y, z));
	}
}
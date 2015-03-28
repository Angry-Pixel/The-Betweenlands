package thebetweenlands.blocks;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSwampReed extends BlockBush implements IPlantable {

	private IIcon top, bottom;
	private final String name;
	Random rnd = new Random();

	protected BlockSwampReed(String name) {
		super(Material.coral);
		this.setTickRandomly(true);
		this.name = name;
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		float w = (1F - 0.8F) / 2F;
		setBlockBounds(w, 0, w, 0.8F + w, 1, 0.8F + w);
		setBlockName("thebetweenlands." + name.substring(0, 1).toLowerCase() + name.substring(1));
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED, 1 + fortune));
		return drops;
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		this.checkBlock(world, x, y, z);
	}

	protected final boolean checkBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
			return false;
		} else {
			return true;
		}
	}
	
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
		boolean hasWater = (world.getBlock(x - 1, y - 1, z).getMaterial() == Material.water || world.getBlock(x + 1, y - 1, z).getMaterial() == Material.water || world.getBlock(x, y - 1, z - 1).getMaterial() == Material.water || world.getBlock(x, y - 1, z + 1).getMaterial() == Material.water);
        return canPlaceBlockOn(world.getBlock(x, y - 1, z)) && hasWater;
    }

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
		if (!world.isRemote) {
			int l = ((MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 2) % 4;
			int size = 1 + rnd.nextInt(4);
			for (int j = 1; j < size; j++)
				world.setBlock(x, y + j, z, this, 0, 2);
			world.setBlock(x, y + size, z, this, 8 | l, 2);
		}
		else world.setBlock(x, y, z, Blocks.air, 0, 2);
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		super.checkAndDropBlock(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) != this)
			return super.canBlockStay(world, x, y, z);
		int l = world.getBlockMetadata(x, y, z);
		return l != 0 ? world.getBlock(x, y - 1, z) == this : world.getBlock(x, y - 1, z) == this || this.canPlaceBlockOn(world.getBlock(x, y - 1, z));
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.DOUBLE_PLANTS.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return top;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return (world.getBlockMetadata(x, y, z) & 8) != 0 ? top : bottom;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		top = reg.registerIcon("thebetweenlands:doublePlant" + name + "Top");
		bottom = reg.registerIcon("thebetweenlands:doublePlant" + name + "Bottom");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		return 0xFFFFFF;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Beach;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}
}

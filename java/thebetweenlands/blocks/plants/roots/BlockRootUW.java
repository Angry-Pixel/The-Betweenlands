package thebetweenlands.blocks.plants.roots;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.render.block.water.WaterRootRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;

import java.util.Random;

public class BlockRootUW extends BlockSwampWater {
	public IIcon iconRoot;

	public BlockRootUW() {
		super(BLFluidRegistry.swampWaterRoot, Material.water);
		setStepSound(Block.soundTypeWood);
		setBlockName("thebetweenlands.rootUW");
		setHardness(1.0F);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		setTickRandomly(true);
		setBlockTextureName("thebetweenlands:weedwoodBark");
		this.canSpread = false;
		this.hasBoundingBox = true;
		this.canCollide = true;
		this.setSpecialRenderer(new WaterRootRenderer());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.iconRoot = iconRegister.registerIcon("thebetweenlands:weedwoodBark");
		super.registerBlockIcons(iconRegister);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z) && this.canPlaceBlockOn(world, world.getBlock(x, y-1, z), x, y-1, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.canPlaceBlockOn(world, world.getBlock(x, y-1, z), x, y-1, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		this.checkAndDropBlock(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		this.checkAndDropBlock(world, x, y, z);			
	}
	
	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.TANGLED_ROOT).getItemDamage();
	}

	@Override
	public int quantityDropped(Random rnd) {
        return 1;
    }
	
	@Override
	public int damageDropped(int p_149692_1_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.TANGLED_ROOT).getItemDamage();
    }
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.TANGLED_ROOT).getItem();
	}

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return false;
    }
	
    @Override
    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        return true;
    }
    
	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
			world.notifyBlockChange(x, y, z, Blocks.air);
		}
	}

	protected boolean canPlaceBlockOn(World world, Block block, int x, int y, int z) {
		return block instanceof BlockRoot || block == this || block.isSideSolid(world, x, y, z, ForgeDirection.UP);
	}
}

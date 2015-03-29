package thebetweenlands.blocks.plants.roots;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.render.block.water.WaterRootRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRootUW extends BlockSwampWater {
	public IIcon iconRoot;

	public BlockRootUW() {
		super(BLFluidRegistry.swampWaterRoot, Material.water);
		setStepSound(Block.soundTypeWood);
		setBlockName("thebetweenlands.rootUW");
		setHardness(1.0F);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockBounds(0.1f, 0.0f, 0.1f, 1.0f, 0.9f, 0.9f);
		setTickRandomly(true);
		setBlockTextureName("thebetweenlands:weedWoodBark");
		this.canSpread = false;
		this.hasBoundingBox = true;
		this.canCollide = true;
		this.setSpecialRenderer(new WaterRootRenderer());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.iconRoot = iconRegister.registerIcon("thebetweenlands:weedWoodBark");
		super.registerBlockIcons(iconRegister);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z) && this.canPlaceBlockOn(world.getBlock(x, y-1, z));
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.canPlaceBlockOn(world.getBlock(x, y-1, z));
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
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return false;
    }
	
	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
			world.notifyBlockChange(x, y, z, Blocks.air);
		}
	}

	protected boolean canPlaceBlockOn(Block block) {
		return block instanceof BlockRootUW || block == BLBlockRegistry.mud;
	}
}

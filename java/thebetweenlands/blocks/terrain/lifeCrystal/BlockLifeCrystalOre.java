package thebetweenlands.blocks.terrain.lifeCrystal;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.render.block.water.LifeCrystalRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;

public class BlockLifeCrystalOre extends BlockSwampWater {
	@SideOnly(Side.CLIENT)
	public IIcon iconBackground, iconGlowing;

	public BlockLifeCrystalOre() {
		super(BLFluidRegistry.swampWaterLifeCrystal, Material.rock);
		setStepSound(Block.soundTypeStone);
		setBlockName("thebetweenlands.lifeCrystalOre");
		setHardness(1.0F);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
		setTickRandomly(true);
		setBlockTextureName("thebetweenlands:pitstone");
		this.canSpread = false;
		this.hasBoundingBox = true;
		this.canCollide = true;
		this.canReplenish = false;
		this.setSpecialRenderer(new LifeCrystalRenderer());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		this.iconBackground = iconRegister.registerIcon("thebetweenlands:lifeCrystalBackground");
		this.iconGlowing = iconRegister.registerIcon("thebetweenlands:lifeCrystalGlowing");
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
	public int quantityDropped(Random rnd) {
		return 1;
	}

	@Override
	public Item getItemDropped(int meta, Random rnd, int fortune) {
		return meta == 1 ? BLItemRegistry.lifeCrystal : null;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, BLBlockRegistry.swampWater, 0, 2);
			world.notifyBlockChange(x, y, z, BLBlockRegistry.swampWater);
		}
	}

	protected boolean canPlaceBlockOn(World world, Block block, int x, int y, int z) {
		return block == this || block.isSideSolid(world, x, y, z, ForgeDirection.UP);
	}
}

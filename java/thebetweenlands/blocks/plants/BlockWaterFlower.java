package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.BLCreativeTabs;

import java.util.Random;

public class BlockWaterFlower extends BlockBush implements IPlantable {
	public BlockWaterFlower() {
		super(Material.plants);
		this.setTickRandomly(true);
		setCreativeTab(BLCreativeTabs.plants);
		setHardness(0.5F);
		setStepSound(Block.soundTypeGrass);
		setBlockBounds(0.1f, 0, 0.1f, 0.9f, 1, 0.9f);
		setBlockName("thebetweenlands.waterFlower");
		setBlockTextureName("thebetweenlands:waterFlower");
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
	}
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random random, int meta) {
        return null;
    }
	
	@Override
	public boolean canPlaceBlockOn(Block block) {
		return block == BLBlockRegistry.waterFlowerStalk;
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
		return canPlaceBlockOn(world.getBlock(x, y - 1, z));
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		super.checkAndDropBlock(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) != this)
			return super.canBlockStay(world, x, y, z);
		return world.getBlock(x, y - 1, z) == this || this.canPlaceBlockOn(world.getBlock(x, y - 1, z));
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
	
	public static void generateFlowerPatch(World world, int x, int y, int z, int tries, int radius) {
		for(int i = 0; i < tries; i++) {
			int bx = x + world.rand.nextInt(radius) - radius/2;
			int by = y + world.rand.nextInt(radius) - radius/2;
			int bz = z + world.rand.nextInt(radius) - radius/2;
			if(Math.sqrt((bx-x)*(bx-x)+(by-y)*(by-y)+(bz-z)*(bz-z)) <= radius) {
				Block cBlock = world.getBlock(bx, by, bz);
				if(BLBlockRegistry.waterFlowerStalk.canPlaceBlockOn(cBlock)) {
					generateFlower(world, bx, by+1, bz);
				}
			}
		}
	}
	
	public static void generateFlower(World world, int x, int y, int z) {
		if(!world.isRemote) {
			int yo = 0;
			for(int i = 0; i < 255; i++) {
				Block cBlock = world.getBlock(x, y+yo, z);
				if(cBlock == BLBlockRegistry.swampWater) {
					world.setBlock(x, y+yo, z, BLBlockRegistry.waterFlowerStalk);
				} else {
					world.setBlock(x, y+yo, z, BLBlockRegistry.waterFlower);
					break;
				}
				++yo;
			}
		}
	}
		
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.rand.nextInt(40) == 0) {
			BLParticle.MOTH.spawn(world, x, y + 1.5, z, 0.0D, 0.0D, 0.0D, 0);
		}
	}
}

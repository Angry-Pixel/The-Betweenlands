package thebetweenlands.blocks.plants.roots;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRoot extends Block {
	public BlockRoot() {
		super(Material.wood);
		this.setTickRandomly(true);
		setCreativeTab(ModCreativeTabs.blocks);
		setHardness(1.0F);
		setStepSound(Block.soundTypeWood);
		//setBlockBounds(0.1f, 0, 0.1f, 0.9f, 1, 0.9f);
		setBlockName("thebetweenlands.root");
		setBlockTextureName("thebetweenlands:weedWoodBark");
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
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return null;
	}
    
	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
			world.notifyBlockChange(x, y, z, Blocks.air);
		}
	}

	protected boolean canPlaceBlockOn(Block block) {
		return block instanceof BlockRootUW || block == this || block.isOpaqueCube();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public int getRenderType() {
		return BlockRenderIDs.ROOT.id();
	}
	
	@Override
    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        return true;
    }
	
	public static void generateWaterRootPatch(World world, int x, int y, int z, int tries, int radius) {
		for(int i = 0; i < tries; i++) {
			int bx = x + world.rand.nextInt(radius) - radius/2;
			int by = y + world.rand.nextInt(radius) - radius/2;
			int bz = z + world.rand.nextInt(radius) - radius/2;
			if(Math.sqrt((bx-x)*(bx-x)+(by-y)*(by-y)+(bz-z)*(bz-z)) <= radius) {
				Block cBlock = world.getBlock(bx, by, bz);
				Block blockAbove = world.getBlock(bx, by+1, bz);
				Block blockAbove2 = world.getBlock(bx, by+2, bz);
				if(cBlock == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == BLBlockRegistry.swampWater) {
					generateRoot(world, bx, by+1, bz, world.rand.nextInt(6) + 2);
				}
			}
		}
	}
	
	public static void generateRootPatch(World world, int x, int y, int z, int tries, int radius) {
		for(int i = 0; i < tries; i++) {
			int bx = x + world.rand.nextInt(radius) - radius/2;
			int by = y + world.rand.nextInt(radius) - radius/2;
			int bz = z + world.rand.nextInt(radius) - radius/2;
			if(Math.sqrt((bx-x)*(bx-x)+(by-y)*(by-y)+(bz-z)*(bz-z)) <= radius) {
				Block cBlock = world.getBlock(bx, by, bz);
				Block blockAbove = world.getBlock(bx, by+1, bz);
				Block blockAbove2 = world.getBlock(bx, by+2, bz);
				boolean hasSpace = blockAbove == Blocks.air && blockAbove2 == Blocks.air;
				if((cBlock == BLBlockRegistry.swampGrass || cBlock == BLBlockRegistry.deadGrass) && hasSpace) {
					generateRoot(world, bx, by+1, bz, world.rand.nextInt(6) + 2);
				}
			}
		}
	}
	
	public static void generateRoot(World world, int x, int y, int z, int height) {
		if(!world.isRemote) {
			for(int yo = 0; yo < height; yo++) {
				Block cBlock = world.getBlock(x, y+yo, z);
				if(cBlock != Blocks.air && cBlock != BLBlockRegistry.swampWater) {
					break;
				}
				if(cBlock == BLBlockRegistry.swampWater) {
					world.setBlock(x, y+yo, z, BLBlockRegistry.rootUW);
				} else {
					world.setBlock(x, y+yo, z, BLBlockRegistry.root);
				}
			}
		}
	}
}

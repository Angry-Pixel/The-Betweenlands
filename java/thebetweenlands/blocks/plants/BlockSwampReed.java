package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.ArrayList;
import java.util.Random;

public class BlockSwampReed extends BlockBush implements IPlantable {
	public IIcon top, bottom;

	public BlockSwampReed() {
		super(Material.coral);
		this.setTickRandomly(true);
		setCreativeTab(ModCreativeTabs.plants);
		setHardness(0.5F);
		setStepSound(Block.soundTypeGrass);
		setBlockBounds(0.1f, 0, 0.1f, 0.9f, 1, 0.9f);
		setBlockName("thebetweenlands.swampReedBlock");
		setTickRandomly(true);
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
	public boolean canPlaceBlockOn(Block block) {
		return block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass || block == BLBlockRegistry.silt || block == BLBlockRegistry.swampReedUW || block == BLBlockRegistry.swampReed || block == BLBlockRegistry.mud || block == BLBlockRegistry.deadGrass;
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

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		boolean hasWater = (world.getBlock(x - 1, y - 1, z).getMaterial() == Material.water || world.getBlock(x + 1, y - 1, z).getMaterial() == Material.water || world.getBlock(x, y - 1, z - 1).getMaterial() == Material.water || world.getBlock(x, y - 1, z + 1).getMaterial() == Material.water);
		return canPlaceBlockOn(world.getBlock(x, y - 1, z)) && hasWater;
	}

	public static void generateReedPatch(World world, int x, int y, int z, int tries, int radius) {
		for(int i = 0; i < tries; i++) {
			int bx = x + world.rand.nextInt(radius) - radius/2;
			int by = y + world.rand.nextInt(radius) - radius/2;
			int bz = z + world.rand.nextInt(radius) - radius/2;
			if(Math.sqrt((bx-x)*(bx-x)+(by-y)*(by-y)+(bz-z)*(bz-z)) <= radius) {
				Block cBlock = world.getBlock(bx, by, bz);
				Block blockAbove = world.getBlock(bx, by+1, bz);
				Block blockAbove2 = world.getBlock(bx, by+2, bz);
				if(cBlock == BLBlockRegistry.mud && blockAbove == BLBlockRegistry.swampWater && blockAbove2 == Blocks.air) {
					BlockSwampReed.generateReed(world, bx, by+1, bz);
				} else if(cBlock.isOpaqueCube() && blockAbove == Blocks.air && blockAbove2 == Blocks.air) {
					if(BLBlockRegistry.swampReed.canPlaceBlockAt(world, bx, by+1, bz)) {
						BlockSwampReed.generateReed(world, bx, by+1, bz);
					}
				}
			}
		}
	}

	public static void generateReed(World world, int x, int y, int z) {
		if(!world.isRemote) {
			int height = world.rand.nextInt(4) + 2;
			for(int yo = 0; yo < height; yo++) {
				Block cBlock = world.getBlock(x, y+yo, z);
				if(cBlock != Blocks.air && cBlock != BLBlockRegistry.swampWater) {
					break;
				}
				if(cBlock == BLBlockRegistry.swampWater) {
					world.setBlock(x, y+yo, z, BLBlockRegistry.swampReedUW);
				} else {
					world.setBlock(x, y+yo, z, BLBlockRegistry.swampReed);
				}
			}
		}
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
	public int getRenderType() {
		return BlockRenderIDs.SWAMP_REED.id();
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
		top = reg.registerIcon("thebetweenlands:swampReedTop");
		bottom = reg.registerIcon("thebetweenlands:swampReedBottom");
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

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.isAirBlock(x, y + 1, z)) {
			int reedHeight;
			for (reedHeight = 1; world.getBlock(x, y - reedHeight, z) == this; ++reedHeight) {;}
			if (reedHeight < 3) {
				int meta = world.getBlockMetadata(x, y, z);
				if (meta == 15) {
					world.setBlock(x, y + 1, z, this);
					world.setBlockMetadataWithNotify(x, y, z, 0, 4);
				} else {
					world.setBlockMetadataWithNotify(x, y, z, meta + 1, 4);
				}
			}
		}
	}
}

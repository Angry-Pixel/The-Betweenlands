package thebetweenlands.blocks.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPuddle extends Block {
	public BlockPuddle() {
		super(Material.ground);
		setHardness(0.1F);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.puddle");
		setBlockBounds(0, 0.0F, 0, 1.0F, 0.07F, 1.0F);
		setTickRandomly(true);
		setBlockTextureName("thebetweenlands:betweenstone");
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rnd) {
		if(!world.isRemote) {
			if(world.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
				EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
				if(!eeRegistry.HEAVY_RAIN.isActive()) {
					world.setBlock(x, y, z, Blocks.air);
				} else if(world.canBlockSeeTheSky(x, y, z)) {
					world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + rnd.nextInt(6), 2);
				}
			} else {
				world.setBlock(x, y, z, Blocks.air);
			}
			if(world.getBlockMetadata(x, y, z) > 2) {
				world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 3, 2);
				for(int xo = -1; xo <= 1; xo++) {
					for(int zo = -1; zo <= 1; zo++) {
						if((xo == 0 && zo == 0) || xo*xo == zo*zo) continue;
						if(world.isAirBlock(x+xo, y, z+zo) && BLBlockRegistry.puddle.canPlaceBlockAt(world, x+xo, y, z+zo)) {
							world.setBlock(x+xo, y, z+zo, BLBlockRegistry.puddle);
						} else if(world.getBlock(x+xo, y, z+zo) == BLBlockRegistry.puddle) {
							world.setBlockMetadataWithNotify(x+xo, y, z+zo, world.getBlockMetadata(x+xo, y, z+zo) + rnd.nextInt(6), 2);
						}
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
		if(blockAccess.getBlock(x, y, z) == this) {
			return false;
		}
		return super.shouldSideBeRendered(blockAccess, x, y, z, side);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return null;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return World.doesBlockHaveSolidTopSurface(world, x, y-1, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return World.doesBlockHaveSolidTopSurface(world, x, y-1, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if(!World.doesBlockHaveSolidTopSurface(world, x, y-1, z)) {
			world.setBlock(x, y, z, Blocks.air);
		}
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return ((BlockSwampWater)BLBlockRegistry.swampWater).stillIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;

		for (int xOff = -1; xOff <= 1; ++xOff) {
			for (int yOff = -1; yOff <= 1; ++yOff) {
				int colorMultiplier = blockAccess.getBiomeGenForCoords(x + yOff, z + xOff).getWaterColorMultiplier();
				avgRed += (colorMultiplier & 16711680) >> 16;
			avgGreen += (colorMultiplier & 65280) >> 8;
			avgBlue += colorMultiplier & 255;
			}
		}

		return (avgRed / 9 & 255) << 16 | (avgGreen / 9 & 255) << 8 | avgBlue / 9 & 255;
	}

	@Override
	public boolean canCollideCheck(int meta, boolean fullHit) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}
}
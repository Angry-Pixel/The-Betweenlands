package thebetweenlands.blocks.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import thebetweenlands.blocks.BLFluidRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSwampWater extends BlockFluidClassic {
	@SideOnly(Side.CLIENT)
	protected IIcon stillIcon, flowingIcon;

	public BlockSwampWater() {
		super(BLFluidRegistry.swampWater, Material.water);
		setBlockName("thebetweenlands.swampWater");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 0 || side == 1 ? stillIcon : flowingIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		this.stillIcon = register.registerIcon("thebetweenlands:swampWater");
		this.flowingIcon = register.registerIcon("thebetweenlands:swampWaterFlowing");
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid())
			return false;
		return super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z).getMaterial().isLiquid())
			return false;
		return super.displaceIfPossible(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		if (this.blockMaterial != Material.water) {
			return 16777215;
		} else {
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
	}
}
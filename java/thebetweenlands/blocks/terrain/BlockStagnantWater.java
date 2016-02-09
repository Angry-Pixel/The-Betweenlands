package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.decay.DecayManager;
import thebetweenlands.recipes.BLMaterial;

/**
 * Created by Bart on 29-8-2015.
 */
public class BlockStagnantWater extends BlockFluidClassic {
	@SideOnly(Side.CLIENT)
	protected IIcon stillIcon, flowingIcon;
	public BlockStagnantWater() {
		super(BLFluidRegistry.stagnantWater, BLMaterial.stagnantWater);
		setBlockName("thebetweenlands.stagnantWater");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 0 || side == 1 ? stillIcon : flowingIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		stillIcon = register.registerIcon("thebetweenlands:stagnantWater");
		flowingIcon = register.registerIcon("thebetweenlands:stagnantWaterFlowing");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (entity instanceof EntityPlayer) {
			if(world.rand.nextInt(16) == 0) {
				DecayManager.getDecayStats((EntityPlayer)entity).addExhaustion(4.0F);
			}
		}
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.canDisplace(world, x, y, z);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z) {
		return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.displaceIfPossible(world, x, y, z);
	}
}

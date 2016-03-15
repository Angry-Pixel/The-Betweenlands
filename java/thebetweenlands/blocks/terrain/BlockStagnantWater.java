package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.decay.DecayManager;
import thebetweenlands.herblore.elixirs.ElixirEffectRegistry;

/**
 * Created by Bart on 29-8-2015.
 */
public class BlockStagnantWater extends BlockFluidClassic {
	@SideOnly(Side.CLIENT)
	protected IIcon stillIcon, flowingIcon;

	public BlockStagnantWater() {
		super(BLFluidRegistry.stagnantWater, Material.water);
		this.setBlockName("thebetweenlands.stagnantWater");
		this.setLightLevel(0.5F);
		this.setMaxScaledLight(0);
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
		if (entity instanceof EntityPlayer && !world.isRemote && !((EntityPlayer)entity).isPotionActive(ElixirEffectRegistry.EFFECT_DECAY.getPotionEffect())) {
			((EntityPlayer)entity).addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(60, 3));
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

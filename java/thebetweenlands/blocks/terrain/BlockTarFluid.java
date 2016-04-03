package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.items.BLMaterial;

public class BlockTarFluid extends BlockFluidClassic {

	@SideOnly(Side.CLIENT)
	protected IIcon stillIcon, flowingIcon;

	public BlockTarFluid() {
		super(BLFluidRegistry.tarFluid, BLMaterial.tar);
		setBlockName("thebetweenlands.tarFluid");
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 0 || side == 1 ? stillIcon : flowingIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		stillIcon = register.registerIcon("thebetweenlands:tar");
		flowingIcon = register.registerIcon("thebetweenlands:tarFlowing");
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

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if (entity instanceof EntityLivingBase && !(entity instanceof EntityTarBeast)) {
			entity.motionX *= 0.005D;
			entity.motionZ *= 0.005D;
			if(entity.motionY < 0)
				entity.motionY *= 0.005D;
			Block blockAbove = world.getBlock(x, y + 1, z);
			if(blockAbove.getMaterial() == BLMaterial.tar && entity.isInsideOfMaterial(BLMaterial.tar)) {
				((EntityLivingBase) entity).attackEntityFrom(DamageSource.drown, 2.0F);
			}
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		this.solidifyTar(world, x, y, z);
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		this.solidifyTar(world, x, y, z);
		super.onNeighborBlockChange(world, x, y, z, block);
	}

	private void solidifyTar(World world, int x, int y, int z) {
		if (world.getBlock(x, y, z) == this) {
			boolean placeTar = false;

			if (!placeTar && world.getBlock(x, y, z - 1).getMaterial() == Material.water)
				placeTar = true;

			if (!placeTar && world.getBlock(x, y, z + 1).getMaterial() == Material.water)
				placeTar = true;

			if (!placeTar && world.getBlock(x - 1, y, z).getMaterial() == Material.water)
				placeTar = true;

			if (!placeTar && world.getBlock(x + 1, y, z).getMaterial() == Material.water)
				placeTar = true;

			if (!placeTar && world.getBlock(x, y + 1, z).getMaterial() == Material.water)
				placeTar = true;

			if (!placeTar && world.getBlock(x, y - 1, z).getMaterial() == Material.water) {
				//Set water block below to solid tar
				world.setBlock(x, y - 1, z, BLBlockRegistry.solidTar);
			}

			if (placeTar) {
				world.setBlock(x, y, z, BLBlockRegistry.solidTar);
				if(world.isRemote) {
					playEffects(world, x, y, z);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void playEffects(World world, int x, int y, int z) {
		world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

		for (int l = 0; l < 8; ++l) {
			world.spawnParticle("largesmoke", (double) x + Math.random(), (double) y + 1.2D, (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
		}
	}
}
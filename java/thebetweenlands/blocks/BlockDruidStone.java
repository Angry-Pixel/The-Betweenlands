package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.creativetabs.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDruidStone
        extends Block
{
	private String type;
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon, sideIconActive;

	public BlockDruidStone(String blockName) {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setLightLevel(0.8F);
		setCreativeTab(ModCreativeTabs.blocks);
        this.type = blockName;
		setBlockName("thebetweenlands." + this.type);
		setBlockTextureName("thebetweenlands:" + this.type);
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + this.type));
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack is) {
		int rot = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rot, 3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		switch(meta) {
		case 0:
			return side == 3 ? this.sideIcon : this.blockIcon;
		case 1:
			return side == 4 ? this.sideIcon : this.blockIcon;
		case 2:
			return side == 2 ? this.sideIcon : this.blockIcon;
		case 3:
			return side == 5 ? this.sideIcon : this.blockIcon;
		case 4:
			return side == 3 ? this.sideIconActive : this.blockIcon;
		case 5:
			return side == 4 ? this.sideIconActive : this.blockIcon;
		case 6:
			return side == 2 ? this.sideIconActive : this.blockIcon;
		case 7:
			return side == 5 ? this.sideIconActive : this.blockIcon;
		default :
			return this.blockIcon;
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("stone");
        this.sideIcon = reg.registerIcon(getTextureName());
        this.sideIconActive = reg.registerIcon(getTextureName()+"Active");
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		Random random = world.rand;
		double pixel = 0.0625D;
		if(world.getBlockMetadata(x, y, z) <= 3) {
			for (int l = 0; l < 5; l++) {
				double particleX = x + random.nextFloat();
				double particleY = y + random.nextFloat();
				double particleZ = z + random.nextFloat();

				if (l == 0 && !world.getBlock(x, y + 1, z).isOpaqueCube())
					particleY = y + 1 + pixel;

				if (l == 1 && !world.getBlock(x, y - 1, z).isOpaqueCube())
					particleY = y - pixel;

				if (l == 2 && !world.getBlock(x, y, z + 1).isOpaqueCube())
					particleZ = z + 1 + pixel;

				if (l == 3 && !world.getBlock(x, y, z - 1).isOpaqueCube())
					particleZ = z - pixel;

				if (l == 4 && !world.getBlock(x + 1, y, z).isOpaqueCube())
					particleX = x + 1 + pixel;

				if (l == 5 && !world.getBlock(x - 1, y, z).isOpaqueCube())
					particleX = x - pixel;

				if (particleX < x || particleX > x + 1 || particleY < y || particleY > y + 1 || particleZ < z || particleZ > z + 1)
					TheBetweenlands.proxy.spawnCustomParticle("druidmagicbig", world, particleX, particleY, particleZ, (random.nextFloat() - random.nextFloat()) *0.1, 0, (random.nextFloat() - random.nextFloat())*0.1, rand.nextFloat() + 0.5F);
			}
		}
	}
}

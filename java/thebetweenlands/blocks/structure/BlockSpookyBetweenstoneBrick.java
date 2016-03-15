package thebetweenlands.blocks.structure;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.BLCreativeTabs;

import java.util.Random;

public class BlockSpookyBetweenstoneBrick
        extends Block
{
	private String type;
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon, sideIconActive;

	public BlockSpookyBetweenstoneBrick(String blockName) {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(BLCreativeTabs.blocks);
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
		System.out.println("Meta: " + rot);
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
        this.blockIcon = reg.registerIcon("thebetweenlands:betweenstoneBricks");
        this.sideIcon = reg.registerIcon(getTextureName());
        this.sideIconActive = reg.registerIcon(getTextureName()+"Active");
	}
	
}

package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockTemplePillar extends BlockRotatedPillar {

	public BlockTemplePillar() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.templePillar");
		setBlockTextureName("thebetweenlands:templePillar");
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected IIcon getSideIcon(int side) {
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		field_150164_N = reg.registerIcon("thebetweenlands:dungeonTile");
	}
}
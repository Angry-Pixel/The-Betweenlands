package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockWeedWoodPlanks extends Block {
	public IIcon icon;

	public BlockWeedWoodPlanks() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setBlockName("thebetweenlands.weedwoodPlanks");
		setBlockTextureName("thebetweenlands:weedwoodPlanks");
		setCreativeTab(ModCreativeTabs.blocks);
		setHardness(2.0F);
		setResistance(5.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		icon = register.registerIcon("thebetweenlands:weedwoodPlanks");
	}

}
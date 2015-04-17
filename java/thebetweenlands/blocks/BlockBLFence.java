package thebetweenlands.blocks;

import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import thebetweenlands.creativetabs.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBLFence extends BlockFence {
	
	private final String textureName;
	
	public BlockBLFence(String name, Material material) {
		super(name, material);
		setCreativeTab(ModCreativeTabs.blocks);
		textureName = "thebetweenlands:" + name;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon) {
		blockIcon = icon.registerIcon(textureName);
	}

}

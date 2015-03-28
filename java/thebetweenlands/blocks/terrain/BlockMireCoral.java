package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.client.render.block.water.WaterMireCoralRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockMireCoral extends BlockSwampWater {
	public IIcon iconMireCoral;
	
	public BlockMireCoral() {
		super(BLFluidRegistry.swampWaterMireCoral, Material.water);
		setBlockName("thebetweenlands.mireCoral");
		setHardness(0.5F);
		setCreativeTab(ModCreativeTabs.blocks);
		this.canSpread = false;
		this.setSpecialRenderer(new WaterMireCoralRenderer());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.iconMireCoral = iconRegister.registerIcon("thebetweenlands:mireCoral");
		super.registerBlockIcons(iconRegister);
	}
}

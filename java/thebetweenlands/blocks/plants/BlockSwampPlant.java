package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.Random;

public class BlockSwampPlant extends BlockBLSmallPlants {
	public IIcon modelTexture1;
	
	public BlockSwampPlant() {
		super("swampPlant");
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture1 = reg.registerIcon("thebetweenlands:swampPlant");
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}
}

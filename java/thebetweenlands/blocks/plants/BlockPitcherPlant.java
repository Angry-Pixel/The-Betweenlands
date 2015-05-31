package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.Random;

public class BlockPitcherPlant extends BlockBLSmallPlants {
	public IIcon modelTexture1;
	
	public BlockPitcherPlant() {
		super("pitcherPlant");
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture1 = reg.registerIcon("thebetweenlands:pitcherPlant");
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}
}

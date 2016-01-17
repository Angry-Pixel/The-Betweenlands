package thebetweenlands.blocks.container;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockSign;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.tileentities.TileEntityBLSign;

/**
 * Created by Bart on 16/01/2016.
 */
public class BlockBLSign extends BlockSign {
	public final String material;

	public BlockBLSign(boolean standing, String material) {
		super(TileEntityBLSign.class, standing);
		this.setBlockName("thebetweenlands." + material + "Sign" + (standing ? "Standing" : "Wall"));
		this.material = material;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return BLItemRegistry.weedwoodSign;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return BLItemRegistry.weedwoodSign;
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("thebetweenlands:" + this.material + "Planks");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int meta, int side) {
		return this.blockIcon;
	}
}

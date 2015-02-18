package thebetweenlands.blocks.terrain;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLBlockRegistry.ISubBlocksBlock;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemBlockGeneric;

import java.util.List;

public class BlockBetweenstone
        extends Block
{
	public BlockBetweenstone() {
		super(Material.rock);
		setHardness(1.5F);
		setResistance(10.0F);
		setStepSound(soundTypeStone);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.betweenstone");
		setBlockTextureName("thebetweenlands:betweenstone");
	}
}

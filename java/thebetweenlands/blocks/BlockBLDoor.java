package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

public class BlockBLDoor extends BlockDoor {
	public final String name;
	private Item item;
	private int renderPass = 0;

	public BlockBLDoor(String name, Material material) {
		super(material);
		disableStats();
		this.name = name;
		setHardness(3.0F);
		setStepSound(soundTypeWood);
		setBlockName("thebetweenlands.door" + name);
		setBlockTextureName("thebetweenlands:door_" + name);
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public BlockBLDoor setRenderPass(int pass) {
		renderPass = pass;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return renderPass;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return item;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return (meta & 8) != 0 ? null : item;
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.DOOR.id();
	}
}
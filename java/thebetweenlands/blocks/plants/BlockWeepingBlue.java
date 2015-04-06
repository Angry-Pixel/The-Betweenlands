package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWeepingBlue extends BlockBLSmallPlants {
	public IIcon modelTexture1;

	public BlockWeepingBlue() {
		super("weepingBlue");
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture1 = reg.registerIcon("thebetweenlands:weepingBlue");
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(BLItemRegistry.weepingBluePetal, 1 + world.rand.nextInt(3) + fortune));
		return drops;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x, y + 1.99F, z, x + 1, y + 2, z + 1);
	}
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		world.spawnParticle("dripWater", x + 0.6F + rand.nextFloat()/3F*(rand.nextInt(2) == 0 ? 1 : -1), y + 1.6F + rand.nextFloat()/2F, z + 0.6F + rand.nextFloat()/3F*(rand.nextInt(2) == 0 ? 1 : -1), 0D, 0D, 0D);
	}
}

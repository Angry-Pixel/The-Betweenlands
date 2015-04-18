package thebetweenlands.blocks.plants;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockThorns extends BlockVine implements IShearable {

	public BlockThorns() {
		super();
		setHardness(0.2F);
		setBlockName("thebetweenlands.thorns");
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		setBlockTextureName("thebetweenlands:thorns");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		entity.attackEntityFrom(DamageSource.cactus, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		return 0xFFFFFF;
	}
}
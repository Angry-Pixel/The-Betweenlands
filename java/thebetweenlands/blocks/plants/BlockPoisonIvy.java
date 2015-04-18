package thebetweenlands.blocks.plants;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import net.minecraft.util.DamageSource;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class BlockPoisonIvy extends BlockVine implements IShearable {

	public BlockPoisonIvy() {
		super();
		setHardness(0.2F);
		setBlockName("thebetweenlands.poisonIvy");
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		setBlockTextureName("thebetweenlands:poisonIvy");
	}

	@Override
	 public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if(entity instanceof EntityLivingBase){
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.getId(), 50, 25));			
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		return 0xFFFFFF;
	}
}
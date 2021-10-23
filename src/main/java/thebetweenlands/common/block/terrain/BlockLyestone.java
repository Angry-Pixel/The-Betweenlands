package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.SoundRegistry;

public class BlockLyestone extends Block {

	public BlockLyestone(Material materialIn) {
		super(materialIn);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setSoundType(SoundType.STONE);
		setHardness(1.2F);
		setResistance(8.0F);
	}

	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote && world.getTotalWorldTime()% 40 == 0)
			world.playSound(null, pos, SoundRegistry.LYESTONE_FIZZ, SoundCategory.BLOCKS, 1F, 1F);
		
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
			if(!boots.isEmpty()) {
				if(!boots.hasTagCompound())
					boots.setTagCompound(new NBTTagCompound());
				boots.getTagCompound().setBoolean("corrosive", true);
			}
			else
				causeDamage(world, pos, entity);
		}
		else
			causeDamage(world, pos, entity);
		
		super.onEntityWalk(world, pos, entity);
	}

	public void causeDamage (World world, BlockPos pos, Entity entity) {
		if (!entity.isImmuneToFire() && entity instanceof EntityLivingBase && !EnchantmentHelper.hasFrostWalkerEnchantment((EntityLivingBase) entity))
			entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		double posX = (double) pos.getX();
		double posY = (double) pos.getY();
		double posZ = (double) pos.getZ();

		if (rand.nextInt(10) == 0) {
			boolean stateBelow = world.isAirBlock(pos.down());
			if (stateBelow) {
				double x = posX + (double) rand.nextFloat();
				double y = posY - 0.05D;
				double z = posZ + (double) rand.nextFloat();
				BLParticles.CAVE_WATER_DRIP.spawn(world, x, y, z).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			}
		}

		BLParticles.FANCY_BUBBLE.spawn(world, posX + 0.5D, posY + 0.5D, posZ + 0.5D);
	}
}

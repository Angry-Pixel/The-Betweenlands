package thebetweenlands.common.item.tools;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemVoodooDoll extends Item {
	public ItemVoodooDoll() {
		maxStackSize = 1;
		setMaxDamage(24);
		setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 40;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase user) {
		if(user instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) user;

			List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(player.posX, player.posY, player.posZ, player.posX, player.posY, player.posZ).grow(5, 5, 5));
			living.remove(player);

			boolean attacked = false;
			for (EntityLivingBase entity : living) {
				if (entity.isEntityAlive() && !(entity instanceof IBLBoss) && entity instanceof EntityPlayer == false) {
					if (!world.isRemote) {
						attacked |= entity.attackEntityFrom(DamageSource.MAGIC, 20);
					} else if (!entity.isEntityInvulnerable(DamageSource.MAGIC)) {
						attacked = true;
						for (int i = 0; i < 20; i++) {
							BLParticles.SWAMP_SMOKE.spawn(world, entity.posX, entity.posY + entity.height / 2.0D, entity.posZ, ParticleFactory.ParticleArgs.get().withMotion((world.rand.nextFloat() - 0.5F) * 0.5F, (world.rand.nextFloat() - 0.5F) * 0.5F, (world.rand.nextFloat() - 0.5F) * 0.5F).withColor(1, 1, 1, 1));
						}
					}
				}
			}

			if (!world.isRemote && attacked) {
				stack.damageItem(1, player);
				world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.VOODOO_DOLL, SoundCategory.PLAYERS, 0.5F, 1.0F - world.rand.nextFloat() * 0.3F);
			}
		}

		return stack;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}
}

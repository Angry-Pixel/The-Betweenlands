package thebetweenlands.common.item.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityAngryPebble;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemAngryPebble extends Item {
	public ItemAngryPebble() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.addPropertyOverride(new ResourceLocation("charge"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F);
		this.addPropertyOverride(new ResourceLocation("charging"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.PLAYERS, 1.0F, 0.5F);
		return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (player instanceof EntityPlayer) {
			Vec3d forward = player.getLookVec();
			float yaw = player.rotationYaw;
			float pitch = player.rotationPitch - 90;
			float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
			float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
			float f2 = -MathHelper.cos(-pitch * 0.017453292F);
			float f3 = MathHelper.sin(-pitch * 0.017453292F);
			Vec3d up = new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
			Vec3d right = forward.crossProduct(up).normalize();
			Vec3d source = player.getPositionVector().add(0, player.getEyeHeight() - 0.2F, 0).add(forward.scale(0.4F)).add(right.scale(0.3F));

			for(int i = 0; i < 5; i++) {
				player.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, source.x + player.world.rand.nextFloat() * 0.5F - 0.25F, source.y + player.world.rand.nextFloat() * 0.5F - 0.25F, source.z + player.world.rand.nextFloat() * 0.5F - 0.25F, 0, 0, 0);
			}
		}

		super.onUsingTick(stack, player, count);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (!worldIn.isRemote && entityLiving instanceof EntityPlayer) {
			int useTime = this.getMaxItemUseDuration(stack) - timeLeft;

			if(useTime > 20) {
				worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundRegistry.SORRY, SoundCategory.PLAYERS, 0.7F, 0.8F);
				EntityAngryPebble pebble = new EntityAngryPebble(worldIn, entityLiving);
				pebble.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, -10, 1.2F, 3.5F);
				worldIn.spawnEntity(pebble);

				if(!((EntityPlayer)entityLiving).isCreative()) {
					stack.shrink(1);
				}
			}
		}
	}
}
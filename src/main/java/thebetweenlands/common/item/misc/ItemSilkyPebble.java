package thebetweenlands.common.item.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntitySilkyPebble;
import thebetweenlands.common.registries.SoundRegistry;

public class ItemSilkyPebble extends ItemAngryPebble {
	public ItemSilkyPebble() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
		this.addPropertyOverride(new ResourceLocation("charge"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F);
		this.addPropertyOverride(new ResourceLocation("charging"), (stack, worldIn, entityIn) ->
				entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		if (!worldIn.isRemote && entityLiving instanceof EntityPlayer) {
			int useTime = this.getMaxItemUseDuration(stack) - timeLeft;

			if(useTime > 20) {
				worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundRegistry.SORRY, SoundCategory.PLAYERS, 0.7F, 0.8F);
				EntitySilkyPebble pebble = new EntitySilkyPebble(worldIn, entityLiving);
				pebble.shoot(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, -10, 1.2F, 3.5F);
				worldIn.spawnEntity(pebble);

				if(!((EntityPlayer)entityLiving).isCreative()) {
					stack.shrink(1);
				}
			}
		}
	}
}
package thebetweenlands.common.herblore.elixir;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thebetweenlands.common.lib.ModInfo;

public class PotionRootBound extends Potion {
	public PotionRootBound() {
		super(true, 5926017);
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "root_bound"));
		this.setPotionName("bl.potion.rootBound");
		this.setIconIndex(1, 0);
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "be8859c1-024b-4f31-b606-5991011ddd98", -1, 2);
	}

	@SubscribeEvent
	public static void onEntityJump(LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity.getActivePotionEffect(ElixirEffectRegistry.ROOT_BOUND) != null) {
			entity.motionX = 0;
			entity.motionZ = 0;
			if(entity.motionY > -0.1D) {
				entity.motionY = -0.1D;
				entity.velocityChanged = true;
			}
		}
	}

	@Override
	public boolean shouldRender(PotionEffect effect) {
		return false;
	}

	@Override
	public boolean shouldRenderHUD(PotionEffect effect) {
		return false;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Collections.emptyList();
	}

	@SubscribeEvent
	public static void onEntityLivingUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if(entity.world.isRemote()) {
			updateClientEntity(entity);
		}

		if(entity.getActivePotionEffect(ElixirEffectRegistry.ROOT_BOUND) != null) {
			entity.setInWeb();
			entity.motionX = entity.motionZ = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static void updateClientEntity(EntityLivingBase entity) {
		if(entity instanceof AbstractClientPlayer) {
			NBTTagCompound nbt = entity.getEntityData();
			if(entity.getActivePotionEffect(ElixirEffectRegistry.ROOT_BOUND) != null) {
				nbt.setInt("thebetweenlands.rootBoundTicks", 5);
			} else {
				int rootBoundTicks = nbt.getInt("thebetweenlands.rootBoundTicks");
				if(rootBoundTicks > 1) {
					nbt.setInt("thebetweenlands.rootBoundTicks", rootBoundTicks - 1);
				} else {
					nbt.removeTag("thebetweenlands.rootBoundTicks");
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onFovUpdate(FOVUpdateEvent event) {
		EntityPlayer entity = event.getEntity();
		NBTTagCompound nbt = entity.getEntityData();
		//NBT is necessary so that FOV doesn't flicker when potion wears off .-.
		if(entity.getActivePotionEffect(ElixirEffectRegistry.ROOT_BOUND) != null || nbt.contains("thebetweenlands.rootBoundTicks")) {
			event.setNewfov(1);
		}
	}
}

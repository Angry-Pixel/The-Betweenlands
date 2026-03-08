package thebetweenlands.common.item.equipment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.component.entity.PuppetData;
import thebetweenlands.common.component.entity.PuppeteerData;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceBlockade;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class RingOfRecruitmentItem extends RingItem {

	public RingOfRecruitmentItem(Properties properties) {
		super(properties);
	}

	@Override
	MutableComponent getUsageTooltip() {
		return Component.empty();
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, Container inventory) {
		if (!entity.level().isClientSide() && entity instanceof Player player) {
			PuppeteerData cap = player.getData(AttachmentRegistry.PUPPETEER);

			if (stack.has(DataComponentRegistry.RING_ACTIVE)) {
				int puppets = cap.getPuppets(player).size();

				if (puppets == 0) {
					stack.set(DataComponentRegistry.RING_ACTIVE, false);
				} else {
					stack.set(DataComponentRegistry.RING_ACTIVE, true);
				}
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, Container inventory) {
		stack.set(DataComponentRegistry.RING_ACTIVE, false);

		//Reset recruitment points
		stack.setDamageValue(0);
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, Container inventory) {
		//Set new ring UUID so that previously recruited but unloaded entities will be unlinked when they're loaded again
		this.setRingUuid(stack, UUID.randomUUID());
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.has(DataComponentRegistry.RING_ACTIVE) && stack.get(DataComponentRegistry.RING_ACTIVE);
	}

	@Override
	public void onKeybindState(Player player, ItemStack stack, Container inventory, boolean active) {
		if (!player.level().isClientSide() && active && !player.getCooldowns().isOnCooldown(ItemRegistry.RING_OF_RECRUITMENT.get())) {
			PuppeteerData cap = player.getData(AttachmentRegistry.PUPPETEER);
			List<Entity> targets = cap.getPuppets(player);
			Set<Entity> spawned = new HashSet<>();

			for (Entity target : targets) {
				PuppetData targetCap = target.getData(AttachmentRegistry.PUPPET);
				if (target.onGround() && (!targetCap.getStay() && !targetCap.getGuard() || target.distanceTo(player) < 6)) {
					List<PrimordialMalevolenceBlockade> collidingEntities = target.level().getEntitiesOfClass(PrimordialMalevolenceBlockade.class, target.getBoundingBox().inflate(0.5D));
					for (PrimordialMalevolenceBlockade collidingEntity : collidingEntities) {
						if (!spawned.contains(collidingEntity)) {
							collidingEntity.kill();
						}
					}
					PrimordialMalevolenceBlockade blockade = new PrimordialMalevolenceBlockade(target.level(), player);
					blockade.moveTo(target.getX(), target.getY() - 0.15f, target.getZ(), target.level().getRandom().nextFloat() * 360.0f, 0);
					blockade.setMaxDespawnTicks(30 + target.level().getRandom().nextInt(20));
					blockade.setTriangleSize(0.75f + target.getBbWidth() * 0.5f);
					spawned.add(blockade);
					target.level().addFreshEntity(blockade);
				}
			}

			if (!spawned.isEmpty()) {
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES, SoundSource.HOSTILE, 0.8f, 0.9f + player.level().getRandom().nextFloat() * 0.15f);
				player.getCooldowns().addCooldown(ItemRegistry.RING_OF_RECRUITMENT.get(), 40);
			}
		}
	}

	@Nullable
	public UUID getRingUuid(ItemStack stack) {
		if (stack.has(DataComponentRegistry.RING_PUPPET_UUID)) {
			return stack.get(DataComponentRegistry.RING_PUPPET_UUID);
		}
		return null;
	}

	public void setRingUuid(ItemStack stack, UUID uuid) {
		stack.set(DataComponentRegistry.RING_PUPPET_UUID, uuid);
	}

	public int getRecruitmentCost(LivingEntity target) {
		float damageMultiplier = 0.5f;
		AttributeInstance damageAttrib = target.getAttribute(Attributes.ATTACK_DAMAGE);
		if (damageAttrib != null)
			damageMultiplier = 1.0f + Math.min((float) (damageAttrib.getBaseValue() - 2.0f) / 10.0f, 0.5f);
		return Math.min(60, Math.max(Mth.floor(target.getMaxHealth() / 2.0f * damageMultiplier), 10));
	}

	public static boolean isRingActive(Entity user, @Nullable PuppetData recruited) {
		return !getActiveRing(user, recruited).isEmpty();
	}

	public static ItemStack getActiveRing(Entity user, @Nullable PuppetData recruited) {
		if (user instanceof Player player) {
			if (player.totalExperience <= 0 && player.experienceLevel <= 0 && player.experienceProgress <= 0)
				return ItemStack.EMPTY;
		}

		ItemStack ring = getEquipment(user, ItemRegistry.RING_OF_RECRUITMENT.get());

		if (!ring.isEmpty()) {
			UUID ringUuid = ((RingOfRecruitmentItem) ring.getItem()).getRingUuid(ring);
			if (recruited != null && ringUuid != null) {
				UUID recruitedRingUuid = recruited.getRingUuid();
				if (recruitedRingUuid != null && !ringUuid.equals(recruitedRingUuid))
					return ItemStack.EMPTY;
			}
			return ring;
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getEquipment(Entity entity, Item item) {
		EquipmentData data = entity.getData(AttachmentRegistry.EQUIPMENT);
		Container inv = data.getContainer(entity, EquipmentInventoryType.RING);
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.is(item)) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

}

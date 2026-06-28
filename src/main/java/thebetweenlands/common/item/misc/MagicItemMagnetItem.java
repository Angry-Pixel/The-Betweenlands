package thebetweenlands.common.item.misc;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.item.RadialMenuEquippable;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.component.item.MagicItemMagnetData;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

public class MagicItemMagnetItem extends Item implements RadialMenuEquippable {// IAnimatorRepairable {
	public MagicItemMagnetItem(Properties properties) {
		super(properties);
		//IEquippable.addEquippedPropertyOverrides(this); //dunno - is this just legacy jank now?
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.has(DataComponentRegistry.MAGIC_ITEM_MAGNET_DATA) ? stack.get(DataComponentRegistry.MAGIC_ITEM_MAGNET_DATA).magnetActive() : false;
	}

	@Override
	public EquipmentInventoryType getEquipmentCategory(ItemStack stack) {
		return EquipmentInventoryType.MISC;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, Player player, Entity target) {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack, @Nullable Player player, Entity target) {
		return target == player && EquipmentHelper.getEquipment(EquipmentInventoryType.MISC, target, this).isEmpty();
	}

	@Override
	public boolean canUnequip(ItemStack stack, @Nullable Player player, Entity target, Container inventory) {
		return true;
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, Container inventory) {
		return true;
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, Container inventory) {
		stack.set(DataComponentRegistry.MAGIC_ITEM_MAGNET_DATA, new MagicItemMagnetData(0, true));
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, Container inventory) {
		stack.set(DataComponentRegistry.MAGIC_ITEM_MAGNET_DATA, new MagicItemMagnetData(0, false));
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, Container inventory) {
		if(stack.getDamageValue() < stack.getMaxDamage()) {
			double range = 7;

			AABB area = new AABB(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ()).inflate(range);
			List<ItemEntity> entities = entity.level().getEntitiesOfClass(ItemEntity.class, area, e -> e.distanceToSqr(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ()) <= range*range);

			for(ItemEntity item : entities) {
				if(!item.isNoGravity()) {

					boolean isGravityCompensated = false;
					
					if(stack.has(DataComponentRegistry.MAGIC_ITEM_MAGNET_DATA) && stack.get(DataComponentRegistry.MAGIC_ITEM_MAGNET_DATA).item_magnet_last_gravity_update() == item.tickCount)
						isGravityCompensated = true;

					stack.set(DataComponentRegistry.MAGIC_ITEM_MAGNET_DATA, new MagicItemMagnetData(item.tickCount, true));

					if(!isGravityCompensated)
						item.setDeltaMovement(item.getDeltaMovement().add(0, 0.03999999910593033D, 0));
				}
				
				double dx = entity.getX() - item.getX();
				double dy = entity.getY() + entity.getBbHeight() / 2 - (item.getY() + item.getBbHeight() / 2);
				double dz = entity.getZ() - item.getZ();
				double len = Math.sqrt(dx*dx + dy*dy + dz*dz);

				if(!entity.level().isClientSide()) {
					item.setDeltaMovement(item.getDeltaMovement().add(dx / len * 0.015D, 0, 0));
					if(item.onGround()) {
						item.setDeltaMovement(item.getDeltaMovement().add(0, 0.015D, 0));
					} else {
						item.setDeltaMovement(item.getDeltaMovement().add(0, dy / len * 0.015D, 0));
					}
					item.setDeltaMovement(item.getDeltaMovement().add(0, 0, dz / len * 0.015D));
					item.hurtMarked = true;
				} else {
					this.spawnParticles(item);
				}
			}
		}
	}

	protected void spawnParticles(ItemEntity item) {
		if(item.tickCount % 4 == 0) {
			// new working code for what the old one looks like it was supposed to do
			TheBetweenlands.createParticle(ParticleRegistry.CORRUPTED.get(), item.level(), item.getX(), item.getY() + item.getBbHeight() / 2.0f + 0.25f, item.getZ(), ParticleFactory.ParticleArgs.get().withScale(0.5f));
			
			//Old un-working particle code
			//BLParticles.CORRUPTED.spawn(item.world, item.getX(), item.getY() + item.height / 2.0f + 0.25f, item.getZ(), ParticleArgs.get().withScale(0.5f));
		}
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the magnet break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	//TODO This stuff needs moving to the new recipe system for the animator
/*
	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 32;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 16;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 38;
	}
	*/
}

package thebetweenlands.common.entity.projectile.arrow;

import javax.annotation.Nullable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.common.entity.monster.TinySludgeWormHelper;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class SludgeWormArrow extends AbstractArrow {

	public SludgeWormArrow(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	public SludgeWormArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.SLUDGE_WORM_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	public SludgeWormArrow(LivingEntity owner, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.SLUDGE_WORM_ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (!this.level().isClientSide()) {
			if (result.getEntity() instanceof LivingEntity)
				spawnWorm((LivingEntity) result.getEntity());
			else if (result.getEntity() instanceof PartEntity) {
				PartEntity part = (PartEntity) result.getEntity();
				if (part.getParent() instanceof LivingEntity)
					spawnWorm((LivingEntity) part.getParent());
			}
		}
	}

	private void spawnWorm(LivingEntity target) {
		TinySludgeWormHelper worm = EntityRegistry.TINY_SLUDGE_WORM_HELPER.get().create(this.level());
		worm.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
		worm.setTarget(target);
		if (this.getOwner() instanceof Player player)
			worm.setOwnerUUID(player.getUUID());
		this.level().addFreshEntity(worm);
		this.discard();
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemRegistry.SLUDGE_WORM_ARROW.toStack();
	}
}

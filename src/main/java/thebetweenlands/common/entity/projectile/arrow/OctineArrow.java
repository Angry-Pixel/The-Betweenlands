package thebetweenlands.common.entity.projectile.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.common.item.misc.OctineIngotItem;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class OctineArrow extends AbstractArrow {

	public OctineArrow(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	public OctineArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.OCTINE_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	public OctineArrow(LivingEntity owner, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.OCTINE_ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
	}

	@Override
	protected void doPostHurtEffects(LivingEntity target) {
		super.doPostHurtEffects(target);
		if (!target.fireImmune()) {
			if (target.isOnFire()) {
				target.igniteForSeconds(9);
			} else {
				target.igniteForSeconds(5);
			}
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);

		if(OctineIngotItem.isTinder(new ItemStack(ItemRegistry.OCTINE_INGOT.get()), this.level().getBlockState(result.getBlockPos()))) {
			this.level().setBlockAndUpdate(result.getBlockPos(), Blocks.FIRE.defaultBlockState());
		}
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemRegistry.OCTINE_ARROW.toStack();
	}
}

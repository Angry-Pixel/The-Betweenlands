package thebetweenlands.common.item.tool;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.entity.PredatorArrowGuide;
import thebetweenlands.common.registries.EntityRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class PredatorBowItem extends BowItem {

	public PredatorBowItem(Properties properties) {
		super(properties);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof Player player) {
			ItemStack itemstack = player.getProjectile(stack);
			if (!itemstack.isEmpty()) {
				int i = this.getUseDuration(stack, entityLiving) - timeLeft;
				i = EventHooks.onArrowLoose(stack, level, player, i, !itemstack.isEmpty());
				if (i < 0) return;
				float f = getPowerForTime(i);
				f *= CorrosionHelper.getModifier(stack);
				if (!((double) f < 0.1)) {
					List<ItemStack> list = draw(stack, itemstack, player);
					if (level instanceof ServerLevel serverlevel && !list.isEmpty()) {
						this.shoot(serverlevel, player, player.getUsedItemHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, null);
					}

					level.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
					player.awardStat(Stats.ITEM_USED.get(this));
				}
			}
		}
	}

	@Override
	protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
		ArrowItem arrowitem = ammo.getItem() instanceof ArrowItem arrowitem1 ? arrowitem1 : (ArrowItem) Items.ARROW;
		AbstractArrow abstractarrow = arrowitem.createArrow(level, ammo, shooter, weapon);
		if (isCrit) {
			abstractarrow.setCritArrow(true);
		}
		abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() * 1.05F);

		return this.customArrow(abstractarrow, ammo, weapon);
	}

	@Override
	protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
		float f = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
		float f1 = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float)(projectileItems.size() - 1);
		float f2 = (float)((projectileItems.size() - 1) % 2) * f1 / 2.0F;
		float f3 = 1.0F;

		for (int i = 0; i < projectileItems.size(); i++) {
			ItemStack itemstack = projectileItems.get(i);
			if (!itemstack.isEmpty()) {
				float f4 = f2 + f3 * (float)((i + 1) / 2) * f1;
				f3 = -f3;
				Projectile projectile = this.createProjectile(level, shooter, weapon, itemstack, isCrit);
				this.shootProjectile(shooter, projectile, i, velocity, inaccuracy, f4, target);
				level.addFreshEntity(projectile);

				PredatorArrowGuide guide = new PredatorArrowGuide(EntityRegistry.PREDATOR_ARROW_GUIDE.get(), level);
				guide.moveTo(projectile.getX(), projectile.getY(), projectile.getZ(), 0, 0);
				guide.startRiding(projectile, true);
				level.addFreshEntity(guide);

				weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
				if (weapon.isEmpty()) {
					break;
				}
			}
		}
	}
}

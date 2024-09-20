package thebetweenlands.common.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.entity.projectile.BetweenstonePebble;
import thebetweenlands.common.entity.fishing.FishBait;
import thebetweenlands.common.item.misc.FishBaitItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;
import java.util.function.Predicate;

public class SlingshotItem extends ProjectileWeaponItem {

	public SlingshotItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		boolean flag = !player.getProjectile(itemstack).isEmpty();

		InteractionResultHolder<ItemStack> ret = EventHooks.onArrowNock(itemstack, level, player, hand, flag);
		if (ret != null) return ret;

		if (!player.hasInfiniteMaterials() && !flag) {
			return InteractionResultHolder.fail(itemstack);
		} else {
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(itemstack);
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
		if (entity instanceof Player player) {
			ItemStack itemstack = player.getProjectile(stack);
			if (!itemstack.isEmpty()) {
				int i = this.getUseDuration(stack, entity) - timeCharged;
				i = EventHooks.onArrowLoose(stack, level, player, i, !itemstack.isEmpty());
				if (i < 0) return;
				float f = this.getPowerForTime(i);
				f *= CorrosionHelper.getModifier(stack);
				if (f >= 0.1D) {
					List<ItemStack> list = draw(stack, itemstack, player);
					if (level instanceof ServerLevel serverlevel && !list.isEmpty()) {
						this.shoot(serverlevel, player, player.getUsedItemHand(), stack, list, f * 3.0F, 1.0F, f == 1.0F, null);
					}

					level.playSound(null, player.blockPosition(), SoundRegistry.SLINGSHOT_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
					player.awardStat(Stats.ITEM_USED.get(this));
				}
			}
		}
	}

	@Override
	protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
		float f = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
		float f1 = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float)(projectileItems.size() - 1);
		float f2 = (float)((projectileItems.size() - 1) % 2) * f1 / 2.0F;
		float f3 = 1.0F;

		for (int i = 0; i < projectileItems.size(); i++) {
			ItemStack itemstack = projectileItems.get(i);
			if (itemstack.getItem() instanceof FishBaitItem bait) {
				FishBait baitEntity = (FishBait) bait.createEntity(level, shooter, itemstack.copy());
				baitEntity.setNeverPickUp();
				itemstack.hurtAndBreak(1, level, shooter, item -> {});
				baitEntity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity * 3.0F, 1.0F);
				level.addFreshEntity(baitEntity);
				if (!(itemstack.getItem() instanceof FishBaitItem)) break;
			} else if (!itemstack.isEmpty()) {
				float f4 = f2 + f3 * (float)((i + 1) / 2) * f1;
				f3 = -f3;
				Projectile projectile = this.createProjectile(level, shooter, weapon, itemstack, isCrit);
				this.shootProjectile(shooter, projectile, i, velocity, inaccuracy, f4, target);
				level.addFreshEntity(projectile);
				weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
				if (weapon.isEmpty()) {
					break;
				}
			}
		}
	}

	private float getPowerForTime(int charge) {
		float f = (float)charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F * 1.15F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		return f;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 100000;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return stack -> stack.is(BLItemTagProvider.SLINGSHOT_AMMO);
	}

	@Override
	public int getDefaultProjectileRange() {
		return 15;
	}

	@Override
	protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
		if (ammo.is(ItemRegistry.BETWEENSTONE_PEBBLE)) {
			var pebble = new BetweenstonePebble(shooter.getX(), shooter.getEyeY() - 0.15F, shooter.getZ(), level, ammo, weapon);
			if (isCrit) {
				pebble.setCritArrow(true);
			}
			return pebble;
		}
		return super.createProjectile(level, shooter, weapon, ammo, isCrit);
	}

	@Override
	protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
		projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, velocity, inaccuracy);
	}

	@Override
	public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack projectileWeaponItem) {
		return new ItemStack(ItemRegistry.BETWEENSTONE_PEBBLE.get());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.YELLOW));
	}
}

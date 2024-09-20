package thebetweenlands.common.items.shield;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.ToolMaterialRegistry;

import java.util.List;

public class ValoniteShieldItem extends SwatShieldItem {
	public ValoniteShieldItem(Properties properties) {
		super(ToolMaterialRegistry.VALONITE, properties);
	}

	@Override
	public int getMaxChargeTime(ItemStack stack, LivingEntity user) {
		return 40;
	}

	@Override
	public void onEnemyRammed(ItemStack stack, LivingEntity user, LivingEntity enemy, Vec3 rammingDir) {
		enemy.knockback(3.0F, -rammingDir.x, -rammingDir.z);
		if(user instanceof Player player) {
			enemy.hurt(user.damageSources().playerAttack(player), 5.0F);
		} else {
			enemy.hurt(user.damageSources().mobAttack(user), 5.0F);
		}
	}

	@Override
	public void onAttackBlocked(ItemStack stack, LivingEntity attacked, float damage, DamageSource source) {
		if(source.getDirectEntity() != null) {
			if(attacked instanceof Player player) {
				source.getDirectEntity().hurt(attacked.damageSources().playerAttack(player), damage * 0.3F);
			} else {
				source.getDirectEntity().hurt(attacked.damageSources().mobAttack(attacked), damage * 0.3F);
			}
		}

		if(source.getDirectEntity() != null && source.getEntity() != null && source.getDirectEntity() instanceof Projectile proj) {
			Vec3 dir = source.getDirectEntity().getEyePosition(1).subtract(source.getEntity().getEyePosition(1)).normalize();
			proj.shoot(dir.x, dir.y, dir.z, 15, 2);
			if(source.getDirectEntity() instanceof AbstractArrow arrow) {
				arrow.setOwner(attacked);
			}
		}

		super.onAttackBlocked(stack, attacked, damage, source);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString(), Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.GRAY));
	}
}

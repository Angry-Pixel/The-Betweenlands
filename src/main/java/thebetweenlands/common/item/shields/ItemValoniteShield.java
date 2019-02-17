package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemValoniteShield extends ItemSwatShield {
	public ItemValoniteShield() {
		super(BLMaterialRegistry.TOOL_VALONITE);
	}

	@Override
	public int getMaxChargeTime(ItemStack stack, EntityLivingBase user) {
		return 40;
	}

	@Override
	public void onEnemyRammed(ItemStack stack, EntityLivingBase user, EntityLivingBase enemy, Vec3d rammingDir) {
		enemy.knockBack(user, 3.0F, -rammingDir.x, -rammingDir.z);
		if(user instanceof EntityPlayer) {
			enemy.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)user), 5.0F);
		} else {
			enemy.attackEntityFrom(DamageSource.causeMobDamage(user), 5.0F);
		}
	}

	@Override
	public void onAttackBlocked(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		if(source.getImmediateSource() != null) {
			if(attacked instanceof EntityPlayer) {
				source.getImmediateSource().attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacked), damage * 0.3F);
			} else {
				source.getImmediateSource().attackEntityFrom(DamageSource.causeMobDamage(attacked), damage * 0.3F);
			}
		}

		if(source instanceof EntityDamageSourceIndirect) {
			EntityDamageSourceIndirect indirectSource = (EntityDamageSourceIndirect) source;
			if(indirectSource.getImmediateSource() != null && indirectSource.getTrueSource() != null && indirectSource.getImmediateSource() instanceof IProjectile) {
				Vec3d dir = indirectSource.getImmediateSource().getPositionEyes(1).subtract(indirectSource.getTrueSource().getPositionEyes(1)).normalize();
				((IProjectile)indirectSource.getImmediateSource()).shoot(dir.x, dir.y, dir.z, 15, 2);
				if(indirectSource.getImmediateSource() instanceof EntityArrow) {
					((EntityArrow)indirectSource.getImmediateSource()).shootingEntity = attacked;
				}
			}
		}

		super.onAttackBlocked(stack, attacked, damage, source);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.valonite_shield", Minecraft.getInstance().gameSettings.keyBindSneak.getDisplayName(), Minecraft.getInstance().gameSettings.keyBindUseItem.getDisplayName()), 0));
	}
}

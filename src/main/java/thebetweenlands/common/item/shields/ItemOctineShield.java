package thebetweenlands.common.item.shields;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;

public class ItemOctineShield extends ItemBLShield {
	public ItemOctineShield() {
		super(BLMaterialRegistry.TOOL_OCTINE);
	}

	@Override
	public float getBlockedDamage(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		if(source instanceof EntityDamageSourceIndirect) {
			EntityDamageSourceIndirect indirectSource = (EntityDamageSourceIndirect) source;
			if(indirectSource.getImmediateSource() != null && indirectSource.isProjectile()) {
				return 0;
			}
		}
		return super.getBlockedDamage(stack, attacked, damage, source);
	}

	@Override
	public void onAttackBlocked(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
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
}

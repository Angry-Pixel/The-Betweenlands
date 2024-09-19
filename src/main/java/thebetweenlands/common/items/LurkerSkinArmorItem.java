package thebetweenlands.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.ArmorMaterialRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class LurkerSkinArmorItem extends ArmorItem {

	public LurkerSkinArmorItem(Type type, Properties properties) {
		super(ArmorMaterialRegistry.LURKER_SKIN, type, properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
		if (entity instanceof Player player && slot >= 36 && slot <= 39) {
			if (!player.isSpectator()) {
				NonNullList<ItemStack> armor = player.getInventory().armor;
				int armorPieces = 0;

				for (ItemStack anArmor : armor) {
					if (anArmor != null && anArmor.getItem() instanceof LurkerSkinArmorItem) {
						armorPieces += 1;
					}
				}

				if (stack.is(ItemRegistry.LURKER_SKIN_BOOTS) && player.isInWater()) {
					BlockState blockState = level.getBlockState(BlockPos.containing(player.getX(), player.getBoundingBox().maxY + 0.1D, player.getZ()));
					boolean fullyInWater = blockState.liquid();

					if (fullyInWater) {
						if (!player.isShiftKeyDown() && player.zza == 0) {
							player.setDeltaMovement(player.getDeltaMovement().x(), Math.sin(player.tickCount / 5.0F) * 0.016D, player.getDeltaMovement().z());
						}

						if (player.zza != 0) {
							if (player.zza > 0) {
								Vec3 lookVec = player.getLookAngle().normalize();
								double speed = 0.01D + 0.05D / 4.0D * armorPieces;
								player.setDeltaMovement(player.getDeltaMovement().add(
									lookVec.x * player.zza * speed,
									lookVec.y * player.zza * speed,
									lookVec.z * player.zza * speed));
								player.getFoodData().addExhaustion(0.0024F);
							}
							player.setDeltaMovement(player.getDeltaMovement().add(0.0D, 0.02D, 0.0D));
						}
					}

					if (armorPieces >= 4) {
						player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 10));

						if (player.tickCount % 3 == 0) {
							player.setAirSupply(player.getAirSupply() - 1);
						}

						if (player.getAirSupply() <= -20) {
							player.setAirSupply(0);

							for (int i = 0; i < 8; ++i) {
								float rx = level.getRandom().nextFloat() - level.getRandom().nextFloat();
								float ry = level.getRandom().nextFloat() - level.getRandom().nextFloat();
								float rz = level.getRandom().nextFloat() - level.getRandom().nextFloat();

								level.addParticle(ParticleTypes.BUBBLE, player.getX() + (double) rx, player.getY() + (double) ry, player.getZ() + (double) rz, player.getDeltaMovement().x(), player.getDeltaMovement().y(), player.getDeltaMovement().z());
							}

							player.hurt(level.damageSources().drown(), 2.0F);
						}
					}
				}
			}
		}
	}

}

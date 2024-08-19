package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import thebetweenlands.api.SimulacrumEffect;
import thebetweenlands.common.registries.AttachmentRegistry;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class ImitationSimulacrumEffect implements SimulacrumEffect {

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
	private static final Method Mob_getAmbientSound = ObfuscationReflectionHelper.findMethod(Mob.class, "getAmbientSound");
	private static final MethodHandle handle_Mob_getAmbientSound;
	private static final Method Mob_getHurtSound = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "getHurtSound", DamageSource.class);
	private static final MethodHandle handle_Mob_getHurtSound;
	private static final Method Mob_getDeathSound = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "getDeathSound");
	private static final MethodHandle handle_Mob_getDeathSound;

	static {
		MethodHandle tmp_handle_Mob_getAmbientSound = null;
		MethodHandle tmp_handle_Mob_getHurtSound = null;
		MethodHandle tmp_handle_Mob_getDeathSound = null;
		try {
			tmp_handle_Mob_getAmbientSound = LOOKUP.unreflect(Mob_getAmbientSound);
			tmp_handle_Mob_getHurtSound = LOOKUP.unreflect(Mob_getHurtSound);
			tmp_handle_Mob_getDeathSound = LOOKUP.unreflect(Mob_getDeathSound);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		handle_Mob_getAmbientSound = tmp_handle_Mob_getAmbientSound;
		handle_Mob_getHurtSound = tmp_handle_Mob_getHurtSound;
		handle_Mob_getDeathSound = tmp_handle_Mob_getDeathSound;
	}

	@Override
	public void executeEffect(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		if (level.isClientSide() && level.getGameTime() % 20 == 0 && --entity.soundCooldown <= 0) {
			entity.soundCooldown = level.getRandom().nextInt(30) + 30;
			this.playImitationSound(level, pos);
		}
	}

	private void playImitationSound(Level level, BlockPos pos) {
		Entity viewer = Minecraft.getInstance().getCameraEntity();

		if (viewer != null && pos.distToCenterSqr(viewer.getX(), viewer.getY(), viewer.getZ()) < 16 * 16) {
			ResourceLocation key = viewer.getData(AttachmentRegistry.LAST_KILLED).getLastKilled();

			if (key != null) {
				Entity entity = BuiltInRegistries.ENTITY_TYPE.get(key).create(level);

				if (entity != null) {
					SoundEvent sound;

					int r = viewer.level().getRandom().nextInt(20);

					try {
						if (r <= 15) {
							sound = (SoundEvent) handle_Mob_getAmbientSound.invoke(entity);
						} else if (r <= 19) {
							sound = (SoundEvent) handle_Mob_getHurtSound.invoke(entity, level.damageSources().generic());
						} else {
							sound = (SoundEvent) handle_Mob_getDeathSound.invoke(entity);
						}
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}

					if (sound != null) {
						level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, SoundSource.BLOCKS, 0.75f, 0.9f, false);
					}

					entity.discard();
				}
			}
		}
	}
}

package thebetweenlands.common.block.entity.simulacrum;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import thebetweenlands.api.block.SimulacrumEffect;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.AttachmentRegistry;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImitationSimulacrumEffect implements SimulacrumEffect {

	private static boolean failed = false;
	private static final Map<EntityType<?>, SoundHolder> SOUND_CACHE = new HashMap<>();
	private static final List<EntityType<?>> FAILED_SOUND_GRABS = new ArrayList<>();

	private static final MethodHandle handle_Mob_getAmbientSound;
	private static final MethodHandle handle_Mob_getHurtSound;
	private static final MethodHandle handle_Mob_getDeathSound;

	static {
		MethodHandle tmp_handle_Mob_getAmbientSound = null;
		MethodHandle tmp_handle_Mob_getHurtSound = null;
		MethodHandle tmp_handle_Mob_getDeathSound = null;
		try {
			tmp_handle_Mob_getAmbientSound = MethodHandles.lookup().unreflect(ObfuscationReflectionHelper.findMethod(Mob.class, "getAmbientSound"));
			tmp_handle_Mob_getHurtSound = MethodHandles.lookup().unreflect(ObfuscationReflectionHelper.findMethod(LivingEntity.class, "getHurtSound", DamageSource.class));
			tmp_handle_Mob_getDeathSound = MethodHandles.lookup().unreflect(ObfuscationReflectionHelper.findMethod(LivingEntity.class, "getDeathSound"));
		} catch (IllegalAccessException e) {
			TheBetweenlands.LOGGER.error("Failed to unreflect entity sound methods.", e);
			failed = true;
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

		if (!failed && viewer != null && pos.distToCenterSqr(viewer.getX(), viewer.getY(), viewer.getZ()) < 16 * 16) {
			EntityType<?> type = viewer.getData(AttachmentRegistry.LAST_KILLED).getLastKilled();

			if (type != null && !FAILED_SOUND_GRABS.contains(type)) {
				if (!SOUND_CACHE.containsKey(type)) {
					Entity entity = type.create(level);

					if (entity != null) {
						try {
							SoundEvent ambient = (SoundEvent) handle_Mob_getAmbientSound.invoke(entity);
							SoundEvent hurt = (SoundEvent) handle_Mob_getHurtSound.invoke(entity, level.damageSources().generic());
							SoundEvent death = (SoundEvent) handle_Mob_getDeathSound.invoke(entity);
							SOUND_CACHE.put(type, new SoundHolder(ambient, hurt, death));
						} catch (Throwable e) {
							TheBetweenlands.LOGGER.error("Failed to acquire sounds for entity {}", type.getDescription().getString(), e);
							FAILED_SOUND_GRABS.add(type);
							entity.discard();
							return;
						}
						entity.discard();
					}
				}

				SoundHolder holder = SOUND_CACHE.get(type);
				@Nullable SoundEvent sound;
				int r = viewer.level().getRandom().nextInt(20);

				if (r <= 15) {
					sound = holder.ambient();
				} else if (r <= 19) {
					sound = holder.hurt();
				} else {
					sound = holder.death();
				}

				if (sound != null) {
					level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, SoundSource.BLOCKS, 0.75f, 0.9f, false);
				}

			}
		}
	}

	public record SoundHolder(@Nullable SoundEvent ambient, @Nullable SoundEvent hurt, @Nullable SoundEvent death) {}
}

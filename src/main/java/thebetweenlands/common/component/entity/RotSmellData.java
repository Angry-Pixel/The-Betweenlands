package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.common.registries.AttachmentRegistry;

public class RotSmellData {

	private long smellyTime;
	private long immunityTime;

	public static final Codec<RotSmellData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.LONG.fieldOf("smell_timestamp").forGetter(o -> o.smellyTime),
		Codec.LONG.fieldOf("immunity_timestamp").forGetter(o -> o.immunityTime)
	).apply(instance, RotSmellData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RotSmellData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, d -> d.smellyTime,
			ByteBufCodecs.VAR_LONG, d -> d.immunityTime,
			RotSmellData::new
		);

	public RotSmellData() {
		this(-1, -1);
	}

	public RotSmellData(long level, long prevLevel) {
		this.smellyTime = level;
		this.immunityTime = prevLevel;
	}

	public boolean isSmellingBad(LivingEntity entity) {
		return this.getRemainingSmellyTicks(entity) > 0 && this.getRemainingImmunityTicks(entity) <= 0;
	}

	public int getRemainingSmellyTicks(LivingEntity entity) {
		return this.smellyTime >= 0 ? Math.max(0, (int)(this.smellyTime - entity.level().getGameTime())) : 0;
	}

	public void setSmellingBad(LivingEntity entity, int duration) {
		if(duration <= 0) {
			this.setNotSmellingBad();
		} else {
			this.smellyTime = entity.level().getGameTime() + duration;
		}
		entity.syncData(AttachmentRegistry.ROT_SMELL);
	}

	public void setNotSmellingBad() {
		if(this.smellyTime != -1) {
			this.smellyTime = -1;
		}
	}

	public int getRemainingImmunityTicks(LivingEntity entity) {
		return this.immunityTime >= 0 ? Math.max(0, (int)(this.immunityTime - entity.level().getGameTime())) : 0;
	}

	public void setImmune(LivingEntity entity, int duration) {
		if(duration <= 0) {
			if(this.immunityTime != -1) {
				this.immunityTime = -1;
			}
		} else {
			this.immunityTime = entity.level().getGameTime() + duration;
		}
	}

	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if(player.level().isClientSide()) {
			RotSmellData cap = player.getData(AttachmentRegistry.ROT_SMELL);
			if (player.level().getRandom().nextInt(4) == 0 && cap.isSmellingBad(player)) {
//				player.level().addParticle(ParticleRegistry.FLY.get(), player.getX(), player.getY() + 1.0D, player.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}
}

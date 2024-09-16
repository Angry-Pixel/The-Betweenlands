package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.component.AttachmentHolderIdentifier;
import thebetweenlands.common.component.ISynchedAttachment;
import thebetweenlands.common.component.SynchedAttachmentType;
import thebetweenlands.common.network.clientbound.attachment.UpdateSynchedAttachmentPacket;
import thebetweenlands.common.registries.AttachmentRegistry;

public class RotSmellData implements ISynchedAttachment<RotSmellData> {

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
			this.setNotSmellingBad(entity);
		} else {
			this.smellyTime = entity.level().getGameTime() + duration;
			this.setChanged(entity);
		}
	}

	public void setNotSmellingBad(LivingEntity entity) {
		if(this.smellyTime != -1) {
			this.smellyTime = -1;
			this.setChanged(entity);
		}
	}

	public int getRemainingImmunityTicks(LivingEntity entity) {
		return this.immunityTime >= 0 ? Math.max(0, (int)(this.immunityTime - entity.level().getGameTime())) : 0;
	}

	public void setImmune(LivingEntity entity, int duration) {
		if(duration <= 0) {
			if(this.immunityTime != -1) {
				this.immunityTime = -1;
				this.setChanged(entity);
			}
		} else {
			this.immunityTime = entity.level().getGameTime() + duration;
			this.setChanged(entity);
		}
	}

	private void setChanged(LivingEntity player) {
		PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new UpdateSynchedAttachmentPacket<RotSmellData>(AttachmentHolderIdentifier.of(player), AttachmentRegistry.ROT_SMELL.get(), this));
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

	@Override
	public SynchedAttachmentType<RotSmellData> getSynchedAttachmentType(AttachmentHolderIdentifier<?> attachee) {
		return AttachmentRegistry.ROT_SMELL_SYNCHER.get();
	}

}

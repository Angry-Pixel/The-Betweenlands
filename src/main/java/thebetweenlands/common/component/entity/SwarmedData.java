package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.network.clientbound.attachment.UpdateSwarmedPacket;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class SwarmedData {

	private float strength;
	private int hurtTimer;

	private int damageTimer;
	private float damage;

	private float lastYaw, lastPitch, lastYawDelta, lastPitchDelta;

	@Nullable
	private WeakReference<Entity> source;

	public static final Codec<SwarmedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("strength").forGetter(o -> o.strength),
		Codec.INT.fieldOf("hurt_timer").forGetter(o -> o.hurtTimer),
		Codec.INT.fieldOf("damage_timer").forGetter(o -> o.damageTimer),
		Codec.FLOAT.fieldOf("damage").forGetter(o -> o.damage)
	).apply(instance, SwarmedData::new));

	public SwarmedData() {
		this(0.0F, 0, 0, 0.0F);
	}

	public SwarmedData(float strength, int hurtTimer, int damageTimer, float damage) {
		this.strength = strength;
		this.hurtTimer = hurtTimer;
		this.damageTimer = damageTimer;
		this.damage = damage;
	}

	public void setSwarmedStrength(Player player, float strength) {
		float newStrength = Mth.clamp(strength, 0, 1);
		if (newStrength != this.strength) {
			this.strength = newStrength;
			this.setChanged(player);
		}
	}

	public float getSwarmedStrength() {
		return this.strength;
	}

	public void setHurtTimer(Player player, int timer) {
		if (timer != this.hurtTimer) {
			this.hurtTimer = timer;
			this.setChanged(player);
		}
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getDamage() {
		return this.damage;
	}

	public void setDamageTimer(int timer) {
		this.damageTimer = timer;
	}

	public int getDamageTimer() {
		return this.damageTimer;
	}

	public int getHurtTimer() {
		return this.hurtTimer;
	}

	public void setLastRotations(float yaw, float pitch) {
		this.lastYaw = yaw;
		this.lastPitch = pitch;
	}

	public float getLastYaw() {
		return this.lastYaw;
	}

	public float getLastPitch() {
		return this.lastPitch;
	}

	public void setLastRotationDeltas(float yaw, float pitch) {
		this.lastYawDelta = yaw;
		this.lastPitchDelta = pitch;
	}

	public float getLastYawDelta() {
		return this.lastYawDelta;
	}

	public float getLastPitchDelta() {
		return this.lastPitchDelta;
	}

	public void setSwarmSource(@Nullable Entity entity) {
		if (entity == null) {
			this.source = null;
		} else {
			this.source = new WeakReference<>(entity);
		}
	}

	@Nullable
	public Entity getSwarmSource() {
		return this.source == null ? null : this.source.get();
	}

	private void setChanged(Player player) {
		if (player instanceof ServerPlayer) {
			PacketDistributor.sendToPlayer((ServerPlayer) player, new UpdateSwarmedPacket(this.strength, this.hurtTimer, this.damageTimer, this.damage));
		}
	}
}

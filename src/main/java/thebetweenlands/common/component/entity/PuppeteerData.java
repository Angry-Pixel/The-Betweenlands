package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.attachment.ProtectionShield;
import thebetweenlands.common.network.clientbound.attachment.UpdatePuppeteerPacket;
import thebetweenlands.common.registries.AttachmentRegistry;

import java.util.List;
import java.util.Optional;

public class PuppeteerData {

	private int activatingTicks;
	private int activatingEntityId;
	@Nullable
	private Entity activatingEntity;

	private final ProtectionShield shield = new ProtectionShield();
	private int shieldRotationTicks = 0;
	private int prevShieldRotationTicks = 0;

	public static final Codec<PuppeteerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.optionalFieldOf("activating_entity", -1).forGetter(o -> o.activatingEntityId),
		Codec.INT.fieldOf("shield_rotation_ticks").forGetter(o -> o.activatingTicks),
		Codec.INT.fieldOf("shield_data").forGetter(o -> o.shield.packActiveData())
	).apply(instance, PuppeteerData::new));

	public PuppeteerData(int activatingEntityId, int shieldRotationTicks, int shieldData) {
		this.activatingEntityId = activatingEntityId;
		this.shieldRotationTicks = shieldRotationTicks;
		this.shield.unpackActiveData(shieldData);
	}

	public List<Entity> getPuppets(Player player) {
		return player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(24.0D), entity -> {
			Optional<PuppetData> cap = entity.getExistingData(AttachmentRegistry.PUPPET);
			return cap.isPresent() && cap.get().getPuppeteer(entity) == player;
		});
	}

	public void setActivatingEntity(@Nullable Entity entity, Player player) {
		this.activatingEntityId = entity == null ? -1 : entity.getId();
		this.activatingEntity = entity;
		this.setChanged(player);
	}

	@Nullable
	public Entity getActivatingEntity(Player player) {
		if (this.activatingEntityId < 0) {
			this.activatingEntity = null;
		} else if (this.activatingEntity == null || !this.activatingEntity.isAlive() || this.activatingEntity.getId() != this.activatingEntityId) {
			this.activatingEntity = player.level().getEntity(this.activatingEntityId);
		}
		return this.activatingEntity;
	}

	public int getActivatingTicks() {
		return this.activatingTicks;
	}

	public void setActivatingTicks(int ticks) {
		this.activatingTicks = ticks;
	}

	public boolean checkAndActivateShield(int index, Player player) {
		if (!this.shield.isActive(index)) {
			this.shield.setActive(index, true);
			this.setChanged(player);
			return true;
		}
		return false;
	}

	public void updateShield(Player player) {
		for (int i = 0; i <= 19; i++) {
			if (this.shield.getAnimationTicks(i) == 0 && player.level().getRandom().nextInt(50) == 0) {
				this.shield.setAnimationTicks(i, 40);
			}
			if (this.shield.getAnimationTicks(i) > 0) {
				this.shield.setAnimationTicks(i, this.shield.getAnimationTicks(i) - 1);
				if (this.shield.getAnimationTicks(i) == 20)
					this.shield.setAnimationTicks(i, 0);
			}
		}

		this.prevShieldRotationTicks = this.shieldRotationTicks;
		if (this.shield.hasShield()) {
			this.shieldRotationTicks++;
			this.setChanged(player);
		}
	}

	public int getShieldRotationTicks() {
		return this.shieldRotationTicks;
	}

	public int getPrevShieldRotationTicks() {
		return this.prevShieldRotationTicks;
	}

	private void setChanged(Player puppeteer) {
		PacketDistributor.sendToPlayersTrackingEntityAndSelf(puppeteer, new UpdatePuppeteerPacket(this.activatingEntityId, this.shieldRotationTicks, this.shield.packActiveData()));
	}
}

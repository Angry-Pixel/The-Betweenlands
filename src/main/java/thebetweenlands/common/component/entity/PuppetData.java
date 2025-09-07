package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.network.clientbound.attachment.UpdatePuppetPacket;

import java.util.Optional;
import java.util.UUID;

public class PuppetData {

	@Nullable
	private Entity puppeteer;
	private Optional<UUID> puppeteerUUID;
	private int remainingTicks;
	private boolean stay;
	private boolean guard;
	private Optional<BlockPos> guardHome;

	private Optional<UUID> ringUUID;
	private int recruitmentCost;

	public static final Codec<PuppetData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		UUIDUtil.CODEC.optionalFieldOf("puppeteer").forGetter(o -> o.puppeteerUUID),
		Codec.INT.fieldOf("remaining_ticks").forGetter(o -> o.remainingTicks),
		Codec.BOOL.fieldOf("stay").forGetter(o -> o.stay),
		Codec.BOOL.fieldOf("guard").forGetter(o -> o.guard),
		BlockPos.CODEC.optionalFieldOf("guard_home").forGetter(o -> o.guardHome),
		UUIDUtil.CODEC.optionalFieldOf("ring").forGetter(o -> o.ringUUID),
		Codec.INT.fieldOf("recruitment_cost").forGetter(o -> o.recruitmentCost)
	).apply(instance, PuppetData::new));


	public PuppetData(Optional<UUID> puppeteerUUID, int remainingTicks, boolean stay, boolean guard, Optional<BlockPos> guardHome, Optional<UUID> ringUUID, int recruitmentCost) {
		this.puppeteerUUID = puppeteerUUID;
		this.remainingTicks = remainingTicks;
		this.stay = stay;
		this.guard = guard;
		this.guardHome = guardHome;
		this.ringUUID = ringUUID;
		this.recruitmentCost = recruitmentCost;
	}

	public void setPuppeteer(Entity puppet, @Nullable Entity puppeteer) {
		this.puppeteerUUID = Optional.ofNullable(puppeteer == null ? null : puppeteer.getUUID());
		this.puppeteer = puppeteer;
		this.setChanged(puppet);
	}

	public boolean hasPuppeteer() {
		return this.puppeteerUUID.isPresent();
	}

	@Nullable
	public Entity getPuppeteer(Entity puppet) {
		if (!this.hasPuppeteer()) {
			this.puppeteer = null;
		} else if (this.puppeteer == null || !this.puppeteer.isAlive() || !this.puppeteer.getUUID().equals(this.puppeteerUUID.orElse(null))) {
			this.puppeteer = null;
			for (Entity entity : puppet.level().getEntitiesOfClass(Entity.class, puppet.getBoundingBox().inflate(24.0D))) {
				if (entity.getUUID().equals(this.puppeteerUUID.orElse(null))) {
					this.puppeteer = entity;
					break;
				}
			}
		}
		return this.puppeteer;
	}

	public void setRemainingTicks(int ticks) {
		this.remainingTicks = ticks;
	}

	public int getRemainingTicks() {
		return this.remainingTicks;
	}

	public void setStay(Entity puppet, boolean stay) {
		this.stay = stay;
		this.setChanged(puppet);
	}

	public boolean getStay() {
		return this.stay;
	}

	public void setGuard(Entity puppet, boolean guard, @Nullable BlockPos pos) {
		this.guard = guard;
		this.guardHome = Optional.ofNullable(pos);
		this.setChanged(puppet);
	}

	public boolean getGuard() {
		return this.guard;
	}

	@Nullable
	public BlockPos getGuardHome() {
		return this.guardHome.orElse(null);
	}

	public void setRingUuid(@Nullable UUID uuid) {
		this.ringUUID = Optional.ofNullable(uuid);
	}

	@Nullable
	public UUID getRingUuid() {
		return this.ringUUID.orElse(null);
	}

	public void setRecruitmentCost(int cost) {
		this.recruitmentCost = cost;
	}

	public int getRecruitmentCost() {
		return this.recruitmentCost;
	}

	private void setChanged(Entity puppet) {
		PacketDistributor.sendToPlayersTrackingEntityAndSelf(puppet, new UpdatePuppetPacket(this.puppeteerUUID, this.remainingTicks, this.stay, this.guard, this.guardHome, this.ringUUID, this.recruitmentCost));
	}
}

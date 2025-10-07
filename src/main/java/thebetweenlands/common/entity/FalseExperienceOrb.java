package thebetweenlands.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.XpOrbTargetingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.item.equipment.RingItem;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.UUID;

public class FalseExperienceOrb extends ExperienceOrb {

	@Nullable
	private UUID playerID;

	public FalseExperienceOrb(EntityType<? extends ExperienceOrb> type, Level level) {
		super(type, level);
	}

	public FalseExperienceOrb(Level level, double x, double y, double z, int value, @Nullable UUID playerID) {
		super(level, x, y, z, value);
		this.playerID = playerID;
	}

	@Override
	public EntityType<?> getType() {
		return EntityRegistry.FALSE_XP.get();
	}

	@Nullable
	public UUID getOwnerUUID() {
		return this.playerID;
	}

	@Nullable
	private Player getOwner() {
		UUID uuid = this.getOwnerUUID();
		return uuid == null ? null : this.level().getPlayerByUUID(uuid);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		if (this.playerID != null) {
			tag.putUUID("target", this.playerID);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		if (tag.hasUUID("target")) {
			this.playerID = tag.getUUID("target");
		}
	}

	@Override
	public void playerTouch(Player player) {
		if (player instanceof ServerPlayer && (this.playerID == null || this.playerID.equals(player.getUUID()))) {
			if (this.value < 0 && player.takeXpDelay == 0) {
				if (NeoForge.EVENT_BUS.post(new PlayerXpEvent.PickupXp(player, this)).isCanceled()) return;

				player.takeXpDelay = 2;
				player.take(this, 1);

				this.value = Math.min(this.value + RingItem.removeXp(player, -this.value), 0);

				if (this.value == 0) {
					this.discard();
				}
			} else {
				super.playerTouch(player);
			}
		}
	}

	//only follow our targeted player, and dont try to merge
	@Override
	public void scanForEntities() {
		if (this.playerID == null) {
			if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 64.0) {
				this.followingPlayer = NeoForge.EVENT_BUS.post(new XpOrbTargetingEvent(this, 8.0)).getFollowingPlayer();
			}
		} else {
			if (this.followingPlayer == null && this.getOwner() != null) {
				this.followingPlayer = this.getOwner();
			}
		}

	}
}

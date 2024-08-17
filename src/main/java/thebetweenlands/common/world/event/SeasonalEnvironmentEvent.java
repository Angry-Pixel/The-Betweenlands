package thebetweenlands.common.world.event;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.client.event.ClientEvents;
import thebetweenlands.common.config.BetweenlandsConfig;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public abstract class SeasonalEnvironmentEvent extends BLEnvironmentEvent {
	@Nullable
	private Level lastWorld;
	private boolean wasSet = false;

	/**
	 * Returns the start time in milliseconds since January 1, 1970, 00:00:00 GMT
	 *
	 * @return
	 */
	public abstract long getStartDateInMs();

	/**
	 * Returns the event's duration in days
	 *
	 * @return
	 */
	public abstract int getDurationInDays();

	public long getDayDiffFromStartDate() {
		return TimeUnit.DAYS.convert(Calendar.getInstance().getTime().getTime() - this.getStartDateInMs(), TimeUnit.MILLISECONDS);
	}

	@Override
	public void setActive(Level level, boolean active) {
		if (active && (!this.isActive() || this.lastWorld != level) && ClientEvents.getClientPlayer() != null && level.isClientSide()) {
			this.lastWorld = level;
			Player player = ClientEvents.getClientPlayer();
			this.showStatusMessage(player);
		}

		super.setActive(level, active);
	}

	protected void showStatusMessage(Player player) {

	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if (!level.isClientSide()) {
			if (BetweenlandsConfig.seasonalEvents) {
				long dayDiff = this.getDayDiffFromStartDate();
				if (dayDiff >= 0 && dayDiff <= this.getDurationInDays()) {
					if (!this.isActive() && !this.wasSet) {
						this.setActive(level, true);
						this.wasSet = true;
					}
				} else if (this.wasSet) {
					this.wasSet = false;
					this.setActive(level, false);
				}
			} else if (this.wasSet) {
				this.wasSet = false;
				this.setActive(level, false);
			}
		}
	}

	@Override
	public void resetActiveState(Level level) {
		long dayDiff = this.getDayDiffFromStartDate();
		if (dayDiff >= 0 && dayDiff <= this.getDurationInDays() && BetweenlandsConfig.seasonalEvents) {
			if (!this.isActive()) {
				this.setActive(level, true);
			}
			this.wasSet = true;
		} else {
			if (this.isActive()) {
				this.setActive(level, false);
			}
			this.wasSet = false;
		}
	}

	@Override
	public void saveEventData(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveEventData(tag, registries);
		tag.putBoolean("wasSet", this.wasSet);
	}

	@Override
	public void loadEventData(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadEventData(tag, registries);
		this.wasSet = tag.getBoolean("wasSet");
	}
}

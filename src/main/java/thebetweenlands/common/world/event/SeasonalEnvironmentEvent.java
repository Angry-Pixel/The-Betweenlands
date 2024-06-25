package thebetweenlands.common.world.event;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.client.ClientEvents;
import thebetweenlands.common.config.BetweenlandsConfig;

public abstract class SeasonalEnvironmentEvent extends BLEnvironmentEvent {
	private Level world;
	private Level lastWorld;
	private boolean wasSet = false;

	public SeasonalEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

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
	public void setActive(boolean active) {
		if (active && ClientEvents.getClientLevel() != null && (!this.isActive() || this.lastWorld != ClientEvents.getClientLevel()) && ClientEvents.getClientPlayer() != null && this.world != null && this.world.isClientSide()) {
			this.lastWorld = ClientEvents.getClientLevel();
			Player player = ClientEvents.getClientPlayer();
			this.showStatusMessage(player);
		}

		super.setActive(active);
	}

	protected void showStatusMessage(Player player) {

	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		this.world = level;

		if (!level.isClientSide()) {
			if (BetweenlandsConfig.seasonalEvents) {
				long dayDiff = this.getDayDiffFromStartDate();
				if (dayDiff >= 0 && dayDiff <= this.getDurationInDays()) {
					if (!this.isActive() && !this.wasSet) {
						this.setActive(true);
						this.wasSet = true;
					}
				} else if (this.wasSet) {
					this.wasSet = false;
					this.setActive(false);
				}
			} else if (this.wasSet) {
				this.wasSet = false;
				this.setActive(false);
			}
		}
	}

	@Override
	public void resetActiveState() {
		long dayDiff = this.getDayDiffFromStartDate();
		if (dayDiff >= 0 && dayDiff <= this.getDurationInDays() && BetweenlandsConfig.seasonalEvents) {
			if (!this.isActive()) {
				this.setActive(true);
			}
			this.wasSet = true;
		} else {
			if (this.isActive()) {
				this.setActive(false);
			}
			this.wasSet = false;
		}
	}

	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().putBoolean("wasSet", this.wasSet);
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		this.wasSet = this.getData().getBoolean("wasSet");
	}
}

package thebetweenlands.common.world.event;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;

public abstract class SeasonalEnvironmentEvent extends BLEnvironmentEvent {
	private World world;
	private World lastWorld;
	private boolean wasSet = false;

	public SeasonalEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	/**
	 * Returns the start time in milliseconds since January 1, 1970, 00:00:00 GMT
	 * @return
	 */
	public abstract long getStartDateInMs();

	/**
	 * Returns the event's duration in days
	 * @return
	 */
	public abstract int getDurationInDays();

	public long getDayDiffFromStartDate() {
		return TimeUnit.DAYS.convert(Calendar.getInstance().getTime().getTime() - this.getStartDateInMs(), TimeUnit.MILLISECONDS);
	}

	@Override
	public void setActive(boolean active) {
		if(active && TheBetweenlands.proxy.getClientWorld() != null && (!this.isActive() || this.lastWorld != TheBetweenlands.proxy.getClientWorld()) && TheBetweenlands.proxy.getClientPlayer() != null && this.world != null && this.world.isRemote) {
			this.lastWorld = TheBetweenlands.proxy.getClientWorld();
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			this.showStatusMessage(player);
		}

		super.setActive(active);
	}

	protected void showStatusMessage(EntityPlayer player) {

	}

	@Override
	public void update(World world) {
		super.update(world);

		this.world = world;

		if(!world.isRemote) {
			if(BetweenlandsConfig.WORLD_AND_DIMENSION.enableSeasonalEvents) {
				long dayDiff = this.getDayDiffFromStartDate();
				if (dayDiff >= 0 && dayDiff <= this.getDurationInDays() && BetweenlandsConfig.WORLD_AND_DIMENSION.enableSeasonalEvents) {
					if (!this.isActive() && !this.wasSet) {
						this.setActive(true);
						this.wasSet = true;
					}
				} else if (this.wasSet) {
					this.wasSet = false;
					this.setActive(false);
				}
			} else if(this.wasSet) {
				this.wasSet = false;
				this.setActive(false);
			}
		}
	}

	@Override
	public void resetActiveState() {
		long dayDiff = this.getDayDiffFromStartDate();
		if (dayDiff >= 0 && dayDiff <= this.getDurationInDays() && BetweenlandsConfig.WORLD_AND_DIMENSION.enableSeasonalEvents) {
			if (!this.isActive()) {
				this.setActive(true);
			}
			this.wasSet = true;
		} else {
			if(this.isActive()) {
				this.setActive(false);
			}
			this.wasSet = false;
		}
	}

	@Override
	public void saveEventData() { 
		super.saveEventData();
		this.getData().setBoolean("wasSet", this.wasSet);
	}

	@Override
	public void loadEventData() { 
		super.loadEventData();
		this.wasSet = this.getData().getBoolean("wasSet");
	}
}

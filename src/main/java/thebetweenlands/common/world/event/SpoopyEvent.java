package thebetweenlands.common.world.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.client.ClientEvents;
import thebetweenlands.common.TheBetweenlands;

public class SpoopyEvent extends SeasonalEnvironmentEvent {
	public static final ResourceLocation ID = TheBetweenlands.prefix("spook");

	private static final long SPOOPY_DATE = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.OCTOBER, 23, 0, 0).getTime().getTime();

	private float skyTransparency = 0.0F;
	private float lastSkyTransparency = 0.0F;

	public SpoopyEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public long getStartDateInMs() {
		return SPOOPY_DATE;
	}

	@Override
	public int getDurationInDays() {
		return 8;
	}

	public static boolean isSpoooopy(Level level) {
		if (level != null) {
//			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(level);
//			if (provider != null) {
//				return provider.getEnvironmentEventRegistry().spoopy.isActive();
//			}
		}
		return false;
	}

	public void setSkyTransparency(float transparency) {
		this.lastSkyTransparency = this.skyTransparency;
		this.skyTransparency = transparency;
	}

	public float getSkyTransparency(float partialTicks) {
		return (this.skyTransparency + (this.skyTransparency - this.lastSkyTransparency) * partialTicks) / 2.0F;
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public void setActive(boolean active) {
		//Mark blocks in range for render update to update block textures
		if (active != this.isActive() && ClientEvents.getClientLevel() != null && ClientEvents.getClientPlayer() != null) {
			updateModelActiveState(active);

			Player player = ClientEvents.getClientPlayer();
			int px = Mth.floor(player.getX()) - 256;
			int py = Mth.floor(player.getY()) - 256;
			int pz = Mth.floor(player.getZ()) - 256;
			Minecraft.getInstance().levelRenderer.setBlocksDirty(px, py, pz, px + 512, py + 512, pz + 512);
		}

		super.setActive(active);
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if (level.isClientSide()) {
			if (this.isActive()) {
				if (this.skyTransparency < 1.0F) {
					this.setSkyTransparency(this.skyTransparency + 0.003F);
				}
				if (this.skyTransparency > 1.0F) {
					this.setSkyTransparency(1.0F);
				}
			} else {
				if (this.skyTransparency > 0.0F) {
					this.setSkyTransparency(this.skyTransparency - 0.003F);
				}
				if (this.skyTransparency < 0.0F) {
					this.setSkyTransparency(0.0F);
				}
			}
		}
	}

//	public static void onClientTick(ClientTickEvent event) {
//		Level level = Minecraft.getInstance().level;
//		if(level != null && level.provider instanceof WorldProviderBetweenlands) {
//			updateModelActiveState(((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().spoopy.isActive());
//		} else {
//			updateModelActiveState(false);
//		}
//	}

	private static void updateModelActiveState(boolean active) {
		//ModelRegistry.SPOOK_EVENT.setActive(active);
	}

	@Override
	protected void showStatusMessage(Player player) {
		player.displayClientMessage(Component.translatable("chat.event.spook"), true);
	}
}

package thebetweenlands.common.world.event;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ModelRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.util.config.ConfigHandler;

public class EventSpoopy extends EnvironmentEvent {
	private static final long SPOOPY_DATE = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), 9, 23, 0, 0).getTime().getTime();

	private World world;
	private World lastWorld;
	private boolean chatSent = false;
	private float skyTransparency = 0.0F;
	private float lastSkyTransparency = 0.0F;

	public void setSkyTransparency(float transparency) {
		this.lastSkyTransparency = this.skyTransparency;
		this.skyTransparency = transparency;
	}

	public float getSkyTransparency(float partialTicks) {
		return (this.skyTransparency + (this.skyTransparency - this.lastSkyTransparency) * partialTicks) / 2.0F;
	}

	public EventSpoopy(EnvironmentEventRegistry registry) {
		super(registry);
	}

	public long getDayDiff() {
		return TimeUnit.DAYS.convert(Calendar.getInstance().getTime().getTime() - SPOOPY_DATE, TimeUnit.MILLISECONDS);
	}

	private boolean wasSet = false;

	public static boolean isSpoopy(World world) {
		if(world != null) {
			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(world);
			if(provider != null) {
				return provider.getEnvironmentEventRegistry().SPOOPY.isActive();
			}
		}
		return false;
	}

	@Override
	public String getEventName() {
		return "Spook";
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		if(active && TheBetweenlands.proxy.getClientWorld() != null && (!this.isActive() || this.lastWorld != TheBetweenlands.proxy.getClientWorld()) && TheBetweenlands.proxy.getClientPlayer() != null && this.world != null && this.world.isRemote) {
			this.lastWorld = TheBetweenlands.proxy.getClientWorld();
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			player.sendMessage(new TextComponentTranslation("chat.event.spook"));
		}
		//Mark blocks in range for render update to update block textures
		if(active != this.isActive() && TheBetweenlands.proxy.getClientWorld() != null && TheBetweenlands.proxy.getClientPlayer() != null) {
			updateModelActiveState(active);
			
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			int px = MathHelper.floor(player.posX) - 256;
			int py = MathHelper.floor(player.posY) - 256;
			int pz = MathHelper.floor(player.posZ) - 256;
			TheBetweenlands.proxy.getClientWorld().markBlockRangeForRenderUpdate(px, py, pz, px + 512, py + 512, pz + 512);
		}
		super.setActive(active, markDirty);
	}

	@Override
	public void update(World world) {
		super.update(world);
		this.world = world;
		if(!world.isRemote) {
			if (ConfigHandler.enableSeasonalEvents) {
				long dayDiff = this.getDayDiff();
				if (dayDiff >= 0 && dayDiff <= 8 && ConfigHandler.enableSeasonalEvents) {
					if (!this.isActive() && !this.wasSet) {
						this.setActive(true, true);
						this.wasSet = true;
					}
				} else if (this.wasSet) {
					this.wasSet = false;
					this.setActive(false, true);
				}
			}
		} else {
			if(this.isActive()) {
				if(this.skyTransparency < 1.0F) {
					this.setSkyTransparency(this.skyTransparency + 0.003F);
				}
				if(this.skyTransparency > 1.0F) {
					this.setSkyTransparency(1.0F);
				}
			} else {
				if(this.skyTransparency > 0.0F) {
					this.setSkyTransparency(this.skyTransparency - 0.003F);
				}
				if(this.skyTransparency < 0.0F) {
					this.setSkyTransparency(0.0F);
				}
			}
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

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onClientTick(ClientTickEvent event) {
		World world = Minecraft.getMinecraft().world;
		if(world != null && world.provider instanceof WorldProviderBetweenlands) {
			updateModelActiveState(((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().SPOOPY.isActive());
		} else {
			updateModelActiveState(false);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static void updateModelActiveState(boolean active) {
		ModelRegistry.SPOOK_EVENT.setActive(active);
	}
}

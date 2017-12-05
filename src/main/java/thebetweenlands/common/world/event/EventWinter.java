package thebetweenlands.common.world.event;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.event.UpdateFogEvent;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.api.misc.FogState;
import thebetweenlands.client.render.sky.BLSnowRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ModelRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EventWinter extends EnvironmentEvent {
	private static final long WINTER_DATE = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), 12, 1, 0, 0).getTime().getTime();

	private World world;
	private World lastWorld;
	private boolean chatSent = false;

	//TODO Sync strength/Save to NBT
	private float snowingStrength = 4.0F;

	public EventWinter(EnvironmentEventRegistry registry) {
		super(registry);
	}

	public long getDayDiff() {
		return TimeUnit.DAYS.convert(Calendar.getInstance().getTime().getTime() - WINTER_DATE, TimeUnit.MILLISECONDS);
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

	public float getSnowingStrength() {
		return this.snowingStrength;
	}

	@Override
	public String getEventName() {
		return "Winter";
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		if(active && TheBetweenlands.proxy.getClientWorld() != null && (!this.isActive() || this.lastWorld != TheBetweenlands.proxy.getClientWorld()) && TheBetweenlands.proxy.getClientPlayer() != null && this.world != null && this.world.isRemote) {
			this.lastWorld = TheBetweenlands.proxy.getClientWorld();
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			player.sendMessage(new TextComponentTranslation("chat.event.winter"));
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
			long dayDiff = this.getDayDiff();
			if(dayDiff >= 0 && dayDiff <= 8) {
				if(!this.isActive() && !this.wasSet) {
					this.setActive(true, true);
					this.wasSet = true;
				}
			} else if(this.wasSet) {
				this.wasSet = false;
				this.setActive(false, true);
			}
		} else {
			this.updateSnowRenderer();
		}

		this.snowingStrength = 8.0F;
	}

	@SideOnly(Side.CLIENT)
	protected void updateSnowRenderer() {
		BLSnowRenderer.INSTANCE.update();
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
			updateModelActiveState(((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().WINTER.isActive());
		} else {
			updateModelActiveState(false);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private static void updateModelActiveState(boolean active) {
		ModelRegistry.WINTER_EVENT.setActive(active);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	@SideOnly(Side.CLIENT)
	public static void onUpdateFog(UpdateFogEvent event) {
		World world = event.getWorld();
		if(world.provider instanceof WorldProviderBetweenlands && ((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().WINTER.isActive()) {
			float snowingStrength = ((WorldProviderBetweenlands)world.provider).getEnvironmentEventRegistry().WINTER.getSnowingStrength();
			FogState state = event.getFogState();
			Fog.MutableFog newFog = new Fog.MutableFog(state.getFog());
			newFog.setStart(Math.min(2.0F + event.getFarPlaneDistance() * 0.8F / (1.0F + snowingStrength), state.getTargetFog().getStart()));
			newFog.setEnd(Math.min(8.0F + event.getFarPlaneDistance() / (1.0F + snowingStrength * 0.5F), state.getTargetFog().getEnd()));
			newFog.setColor(
					MathHelper.clamp(0.8F / 4.0F * snowingStrength, 0.5F, 0.8F), 
					MathHelper.clamp(0.8F / 4.0F * snowingStrength, 0.5F, 0.8F), 
					MathHelper.clamp(0.8F / 4.0F * snowingStrength, 0.5F, 0.8F)
					);
			state.setTargetFog(newFog.toImmutable());
		}
	}
}

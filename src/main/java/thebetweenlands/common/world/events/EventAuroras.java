package thebetweenlands.common.world.events;

import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.TimedEnvironmentEvent;

public class EventAuroras extends TimedEnvironmentEvent {
	public EventAuroras(EnvironmentEventRegistry registry) {
		super(registry);
	}

	private short auroraType = 0;

	@Override
	public String getEventName() {
		return "auroras";
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(42000) + 28000;
	}

	@Override
	public int getOnTime(Random rnd) {
		this.auroraType = (short)rnd.nextInt(3);
		return rnd.nextInt(20000) + 8000;
	}

	@Override
	public void setActive(boolean active, boolean markDirty) {
		if(active && this.getRegistry().getActiveEvents().size() <= 1) {
			super.setActive(active, markDirty);
			return;
		} else if(!active) {
			super.setActive(active, markDirty);
		}
	}

	@Override
	public void update(World world) {
		super.update(world);
		if(this.getRegistry().getActiveEvents().size() > 1) {
			this.setActive(false, true);
		}
	}
	
	@Override
	public void saveEventData() {
		super.saveEventData();
		this.getData().setShort("auroraType", this.auroraType);
	}

	@Override
	public void loadEventData() {
		super.loadEventData();
		this.auroraType = this.getData().getShort("auroraType");
	}

	@Override
	public void loadEventPacket(ByteBuf buffer) { 
		this.auroraType = buffer.readShort();
	}

	@Override
	public void sendEventPacket(ByteBuf buffer) { 
		buffer.writeShort(this.auroraType);
	}

	public short getAuroraType() {
		return this.auroraType;
	}
}

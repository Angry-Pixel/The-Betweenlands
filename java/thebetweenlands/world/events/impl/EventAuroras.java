package thebetweenlands.world.events.impl;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import thebetweenlands.world.events.TimedEnvironmentEvent;

public class EventAuroras extends TimedEnvironmentEvent {
	public EventAuroras(EnvironmentEventRegistry registry) {
		super(registry);
	}

	private short auroraType = 0;

	@Override
	public String getEventName() {
		return "Auroras";
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(40000) + 20000;
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

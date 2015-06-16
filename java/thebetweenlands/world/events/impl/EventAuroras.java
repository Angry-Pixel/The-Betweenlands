package thebetweenlands.world.events.impl;

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
import thebetweenlands.world.events.TimedEnvironmentEvent;

public class EventAuroras extends TimedEnvironmentEvent {
	@Override
	public String getEventName() {
		return "Auroras";
	}
	@Override
	public int getOffTime(Random rnd) {
		return 0;
	}
	@Override
	public int getOnTime(Random rnd) {
		return 100000;
	}
}

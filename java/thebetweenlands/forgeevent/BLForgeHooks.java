package thebetweenlands.forgeevent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.forgeevent.entity.LivingSetRevengeTargetEvent;
import thebetweenlands.forgeevent.entity.player.PlayerEventGetHurtSound;
import thebetweenlands.utils.confighandler.ConfigHandler;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

public final class BLForgeHooks {
	private BLForgeHooks() {}

	public static void onLivingSetRevengeTarget(EntityLivingBase entity, EntityLivingBase target) {
		MinecraftForge.EVENT_BUS.post(new LivingSetRevengeTargetEvent(entity, target));
	}

	public static String onPlayerGetHurtSound(EntityPlayer player) {
		PlayerEventGetHurtSound event = new PlayerEventGetHurtSound(player, "game.player.hurt");
		MinecraftForge.EVENT_BUS.post(event);
		return event.hurtSound;
	}

	public static EntityPlayerMP createPlayerForUser(ServerConfigurationManager serverConfigurationManager, GameProfile gameProfile) {
		UUID uuid = EntityPlayer.func_146094_a(gameProfile);
		ArrayList sameUUIDPlayers = Lists.newArrayList();
		for (int i = 0; i < serverConfigurationManager.playerEntityList.size(); i++) {
			EntityPlayerMP player = (EntityPlayerMP) serverConfigurationManager.playerEntityList.get(i);
			if (player.getUniqueID().equals(uuid)) {
				sameUUIDPlayers.add(player);
			}
		}
		Iterator<EntityPlayerMP> playerIterator = sameUUIDPlayers.iterator();
		while (playerIterator.hasNext()) {
			EntityPlayerMP player = playerIterator.next();
			player.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
		}
		int dimensionId = TheBetweenlands.proxy.getDebugHandler().isInDebugWorld() ? ConfigHandler.DIMENSION_ID : 0;
		MinecraftServer mcServer = serverConfigurationManager.getServerInstance();
		ItemInWorldManager itemInWorldManager;
		WorldServer world = mcServer.worldServerForDimension(dimensionId);
		if (mcServer.isDemo()) {
			itemInWorldManager = new DemoWorldManager(world);
		} else {
			itemInWorldManager = new ItemInWorldManager(world);
		}
		return new EntityPlayerMP(mcServer, world, gameProfile, itemInWorldManager);
	}
}

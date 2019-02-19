package thebetweenlands.common;

import java.net.Proxy;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class DistUtils {
	@Nullable
	public static EntityPlayer getClientPlayer() {
		return DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
	}

	@Nullable
	public static World getClientWorld() {
		return DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().world);
	}
	
	@Nullable
	public static Proxy getNetProxy() {
		return DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().getProxy());
	}
}

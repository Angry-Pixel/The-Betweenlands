package thebetweenlands.network.base;

import cpw.mods.fml.relauncher.Side;

import java.util.List;
import java.util.Map;

public interface IPacketProxy {
	public void registerPacketHandler(String className, Side side) throws Exception;
	public Map<Class<? extends IPacket>, List<ListenerEntry>> getListeners();
}

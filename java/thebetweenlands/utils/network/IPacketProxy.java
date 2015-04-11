package thebetweenlands.utils.network;

import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;

public interface IPacketProxy {
	public void registerPacketHandler(String className, Side side) throws Exception;
	public Map<Class<? extends IPacket>, List<ListenerEntry>> getListeners();
}

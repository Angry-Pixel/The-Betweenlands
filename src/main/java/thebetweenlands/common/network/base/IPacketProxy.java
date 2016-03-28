package thebetweenlands.common.network.base;

import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.relauncher.Side;

public interface IPacketProxy {
	public void registerPacketHandler(String className, Side side) throws Exception;
	public Map<Class<? extends IPacket>, List<ListenerEntry>> getListeners();
}

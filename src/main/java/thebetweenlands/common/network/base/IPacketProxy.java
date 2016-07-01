package thebetweenlands.common.network.base;

import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Map;

public interface IPacketProxy {
    void registerPacketHandler(String className, Side side) throws Exception;

    Map<Class<? extends IPacket>, List<ListenerEntry>> getListeners();
}

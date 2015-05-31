package thebetweenlands.network.base.impl;

import thebetweenlands.network.base.IPacket;
import thebetweenlands.network.base.ListenerEntry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPacketProxy extends CommonPacketProxy {
	protected Map<Class<? extends IPacket>, List<ListenerEntry>> clientListeners = (Map<Class<? extends IPacket>, List<ListenerEntry>>) Collections.synchronizedMap(new HashMap<Class<? extends IPacket>, List<ListenerEntry>>());

	@Override
	public final Map<Class<? extends IPacket>, List<ListenerEntry>> getListeners() {
		return this.clientListeners;
	}
}

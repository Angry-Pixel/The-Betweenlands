package thebetweenlands.network.base.impl;

import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.network.base.IPacket;
import thebetweenlands.network.base.IPacketProxy;
import thebetweenlands.network.base.ListenerEntry;
import thebetweenlands.network.base.SubscribePacket;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class CommonPacketProxy implements IPacketProxy {
    protected Map<Class<? extends IPacket>, List<ListenerEntry>> commonListeners = (Map<Class<? extends IPacket>, List<ListenerEntry>>) Collections.synchronizedMap(new HashMap<Class<? extends IPacket>, List<ListenerEntry>>());

    private static final List<ListenerEntry> getListenerEntries(Class clazz) throws Exception {
        List<ListenerEntry> listenerEntries = new ArrayList<ListenerEntry>();
        for (Method method : clazz.getDeclaredMethods()) {
            SubscribePacket annotation = method.getAnnotation(SubscribePacket.class);
            if (annotation != null) {
                int methodModifiers = method.getModifiers();
                if ((methodModifiers & Modifier.STATIC) == 0
                        || (methodModifiers & Modifier.ABSTRACT) != 0
                        || (methodModifiers & Modifier.PRIVATE) != 0
                        || (methodModifiers & Modifier.PROTECTED) != 0) {
                    throw new Exception("Invalid method modifiers for method! Class: " + clazz.getName() + " Method: " + method.getName());
                }
                boolean validParams = false;
                Class[] params = method.getParameterTypes();
                if (params.length == 1) {
                    Class messageType = params[0];
                    if (IPacket.class.isAssignableFrom(messageType)) {
                        listenerEntries.add(new ListenerEntry(method, clazz, messageType));
                        validParams = true;
                    }
                }
                if (!validParams) {
                    throw new Exception("Invalid packet listener parameters! Class: " + clazz.getName() + " Method: " + method.getName());
                }
            }
        }
        return listenerEntries;
    }

    @Override
    public Map<Class<? extends IPacket>, List<ListenerEntry>> getListeners() {
        return this.commonListeners;
    }

    @Override
    public final void registerPacketHandler(String className, Side side) throws Exception {
        if ((this instanceof ClientPacketProxy) || (side == Side.SERVER && !(this instanceof ClientPacketProxy))) {
            Class clazz = Class.forName(className);
            this.registerPacketHandler(clazz);
        }
    }

    protected final void registerPacketHandler(Class clazz) throws Exception {
        List<ListenerEntry> foundEntries = getListenerEntries(clazz);
        if (foundEntries.size() > 0) {
            for (ListenerEntry foundEntry : foundEntries) {
                Class pktType = foundEntry.getParamType();
                List<ListenerEntry> listenerEntries = this.getListeners().get(pktType);
                if (listenerEntries == null) {
                    listenerEntries = new ArrayList<ListenerEntry>();
                    this.getListeners().put(pktType, listenerEntries);
                }
                listenerEntries.add(foundEntry);
            }
        }
    }
}

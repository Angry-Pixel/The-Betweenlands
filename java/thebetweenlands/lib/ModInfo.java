package thebetweenlands.lib;

import thebetweenlands.utils.confighandler.ConfigHandler;

public class ModInfo
{
	public static final String ID = "thebetweenlands";
	public static final String NAME = "The Betweenlands";
	public static final String VERSION = "1.0.7-alpha";
	public static final String CHANNEL = ID;
	public static final String CLIENTPROXY_LOCATION = "thebetweenlands.proxy.ClientProxy";
	public static final String COMMONPROXY_LOCATION = "thebetweenlands.proxy.CommonProxy";
	public static final String CLIENTPACKETPROXY_LOCATION = "thebetweenlands.network.base.impl.ClientPacketProxy";
	public static final String COMMONPACKETPROXY_LOCATION = "thebetweenlands.network.base.impl.CommonPacketProxy";
	public static final String CONFIG_GUI = "thebetweenlands.utils.confighandler.ConfigGuiFactory";
	public static final int DIMENSION_ID = ConfigHandler.DIMENSION_ID;
	public static final int MAX_LANTERN_LENGTH = 20;
}

package thebetweenlands.common.lib;

public class ModInfo {
	public static final String ID = "thebetweenlands";
	public static final String NAME_PREFIX = ID + ".";
	public static final String ASSETS_PREFIX = ID + ":";
	public static final String NAME = "The Betweenlands";
	public static final String CHANNEL = ID;
	public static final String CLIENTPROXY_LOCATION = "thebetweenlands.client.proxy.ClientProxy";
	public static final String COMMONPROXY_LOCATION = "thebetweenlands.common.proxy.CommonProxy";
	public static final String API_NAME = "BetweenlandsAPI";

	public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2773,);after:tombmanygraves2api@[1.12-4.1.0,);after:tombmanygraves@[1.12-4.1.0,);after:jei@[4.12.0,)";
	public static final String MC_VERSIONS = "[1.12.2]";
	public static final String VERSION = "3.8.1";
	public static final String API_VERSION = "1.13.5";
	public static final String CONFIG_VERSION = "1.0.0"; //Increment and add updater if properties are moved/renamed or removed
	public static final String GALLERY_VERSION = "1.0.0"; //Protocol version of the online gallery. Change this when a breaking change is introduced to the gallery protocol or format. For backwards compatibility.
	
	//Values are replaced by build script
	public static final boolean IDE = /*!ide*/true/*ide!*/;
	public static final boolean SERVER_ONLY = /*!server_only*/false/*server_only!*/;
	
}

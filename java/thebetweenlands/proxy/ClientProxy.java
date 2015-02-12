package thebetweenlands.proxy;

import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.entities.render.RenderDarkDruid;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderInformation() {
		RenderingRegistry.registerEntityRenderingHandler(EntityDarkDruid.class, new RenderDarkDruid());
	}
}

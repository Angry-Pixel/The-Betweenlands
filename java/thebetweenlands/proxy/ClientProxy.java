package thebetweenlands.proxy;

import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.entities.mobs.models.ModelDarkDruid;
import thebetweenlands.entities.render.RenderDarkDruid;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void initRenderers() {
		EntityRegistry.registerGlobalEntityID(EntityDarkDruid.class, "darkDruid", EntityRegistry.findGlobalUniqueEntityId(), 0x40FF00, 0x0B610B);//the last two parameters are the colour of the spawnegg you can use HTML color codes just replace the '#' with '0x'
		RenderingRegistry.registerEntityRenderingHandler(EntityDarkDruid.class, new RenderDarkDruid(new ModelDarkDruid(), 0.7F));//the 0.5F is the shadowsize
	}
}

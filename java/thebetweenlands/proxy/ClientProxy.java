package thebetweenlands.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.render.entity.RenderDarkDruid;
import thebetweenlands.client.render.item.ItemDruidAltarRenderer;
import thebetweenlands.client.render.tileentity.TileEntityDruidAltarRenderer;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderInformation() {
		
		//Mob Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityDarkDruid.class, new RenderDarkDruid());
		
		//Tile Entities
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new TileEntityDruidAltarRenderer());
		
		//Item Entities
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.druidAltar), new ItemDruidAltarRenderer());
	}
}

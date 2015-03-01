package thebetweenlands.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.render.entity.RenderAngler;
import thebetweenlands.client.render.entity.RenderDarkDruid;
import thebetweenlands.client.render.entity.RenderSludge;
import thebetweenlands.client.render.entity.RenderSwampHag;
import thebetweenlands.client.render.entity.RenderTarBeast;
import thebetweenlands.client.render.entity.RenderWight;
import thebetweenlands.client.render.item.ItemDruidAltarRenderer;
import thebetweenlands.client.render.tileentity.TileEntityDruidAltarRenderer;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.entities.mobs.EntitySludge;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.entities.particles.EntityAltarCraftingFX;
import thebetweenlands.entities.particles.EntityDruidCastingFX;
import thebetweenlands.tileentities.TileEntityDruidAltar;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderInformation() {

        //Mob Entities
        RenderingRegistry.registerEntityRenderingHandler(EntityDarkDruid.class, new RenderDarkDruid());
        RenderingRegistry.registerEntityRenderingHandler(EntityAngler.class, new RenderAngler());
        RenderingRegistry.registerEntityRenderingHandler(EntitySludge.class, new RenderSludge());
        RenderingRegistry.registerEntityRenderingHandler(EntitySwampHag.class, new RenderSwampHag());
        RenderingRegistry.registerEntityRenderingHandler(EntityTarBeast.class, new RenderTarBeast());
        RenderingRegistry.registerEntityRenderingHandler(EntityWight.class, new RenderWight());
        
        //Tile Entities
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new TileEntityDruidAltarRenderer());

        //Item Entities
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BLBlockRegistry.druidAltar), new ItemDruidAltarRenderer());
    }

    @Override
    public void spawnCustomParticle(String particleName, World world, double x, double y, double z, double vecX, double vecY, double vecZ, float scale, Object... data) {
        EntityFX fx = null;

        if( particleName.equals("druidmagic") ) {
            fx = new EntityDruidCastingFX(world, x, y, z, vecX, vecY, vecZ, scale);
        }
        
        if( particleName.equals("druidmagicbig") ) {
            fx = new EntityDruidCastingFX(world, x, y, z, vecX, vecY, vecZ, scale);
            fx.setRBGColorF(0F, 1F, 1F);
        }
        
        if( particleName.equals("altarcrafting") ) {
            fx = new EntityAltarCraftingFX(world, x, y, z, vecX, vecY, vecZ, scale, (TileEntityDruidAltar)data[0]);
        }
        
        if( particleName.equals("smoke") ) {
            fx = new EntitySmokeFX(world, x, y, z, vecX, vecY, vecZ);
        }
        
        if( particleName.equals("flame") ) {
            fx = new EntityFlameFX(world, x, y, z, vecX, vecY, vecZ);
        }
        
        if( particleName.equals("sulfurTorch") ) {
            fx = new EntitySmokeFX(world, x, y, z, 0F, 0F, 0F);
            fx.setRBGColorF(1F, 0.9294F, 0F);
        }
        
		if (particleName.equals("sulfurOre")) {
			fx = new EntitySpellParticleFX(world, x, y, z, vecX, vecY, vecZ);
			fx.setRBGColorF(1F, 0.9294F, 0F);
		}
        
        if (fx != null)
        	Minecraft.getMinecraft().effectRenderer.addEffect(fx);
    }
}

package thebetweenlands.client.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.client.render.render.entity.*;
import thebetweenlands.client.render.render.entity.projectile.RenderFactorySnailPoisonJet;
import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.util.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {

    //Please turn this off again after using
    private static final boolean createJSONFile = false;

    public static RenderFactoryDragonfly dragonFlyRenderer;

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public void registerDefaultBlockItemRenderer(Block block) {
        String blockName = block.getRegistryName().toString().replace(ModInfo.ASSETS_PREFIX, "");
        if (ConfigHandler.debug && createJSONFile)
            JsonRenderGenerator.createJSONForBlock(block, blockName);
        ModelLoader.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(ModInfo.ASSETS_PREFIX + blockName, "inventory"));
        //FIXME: Uhm yeah, ModelLoader#registerItemVariants (the proper way afaik?) doesn't seem to work, so I've also added this here
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + blockName, "inventory"));
    }

    @Override
    public void registerDefaultItemRenderer(Item item) {
        List<ItemStack> list = new ArrayList<>();
        item.getSubItems(item, null, list);
        if (list.size() > 0) {
            for (ItemStack itemStack : list) {
                //Leaving this like this because of easier "multi item items"
                String name = item.getUnlocalizedName(itemStack);
                String itemName = name.substring(name.lastIndexOf(".") + 1, name.length());
                if (ConfigHandler.debug && createJSONFile)
                    JsonRenderGenerator.createJSONForItem(item, itemStack.getItemDamage(), itemName);
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, itemStack.getItemDamage(), new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
                ModelLoader.setCustomModelResourceLocation(item, itemStack.getItemDamage(), new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
            }
        } else {
            String itemName = item.getRegistryName().toString().replace(ModInfo.ASSETS_PREFIX, "");
            if (ConfigHandler.debug && createJSONFile)
                JsonRenderGenerator.createJSONForItem(item, 0, itemName);
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
        }
    }

    @Override
    public void preInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityAngler.class, new RenderFactoryAngler());
        RenderingRegistry.registerEntityRenderingHandler(EntityBlindCaveFish.class, new RenderFactoryBlindCaveFish());
        RenderingRegistry.registerEntityRenderingHandler(EntityMireSnail.class, new RenderFactoryMireSnail());
        RenderingRegistry.registerEntityRenderingHandler(EntityMireSnailEgg.class, new RenderFactoryMireSnailEgg());
        RenderingRegistry.registerEntityRenderingHandler(EntityBloodSnail.class, new RenderFactoryBloodSnail());
        RenderingRegistry.registerEntityRenderingHandler(EntitySnailPoisonJet.class, new RenderFactorySnailPoisonJet());
        RenderingRegistry.registerEntityRenderingHandler(EntitySwampHag.class, new RenderFactorySwampHag());
        RenderingRegistry.registerEntityRenderingHandler(EntityChiromaw.class, new RenderFactoryChiromaw());
        RenderingRegistry.registerEntityRenderingHandler(EntityDragonfly.class, dragonFlyRenderer = new RenderFactoryDragonfly());
        RenderingRegistry.registerEntityRenderingHandler(EntityLurker.class, new RenderFactoryLurker());
        RenderingRegistry.registerEntityRenderingHandler(EntityFrog.class, new RenderFactoryFrog());
        RenderingRegistry.registerEntityRenderingHandler(EntityGiantToad.class, new RenderFactoryGiantToad());
    }
}

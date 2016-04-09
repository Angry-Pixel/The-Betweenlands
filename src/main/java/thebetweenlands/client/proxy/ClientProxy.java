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
import thebetweenlands.client.render.render.entity.*;
import thebetweenlands.client.render.render.entity.projectile.RenderFactorySnailPoisonJet;
import thebetweenlands.common.entity.mobs.*;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.item.ICustomItemRenderType;
import thebetweenlands.common.item.ICustomResourceLocationItem;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.util.config.ConfigHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {

    //Please turn this off again after using
    private static final boolean createJSONFile = false;

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
        String name = block.getUnlocalizedName();
        String blockName = name.substring(name.lastIndexOf(".") + 1, name.length());
        ModelLoader.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(ModInfo.ASSETS_PREFIX + blockName, "inventory"));
        //FIXME: Uhm yeah, ModelLoader#registerItemVariants (the proper way afaik?) doesn't seem to work, so I've also added this here
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + blockName, "inventory"));
        if (ConfigHandler.debug && createJSONFile)
            createJSONForBlock(blockName);
    }

    @Override
    public void registerDefaultItemRenderer(Item item) {
        List<ItemStack> list = new ArrayList<>();
        item.getSubItems(item, null, list);
        if (list.size() > 0) {
            for (ItemStack itemStack : list) {
                String name = item.getUnlocalizedName(itemStack);
                String itemName = name.substring(name.lastIndexOf(".") + 1, name.length());
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, itemStack.getItemDamage(), new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
                ModelLoader.setCustomModelResourceLocation(item, itemStack.getItemDamage(), new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
                if (ConfigHandler.debug && createJSONFile)
                    createJSONForItem(item, itemStack.getItemDamage(), itemName);
            }
        } else {
            String name = item.getUnlocalizedName();
            String itemName = name.substring(name.lastIndexOf(".") + 1, name.length());
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
            if (ConfigHandler.debug && createJSONFile)
                createJSONForItem(item, 0, itemName);
        }
    }


    private void createJSONForItem(Item item, int meta, String itemName) {
        String location;
        if (item instanceof ICustomResourceLocationItem)
            location = ((ICustomResourceLocationItem) item).getCustomResourceLocation(meta);
        else
            location = itemName;
        String path = "models/item/" + itemName + ".json";

        String renderType;
        if (item instanceof ICustomItemRenderType)
            renderType = ((ICustomItemRenderType) item).getCustomRenderType(meta);
        else
            renderType = "item/generated";
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file);
                writer.println("{");
                writer.println("  \"parent\": \"" + renderType + "\",\n" +
                        "   \"textures\": {\n" +
                        "     \"layer0\": \"thebetweenlands:items/" + location + "\"\n" +
                        "   }");
                writer.println("}");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createJSONForBlock(String blockName) {
        String path = "models/block/" + blockName + ".json";

        String renderType = "block/cube_all";
        File file = new File(path);
        try {
            if (file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file);
                writer.println("{");
                writer.println("  \"parent\": \"" + renderType + "\",\n" +
                        "   \"textures\": {\n" +
                        "     \"all\": \"thebetweenlands:blocks/" + blockName + "\"\n" +
                        "   }");
                writer.println("}");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        path = "models/item/" + blockName + ".json";

        renderType = "thebetweenlands:block/" + blockName;
        file = new File(path);
        try {
            if (file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file);
                writer.println("{");
                writer.println("  \"parent\": \"" + renderType);
                writer.println("}");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    }
}

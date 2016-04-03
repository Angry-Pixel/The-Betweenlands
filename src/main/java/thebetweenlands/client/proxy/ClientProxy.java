package thebetweenlands.client.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
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
        String path = "models/" + itemName + ".json";

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
}

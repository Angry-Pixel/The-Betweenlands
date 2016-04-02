package thebetweenlands.client.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.proxy.CommonProxy;

import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {
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
            }
        } else {
            String name = item.getUnlocalizedName();
            String itemName = name.substring(name.lastIndexOf(".") + 1, name.length());
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
        }
    }
}

package thebetweenlands.client.proxy;

import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import thebetweenlands.client.event.CorrosionTextureStitchHandler;
import thebetweenlands.client.gui.inventory.GuiDruidAltar;
import thebetweenlands.client.gui.inventory.GuiPurifier;
import thebetweenlands.client.gui.inventory.GuiWeedwoodWorkbench;
import thebetweenlands.client.render.json.JsonRenderGenerator;
import thebetweenlands.client.render.model.loader.CustomModelManager;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryAngler;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryBlindCaveFish;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryBloodSnail;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryChiromaw;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryDragonFly;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryFrog;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryGiantToad;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryLurker;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryMireSnail;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactoryMireSnailEgg;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactorySnailPoisonJet;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactorySporeling;
import thebetweenlands.client.render.render.entity.renderfactory.RenderFactorySwampHag;
import thebetweenlands.client.render.render.item.RenderBLShield;
import thebetweenlands.client.render.render.item.TileEntityOctineShield;
import thebetweenlands.client.render.render.item.TileEntitySymoriteShield;
import thebetweenlands.client.render.render.item.TileEntityValoniteShield;
import thebetweenlands.client.render.render.item.TileEntityWeedwoodShield;
import thebetweenlands.client.render.render.tile.DruidAltarRenderer;
import thebetweenlands.client.render.render.tile.PurifierRenderer;
import thebetweenlands.client.render.render.tile.WeedwoodWorkbenchRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.mobs.EntityAngler;
import thebetweenlands.common.entity.mobs.EntityBlindCaveFish;
import thebetweenlands.common.entity.mobs.EntityBloodSnail;
import thebetweenlands.common.entity.mobs.EntityChiromaw;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.entity.mobs.EntityFrog;
import thebetweenlands.common.entity.mobs.EntityGiantToad;
import thebetweenlands.common.entity.mobs.EntityLurker;
import thebetweenlands.common.entity.mobs.EntityMireSnail;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;
import thebetweenlands.common.entity.mobs.EntitySporeling;
import thebetweenlands.common.entity.mobs.EntitySwampHag;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.common.tile.TileEntityPurifier;
import thebetweenlands.common.tile.TileEntityWeedwoodWorkbench;
import thebetweenlands.util.config.ConfigHandler;

public class ClientProxy extends CommonProxy {

    //Please turn this off again after using
    private static final boolean createJSONFile = false;

    public static RenderFactoryDragonFly dragonFlyRenderer;

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (id) {
            case GUI_DRUID_ALTAR: {
                if (tile instanceof TileEntityDruidAltar)
                    return new GuiDruidAltar(player.inventory, (TileEntityDruidAltar) tile);
                break;
            }
            case GUI_PURIFIER: {
                if (tile instanceof TileEntityPurifier) {
                    return new GuiPurifier(player.inventory, (TileEntityPurifier) tile);
                }
                break;
            }
            case GUI_WEEDWOOD_CRAFT: {
                if (tile instanceof TileEntityWeedwoodWorkbench) {
                    return new GuiWeedwoodWorkbench(player.inventory, (TileEntityWeedwoodWorkbench) tile);
                }
                break;
            }
        }
        return null;
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public void registerItemAndBlockRenderers() {
    	CustomModelManager.INSTANCE.registerLoader();
        //TODO ItemRegistry.registerRenderers();
        BlockRegistry.registerRenderers();
    }

    @Override
    public void setCustomStateMap(Block block, StateMap stateMap) {
        ModelLoader.setCustomStateMapper(block, stateMap);
    }

    /*
        @Override
        public void registerDefaultBlockItemRenderer(Block block) {
            if (block instanceof BlockRegistry.ISubBlocksBlock) {
                List<String> models = ((BlockRegistry.ISubBlocksBlock) block).getModels();
                if (block instanceof BlockDruidStone) {
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(0), "inventory"));
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 4, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(1), "inventory"));
                } else
                    for (int i = 0; i < models.size(); i++) {
                        if (ConfigHandler.debug && createJSONFile)
                            JsonRenderGenerator.createJSONForBlock(block, models.get(i));
                        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(i), "inventory"));
                    }
            } else {
                String name = block.getRegistryName().toString().replace("thebetweenlands:", "");
                if (ConfigHandler.debug && createJSONFile)
                    JsonRenderGenerator.createJSONForBlock(block, name);
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + name, "inventory"));
            }
        }
    */
    @Override
    public void registerDefaultItemRenderer(Item item) {
        if (item instanceof ItemRegistry.ISubItemsItem) {
            List<String> models = ((ItemRegistry.ISubItemsItem) item).getModels();
            for (int i = 0; i < models.size(); i++) {
                if (ConfigHandler.debug && createJSONFile)
                    JsonRenderGenerator.createJSONForItem(item, models.get(i));
                ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + models.get(i), "inventory"));
            }
        } else if (item instanceof ItemRegistry.ISingleJsonSubItems) {
            List<String> types = ((ItemRegistry.ISingleJsonSubItems) item).getTypes();
            for (int i = 0; i < types.size(); i++) {
                //if (ConfigHandler.debug && createJSONFile)
                //JsonRenderGenerator.createJSONForItem(item, types.get(i)); //TODO: Make this work. Tomorrow, (hopefully), so don't panic
                ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + item.getRegistryName().getResourcePath(), types.get(i)));
            }
        } else {
            String itemName = item.getRegistryName().toString().replace("thebetweenlands:", "");
            if (ConfigHandler.debug && createJSONFile)
                JsonRenderGenerator.createJSONForItem(item, itemName);
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ModInfo.ASSETS_PREFIX + itemName, "inventory"));
        }
    }


    //Probably will only be used while updating
    @Override
    public void changeFileNames() {
        File textures = new File(TheBetweenlands.sourceFile, "assets/thebetweenlands/sounds");
        if (textures.listFiles() != null)
            for (File file : textures.listFiles()) {
                if (file.getName().contains(".ogg")) {
                    CharSequence sequence = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

                    String text = file.getName();
                    for (int i = 0; i < sequence.length(); i++) {
                        text = text.replace("" + sequence.charAt(i), "_" + ("" + sequence.charAt(i)).toLowerCase());
                    }
                    File newFile = new File(file.getPath().replace(file.getName(), "") + text);
                    System.out.println(file.renameTo(newFile));
                } else
                    for (File file2 : file.listFiles()) {
                        if (file2.getName().contains(".ogg")) {
                            CharSequence sequence = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";

                            String text = file2.getName();
                            for (int i = 0; i < sequence.length(); i++) {
                                text = text.replace("" + sequence.charAt(i), "_" + ("" + sequence.charAt(i)).toLowerCase());
                            }
                            File newFile = new File(file2.getPath().replace(file2.getName(), "") + text);
                            System.out.println(file2.renameTo(newFile));
                        }
                    }
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
        RenderingRegistry.registerEntityRenderingHandler(EntityDragonFly.class, dragonFlyRenderer = new RenderFactoryDragonFly());
        RenderingRegistry.registerEntityRenderingHandler(EntityLurker.class, new RenderFactoryLurker());
        RenderingRegistry.registerEntityRenderingHandler(EntityFrog.class, new RenderFactoryFrog());
        RenderingRegistry.registerEntityRenderingHandler(EntityGiantToad.class, new RenderFactoryGiantToad());
        RenderingRegistry.registerEntityRenderingHandler(EntitySporeling.class, new RenderFactorySporeling());
    }

    @Override
    public void postInit() {
        //Tile entities
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPurifier.class, new PurifierRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDruidAltar.class, new DruidAltarRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedwoodWorkbench.class, new WeedwoodWorkbenchRenderer());
        //shield rendering
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOctineShield.class, new RenderBLShield(RenderBLShield.Shieldtype.OCTINE));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySymoriteShield.class, new RenderBLShield(RenderBLShield.Shieldtype.SYMORITE));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityValoniteShield.class, new RenderBLShield(RenderBLShield.Shieldtype.VALONITE));
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWeedwoodShield.class, new RenderBLShield(RenderBLShield.Shieldtype.WEEDWOOD));

        //item models
        ForgeHooksClient.registerTESRItemStack(ItemRegistry.OCTINE_SHIELD, 0, TileEntityOctineShield.class);
        ForgeHooksClient.registerTESRItemStack(ItemRegistry.VALONITE_SHIELD, 0, TileEntityValoniteShield.class);
        ForgeHooksClient.registerTESRItemStack(ItemRegistry.WEEDWOOD_SHIELD, 0, TileEntityWeedwoodShield.class);
        ForgeHooksClient.registerTESRItemStack(ItemRegistry.SYMORITE_SHIELD, 0, TileEntitySymoriteShield.class);
    }
    
    @Override
    public void registerEventHandlers() {
    	MinecraftForge.EVENT_BUS.register(new CorrosionTextureStitchHandler());
    }
}

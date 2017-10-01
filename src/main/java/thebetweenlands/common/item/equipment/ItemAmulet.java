package thebetweenlands.common.item.equipment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.ICircleGemCapability;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGem;
import thebetweenlands.common.capability.circlegem.CircleGem.CombatType;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.entity.mobs.EntityGiantToad;
import thebetweenlands.common.entity.mobs.EntityTarminion;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.LightingUtil;
import thebetweenlands.util.NBTHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemAmulet extends Item implements IEquippable {
    public static final List<Class<? extends EntityLivingBase>> SUPPORTED_ENTITIES = new ArrayList<Class<? extends EntityLivingBase>>();

    static {
        SUPPORTED_ENTITIES.add(EntityTarminion.class);
        SUPPORTED_ENTITIES.add(EntityGiantToad.class);
    }

    public ItemAmulet() {
        this.setCreativeTab(BLCreativeTabs.SPECIALS);
        this.setMaxStackSize(1);

        CircleGemHelper.addGemPropertyOverrides(this);
        IEquippable.addEquippedPropertyOverrides(this);
    }

    /**
     * Adds an amulet to the specified entity
     *
     * @param gem
     * @param entity
     * @param canUnequip
     * @param canDrop
     * @return True if successful
     */
    public static boolean addAmulet(CircleGemType gem, Entity entity, boolean canUnequip, boolean canDrop) {
        ItemStack amulet = createStack(gem);

        NBTTagCompound nbt = NBTHelper.getStackNBTSafe(amulet);
        nbt.setBoolean("canUnequip", canUnequip);
        nbt.setBoolean("canDrop", canDrop);

        ItemStack result = EquipmentHelper.equipItem(null, entity, amulet, false);

        return result == null || result.getCount() != amulet.getCount();

    }

    /**
     * Creates an amulet with the specified gem
     *
     * @param gem
     * @return
     */
    public static ItemStack createStack(CircleGemType gem) {
        ItemStack stack = new ItemStack(ItemRegistry.AMULET);
        CircleGemHelper.setGem(stack, gem);
        return stack;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLiving(RenderLivingEvent.Specials.Post<EntityLivingBase> event) {
        if (event.getEntity() != null) {
            renderAmulet(event.getEntity(), event.getX(), event.getY(), event.getZ(), WorldRenderHandler.getPartialTicks());
        }
    }

    @SideOnly(Side.CLIENT)
    private static void renderAmulet(EntityLivingBase entity, double x, double y, double z, float partialTicks) {
        if (entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
            IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
            IInventory inv = cap.getInventory(EnumEquipmentInventory.AMULET);
            List<ItemStack> items = new ArrayList<ItemStack>(inv.getSizeInventory());

            for (int i = 0; i < inv.getSizeInventory(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack != null && CircleGemHelper.getGem(stack) != CircleGemType.NONE) {
                    items.add(stack);
                }
            }

            int amulets = items.size();
            float degOffset = 360.0F / amulets;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            ITextureObject texture = textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            texture.setBlurMipmap(false, false);

            int i = 0;
            for (ItemStack stack : items) {
                GlStateManager.rotate(degOffset, 0, 1, 0);

                CircleGemType gem = CircleGemHelper.getGem(stack);
                ItemStack gemItem = null;

                switch (gem) {
                    case CRIMSON:
                        gemItem = new ItemStack(ItemRegistry.CRIMSON_MIDDLE_GEM);
                        break;
                    case AQUA:
                        gemItem = new ItemStack(ItemRegistry.AQUA_MIDDLE_GEM);
                        break;
                    case GREEN:
                        gemItem = new ItemStack(ItemRegistry.GREEN_MIDDLE_GEM);
                        break;
                    default:
                }

                if (gemItem != null) {
                    IBakedModel model = renderItem.getItemModelMesher().getItemModel(gemItem);

                    GlStateManager.pushMatrix();
                    GlStateManager.rotate((entity.ticksExisted + partialTicks) * 1.5F, 0, 1, 0);
                    double eyeHeight = entity.getEyeHeight();
                    GlStateManager.translate(0, eyeHeight / 1.5D + Math.sin((entity.ticksExisted + partialTicks) / 60.0D + (double) i / amulets * Math.PI * 2.0D) / 2.0D * entity.height / 4.0D, entity.width / 1.25D);
                    GlStateManager.scale(0.25F * entity.height / 2.0D, 0.25F * entity.height / 2.0D, 0.25F * entity.height / 2.0D);
                    GlStateManager.enableBlend();
                    GlStateManager.color(1, 1, 1, 0.8F);
                    GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

                    LightingUtil.INSTANCE.setLighting(255);

                    renderItem.renderItem(stack, model);

                    LightingUtil.INSTANCE.revert();

                    GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
                    float scale = ((float) Math.cos(entity.ticksExisted / 5.0F) + 1.0F) / 15.0F + 1.05F;
                    GlStateManager.scale(scale, scale, scale);
                    GlStateManager.colorMask(false, false, false, false);

                    renderItem.renderItem(stack, model);

                    GlStateManager.colorMask(true, true, true, true);

                    renderItem.renderItem(stack, model);

                    GlStateManager.popMatrix();

                    i++;
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

            texture.restoreLastBlurMipmap();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            list.add(createStack(CircleGemType.NONE));
            list.add(createStack(CircleGemType.AQUA));
            list.add(createStack(CircleGemType.CRIMSON));
            list.add(createStack(CircleGemType.GREEN));
        }
    }

    @Override
    public EnumEquipmentInventory getEquipmentCategory(ItemStack stack) {
        return EnumEquipmentInventory.AMULET;
    }

    @Override
    public boolean canEquipOnRightClick(ItemStack stack, EntityPlayer player, Entity target) {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack stack, EntityPlayer player, Entity target) {
        if (CircleGemHelper.getGem(stack) == CircleGemType.NONE || (target instanceof EntityPlayer == false && !SUPPORTED_ENTITIES.contains(target.getClass()) && player != null)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityPlayer player, Entity target, IInventory inventory) {
        return target == player || stack.getTagCompound() == null || !stack.getTagCompound().hasKey("canUnequip") || stack.getTagCompound().getBoolean("canUnequip");
    }

    @Override
    public boolean canDrop(ItemStack stack, Entity entity, IInventory inventory) {
        return stack.getTagCompound() == null || !stack.getTagCompound().hasKey("canDrop") || stack.getTagCompound().getBoolean("canDrop");
    }

    @Override
    public void onEquip(ItemStack stack, Entity entity, IInventory inventory) {
        if (entity.hasCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null)) {
            ICircleGemCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null);
            cap.addGem(new CircleGem(CircleGemHelper.getGem(stack), CombatType.BOTH));
        }
    }

    @Override
    public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) {
        if (entity.hasCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null)) {
            ICircleGemCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CIRCLE_GEM, null);
            List<CircleGem> gems = cap.getGems();
            CircleGemType type = CircleGemHelper.getGem(stack);

            for (CircleGem gem : gems) {
                if (gem.getCombatType() == CombatType.BOTH && gem.getGemType() == type) {
                    cap.removeGem(gem);
                    break;
                }
            }
        }
    }

    @Override
    public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
    }
}

package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.ICustomItemRenderType;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemWeedwoodBow extends ItemBow implements ICustomItemRenderType {
    public static final int ANIMATION_LENGTH = 3;

    public ItemWeedwoodBow() {
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 100000;
    }

    @Override
    public String getCustomRenderType(int meta) {
        return "item/handheld";
    }
/*
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        int maxUseDuration = getMaxItemUseDuration(stack) - inUseCount;

        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, maxUseDuration);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
            return;
        maxUseDuration = Math.min(event.charge, 10);

        boolean canShoot = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

        EnumArrowType type = null;
        ItemBLArrow arrowItem = null;
        for(int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack currentStack = player.inventory.mainInventory[i];
            if(currentStack != null && currentStack.getItem() instanceof ItemBLArrow) {
                arrowItem =  ((ItemBLArrow)currentStack.getItem());
                type = arrowItem.getType();
                break;
            }
        }

        if (canShoot || type != null) {
            float power = maxUseDuration / 10.0F;
            power = (power * power + power * 2.0F) / 2.0F;

            power *= CorrodibleItemHelper.getModifier(stack) * 1.15F;

            if (power < 0.1F)
                return;

            if (power > 1.0F)
                power = 1.0F;

            EntityBLArrow entityarrow = new EntityBLArrow(world, player, power * 2.0f);
            if (!world.isRemote) {
                entityarrow.setArrowType(type != null ? type : EnumArrowType.DEFAULT);
            }
            if (power == 1.0F) {
                entityarrow.setIsCritical(true);
            }

            int powerEnchant = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

            if (powerEnchant > 0) {
                entityarrow.setDamage(entityarrow.getDamage() + (double) powerEnchant * 0.5D + 0.5D);
            }

            int punchEnchant = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

            if (punchEnchant > 0) {
                entityarrow.setKnockbackStrength(punchEnchant);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
                entityarrow.setFire(100);
            }

            stack.damageItem(1, player);
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

            if (player.capabilities.isCreativeMode) {
                entityarrow.canBePickedUp = 2;
            } else if (arrowItem != null) {
                player.inventory.consumeInventoryItem(arrowItem);
            }

            if (!world.isRemote) {
                world.spawnEntityInWorld(entityarrow);
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        ArrowNockEvent event = new ArrowNockEvent(player, item);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return event.result;
        }

        EnumArrowType type = null;
        for(int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack currentStack = player.inventory.mainInventory[i];
            if(currentStack != null && currentStack.getItem() instanceof ItemBLArrow) {
                type = ((ItemBLArrow)currentStack.getItem()).getType();
                break;
            }
        }

        if (player.capabilities.isCreativeMode || type != null) {
            player.setItemInUse(item, this.getMaxItemUseDuration(item));
        }

        return item;
    }*/
}

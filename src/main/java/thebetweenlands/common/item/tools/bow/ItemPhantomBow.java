package thebetweenlands.common.item.tools.bow;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemPhantomBow extends ItemBLBow {
    private static float ROTATION_SPAN = 60f;

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            boolean infiniteArrows = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack arrow = this.findArrows(player);

            int usedTicks = this.getMaxItemUseDuration(stack) - timeLeft;
            usedTicks = ForgeEventFactory.onArrowLoose(stack, world, (EntityPlayer) entityLiving, usedTicks, arrow != null || infiniteArrows);

            if (usedTicks < 0) {
                return;
            }

            if (arrow != null || infiniteArrows) {
                if (arrow == null) {
                    arrow = new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW);
                }

                float strength = getArrowVelocity(usedTicks);

                strength *= CorrosionHelper.getModifier(stack);

                if (strength >= 0.1F) {
                    if (!world.isRemote) {
                        for (int i = 0; i < 5; i++) {
                            ItemArrow itemArrow = (ItemArrow) arrow.getItem();
                            EntityArrow entityArrow = itemArrow.createArrow(world, arrow, player);
                            entityArrow.setAim(player, player.rotationPitch, player.rotationYaw + (ROTATION_SPAN/5)*(i-2), 0.0F, strength * 3.0F, 1.0F);

                            if (strength == 1.0F) {
                                entityArrow.setIsCritical(true);
                            }

                            int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                            if (j > 0) {
                                entityArrow.setDamage(entityArrow.getDamage() + (double) j * 0.5D + 0.5D);
                            }

                            int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                            if (k > 0) {
                                entityArrow.setKnockbackStrength(k);
                            }

                            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                                entityArrow.setFire(100);
                            }

                            stack.damageItem(1, player);

                            if (infiniteArrows) {
                                entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                            }

                            world.spawnEntityInWorld(entityArrow);
                        }
                    }

                    world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + strength * 0.5F);

                    if (!infiniteArrows) {
                        arrow.stackSize -=5;

                        if (arrow.stackSize == 0) {
                            player.inventory.deleteStack(arrow);
                        }
                    }

                    player.addStat(StatList.getObjectUseStats(this));
                }
            }
        }
    }
}

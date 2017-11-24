package thebetweenlands.common.item.herblore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.projectiles.EntityElixir;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

public class ItemElixir extends Item implements ITintedItem, ItemRegistry.IBlockStateItemModelDefinition {
    private final List<ElixirEffect> effects = new ArrayList<>();

    public ItemElixir() {
        this.effects.addAll(ElixirEffectRegistry.getEffects());

        this.setCreativeTab(BLCreativeTabs.HERBLORE);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);

    }

    private ElixirEffect getElixirByID(int id) {
        for(ElixirEffect effect : this.effects) {
            if(id == effect.getID()) return effect;
        }
        return null;
    }

    private ElixirEffect getElixirFromItem(ItemStack stack) {
        return this.getElixirByID(stack.getItemDamage() / 2);
    }

    @Override
    public int getColorMultiplier(ItemStack stack, int tintIndex) {
        if (tintIndex <= 0) {
            ElixirEffect effect = this.getElixirFromItem(stack);
            if (effect != null) {
                ElixirRecipe recipe = ElixirRecipes.getFromEffect(effect);
                if (recipe != null) {
                    return recipe.infusionFinishedColor;
                }
            }
        }
        return -1;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (ElixirEffect effect : this.effects) {
                items.add(new ItemStack(this, 1, effect.getID() * 2));
                items.add(new ItemStack(this, 1, effect.getID() * 2 + 1));
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        try {
            return "item.thebetweenlands." + this.getElixirFromItem(stack).getEffectName();
        } catch (Exception e) {
            return "item.thebetweenlands.unknown";
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TranslationHelper.translateToLocal(this.getElixirFromItem(stack).getEffectName());
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("throwing") && stack.getTagCompound().getBoolean("throwing")) {
            return EnumAction.BOW;
        }
        return EnumAction.DRINK;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("throwing") && stack.getTagCompound().getBoolean("throwing")) {
            if (!((EntityPlayer)entityLiving).capabilities.isCreativeMode) {
                stack.shrink(1);
                if(stack.isEmpty()) {
                    ((EntityPlayer) entityLiving).inventory.deleteStack(stack);
                }
            }
            world.playSound((EntityPlayer)entityLiving, entityLiving.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            if (!world.isRemote) {
                int useCount = this.getMaxItemUseDuration(stack) - timeLeft;
                EntityElixir elixir = new EntityElixir(world, entityLiving, stack);
                float strength = Math.min(0.2F + useCount / 20.0F, 1.0F);
                elixir.shoot(entityLiving, ((EntityPlayer)entityLiving).rotationPitch, ((EntityPlayer)entityLiving).rotationYaw, -20.0F, strength, 1.0F);
                world.spawnEntity(elixir);
            }
        }
    }

    /**
     * Creates an item stack with the specified effect, duration, strength and vial type.
     * Vial types: 0 = green, 1 = orange
     * @param effect
     * @param duration
     * @param strength
     * @param vialType
     * @return
     */
    public ItemStack getElixirItem(ElixirEffect effect, int duration, int strength, int vialType) {
        ItemStack elixirStack = new ItemStack(this, 1, effect.getID() * 2 + vialType);
        NBTTagCompound elixirData = new NBTTagCompound();
        elixirData.setInteger("duration", duration);
        elixirData.setInteger("strength", strength);
        if(elixirStack.getTagCompound() == null) elixirStack.setTagCompound(new NBTTagCompound());
        elixirStack.getTagCompound().setTag("elixirData", elixirData);
        return elixirStack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("throwing") && stack.getTagCompound().getBoolean("throwing")) {
            return 100000;
        }
        return 32;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setBoolean("throwing", playerIn.isSneaking());
        playerIn.setActiveHand(handIn);
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
            stack.shrink(1);
        }

        if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
        }

        if (!world.isRemote) {
            ElixirEffect effect = this.getElixirFromItem(stack);
            int duration = this.getElixirDuration(stack);
            int strength = this.getElixirStrength(stack);
            entityplayer.addPotionEffect(effect.createEffect(duration == -1 ? 1200 : duration, strength == -1 ? 0 : strength));
        }

        if (entityplayer != null) {
            entityplayer.addStat(StatList.getObjectUseStats(this));
        }

        //Add empty dentrothyst vial
        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
            if (stack.isEmpty()) {
                return ItemRegistry.DENTROTHYST_VIAL.createStack(stack.getItemDamage() % 2 == 0 ? 1 : 2);
            }
            if (entityplayer != null) {
                entityplayer.inventory.addItemStackToInventory(ItemRegistry.DENTROTHYST_VIAL.createStack(stack.getItemDamage() % 2 == 0 ? 1 : 2));
            }
        }

        return stack;
    }

    public void applyEffect(ItemStack stack, EntityLivingBase entity, double modifier) {
        ElixirEffect effect = this.getElixirFromItem(stack);
        int strength = this.getElixirStrength(stack);
        int duration = this.getElixirDuration(stack);
        entity.addPotionEffect(effect.createEffect(duration == -1 ? (int)(1200 * modifier) : (int)(duration * modifier), strength == -1 ? 0 : strength));
    }

    public int getElixirDuration(ItemStack stack) {
        if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("elixirData")) {
            NBTTagCompound elixirData = stack.getTagCompound().getCompoundTag("elixirData");
            return elixirData.getInteger("duration");
        }
        return -1;
    }

    public int getElixirStrength(ItemStack stack) {
        if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("elixirData")) {
            NBTTagCompound elixirData = stack.getTagCompound().getCompoundTag("elixirData");
            return elixirData.getInteger("strength");
        }
        return -1;
    }
	
	@Override
    public Map<Integer, String> getVariants() {
        Map<Integer, String> variants = new HashMap<>();
        for (ElixirEffect effect : this.effects) {
        	variants.put(effect.getID() * 2, "green");
        	variants.put(effect.getID() * 2 + 1, "orange");
        }
        return variants;
    }
}


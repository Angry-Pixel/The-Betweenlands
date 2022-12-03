package thebetweenlands.common.item.food;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.api.capability.IFallDamageReductionCapability;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.capability.IInfestationIgnoreCapability;
import thebetweenlands.common.capability.foodsickness.FoodSickness;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.EnumBLDrinkableBrew;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemDrinkableBrew extends ItemBLFood implements ItemRegistry.IMultipleItemModelDefinition {
	
    public ItemDrinkableBrew() {
    	super(3, 0.5F, false);//default for safety
        setMaxDamage(0);
        setHasSubtypes(true);
		setAlwaysEdible();
    }

	@Override
	public int getHealAmount(ItemStack stack) {
		return EnumBLDrinkableBrew.byMetadata(stack.getMetadata()).getHealAmount();
	}

	@Override
	public float getSaturationModifier(ItemStack stack) {
		return EnumBLDrinkableBrew.byMetadata(stack.getMetadata()).getSaturationModifier();
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return EnumItemMisc.WEEDWOOD_BOWL.create(1);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}

	@Override
	public boolean canGetSickOf(@Nullable EntityPlayer player, ItemStack stack) {
		return false;  // immune to food sickness mechanic atm :P
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
		super.onFoodEaten(stack, world, player);
		if (stack.getCount() != 0)
			player.inventory.addItemStackToInventory(getContainerItem(stack));
		if (EnumBLDrinkableBrew.byMetadata(stack.getMetadata()).hasBuff())
			applyBuffToPlayer(stack, world, player);
	}

	private void applyBuffToPlayer(ItemStack stack, World world, EntityPlayer player) {
		int meta = stack.getMetadata();
		int duration = EnumBLDrinkableBrew.byMetadata(meta).getBuffDuration();
		switch (meta) {
		case 0:
			break;
		case 1:
			//player.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, 1)); //test
			break;
		case 2:
			if (!world.isRemote) {
				IInfestationIgnoreCapability ignore = player.getCapability(CapabilityRegistry.CAPABILITY_INFESTATION_IGNORE, null);
				if (ignore != null)
					if (!ignore.isImmune())
						ignore.setImmune(Math.max(ignore.getRemainingImmunityTicks(), duration));
			}
			break;
		case 3:
			//decay reduction?
			player.addPotionEffect(ElixirEffectRegistry.EFFECT_RIPENING.createEffect(duration, 1));
			break;
		case 4:
			//gives lots of saturation
			break;
		case 5:
			//reduce fall damage? O.o
			if (!world.isRemote) {
				IFallDamageReductionCapability reduce = player.getCapability(CapabilityRegistry.CAPABILITY_FALL_DAMAGE_REDUCTION, null);
				if (reduce != null)
					if (!reduce.isActive())
						reduce.setActive(Math.max(reduce.getRemainingActiveTicks(), duration));
			}
			break;
		case 6:
			 // NV and Hunter's sense
			player.addPotionEffect(ElixirEffectRegistry.EFFECT_HUNTERSSENSE.createEffect(duration, 1));
			player.addPotionEffect(ElixirEffectRegistry.EFFECT_CATSEYES.createEffect(duration, 1));
			break;
		case 7:
			// water breathing?
			player.addPotionEffect(ElixirEffectRegistry.EFFECT_GILLSGROWTH.createEffect(duration, 1));
			break;
		case 8:
			// light footed across sludge and mud etc?
			player.addPotionEffect(ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.createEffect(duration, 1));
			break;
		case 9:
			 // jumping for 20 secs.
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, 1));
			break;
		case 10:
			// restores food sickness and something about worms?
			IFoodSicknessCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FOOD_SICKNESS, null);
			if (!world.isRemote && cap != null)
				if (FoodSickness.getSicknessForHatred(cap.getFoodHatred(this)) != FoodSickness.SICK)
					cap.increaseFoodHatred(this, 0, FoodSickness.SICK.maxHatred);
			break;
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			break;
		}
	}

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        int i = stack.getMetadata();
        return super.getTranslationKey() + "." + EnumBLDrinkableBrew.byMetadata(i).getTranslationKey();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab)) {
            Stream.of(EnumBLDrinkableBrew.values()).forEach(s -> list.add(new ItemStack(this, 1, s.getMetadata())));
        }
    }

    @Override
    public Map<Integer, ResourceLocation> getModels() {
        Map<Integer, ResourceLocation> models = new HashMap<>();
        for(EnumBLDrinkableBrew type : EnumBLDrinkableBrew.values()) {
            models.put(type.getMetadata(), new ResourceLocation(ModInfo.ID, type.getBrewName()));
        }
        return models;
    }
}

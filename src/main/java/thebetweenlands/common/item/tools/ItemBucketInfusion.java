package thebetweenlands.common.item.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.DiscoveryContainer;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemBucketInfusion extends Item implements ITintedItem, ItemRegistry.IMultipleItemModelDefinition {

	public ItemBucketInfusion() {
		this.setCreativeTab(BLCreativeTabs.GEARS);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		if (hasTag(stack)) {
			if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("infused") && stack.getTagCompound().hasKey("ingredients") && stack.getTagCompound().hasKey("infusionTime")) {
				int infusionTime = stack.getTagCompound().getInteger("infusionTime");
				list.add(I18n.format("tooltip.bl.infusion.time", StringUtils.ticksToElapsedTime(infusionTime), infusionTime));
				list.add(I18n.format("tooltip.bl.infusion.ingredients"));
				// The properties will be retrieved in the Alembic's TE logic
				NBTTagList nbtList = (NBTTagList) stack.getTagCompound().getTag("ingredients");
				Map<ItemStack, Integer> stackMap = new LinkedHashMap<ItemStack, Integer>();
				for (int i = 0; i < nbtList.tagCount(); i++) {
					ItemStack ingredient = new ItemStack(nbtList.getCompoundTagAt(i));
					boolean contained = false;
					for (Map.Entry<ItemStack, Integer> stackCount : stackMap.entrySet()) {
						if (ItemStack.areItemStacksEqual(stackCount.getKey(), ingredient)) {
							stackMap.put(stackCount.getKey(), stackCount.getValue() + 1);
							contained = true;
						}
					}
					if (!contained) {
						stackMap.put(ingredient, 1);
					}
				}
				for (Map.Entry<ItemStack, Integer> stackCount : stackMap.entrySet()) {
					ItemStack ingredient = stackCount.getKey();
					int count = stackCount.getValue();
					if (!ingredient.isEmpty()) {
						list.add((count > 1 ? (count + "x ") : "") + ingredient.getDisplayName());
						ItemAspectContainer container = ItemAspectContainer.fromItem(ingredient, AspectManager.get(TheBetweenlands.proxy.getClientWorld()));
						List<Aspect> ingredientAspects = container.getAspects(DiscoveryContainer.getMergedDiscoveryContainer(FMLClientHandler.instance().getClientPlayerEntity()));
						if (ingredientAspects.size() >= 1) {
							if (GuiScreen.isShiftKeyDown()) {
								for (Aspect aspect : ingredientAspects) {
									list.add("  - " + aspect.type.getName() + " (" + Aspect.ASPECT_AMOUNT_FORMAT.format(aspect.getDisplayAmount() * count) + ")");
								}
							}
						}
					}
				}
			} else {
				list.add(I18n.format("tooltip.bl.infusion.empty"));
			}
		}
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if(player.isSneaking()) {
			pos = pos.offset(side);
			world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
			for(int i = 0; i < 50; i++) {
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + world.rand.nextFloat(), pos.getY() + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat(), 0, 0, 0);
			}
			player.setHeldItem(hand, getEmptyBucket(player.getHeldItem(hand)));
			return EnumActionResult.SUCCESS;
		}
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		stack.setTagCompound(new NBTTagCompound());
	}

	private boolean hasTag(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
			return false;
		}
		return true;
	}

	public ElixirRecipe getInfusionElixirRecipe(ItemStack stack) {
		return ElixirRecipes.getFromAspects(this.getInfusingAspects(stack));
	}

	public List<IAspectType> getInfusingAspects(ItemStack stack) {
		List<IAspectType> infusingAspects = new ArrayList<IAspectType>();
		if (hasTag(stack)) {
			if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("infused") && stack.getTagCompound().hasKey("ingredients") && stack.getTagCompound().hasKey("infusionTime")) {
				NBTTagList nbtList = (NBTTagList) stack.getTagCompound().getTag("ingredients");
				for (int i = 0; i < nbtList.tagCount(); i++) {
					ItemStack ingredient = new ItemStack(nbtList.getCompoundTagAt(i));
					ItemAspectContainer container = ItemAspectContainer.fromItem(ingredient, AspectManager.get(TheBetweenlands.proxy.getClientWorld()));
					for (Aspect aspect : container.getAspects()) {
						infusingAspects.add(aspect.type);
					}
					//infusingAspects.addAll(AspectManager.get(TheBetweenlands.proxy.getClientWorld()).getDiscoveredAspectTypes(AspectManager.getAspectItem(ingredient), null));
				}
			}
		}
		return infusingAspects;
	}

	public int getInfusionTime(ItemStack stack) {
		if (hasTag(stack)) {
			if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("infused") && stack.getTagCompound().hasKey("ingredients") && stack.getTagCompound().hasKey("infusionTime")) {
				return stack.getTagCompound().getInteger("infusionTime");
			}
		}
		return 0;
	}

	@Override
	public int getColorMultiplier(ItemStack stack, int tintIndex) {
		if(tintIndex == 1) {
			ElixirRecipe recipe = this.getInfusionElixirRecipe(stack);
			int infusionTime = this.getInfusionTime(stack);
			//Infusion liquid
			if(recipe != null) {
				if(infusionTime > recipe.idealInfusionTime + recipe.infusionTimeVariation) {
					float[] failedColor = recipe.getRGBA(recipe.infusionFailedColor);
					return this.getColorFromRGBA(failedColor[0], failedColor[1], failedColor[2], failedColor[3]);
				} else if(infusionTime > recipe.idealInfusionTime - recipe.infusionTimeVariation
						&& infusionTime < recipe.idealInfusionTime + recipe.infusionTimeVariation) {
					float[] finishedColor = recipe.getRGBA(recipe.infusionFinishedColor);
					return this.getColorFromRGBA(finishedColor[0], finishedColor[1], finishedColor[2], finishedColor[3]);
				} else {
					float startR = 0.2F;
					float startG = 0.6F;
					float startB = 0.4F;
					float startA = 0.9F;
					float[] targetColor = recipe.getRGBA(recipe.infusionGradient);
					int targetTime = recipe.idealInfusionTime - recipe.infusionTimeVariation;
					float infusingPercentage = (float)infusionTime / (float)targetTime;
					float interpR = startR + (targetColor[0] - startR) * infusingPercentage;
					float interpG = startG + (targetColor[1] - startG) * infusingPercentage;
					float interpB = startB + (targetColor[2] - startB) * infusingPercentage;
					float interpA = startA + (targetColor[3] - startA) * infusingPercentage;
					return this.getColorFromRGBA(interpR, interpG, interpB, interpA);
				}
			} else {
				return this.getColorFromRGBA(0.8F, 0.0F, 0.8F, 1.0F);
			}
		}
		return 0xFFFFFFFF;
	}

	private int getColorFromRGBA(float r, float g, float b, float a) {
		return ((int) (a * 255.0F) << 24) | ((int) (r * 255.0F) << 16) | ((int) (g * 255.0F) << 8) | ((int) (b * 255.0F));
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		try {
			switch (stack.getItemDamage()) {
			case 0:
				return "item.thebetweenlands.bl_bucket_infusion.weedwood";
			case 1:
				return "item.thebetweenlands.bl_bucket_infusion.syrmorite";
			}
		} catch (Exception e) {
		}
		return "item.thebetweenlands.unknown";
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		Map<Integer, ResourceLocation> models = new HashMap<>();
		models.put(0, new ResourceLocation(getRegistryName().toString() + "_weedwood"));
		models.put(1, new ResourceLocation(getRegistryName().toString() + "_syrmorite"));
		return models;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		return getEmptyBucket(itemStack);
	}
	
	public static ItemStack getEmptyBucket(ItemStack stack) {
		return ItemRegistry.BL_BUCKET.getEmpty(new ItemStack(ItemRegistry.BL_BUCKET, 1, stack.getMetadata()));
	}
}

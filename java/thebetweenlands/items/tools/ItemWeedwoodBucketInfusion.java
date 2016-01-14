package thebetweenlands.items.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.herblore.aspects.Aspect;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.herblore.elixirs.ElixirRecipe;
import thebetweenlands.herblore.elixirs.ElixirRecipes;

public class ItemWeedwoodBucketInfusion extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon iconLiquid;

	public ItemWeedwoodBucketInfusion() {
		this.setMaxStackSize(1);
		this.setUnlocalizedName("thebetweenlands.weedwoodBucketInfusion");
		this.setTextureName("thebetweenlands:weedwoodBucket");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		if (hasTag(stack)) {
			if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("infused") && stack.stackTagCompound.hasKey("ingredients") && stack.stackTagCompound.hasKey("infusionTime")) {
				int infusionTime = stack.stackTagCompound.getInteger("infusionTime");
				String infusionTimeSeconds = BigDecimal.valueOf(infusionTime / 20.0F).setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();
				list.add(EnumChatFormatting.GREEN + "Infusion time: " + EnumChatFormatting.RESET + infusionTimeSeconds);
				list.add(EnumChatFormatting.GREEN + "Ingredients:");
				// The properties will be retrieved in the Alembic's TE logic
				NBTTagList nbtList = (NBTTagList)stack.stackTagCompound.getTag("ingredients");
				Map<ItemStack, Integer> stackMap = new LinkedHashMap<ItemStack, Integer>();
				for(int i = 0; i < nbtList.tagCount(); i++) {
					ItemStack ingredient = ItemStack.loadItemStackFromNBT(nbtList.getCompoundTagAt(i));
					boolean contained = false;
					for(Entry<ItemStack, Integer> stackCount : stackMap.entrySet()) {
						if(ItemStack.areItemStacksEqual(stackCount.getKey(), ingredient)) {
							stackMap.put(stackCount.getKey(), stackCount.getValue() + 1);
							contained = true;
						}
					}
					if(!contained) {
						stackMap.put(ingredient, 1);
					}
				}
				for(Entry<ItemStack, Integer> stackCount : stackMap.entrySet()) {
					ItemStack ingredient = stackCount.getKey();
					int count = stackCount.getValue();
					if(ingredient != null) {
						list.add((count > 1 ? (count + "x ") : "") + ingredient.getDisplayName());
						List<Aspect> ingredientAspects = AspectManager.get(TheBetweenlands.proxy.getClientWorld()).getAspects(ingredient, player);
						if(ingredientAspects.size() >= 1) {
							if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
								for(Aspect aspect : ingredientAspects) {
									list.add("  - " + aspect.aspect.getName() + " (" + aspect.amount * count + ")");
								}
							}
						}
					}
				}
			} else {
				list.add("This Infusion Contains Nothing");
			}
		}
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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		this.iconLiquid = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/infusionLiquid");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
		return pass == 1 ? this.iconLiquid : super.getIconFromDamageForRenderPass(damage, pass);
	}

	public ElixirRecipe getInfusionElixirRecipe(ItemStack stack) {
		return ElixirRecipes.getFromAspects(this.getInfusingAspects(stack));
	}

	public List<IAspectType> getInfusingAspects(ItemStack stack) {
		List<IAspectType> infusingAspects = new ArrayList<IAspectType>();
		if (hasTag(stack)) {
			if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("infused") && stack.stackTagCompound.hasKey("ingredients") && stack.stackTagCompound.hasKey("infusionTime")) {
				NBTTagList nbtList = (NBTTagList)stack.stackTagCompound.getTag("ingredients");
				Map<ItemStack, Integer> stackMap = new LinkedHashMap<ItemStack, Integer>();
				for(int i = 0; i < nbtList.tagCount(); i++) {
					ItemStack ingredient = ItemStack.loadItemStackFromNBT(nbtList.getCompoundTagAt(i));
					infusingAspects.addAll(AspectManager.get(TheBetweenlands.proxy.getClientWorld()).getAspectTypes(ingredient, null));
				}
			}
		}
		return infusingAspects;
	}

	public int getInfusionTime(ItemStack stack) {
		if (hasTag(stack)) {
			if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("infused") && stack.stackTagCompound.hasKey("ingredients") && stack.stackTagCompound.hasKey("infusionTime")) {
				int infusionTime = stack.stackTagCompound.getInteger("infusionTime");
				return infusionTime;
			}
		}
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if(pass == 1) {
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
		return ((int)(a * 255.0F) << 24) | ((int)(r * 255.0F) << 16) | ((int)(g * 255.0F) << 8) | ((int)(b * 255.0F));
	}
}

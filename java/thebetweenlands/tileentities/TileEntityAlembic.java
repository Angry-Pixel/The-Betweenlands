package thebetweenlands.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import thebetweenlands.herblore.Amounts;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.herblore.aspects.AspectRegistry.ItemEntry;
import thebetweenlands.herblore.aspects.IAspect;
import thebetweenlands.herblore.aspects.ItemAspect;
import thebetweenlands.herblore.elixirs.ElixirRecipe;
import thebetweenlands.herblore.elixirs.ElixirRecipes;
import thebetweenlands.herblore.elixirs.ElixirRegistry;
import thebetweenlands.herblore.elixirs.effects.ElixirEffect;
import thebetweenlands.items.BLItemRegistry;

public class TileEntityAlembic extends TileEntity {
	public static final int DISTILLING_TIME = 4800; //4 Minutes

	public static final float AMOUNT_PER_VIAL = Amounts.VIAL;

	private boolean running = false;
	private int progress = 0;
	private ItemStack infusionBucket = null;
	private float producedAmount = 0.0F;
	private float producableAmount = 0.0F;
	private int producableStrength;
	private int producableDuration;
	private ElixirEffect producableElixir = null;

	public void addInfusion(ItemStack bucket) {
		this.infusionBucket = bucket;
		this.loadFromInfusion();
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if(!this.worldObj.isRemote) {
			if(this.isFull() && !this.hasFinished()) {
				this.progress++;
				if(!this.running) {
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
				this.running = true;
				if(this.hasFinished()) {
					this.producedAmount = this.producableAmount;
				}
			} else {
				if(this.running) {
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
				this.running = false;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(this.infusionBucket != null) nbt.setTag("infusionBucket", this.infusionBucket.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("progress", this.progress);
		nbt.setFloat("producedAmount", this.producedAmount);
		nbt.setBoolean("running", this.running);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("infusionBucket")) this.infusionBucket = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("infusionBucket"));
		this.loadFromInfusion();
		this.progress = nbt.getInteger("progress");
		this.producedAmount = nbt.getFloat("producedAmount");
		this.running = nbt.getBoolean("running");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("running", this.running);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		this.running = packet.func_148857_g().getBoolean("running");
	}

	private void loadFromInfusion() {
		if(this.infusionBucket == null) return;
		int infusionTime = this.infusionBucket.stackTagCompound.getInteger("infusionTime");
		if(!this.infusionBucket.stackTagCompound.hasKey("ingredients")) {
			this.addInvalidInfusion();
			return;
		}
		NBTTagList nbtList = (NBTTagList)this.infusionBucket.stackTagCompound.getTag("ingredients");
		List<ItemStack> infusionIngredients = new ArrayList<ItemStack>();
		for(int i = 0; i < nbtList.tagCount(); i++) {
			infusionIngredients.add(ItemStack.loadItemStackFromNBT(nbtList.getCompoundTagAt(i)));
		}
		List<IAspect> infusionAspects = this.getInfusionAspects(infusionIngredients);
		ElixirRecipe recipe = ElixirRecipes.getFromAspects(infusionAspects);
		if(recipe == null || infusionTime < recipe.idealInfusionTime - recipe.infusionTimeVariation || infusionTime > recipe.idealInfusionTime + recipe.infusionTimeVariation) {
			this.addInvalidInfusion();
			return;
		}
		List<ItemAspect> infusionItemAspects = this.getInfusionItemAspects(infusionIngredients);
		float totalAmount = Amounts.VERY_LOW; //Base amount
		float strengthAmount = 0.0F;
		float durationAmount = 0.0F;
		for(ItemAspect a : infusionItemAspects) {
			totalAmount += a.amount;
			if(recipe.strengthAspect != null && a.aspect == recipe.strengthAspect) strengthAmount += a.amount;
			if(recipe.durationAspect != null && a.aspect == recipe.durationAspect) durationAmount += a.amount;
		}
		int recipeByariis = 0;
		for(IAspect a : recipe.aspects) {
			if(a == AspectRegistry.BYARIIS) {
				recipeByariis++;
			}
		}
		this.producableAmount = totalAmount;
		boolean isPositive = true;
		for(IAspect a : infusionAspects) {
			if(a == AspectRegistry.BYARIIS) {
				if(recipeByariis <= 0) {
					isPositive = !isPositive;
				} else {
					recipeByariis--;
				}
			}
		}
		this.producableElixir = isPositive ? recipe.positiveElixir : recipe.negativeElixir;
		float relStrengthAmount = strengthAmount / Amounts.MAX_ASPECT_AMOUNT;
		float relDurationAmount = durationAmount / Amounts.MAX_ASPECT_AMOUNT;
		this.producableStrength = MathHelper.floor_float(relStrengthAmount * 4.0F);
		this.producableDuration = recipe.baseDuration + MathHelper.floor_float(recipe.baseDuration * relDurationAmount * 2.0F);
	}

	private void addInvalidInfusion() {
		//Invalid recipe or infusion too short or too long
		this.producableElixir = ElixirRegistry.EFFECT_TEST;
		this.producableAmount = 0.2F;
		this.producableDuration = 0;
		this.producableStrength = 0;
	}

	private List<IAspect> getInfusionAspects(List<ItemStack> ingredients) {
		List<IAspect> infusingAspects = new ArrayList<IAspect>();
		for(ItemStack ingredient : ingredients) {
			infusingAspects.addAll(AspectRecipes.REGISTRY.getAspects(new ItemEntry(ingredient)));
		}
		return infusingAspects;
	}

	private List<ItemAspect> getInfusionItemAspects(List<ItemStack> ingredients) {
		List<ItemAspect> infusingItemAspects = new ArrayList<ItemAspect>();
		for(ItemStack ingredient : ingredients) {
			infusingItemAspects.addAll(AspectRecipes.REGISTRY.getItemAspects(new ItemEntry(ingredient)));
		}
		return infusingItemAspects;
	}

	public boolean isFull() {
		return this.infusionBucket != null;
	}

	public boolean hasFinished() {
		return this.progress >= DISTILLING_TIME;
	}

	public boolean hasElixir() {
		return this.producedAmount > 0.0F;
	}

	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Creates an item stack with the elixir in the alembic.
	 * Vial types: 0 = green, 1 = orange
	 * @param vialType
	 * @return
	 */
	public ItemStack getElixir(int vialType) {
		if(this.isFull() && this.hasFinished()) {
			ItemStack elixir = null;
			if(this.hasElixir()) {
				elixir = this.createElixir(this.producableElixir, this.producableStrength, this.producableDuration, vialType);
			}
			System.out.println(this.producedAmount + " " + AMOUNT_PER_VIAL);
			this.producedAmount -= AMOUNT_PER_VIAL;
			if(this.producedAmount <= 0.0F || !this.hasElixir()) {
				this.infusionBucket = null;
				this.producableAmount = 0.0F;
				this.producableDuration = 0;
				this.producableElixir = null;
				this.producableStrength = 0;
				this.producedAmount = 0.0F;
				this.progress = 0;
			}
			return elixir;
		}
		return null;
	}

	private ItemStack createElixir(ElixirEffect elixir, int strength, int duration, int vialType) {
		return BLItemRegistry.elixir.getElixirItem(elixir, duration, strength, vialType);
	}
}

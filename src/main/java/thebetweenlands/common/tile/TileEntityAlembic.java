package thebetweenlands.common.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.Amounts;
import thebetweenlands.common.herblore.aspect.Aspect;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.ItemAspectContainer;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntityAlembic extends TileEntity implements ITickable {
    public static final int DISTILLING_TIME = 4800; //4 Minutes

    public static final float AMOUNT_PER_VIAL = Amounts.VIAL;

    public static final float ISOLATION_LOSS_MULTIPLIER = 0.18F;

    private boolean running = false;
    private int progress = 0;
    private ItemStack infusionBucket = null;
    private float producedAmount = 0.0F;
    private float producableAmount = 0.0F;
    private int producableStrength;
    private int producableDuration;
    private ElixirEffect producableElixir = null;
    private List<Aspect> producableItemAspects = new ArrayList<Aspect>();
    private ElixirRecipe recipe = null;
    private int bucketInfusionTime;

    private boolean loadInfusionData = false;

    public void addInfusion(ItemStack bucket) {
        this.infusionBucket = bucket;
        this.loadFromInfusion();
        worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
    }


    @Override
    public void update() {
        if (this.loadInfusionData) {
            this.loadFromInfusion();
            this.loadInfusionData = false;
        }

        if (this.isFull() && !this.hasFinished()) {
            this.progress++;
            if (!this.worldObj.isRemote) {
                if (!this.running || this.progress % 20 == 0) {
                    worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
                }
                this.running = true;
                if (this.hasFinished()) {
                    this.producedAmount = this.producableAmount;
                }
            }
        } else {
            if (!this.worldObj.isRemote) {
                if (this.running) {
                    worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
                }
                this.running = false;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.infusionBucket != null)
            nbt.setTag("infusionBucket", this.infusionBucket.writeToNBT(new NBTTagCompound()));
        nbt.setInteger("progress", this.progress);
        nbt.setFloat("producedAmount", this.producedAmount);
        nbt.setBoolean("running", this.running);
        NBTTagList aspectList = new NBTTagList();
        for (Aspect aspect : this.producableItemAspects) {
            NBTTagCompound aspectCompound = new NBTTagCompound();
            aspect.writeToNBT(aspectCompound);
            aspectList.appendTag(aspectCompound);
        }
        nbt.setTag("producableItemAspects", aspectList);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("infusionBucket"))
            this.infusionBucket = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("infusionBucket"));
        this.loadInfusionData = true;
        this.progress = nbt.getInteger("progress");
        this.producedAmount = nbt.getFloat("producedAmount");
        this.running = nbt.getBoolean("running");
        if (nbt.hasKey("producableItemAspects")) {
            this.producableItemAspects.clear();
            NBTTagList aspectList = nbt.getTagList("producableItemAspects", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < aspectList.tagCount(); i++) {
                NBTTagCompound aspectCompound = aspectList.getCompoundTagAt(i);
                Aspect aspect = Aspect.readFromNBT(aspectCompound);
                this.producableItemAspects.add(aspect);
            }
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("running", this.running);
        NBTTagCompound itemStackCompound = new NBTTagCompound();
        if (this.infusionBucket != null) {
            this.infusionBucket.writeToNBT(itemStackCompound);
        }
        nbt.setTag("infusionBucket", itemStackCompound);
        nbt.setInteger("progress", this.progress);
        return new SPacketUpdateTileEntity(pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.running = packet.getNbtCompound().getBoolean("running");
        NBTTagCompound itemStackCompound = packet.getNbtCompound().getCompoundTag("infusionBucket");
        ItemStack oldStack = this.infusionBucket;
        if (itemStackCompound != null && itemStackCompound.getShort("id") != 0)
            this.infusionBucket = ItemStack.loadItemStackFromNBT(itemStackCompound);
        else
            this.infusionBucket = null;
        if (this.infusionBucket != null && !ItemStack.areItemStacksEqual(this.infusionBucket, oldStack)) {
            this.loadFromInfusion();
        }
        this.progress = packet.getNbtCompound().getInteger("progress");
    }

    public ElixirRecipe getElixirRecipe() {
        return this.recipe;
    }

    public int getInfusionTime() {
        return this.bucketInfusionTime;
    }

    public float getProgress() {
        return (float) this.progress / (float) DISTILLING_TIME;
    }

    private void loadFromInfusion() {
        this.recipe = null;
        if (this.infusionBucket == null) return;
        int infusionTime = this.infusionBucket.getTagCompound().getInteger("infusionTime");
        this.bucketInfusionTime = infusionTime;
        if (!this.infusionBucket.getTagCompound().hasKey("ingredients")) {
            this.addInvalidInfusion();
            return;
        }
        NBTTagList nbtList = (NBTTagList) this.infusionBucket.getTagCompound().getTag("ingredients");
        List<ItemStack> infusionIngredients = new ArrayList<ItemStack>();
        for (int i = 0; i < nbtList.tagCount(); i++) {
            infusionIngredients.add(ItemStack.loadItemStackFromNBT(nbtList.getCompoundTagAt(i)));
        }
        List<IAspectType> infusionAspects = this.getInfusionAspects(infusionIngredients);
        ElixirRecipe recipe = ElixirRecipes.getFromAspects(infusionAspects);
        this.recipe = recipe;
        if (recipe == null || infusionTime < recipe.idealInfusionTime - recipe.infusionTimeVariation || infusionTime > recipe.idealInfusionTime + recipe.infusionTimeVariation) {
            this.addInvalidInfusion();
            return;
        }
        List<Aspect> infusionItemAspects = this.getInfusionItemAspects(infusionIngredients);
        float totalAmount = Amounts.VERY_LOW; //Base amount
        float strengthAspectAmount = 0.0F;
        float durationAspectAmount = 0.0F;
        for (Aspect a : infusionItemAspects) {
            totalAmount += a.getDisplayAmount();
            if (recipe.strengthAspect != null && a.type == recipe.strengthAspect)
                strengthAspectAmount += a.getDisplayAmount();
            if (recipe.durationAspect != null && a.type == recipe.durationAspect)
                durationAspectAmount += a.getDisplayAmount();
        }
        int recipeByariis = 0;
        for (IAspectType a : recipe.aspects) {
            if (a == AspectRegistry.BYARIIS) {
                recipeByariis++;
            }
        }
        this.producableAmount = totalAmount;
        boolean isPositive = true;
        for (IAspectType a : infusionAspects) {
            if (a == AspectRegistry.BYARIIS) {
                if (recipeByariis <= 0) {
                    isPositive = !isPositive;
                } else {
                    recipeByariis--;
                }
            }
        }
        this.producableElixir = isPositive ? recipe.positiveElixir : recipe.negativeElixir;
        float relStrengthAmount = strengthAspectAmount / Amounts.MAX_ASPECT_AMOUNT;
        float relDurationAmount = durationAspectAmount / Amounts.MAX_ASPECT_AMOUNT;
        this.producableStrength = MathHelper.floor_float(relStrengthAmount * 4.0F);
        if (isPositive) {
            this.producableDuration = recipe.baseDuration + MathHelper.floor_float(recipe.durationModifier * relDurationAmount * 2.0F);
        } else {
            this.producableDuration = recipe.negativeBaseDuration + MathHelper.floor_float(recipe.negativeDurationModifier * relDurationAmount * 2.0F);
        }
    }

    private void addInvalidInfusion() {
        //Invalid recipe or infusion too short or too long
        this.producableElixir = null;
        this.producableAmount = 0;
        this.producableDuration = 0;
        this.producableStrength = 0;
        this.producableItemAspects.clear();
        if (this.infusionBucket != null && this.infusionBucket.getTagCompound() != null && this.infusionBucket.getTagCompound().hasKey("ingredients")) {
            NBTTagList nbtList = (NBTTagList) this.infusionBucket.getTagCompound().getTag("ingredients");
            List<ItemStack> infusionIngredients = new ArrayList<ItemStack>();
            for (int i = 0; i < nbtList.tagCount(); i++) {
                infusionIngredients.add(ItemStack.loadItemStackFromNBT(nbtList.getCompoundTagAt(i)));
            }
            List<Aspect> infusionAspects = this.getInfusionItemAspects(infusionIngredients);
            for (Aspect aspect : infusionAspects) {
                this.producableItemAspects.add(new Aspect(aspect.type, (int) (aspect.getDisplayAmount() * ISOLATION_LOSS_MULTIPLIER * 100)));
            }
        }
    }

    public List<IAspectType> getInfusionAspects(List<ItemStack> ingredients) {
        List<IAspectType> infusingAspects = new ArrayList<IAspectType>();
        for (ItemStack ingredient : ingredients) {
            infusingAspects.addAll(AspectManager.get(this.worldObj).getDiscoveredAspectTypes(AspectManager.getAspectItem(ingredient), null));
        }
        return infusingAspects;
    }

    private List<Aspect> getInfusionItemAspects(List<ItemStack> ingredients) {
        List<Aspect> infusingItemAspects = new ArrayList<Aspect>();
        for (ItemStack ingredient : ingredients) {
            infusingItemAspects.addAll(AspectManager.get(this.worldObj).getDiscoveredAspects(AspectManager.getAspectItem(ingredient), null));
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
     *
     * @param vialType
     * @return
     */
    public ItemStack getElixir(int vialType) {
        if (this.isFull() && this.hasFinished()) {
            if (this.producableElixir != null) {
                ItemStack elixir = null;
                if (this.hasElixir()) {
                    //TODO add elixir
                    //elixir = this.createElixir(this.producableElixir, this.producableStrength, this.producableDuration, vialType);
                }
                this.producedAmount -= AMOUNT_PER_VIAL;
                if (this.producedAmount <= 0.0F || !this.hasElixir()) {
                    this.reset();
                }
                return elixir;
            } else {
                ItemStack aspectVial = null;
                if (this.producableItemAspects.size() >= 1) {
                    Aspect aspect = this.producableItemAspects.get(0);
                    this.producableItemAspects.remove(0);
                    float totalAmount = aspect.getDisplayAmount() * 100;
                    Iterator<Aspect> itemAspectIT = this.producableItemAspects.iterator();
                    while (itemAspectIT.hasNext()) {
                        Aspect currentAspect = itemAspectIT.next();
                        if (currentAspect.type == aspect.type) {
                            totalAmount += currentAspect.getDisplayAmount() * 100;
                            itemAspectIT.remove();
                        }
                    }
                    aspectVial = new ItemStack(ItemRegistry.ASPECT_VIAL, 1, vialType);
                    ItemAspectContainer aspectContainer = ItemAspectContainer.fromItem(aspectVial);
                    aspectContainer.add(aspect.type, (int) totalAmount);
                }
                if (this.producableItemAspects.size() == 0) {
                    this.reset();
                }
                return aspectVial;
            }
        }
        return null;
    }

    public void reset() {
        this.producableItemAspects.clear();
        this.infusionBucket = null;
        this.producableAmount = 0.0F;
        this.producableDuration = 0;
        this.producableElixir = null;
        this.producableStrength = 0;
        this.producedAmount = 0.0F;
        this.progress = 0;
        worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
    }
    /*TODO add elixir
    private ItemStack createElixir(ElixirEffect elixir, int strength, int duration, int vialType) {
        return ItemRegistry.ELIXIR.getElixirItem(elixir, duration, strength, vialType);
    }*/
}

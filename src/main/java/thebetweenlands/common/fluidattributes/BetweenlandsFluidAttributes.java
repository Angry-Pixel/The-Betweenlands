package thebetweenlands.common.fluidattributes;

import java.util.function.BiFunction;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.common.registries.BlockColorRegistry;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsFluidAttributes extends FluidAttributes {
	
	public static final int BUCKET_VOLUME = 1000;

    /**
     * The light level emitted by this fluid.
     *
     * Default value is 0, as most fluids do not actively emit light.
     */

    /**
     * The rarity of the fluid.
     *
     * Used primarily in tool tips.
     */

    protected BetweenlandsFluidAttributes(Builder builder, Fluid fluid)
    {
		super(builder, fluid);
    }

    public ItemStack getBucket(FluidStack stack)
    {
        return new ItemStack(stack.getFluid().getBucket());
    }

    public BlockState getBlock(BlockAndTintGetter reader, BlockPos pos, FluidState state)
    {
        return state.createLegacyBlock();
    }

    public FluidState getStateForPlacement(BlockAndTintGetter reader, BlockPos pos, FluidStack state)
    {
        return state.getFluid().defaultFluidState();
    }

    /**
     * Determines if this fluid should vaporize in dimensions where water vaporizes when placed.
     * To preserve the intentions of vanilla, fluids that can turn lava into obsidian should vaporize.
     * This prevents players from making the nether safe with a single bucket.
     * Based on {@link BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     *
     * @param fluidStack The fluidStack is trying to be placed.
     * @return true if this fluid should vaporize in dimensions where water vaporizes when placed.
     */
    public boolean doesVaporize(BlockAndTintGetter reader, BlockPos pos, FluidStack fluidStack)
    {
        BlockState blockstate = getBlock(reader, pos, getStateForPlacement(reader, pos, fluidStack));
        if (blockstate == null)
            return false;
        return blockstate.getMaterial() == net.minecraft.world.level.material.Material.WATER;
    }

    /**
     * Called instead of placing the fluid block if {@link DimensionType#ultraWarm()} and {@link #doesVaporize(BlockAndTintGetter, BlockPos, FluidStack)} are true.
     * Override this to make your explosive liquid blow up instead of the default smoke, etc.
     * Based on {@link BucketItem#emptyContents(Player, Level, BlockPos, BlockHitResult)}
     *
     * @param player     Player who tried to place the fluid. May be null for blocks like dispensers.
     * @param worldIn    World to vaporize the fluid in.
     * @param pos        The position in the world the fluid block was going to be placed.
     * @param fluidStack The fluidStack that was going to be placed.
     */
    public void vaporize(@Nullable Player player, Level worldIn, BlockPos pos, FluidStack fluidStack)
    {
        worldIn.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.8F);

        for (int l = 0; l < 8; ++l)
        {
            worldIn.addAlwaysVisibleParticle(ParticleTypes.LARGE_SMOKE, (double) pos.getX() + Math.random(), (double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Returns the localized name of this fluid.
     */
    public Component getDisplayName(FluidStack stack)
    {
        return new TranslatableComponent(getTranslationKey());
    }

    /**
     * A FluidStack sensitive version of getTranslationKey
     */
    public String getTranslationKey(FluidStack stack)
    {
        return this.getTranslationKey();
    }

    /* Stack-based Accessors */
    public int getLuminosity(FluidStack stack){ return getLuminosity(); }
    public int getDensity(FluidStack stack){ return getDensity(); }
    public int getTemperature(FluidStack stack){ return getTemperature(); }
    public int getViscosity(FluidStack stack){ return getViscosity(); }
    public boolean isGaseous(FluidStack stack){ return isGaseous(); }
    public Rarity getRarity(FluidStack stack){ return getRarity(); }
    public int getColor(FluidStack stack){ return getColor(); }
    public ResourceLocation getStillTexture(FluidStack stack) { return getStillTexture(); }
    public ResourceLocation getFlowingTexture(FluidStack stack) { return getFlowingTexture(); }
    public SoundEvent getFillSound(FluidStack stack) { return getFillSound(); }
    public SoundEvent getEmptySound(FluidStack stack) { return getEmptySound(); }

    /* World-based Accessors */
    public int getLuminosity(BlockAndTintGetter world, BlockPos pos){ return getLuminosity(); }
    public int getDensity(BlockAndTintGetter world, BlockPos pos){ return getDensity(); }
    public int getTemperature(BlockAndTintGetter world, BlockPos pos){ return getTemperature(); }
    public int getViscosity(BlockAndTintGetter world, BlockPos pos){ return getViscosity(); }
    public boolean isGaseous(BlockAndTintGetter world, BlockPos pos){ return isGaseous(); }
    public Rarity getRarity(BlockAndTintGetter world, BlockPos pos){ return getRarity(); }
    public int getColor(BlockAndTintGetter world, BlockPos pos){ return getColor(); }
    public ResourceLocation getStillTexture(BlockAndTintGetter world, BlockPos pos) { return getStillTexture(); }
    public ResourceLocation getFlowingTexture(BlockAndTintGetter world, BlockPos pos) { return getFlowingTexture(); }
    public SoundEvent getFillSound(BlockAndTintGetter world, BlockPos pos) { return getFillSound(); }
    public SoundEvent getEmptySound(BlockAndTintGetter world, BlockPos pos) { return getEmptySound(); }

    public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        return new BetweenlandsBuilder(stillTexture, flowingTexture, BetweenlandsFluidAttributes::new);
    }

    public static class BetweenlandsBuilder extends Builder
    {
        private BiFunction<Builder,Fluid,FluidAttributes> factory;

        public BetweenlandsBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture, BiFunction<Builder,Fluid,FluidAttributes> factory) {
        	super(stillTexture, flowingTexture, factory);
            this.factory = factory;
        }

        public FluidAttributes build(Fluid fluid)
        {
            return factory.apply(this, fluid);
        }
    }

    public static class SwampWater extends FluidAttributes
    {
    	protected SwampWater(Builder builder, Fluid fluid)
        {
            super(builder, fluid);
        }

        public int getColor(BlockAndTintGetter world, BlockPos pos)
        {
        	return BlockColorRegistry.SWAMP_WATER.getColor(BlockRegistry.SWAMP_WATER_BLOCK.get().defaultBlockState(), world, pos, 0);
        }
        
        public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
            return new BetweenlandsBuilder(stillTexture, flowingTexture, SwampWater::new);
        }
    }
}

package thebetweenlands.common.block.container;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.FilteredSiltGlassJarBlockEntity;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.List;
import java.util.Optional;

public class FilteredSiltGlassJarBlock extends BaseEntityBlock {

	public static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);

	public FilteredSiltGlassJarBlock(Properties properties) {
		super(properties);
	}


	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, hitResult.getDirection());

		if (fluidHandler.isPresent() && FluidUtil.getFluidHandler(stack).isPresent()) {
			if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection())) {
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (!stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).isEmpty()) {
			FluidStack fluid = stack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY).copy();
			tooltip.add(Component.translatable("block.thebetweenlands.filtered_silt_glass_jar.fluid", fluid.getHoverName()).withStyle(ChatFormatting.GREEN));
			tooltip.add(Component.translatable("block.thebetweenlands.filtered_silt_glass_jar.amount", fluid.getAmount()).withStyle(ChatFormatting.BLUE));
		} else {
			tooltip.add(Component.translatable("block.thebetweenlands.filtered_silt_glass_jar.empty").withStyle(ChatFormatting.RED));
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		if (level.getBlockEntity(pos) instanceof FilteredSiltGlassJarBlockEntity jar && !jar.tank.getFluid().isEmpty()) {
			ItemStack stack = new ItemStack(this);
			stack.applyComponents(jar.collectComponents());
			return stack;
		}
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FilteredSiltGlassJarBlockEntity(pos, state);
	}
}

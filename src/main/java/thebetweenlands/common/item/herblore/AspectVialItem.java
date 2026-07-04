package thebetweenlands.common.item.herblore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.block.container.AspectVialBlock;
import thebetweenlands.common.block.entity.AspectVialBlockEntity;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.herblore.aspect.IAspectVial;
import thebetweenlands.common.registries.AspectTypeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.List;

public class AspectVialItem extends BlockItem implements IAspectVial {
	private final IAspectVial.VialType type;
	public AspectVialItem(Block block, Properties properties, IAspectVial.VialType type) {
		super(block, properties);
		this.type = type;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.level.LevelReader level, BlockPos pos, Player player) {
        return level.getBlockState(pos).getBlock() instanceof AspectVialBlock;
    }

	@Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos toPlacePos = context.getClickedPos().above();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

		if (!stack.has(DataComponentRegistry.ASPECT_CONTENTS)) return InteractionResult.FAIL;

		AspectContents contents = stack.get(DataComponentRegistry.ASPECT_CONTENTS);
		if (!contents.aspect().isPresent()) return InteractionResult.FAIL;


        if (level.getBlockState(context.getClickedPos()).getBlock() instanceof AspectVialBlock) {
			return InteractionResult.PASS;
		} else if(player.isCrouching() && context.getClickedFace() == Direction.UP) {
			InteractionResult wasPlaced = super.useOn(context);
			if ((wasPlaced.equals(InteractionResult.SUCCESS) || wasPlaced.equals(InteractionResult.CONSUME)) && !level.isClientSide) {
				AspectVialBlockEntity tile = (AspectVialBlockEntity) level.getBlockEntity(toPlacePos);
				if(tile != null)
					tile.setAspect(new Aspect(contents.aspect().get(), contents.amount()));
				return InteractionResult.SUCCESS;
		    }
        }
        return InteractionResult.FAIL;
    }

	@Override
	public Component getName(ItemStack stack) {
		if (stack.has(DataComponentRegistry.ASPECT_CONTENTS)) {
			AspectContents contents = stack.get(DataComponentRegistry.ASPECT_CONTENTS);
			if (contents.aspect().isPresent()) {
				return Component.translatable("item.thebetweenlands.aspect_vial.aspect", AspectType.getAspectName(contents.aspect().get()), Aspect.ASPECT_AMOUNT_FORMAT.format(contents.amount() / 1000.0D));
			}
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (stack.has(DataComponentRegistry.ASPECT_CONTENTS) && stack.get(DataComponentRegistry.ASPECT_CONTENTS).aspect().isPresent()) {
			if (stack.get(DataComponentRegistry.ASPECT_CONTENTS).aspect().get().is(AspectTypeRegistry.BYARIIS)) {
				tooltip.add(Component.translatable("item.thebetweenlands.aspect_vial.byariis"));
			} else if (stack.get(DataComponentRegistry.ASPECT_CONTENTS).aspect().get().is(AspectTypeRegistry.FREIWYNN)) {
				tooltip.add(Component.translatable("item.thebetweenlands.aspect_vial.freiwynn"));
			}
		}
	}

	@Override
	public VialType type() {
		return type;
	}
}

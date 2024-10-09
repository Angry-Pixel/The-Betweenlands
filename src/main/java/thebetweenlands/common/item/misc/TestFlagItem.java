package thebetweenlands.common.item.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.boss.Barrishee;
import thebetweenlands.common.entity.monster.MummyArm;
import thebetweenlands.common.entity.monster.Stalker;

import java.util.List;

public class TestFlagItem extends Item {
	public TestFlagItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
		return itemStack;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		float h = ((System.currentTimeMillis() & 0x3FF) % (float) 0x3FF) / (float) 0x3FF;
		int rgb = Mth.hsvToRgb(h, 1, 1);
		tooltip.add(Component.literal("You are valid!").withStyle(TheBetweenlands.HERBLORE_FONT.withColor(rgb).withItalic(true)));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		MummyArm arm = new MummyArm(context.getLevel(), context.getPlayer());
		arm.moveTo(context.getClickLocation());
		context.getLevel().addFreshEntity(arm);
		return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (entity instanceof Barrishee barrishee) {
			barrishee.setIsScreaming(!barrishee.isScreaming());
			barrishee.setIsScreamingBeam(!barrishee.isScreamingBeam());
			barrishee.setScreamTimer(0);
			return InteractionResult.sidedSuccess(player.level().isClientSide());
		}
		return super.interactLivingEntity(stack, player, entity, hand);
	}
}

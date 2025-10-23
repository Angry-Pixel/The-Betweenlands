package thebetweenlands.common.item.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DungeonDoorRunesBlockEntity;
import thebetweenlands.common.block.entity.ItemCageBlockEntity;
import thebetweenlands.common.component.entity.BlessingData;
import thebetweenlands.common.entity.SwordEnergy;
import thebetweenlands.common.entity.boss.Barrishee;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.MobEffectRegistry;

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
		if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DungeonDoorRunesBlockEntity runes) {
			runes.is_in_dungeon = true;
		} else {
			int offset = 4;
			SwordEnergy energy = new SwordEnergy(EntityRegistry.SWORD_ENERGY.get(), context.getLevel());

			energy.setPos(context.getClickedPos().above(offset - 1).getCenter());

			context.getLevel().addFreshEntity(energy);

			ItemCageBlockEntity.setBlockWithType(context.getLevel(), context.getClickedPos().offset(-3, offset, -3), BlockRegistry.ITEM_CAGE.get().defaultBlockState(), 0);
			ItemCageBlockEntity.setBlockWithType(context.getLevel(), context.getClickedPos().offset(3, offset, -3), BlockRegistry.ITEM_CAGE.get().defaultBlockState(), 1);
			ItemCageBlockEntity.setBlockWithType(context.getLevel(), context.getClickedPos().offset(3, offset, 3), BlockRegistry.ITEM_CAGE.get().defaultBlockState(), 2);
			ItemCageBlockEntity.setBlockWithType(context.getLevel(), context.getClickedPos().offset(-3, offset, 3), BlockRegistry.ITEM_CAGE.get().defaultBlockState(), 3);
		}

		return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		if (!level.isClientSide() && player.getData(AttachmentRegistry.BLESSING).isBlessed()) {
			player.setData(AttachmentRegistry.BLESSING, BlessingData.noBlessing());
			player.displayClientMessage(Component.literal("Fuck you (unblesses you)"), true);
			player.removeEffect(MobEffectRegistry.BLESSED);
		}
		return super.use(level, player, usedHand);
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

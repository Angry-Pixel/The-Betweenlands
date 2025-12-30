package thebetweenlands.common.item.misc;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thebetweenlands.common.block.misc.GlowingGoopBlock;
import thebetweenlands.common.component.item.UpgradeDamage;
import thebetweenlands.common.entity.projectile.GlowingGoop;
import thebetweenlands.common.item.armor.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

public class GlowingGoopItem extends BlockItem {

	public GlowingGoopItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player entity, InteractionHand hand) {
		if (!canUse(entity.getItemInHand(hand)))
			return InteractionResultHolder.fail(entity.getItemInHand(hand));
		entity.startUsingItem(hand);
		return InteractionResultHolder.sidedSuccess(entity.getItemInHand(hand), level.isClientSide());
	}

	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
		if (entity instanceof Player player) {
			Vec3 forward = player.getLookAngle();
			float yaw = player.getYRot();
			float pitch = player.getXRot() - 90;
			float f = Mth.cos(-yaw * 0.017453292F - Mth.PI);
			float f1 = Mth.sin(-yaw * 0.017453292F - Mth.PI);
			float f2 = -Mth.cos(-pitch * 0.017453292F);
			float f3 = Mth.sin(-pitch * 0.017453292F);
			Vec3 up = new Vec3(f1 * f2, f3, f * f2);
			Vec3 right = forward.cross(up).normalize();
			Vec3 source = player.position().add(0, player.getEyeHeight() - 0.2F, 0).add(forward.scale(0.4F)).add(right.scale(0.3F));
			level.addParticle(ParticleTypes.ITEM_SLIME, source.x + level.getRandom().nextFloat() * 0.5F - 0.25F, source.y + level.getRandom().nextFloat() * 0.5F - 0.25F, source.z + level.getRandom().nextFloat() * 0.5F - 0.25F, 0, 0, 0);
		}

		super.onUseTick(level, entity, stack, remainingUseDuration);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
		if (!canUse(stack))
			return;

		if (!level.isClientSide() && entity instanceof Player player) {
			int useTime = this.getUseDuration(stack, player) - timeCharged;

			if (useTime > 20) {
				GlowingGoop goop = new GlowingGoop(level, player);
				goop.shootFromRotation(player, player.getXRot(), player.getYRot(), -10, 1.2F, 3.5F);
				level.addFreshEntity(goop);
				level.playSound(null, player.blockPosition(), SoundEvents.SLIME_JUMP, SoundSource.NEUTRAL, 1F, 1F);
				if (!player.isCreative())
					stack.consume(1, player);
				System.out.println("hello?");
			}
		}
	}

	@Override
	protected boolean canPlace(BlockPlaceContext context, BlockState state) {
		if (!canUse(context.getItemInHand()))
			return false;

		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Block block = this.getBlock();
		state = block.getStateForPlacement(context);

		if (!Block.canSupportCenter(level, pos.relative(state.getValue(GlowingGoopBlock.FACING).getOpposite()), state.getValue(GlowingGoopBlock.FACING)) && !level.setBlock(pos, state, 11))
			return false;

		if (!level.isClientSide()) {
			block.setPlacedBy(level, pos, state, context.getPlayer(), context.getItemInHand());
			level.playSound(null, pos, SoundEvents.SLIME_JUMP, SoundSource.NEUTRAL, 1F, 1F);
			if (context.getPlayer() instanceof ServerPlayer player)
				CriteriaTriggers.PLACED_BLOCK.trigger(player, pos, context.getItemInHand());
		}
		return true;
	}

	private boolean canUse(ItemStack stack) {
		UpgradeDamage damage = stack.getOrDefault(DataComponentRegistry.UPGRADE_DAMAGE, UpgradeDamage.EMPTY);
		return damage.damage() == 0;
	}
}
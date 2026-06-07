package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.item.BigSwingAnimation;
import thebetweenlands.common.entity.MistBridgeEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class MistStaffItem extends Item implements BigSwingAnimation {

	public MistStaffItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockPos pos = player.blockPosition().below().offset(1, 0, 1);
		BlockState blockStart = level.getBlockState(pos);

		if (player.getCooldowns().isOnCooldown(this))
			return InteractionResultHolder.pass(stack);

		if (isMistifiableBlock(level, player, pos, blockStart)) {
			if (!level.isClientSide()) {

				stack.hurtAndBreak(2, player, EquipmentSlot.MAINHAND);
				
				float direction = Mth.DEG_TO_RAD * player.getYRot();
				Vec3 diag = new Vec3(Mth.sin((float) (direction + Mth.HALF_PI)), 0, Mth.cos(direction + Mth.HALF_PI)).normalize();
				List<BlockPos> spawnedPos = new ArrayList<BlockPos>();
				List<BlockPos> convertPos = new ArrayList<BlockPos>();
				List<Integer> blockDistance = new ArrayList<Integer>();
				
				for (int distance = -1; distance <= 16; distance++) {
					for (int distance2 = -distance; distance2 <= distance; distance2++) {
						for (int yo = 0; yo <= 1; yo++) {
							int originX = Mth.floor(pos.getX() - 0.5D - Mth.sin(direction) * distance - diag.x * distance2 * 0.25D);
							int originY = pos.getY();
							int originZ = Mth.floor(pos.getZ() - 0.5D + Mth.cos(direction) * distance + diag.z * distance2 * 0.25D);
							BlockPos origin = new BlockPos(originX, originY, originZ);

							if (spawnedPos.contains(origin))
								continue;

							spawnedPos.add(origin);

							BlockState block = level.getBlockState(new BlockPos(originX, originY, originZ));

							if (isMistifiableBlock(level, player, origin, block)) {
								convertPos.add(origin);
								blockDistance.add(distance);
								break;
							}
						}
					}
				}
				
				spawnEntity(level, pos, blockDistance, convertPos);
				level.playSound(null, pos, SoundRegistry.MIST_STAFF_CAST.get(), SoundSource.BLOCKS, 1F, 1.0F);
				player.getCooldowns().addCooldown(this, 200);
			}
			
			player.swing(hand);
		} else {
			return InteractionResultHolder.pass(stack);
		}
		
		return InteractionResultHolder.success(stack);
	}

	@SuppressWarnings("deprecation")
	private boolean isMistifiableBlock (Level level, Player player, BlockPos pos, BlockState state) {
		return (state.isSolid() || state.is(BlockTags.REPLACEABLE)) && !state.hasBlockEntity() && state.getDestroyProgress(player, level, pos) > 0.01 && !(state.is(BlockRegistry.MIST_BRIDGE.get())) && !(state.is(BlockRegistry.SHADOW_WALKER.get()));
	}

	private void spawnEntity(Level level, BlockPos pos, List<Integer> blockDistance, List<BlockPos> convertPos) {
		if (!level.isClientSide()) {// && level.getDifficulty() != EnumDifficulty.PEACEFUL) {
			MistBridgeEntity mist_bridge = new MistBridgeEntity(EntityRegistry.MIST_BRIDGE.get(), level);
			if (mist_bridge != null) {
				mist_bridge.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				mist_bridge.setBlockList(blockDistance, convertPos, true);
				mist_bridge.setStartExtention(true);
				level.addFreshEntity(mist_bridge);
			}
		}
	}
/*
	@Override
	public boolean shouldUseBigSwingAnimation(ItemStack stack) {
		return true;
	}
*/
	@Override
	public float getSwingSpeedMultiplier(LivingEntity entity, ItemStack stack) {
		return 0.35f;
	}

}

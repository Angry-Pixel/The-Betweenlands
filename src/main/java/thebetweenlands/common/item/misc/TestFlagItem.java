package thebetweenlands.common.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.DungeonDoorRunesBlockEntity;
import thebetweenlands.common.block.structure.DecayPitGroundChainBlock;
import thebetweenlands.common.component.entity.BlessingData;
import thebetweenlands.common.entity.DecayPitTarget;
import thebetweenlands.common.entity.GreeblingCorpse;
import thebetweenlands.common.entity.MovingWall;
import thebetweenlands.common.entity.boss.Barrishee;
import thebetweenlands.common.entity.creature.Greebling;
import thebetweenlands.common.registries.*;

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
		} else if (context.getLevel().getBlockState(context.getClickedPos()).is(BlockRegistry.COMPACTED_MUD)) {
			context.getLevel().setBlockAndUpdate(context.getClickedPos(), BlockRegistry.DECAY_PIT_CONTROL.get().defaultBlockState());
			context.getLevel().setBlockAndUpdate(context.getClickedPos().above(15), BlockRegistry.DECAY_PIT_HANGING_CHAIN.get().defaultBlockState());
			for (Direction dir : Direction.Plane.HORIZONTAL) {
				context.getLevel().setBlockAndUpdate(context.getClickedPos().above(10).relative(dir, 12), BlockRegistry.DECAY_PIT_GROUND_CHAIN.get().defaultBlockState().setValue(DecayPitGroundChainBlock.FACING, dir.getOpposite()));
			}

			DecayPitTarget target = new DecayPitTarget(EntityRegistry.DECAY_PIT_TARGET.get(), context.getLevel());
			target.setPos(Vec3.atCenterOf(context.getClickedPos().above(8)));
			context.getLevel().addFreshEntity(target);
		} else if (context.getLevel().getBlockState(context.getClickedPos()).is(BlockRegistry.DECAYED_MUD_TILES) && !context.getLevel().isClientSide()) {
			this.createWallHall(context.getLevel(), context.getClickedPos(), 20);
		} else {
//			int offset = 4;
//			SwordEnergy energy = new SwordEnergy(EntityRegistry.SWORD_ENERGY.get(), context.getLevel());
//
//			energy.setPos(context.getClickedPos().above(offset - 1).getCenter());
//
//			context.getLevel().addFreshEntity(energy);
//
//			ItemCageBlockEntity.setBlockWithType(context.getLevel(), context.getClickedPos().offset(-3, offset, -3), BlockRegistry.ITEM_CAGE.get().defaultBlockState(), 0);
//			ItemCageBlockEntity.setBlockWithType(context.getLevel(), context.getClickedPos().offset(3, offset, -3), BlockRegistry.ITEM_CAGE.get().defaultBlockState(), 1);
//			ItemCageBlockEntity.setBlockWithType(context.getLevel(), context.getClickedPos().offset(3, offset, 3), BlockRegistry.ITEM_CAGE.get().defaultBlockState(), 2);
//			ItemCageBlockEntity.setBlockWithType(context.getLevel(), context.getClickedPos().offset(-3, offset, 3), BlockRegistry.ITEM_CAGE.get().defaultBlockState(), 3);
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
		} else if (entity instanceof Greebling) {
			GreeblingCorpse corpse = new GreeblingCorpse(EntityRegistry.GREEBLING_CORPSE.get(), entity.level());
			corpse.moveTo(entity.position());
			entity.level().addFreshEntity(corpse);
			entity.playSound(SoundRegistry.GREEBLING_FALL.get());
			entity.discard();
		}
		return super.interactLivingEntity(stack, player, entity, hand);
	}

	private void createWallHall(Level level, BlockPos startPos, int hallLength) {
		Direction hallDir = Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom());
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		for (int i = -1; i <= hallLength; i++) {
			for (int x = -2; x <= 2; x++) {
				for (int z = -2; z <= 2; z++) {
					for (int y = 0; y < 5; y++) {
						if (y == 0) {
							mutable.set(startPos.offset(x, y, z).relative(hallDir, i));
							level.setBlock(mutable, BlockRegistry.DECAYED_MUD_TILES.get().defaultBlockState(), 2);
						} else {
							mutable.set(startPos.offset(x, y, z).relative(hallDir, i));
							level.setBlock(mutable, BlockRegistry.SLUDGY_CARVED_MUD_BRICKS_4.get().defaultBlockState(), 2);
						}
					}
				}
			}
		}
		for (int i = -1; i <= hallLength; i++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int y = 1; y < 4; y++) {
						mutable.set(startPos.offset(x, y, z).relative(hallDir, i));
						level.setBlockAndUpdate(mutable, Blocks.AIR.defaultBlockState());
					}
				}
			}
		}
		MovingWall wall = new MovingWall(level, false);
		wall.setPos(startPos.getX() + 0.5F, startPos.getY() + 1, startPos.getZ() + 0.5F);
		level.addFreshEntity(wall);
	}
}

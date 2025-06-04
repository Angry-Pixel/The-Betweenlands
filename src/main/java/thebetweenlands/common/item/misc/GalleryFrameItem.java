package thebetweenlands.common.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import thebetweenlands.common.entity.GalleryFrame;

public class GalleryFrameItem extends Item {

	private final GalleryFrame.Type type;

	public GalleryFrameItem(GalleryFrame.Type type, Properties properties) {
		super(properties);
		this.type = type;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockPos blockpos = context.getClickedPos();
		Direction direction = context.getClickedFace();
		BlockPos blockpos1 = blockpos.relative(direction);
		Player player = context.getPlayer();
		ItemStack itemstack = context.getItemInHand();
		if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
			return InteractionResult.FAIL;
		} else {
			Level level = context.getLevel();
			GalleryFrame frame = new GalleryFrame(level, blockpos1, direction, this.type);

			CustomData customdata = itemstack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
			if (!customdata.isEmpty()) {
				EntityType.updateCustomEntityTag(level, player, frame, customdata);
			}

			if (frame.survives()) {
				if (!level.isClientSide) {
					frame.playPlacementSound();
					level.gameEvent(player, GameEvent.ENTITY_PLACE, frame.position());
					level.addFreshEntity(frame);
				}

				itemstack.shrink(1);
				return InteractionResult.sidedSuccess(level.isClientSide);
			} else {
				return InteractionResult.CONSUME;
			}
		}
	}

	protected boolean mayPlace(Player player, Direction direction, ItemStack hangingEntityStack, BlockPos pos) {
		return !direction.getAxis().isVertical() && player.mayUseItemAt(pos, direction, hangingEntityStack);
	}
}

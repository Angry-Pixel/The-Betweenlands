package thebetweenlands.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import thebetweenlands.common.entities.BLItemFrame;
import thebetweenlands.util.BLDyeColor;

public class BLItemFrameItem extends Item {

	private final int color;

	public BLItemFrameItem(BLDyeColor color, Properties properties) {
		this(color.getColorValue(), properties);
	}

	public BLItemFrameItem(int color, Properties properties) {
		super(properties);
		this.color = color;
	}

	public int getColor() {
		return this.color | 0xFF000000;
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
			HangingEntity hangingentity = new BLItemFrame(level, blockpos1, direction, this.getColor());

			CustomData customdata = itemstack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
			if (!customdata.isEmpty()) {
				EntityType.updateCustomEntityTag(level, player, hangingentity, customdata);
			}

			if (hangingentity.survives()) {
				if (!level.isClientSide()) {
					hangingentity.playPlacementSound();
					level.gameEvent(player, GameEvent.ENTITY_PLACE, hangingentity.position());
					level.addFreshEntity(hangingentity);
				}

				itemstack.shrink(1);
				return InteractionResult.sidedSuccess(level.isClientSide());
			} else {
				return InteractionResult.CONSUME;
			}
		}
	}

	protected boolean mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos pos) {
		return !player.level().isOutsideBuildHeight(pos) && player.mayUseItemAt(pos, direction, itemStack);
	}
}

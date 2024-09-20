package thebetweenlands.common.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

public class MireSnailEggItem extends Item {
	public MireSnailEggItem(Properties properties) {
		super(properties);
	}

//	public static ItemStack fromEgg(MireSnailEgg egg) {
//		ItemStack stack = new ItemStack(ItemRegistry.MIRE_SNAIL_EGG);
//		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(Util.make(new CompoundTag(), tag -> {
//			CompoundTag tag1 = new CompoundTag();
//			egg.addAdditionalSaveData(tag1);
//			tag.put("egg",tag1);
//		})));
//		return stack;
//	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		Vec3 vec = context.getClickLocation();
		Direction facing = context.getClickedFace();
		if (context.getLevel().isClientSide()) return InteractionResult.FAIL;
//		MireSnailEgg entity = new MireSnailEgg(context.getLevel());
//		CustomData nbt = stack.get(DataComponents.CUSTOM_DATA);
//		if(nbt != null) {
//			entity.readAdditionalSaveData(nbt.copyTag());
//		}
//		entity.moveTo(pos.getX() + vec.x() + facing.getStepY() * entity.getBbWidth(), pos.getY() + vec.y() + (facing.getStepY() < 0 ? -entity.getBbHeight() - 0.005F : 0.0F), pos.getZ() + vec.z() + facing.getStepZ() * entity.getBbWidth(), 0.0F, 0.0F);
//		if(context.getLevel().noCollision(entity)) {
//			context.getLevel().addFreshEntity(entity);
//			entity.playAmbientSound();
//			stack.shrink(1);
//		}
		return InteractionResult.SUCCESS;
	}
}

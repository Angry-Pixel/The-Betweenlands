package thebetweenlands.common.item.food;

import java.util.UUID;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityMireSnailEgg;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemMireSnailEgg extends ItemBLFood {
	public ItemMireSnailEgg() {
		super(2, 0.2f, false);
		this.maxStackSize = 1;
	}

	public static ItemStack fromEgg(EntityMireSnailEgg egg) {
		ItemStack stack = new ItemStack(ItemRegistry.MIRE_SNAIL_EGG);
		stack.setTagInfo("egg", egg.writeToNBT(new NBTTagCompound()));
		return stack;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) return EnumActionResult.FAIL;
		EntityLiving entity = new EntityMireSnailEgg(world);
		NBTTagCompound nbt = stack.getSubCompound("egg");
		if(nbt != null) {
			entity.readFromNBT(nbt);
		}
		entity.setUniqueId(UUID.randomUUID());
		entity.setLocationAndAngles(pos.getX() + hitX + facing.getFrontOffsetX() * entity.width, pos.getY() + hitY + (facing.getFrontOffsetY() < 0 ? -entity.height - 0.005F : 0.0F), pos.getZ() + hitZ + facing.getFrontOffsetZ() * entity.width, 0.0F, 0.0F);
		if(entity.isNotColliding()) {
			world.spawnEntity(entity);
			entity.playLivingSound();
			stack.shrink(1);
		}
		return EnumActionResult.SUCCESS;
	}
}

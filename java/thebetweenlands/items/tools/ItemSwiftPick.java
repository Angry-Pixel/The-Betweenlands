package thebetweenlands.items.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class ItemSwiftPick extends ItemPickaxeBL {
	public ItemSwiftPick(ToolMaterial material) {
		super(material);
	}

	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta) {
		if (ForgeHooks.isToolEffective(stack, block, meta)) {
            return 100.0F;
        }
		return 1.0F;
	}
}

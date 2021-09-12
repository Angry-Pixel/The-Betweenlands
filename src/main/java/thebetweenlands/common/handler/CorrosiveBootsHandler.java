package thebetweenlands.common.handler;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thebetweenlands.common.entity.EntityTriggeredFallingBlock;

public class CorrosiveBootsHandler {
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent e) {
		if (e.side.isServer() && !(e.player instanceof FakePlayer)) {
			if (e.phase != Phase.START)
				return;
			World world = e.player.getEntityWorld();
			if (world != null) {
				if (world.getDifficulty() != EnumDifficulty.PEACEFUL) {
					if (world.rand.nextInt(20) == 0) {
						ItemStack boots = e.player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
						if (!boots.isEmpty()) {
							if (boots.hasTagCompound() && boots.getTagCompound().getBoolean("corrosive")) {
								if(e.player.isInWater() || e.player.isWet())
									boots.getTagCompound().setBoolean("corrosive", false);
								else if (!world.isAirBlock(e.player.getPosition().down()) && !world.getBlockState(e.player.getPosition().down()).getBlock().hasTileEntity() && world.isAirBlock(e.player.getPosition().down(2))) {
									EntityTriggeredFallingBlock falling_block = new EntityTriggeredFallingBlock(world);
									falling_block.setPosition(e.player.getPosition().getX() + 0.5D, e.player.getPosition().getY() - 1D, e.player.getPosition().getZ() + 0.5D);
									falling_block.setWalkway(true);
									world.spawnEntity(falling_block);
								}
							}
						}
					}
				}
			}
		}
	}
}

package thebetweenlands.items;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import thebetweenlands.entities.EntityBLItemFrame;

/**
 * Created by Bart on 27-6-2015.
 */
public class ItemBLHangingEntity extends Item {
    public final Class hangingEntityClass;

    public ItemBLHangingEntity(Class entityClass) {
        hangingEntityClass = entityClass;
    }

    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (side == 0) {
            return false;
        } else if (side == 1) {
            return false;
        } else {
            int i1 = Direction.facingToDirection[side];
            EntityHanging entityhanging = this.createHangingEntity(world, x, y, z, i1);

            if (!entityPlayer.canPlayerEdit(x, y, z, side, itemStack)) {
                return false;
            } else {
                if (entityhanging != null && entityhanging.onValidSurface()) {
                    if (!world.isRemote) {
                        world.spawnEntityInWorld(entityhanging);
                    }
                    --itemStack.stackSize;
                }
                return true;
            }
        }
    }

    public EntityHanging createHangingEntity(World world, int x, int y, int z, int direction) {
        return (EntityHanging) (this.hangingEntityClass == EntityBLItemFrame.class ? new EntityBLItemFrame(world, x, y, z, direction) : null);
    }
}

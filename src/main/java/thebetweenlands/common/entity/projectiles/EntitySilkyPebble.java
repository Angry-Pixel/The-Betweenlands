package thebetweenlands.common.entity.projectiles;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySilkyPebble extends EntityAngryPebble {
	public EntitySilkyPebble(World world) {
		super(world);
	}

	public EntitySilkyPebble(World world, EntityLivingBase entity) {
		super(world, entity);
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void boomEvent(ExplosionEvent.Detonate event) {
		if (event.getWorld().isRemote)
			return;

		World world = event.getWorld();
		Explosion explosion = event.getExplosion();
		EntityLivingBase placedBy = explosion.getExplosivePlacedBy();
		
		if (placedBy instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) placedBy;
			
			List<Entity> entityList = event.getAffectedEntities();
			List<BlockPos> blockPosList = event.getAffectedBlocks();
			
			if(entityList.stream().anyMatch(entity -> entity instanceof EntitySilkyPebble)) {
				Iterator<BlockPos> it = blockPosList.iterator();
				
				while(it.hasNext()) {
					BlockPos pos = it.next();
				
					IBlockState state = world.getBlockState(pos);
					Block block = state.getBlock();
					TileEntity te = world.getTileEntity(pos);
					
					if (block.canSilkHarvest(event.getWorld(), pos, state, player)) {
						// Remove from affected blocks and
						// destroy block
						it.remove();
						block.onBlockExploded(event.getWorld(), pos, explosion);
						
						// Create virtual silk touch pebble
						ItemStack pebble = new ItemStack(ItemRegistry.SILKY_PEBBLE, 1);
						pebble.addEnchantment(Enchantments.SILK_TOUCH, 1);
						
						// Harvest block silk touch drops
						block.harvestBlock(event.getWorld(), player, pos, state, te, pebble);
					}
				}
			}
		}
	}
	
	@Override
	protected void explode() {
		boolean blockDamage = this.world.getGameRules().getBoolean("mobGriefing");
		this.world.createExplosion(this.thrower != null ? this.thrower : this, this.posX, this.posY, this.posZ, 3.0F, blockDamage);
	}
}
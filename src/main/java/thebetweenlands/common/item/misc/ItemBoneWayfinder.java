package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.api.item.IRenamableItem;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.structure.BlockWaystone;
import thebetweenlands.common.handler.PlayerRespawnHandler;
import thebetweenlands.common.item.equipment.ItemRing;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.PlayerUtil;

public class ItemBoneWayfinder extends Item implements IRenamableItem, IAnimatorRepairable {
	public ItemBoneWayfinder() {
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.setMaxDamage(10);
		this.setMaxStackSize(1);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return this.getBoundWaystone(stack) != null;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if(this.getBoundWaystone(stack) == null && stack.getItemDamage() < stack.getMaxDamage()) {
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() == BlockRegistry.WAYSTONE && this.activateWaystone(world, pos, state, stack)) {
				if(!world.isRemote) {
					this.setBoundWaystone(stack, pos);
				}
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(player.isSneaking()) {
			if (!world.isRemote) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_ITEM_RENAMING, world, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
			}
		} else {
			if(stack.getItemDamage() < stack.getMaxDamage() && this.getBoundWaystone(stack) != null) {
				player.setActiveHand(hand);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}

		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entity) {
		if(!worldIn.isRemote && stack.getItemDamage() < stack.getMaxDamage()) {
			BlockPos waystone = this.getBoundWaystone(stack);
			if(waystone != null) {
				BlockPos spawnPoint = PlayerRespawnHandler.getSpawnPointNearPos(worldIn, waystone, 8, false, 4, 0);

				if(spawnPoint != null) {
					if(entity.getDistanceSq(spawnPoint) > 24) {
						this.playThunderSounds(worldIn, entity.posX, entity.posY, entity.posZ);
					}

					PlayerUtil.teleport(entity, spawnPoint.getX() + 0.5D, spawnPoint.getY(), spawnPoint.getZ() + 0.5D);

					this.playThunderSounds(worldIn, entity.posX, entity.posY, entity.posZ);

					entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 1));

					stack.damageItem(1, entity);
				} else if(entity instanceof EntityPlayerMP) {
					((EntityPlayerMP) entity).sendStatusMessage(new TextComponentTranslation("chat.waystone.obstructed"), true);
				}
			}
		}
		return stack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 100;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage) {
			//Don't let the wayfinder break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}

	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("link", Constants.NBT.TAG_LONG)) {
			BlockPos waystone = BlockPos.fromLong(stack.getTagCompound().getLong("link"));
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.bone_wayfinder_linked", waystone.getX(), waystone.getY(), waystone.getZ()), 0));
		} else {
			tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.translateToLocalFormatted("tooltip.bone_wayfinder"), 0));
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
		if(!entity.world.isRemote) {
			if(entity.hurtTime > 0) {
				entity.stopActiveHand();
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
			}

			if(entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative() && count < 60 && entity.ticksExisted % 3 == 0) {
				int removed = ItemRing.removeXp((EntityPlayer) entity, 1);
				if(removed == 0) {
					entity.stopActiveHand();
					entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1, 1);
				}
			}

			if(count < 90 && count % 20 == 0) {
				entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundRegistry.PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.05F + 0.4F * (float)MathHelper.clamp(80 - count, 1, 80) / 80.0F, 0.9F + entity.world.rand.nextFloat() * 0.2F);
			}
		} else {
			Random rand = entity.world.rand;
			for(int i = 0; i < MathHelper.clamp(60 - count, 1, 60); i++) {
				entity.world.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, entity.posX + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 6, entity.posY + rand.nextFloat() * 4 - 2, entity.posZ + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 6, 0, 0.2D, 0);
			}
		}
	}

	protected void playThunderSounds(World world, double x, double y, double z) {
		world.playSound(null, x, y, z, SoundRegistry.RIFT_CREAK, SoundCategory.PLAYERS, 2, 1);
		world.playSound(null, x, y, z, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.75F, 0.75F);
	}

	protected boolean activateWaystone(World world, BlockPos pos, IBlockState state, ItemStack stack) {
		BlockWaystone block = (BlockWaystone) state.getBlock();
		if(block.isValidWaystone(world, pos, state)) {
			BlockWaystone.Part part = state.getValue(BlockWaystone.PART);

			if(!world.isRemote) {
				int startY = part == BlockWaystone.Part.BOTTOM ? 0 : (part == BlockWaystone.Part.MIDDLE ? -1 : -2);
				for(int yo = startY; yo < startY + 3; yo++) {
					IBlockState newState = world.getBlockState(pos.up(yo)).withProperty(BlockWaystone.ACTIVE, true);
					world.setBlockState(pos.up(yo), newState);
					world.notifyBlockUpdate(pos.up(yo), newState, newState, 2); //why tf is this necessary
				}

				this.playThunderSounds(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				List<LocationStorage> waystoneLocations = BetweenlandsWorldStorage.forWorld(world).getLocalStorageHandler()
						.getLocalStorages(LocationStorage.class, new AxisAlignedBB(pos.getX(), pos.getY() + startY, pos.getZ(), pos.getX() + 1, pos.getY() + startY + 3, pos.getZ() + 1), storage -> storage.getType() == EnumLocationType.WAYSTONE);
				if(!waystoneLocations.isEmpty()) {
					LocationStorage location = waystoneLocations.get(0);

					if(stack.hasDisplayName()) {
						location.setName(stack.getDisplayName());
						location.setVisible(true);
						location.markDirty();
					} else {
						location.setName("waystone");
						location.setVisible(false);
						location.markDirty();
					}
				}
			} else {
				this.spawnWaystoneParticles(world, pos, part);
			}

			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	protected void spawnWaystoneParticles(World world, BlockPos pos, BlockWaystone.Part part) {
		int startY = part == BlockWaystone.Part.BOTTOM ? 0 : (part == BlockWaystone.Part.MIDDLE ? -1 : -2);
		for(int yo = startY; yo < startY + 3; yo++) {
			for(int i = 0; i < 4; i++) {
				Vec3d dir = new Vec3d(world.rand.nextFloat() - 0.5F, world.rand.nextFloat() - 0.5F + 0.25F, world.rand.nextFloat() - 0.5F);
				dir = dir.normalize().scale(2);
				BLParticles.CORRUPTED.spawn(world, pos.getX() + 0.5D + world.rand.nextFloat() / 2.0F - 0.25F, pos.getY() + yo + 0.5D + world.rand.nextFloat() / 2.0F - 0.25F, pos.getZ() + 0.5D + world.rand.nextFloat() / 2.0F - 0.25F, ParticleArgs.get().withMotion(dir.x, dir.y, dir.z));
			}
		}
	}

	@Nullable
	public BlockPos getBoundWaystone(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("link", Constants.NBT.TAG_LONG)) {
				return BlockPos.fromLong(nbt.getLong("link"));
			}
		}
		return null;
	}

	public void setBoundWaystone(ItemStack stack, @Nullable BlockPos pos) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(pos == null) {
			if(nbt != null) {
				nbt.removeTag("link");
				stack.setTagCompound(nbt);
			}
		} else {
			if(nbt == null) {
				nbt = new NBTTagCompound();
			}
			nbt.setLong("link", pos.toLong());
			stack.setTagCompound(nbt);
		}
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 32;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 16;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 38;
	}
}

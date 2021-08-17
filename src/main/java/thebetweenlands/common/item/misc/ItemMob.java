package thebetweenlands.common.item.misc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.util.NBTHelper;

public class ItemMob extends Item {
	private final Class<? extends Entity> defaultMob;
	private final Consumer<Entity> defaultMobSetter;

	/**
	 * @param maxStackSize Max stack size of the item. If this is > 1 then only the entity's ID and no additional NBT is stored.
	 * @param defaultMob Default mob type of this item
	 * @param defaultMobSetter Sets the properties of the default mob
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> ItemMob(int maxStackSize, @Nullable Class<T> defaultMob, @Nullable Consumer<T> defaultMobSetter) {
		this.maxStackSize = maxStackSize;
		this.defaultMob = defaultMob;
		this.defaultMobSetter = (Consumer<Entity>) defaultMobSetter;
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	public ItemStack capture(Class<? extends Entity> cls) {
		return this.capture(cls, null);
	}

	public ItemStack capture(Class<? extends Entity> cls, @Nullable NBTTagCompound nbt) {
		ResourceLocation id = EntityList.getKey(cls);
		if(id != null) {
			if(nbt == null) {
				nbt = new NBTTagCompound();
			}
			nbt.setString("id", id.toString());

			ItemStack stack = new ItemStack(this);

			stack.setTagInfo("Entity", nbt);

			return stack;
		}

		return ItemStack.EMPTY;
	}

	public ItemStack capture(Entity entity) {
		NBTTagCompound nbt = new NBTTagCompound();

		if(this.maxStackSize == 1) {
			entity.writeToNBTOptional(nbt);
		} else {
			NBTTagCompound entityNbt = new NBTTagCompound();
			if(entity.writeToNBTOptional(entityNbt) && entityNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				nbt.setString("id", entityNbt.getString("id"));
			}
		}

		if(!nbt.isEmpty()) {
			ItemStack stack = new ItemStack(this);

			stack.setTagInfo("Entity", nbt);

			if(entity.hasCustomName()) {
				stack.setStackDisplayName(entity.getCustomNameTag());
			}

			return stack;
		}

		return ItemStack.EMPTY;
	}

	public boolean isCapturedEntity(ItemStack stack, Class<? extends Entity> cls) {
		if(stack.getItem() != this) {
			return false;
		}

		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null && nbt.hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound entityNbt = nbt.getCompoundTag("Entity");

			if(entityNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				Class<? extends Entity> entityCls = EntityList.getClass(new ResourceLocation(entityNbt.getString("id")));
				return entityCls != null && cls.isAssignableFrom(entityCls);
			}
		}

		return this.defaultMob != null && cls.isAssignableFrom(this.defaultMob);
	}

	public boolean hasEntityData(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt != null && nbt.hasKey("Entity", Constants.NBT.TAG_COMPOUND);
	}

	@Nullable
	public NBTTagCompound getEntityData(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null) {
			return nbt.getCompoundTag("Entity");
		}
		return null;
	}

	public void setEntityData(ItemStack stack, @Nullable NBTTagCompound entityData) {
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		if(entityData == null) {
			nbt.removeTag("Entity");
		} else {
			nbt.setTag("Entity", entityData);
		}
	}

	@Nullable
	public ResourceLocation getCapturedEntityId(ItemStack stack) {
		if(stack.getItem() != this) {
			return null;
		}

		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound entityNbt = stack.getTagCompound().getCompoundTag("Entity");

			if(entityNbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				return new ResourceLocation(entityNbt.getString("id"));
			}
		}

		if(this.defaultMob != null) {
			return EntityList.getKey(this.defaultMob);
		}

		return null;
	}

	protected static void handleOnInitialSpawn(Entity entity) {
		if(!entity.world.isRemote && entity instanceof EntityLiving) {
			((EntityLiving) entity).onInitialSpawn(entity.world.getDifficultyForLocation(new BlockPos(entity)), null);
		}
	}

	@Nullable
	public Entity createCapturedEntity(World world, double x, double y, double z, ItemStack stack, boolean allowOnInitialSpawn) {
		return this.createCapturedEntity(world, x, y, z, stack, entity -> {
			if(allowOnInitialSpawn) {
				handleOnInitialSpawn(entity);
			}
		}); 
	}

	@Nullable
	public Entity createCapturedEntity(World world, double x, double y, double z, ItemStack stack, @Nullable Consumer<Entity> onNewEntityCreated) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
			return this.createCapturedEntityFromNBT(world, x, y, z, stack.getTagCompound().getCompoundTag("Entity"));
		}

		if(this.defaultMob != null) {
			ResourceLocation id = EntityList.getKey(this.defaultMob);
			if(id != null) {
				Entity entity = EntityList.createEntityByIDFromName(id, world);
				if(entity != null) {
					entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0f, 0);
					if(this.defaultMobSetter != null) {
						this.defaultMobSetter.accept(entity);
					}
					if(onNewEntityCreated != null) {
						onNewEntityCreated.accept(entity);
					}
					return entity;
				}
			}
		}

		return null;
	}

	@Nullable
	protected Entity createCapturedEntityFromNBT(World world, double x, double y, double z, NBTTagCompound nbt) {
		Entity entity = EntityList.createEntityFromNBT(nbt, world);

		if(entity != null) {
			entity.setUniqueId(UUID.randomUUID());
			entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0f, 0);
			entity.motionX = entity.motionY = entity.motionZ = 0;
			return entity;
		}

		return null;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.defaultMob != null && this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);

		final AtomicBoolean isNewEntity = new AtomicBoolean();

		Entity entity = this.createCapturedEntity(world, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack, e -> isNewEntity.set(true));
		if(entity != null) {
			if(facing.getXOffset() != 0) {
				entity.setPosition(entity.posX + facing.getXOffset() * entity.width * 0.5f, entity.posY, entity.posZ);
			}
			if(facing.getYOffset() < 0) {
				entity.setPosition(entity.posX, entity.posY - entity.height, entity.posZ);
			}
			if(facing.getZOffset() != 0) {
				entity.setPosition(entity.posX, entity.posY, entity.posZ + facing.getZOffset() * entity.width * 0.5f);
			}

			if(world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
				EnumActionResult result = this.spawnCapturedEntity(player, world, pos, hand, facing, hitX, hitY, hitZ, entity, isNewEntity.get());

				if(result == EnumActionResult.SUCCESS) {
					if(!world.isRemote) {
						stack.shrink(1);
					}
				}

				return result;
			}

			return EnumActionResult.FAIL;
		}

		return EnumActionResult.PASS;
	}

	protected EnumActionResult spawnCapturedEntity(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, Entity entity, boolean isNewEntity) {
		if(!world.isRemote) {
			if(isNewEntity) {
				handleOnInitialSpawn(entity);
			}

			world.spawnEntity(entity);

			if(entity instanceof EntityLiving) {
				((EntityLiving) entity).playLivingSound();
			}
		}

		return EnumActionResult.SUCCESS;
	}

	public void onCapturedByPlayer(EntityPlayer player, EnumHand hand, ItemStack captured, EntityLivingBase entity) {

	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		ResourceLocation id = this.getCapturedEntityId(stack);
		if(id != null) {
			return "entity." + id.getNamespace() + "." + id.getPath();
		}
		return super.getTranslationKey(stack);
	}

	@Override
	public boolean hasCustomProperties() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if(worldIn != null) {
			Entity entity = this.createCapturedEntity(worldIn, 0, 0, 0, stack, false);
			if(entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				tooltip.add(I18n.format("tooltip.bl.item_mob.health", MathHelper.ceil(living.getHealth() / 2), MathHelper.ceil(living.getMaxHealth() / 2)));
			}
		}
	}
}

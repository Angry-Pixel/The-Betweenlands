package net.minecraft.core.dispenser;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;

public interface DispenseItemBehavior {
   Logger LOGGER = LogUtils.getLogger();
   DispenseItemBehavior NOOP = (p_123400_, p_123401_) -> {
      return p_123401_;
   };

   ItemStack dispense(BlockSource p_123403_, ItemStack p_123404_);

   static void bootStrap() {
      DispenserBlock.registerBehavior(Items.ARROW, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level p_123407_, Position p_123408_, ItemStack p_123409_) {
            Arrow arrow = new Arrow(p_123407_, p_123408_.x(), p_123408_.y(), p_123408_.z());
            arrow.pickup = AbstractArrow.Pickup.ALLOWED;
            return arrow;
         }
      });
      DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level p_123420_, Position p_123421_, ItemStack p_123422_) {
            Arrow arrow = new Arrow(p_123420_, p_123421_.x(), p_123421_.y(), p_123421_.z());
            arrow.setEffectsFromItem(p_123422_);
            arrow.pickup = AbstractArrow.Pickup.ALLOWED;
            return arrow;
         }
      });
      DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level p_123456_, Position p_123457_, ItemStack p_123458_) {
            AbstractArrow abstractarrow = new SpectralArrow(p_123456_, p_123457_.x(), p_123457_.y(), p_123457_.z());
            abstractarrow.pickup = AbstractArrow.Pickup.ALLOWED;
            return abstractarrow;
         }
      });
      DispenserBlock.registerBehavior(Items.EGG, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level p_123468_, Position p_123469_, ItemStack p_123470_) {
            return Util.make(new ThrownEgg(p_123468_, p_123469_.x(), p_123469_.y(), p_123469_.z()), (p_123466_) -> {
               p_123466_.setItem(p_123470_);
            });
         }
      });
      DispenserBlock.registerBehavior(Items.SNOWBALL, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level p_123476_, Position p_123477_, ItemStack p_123478_) {
            return Util.make(new Snowball(p_123476_, p_123477_.x(), p_123477_.y(), p_123477_.z()), (p_123474_) -> {
               p_123474_.setItem(p_123478_);
            });
         }
      });
      DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level p_123485_, Position p_123486_, ItemStack p_123487_) {
            return Util.make(new ThrownExperienceBottle(p_123485_, p_123486_.x(), p_123486_.y(), p_123486_.z()), (p_123483_) -> {
               p_123483_.setItem(p_123487_);
            });
         }

         protected float getUncertainty() {
            return super.getUncertainty() * 0.5F;
         }

         protected float getPower() {
            return super.getPower() * 1.25F;
         }
      });
      DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenseItemBehavior() {
         public ItemStack dispense(BlockSource p_123491_, ItemStack p_123492_) {
            return (new AbstractProjectileDispenseBehavior() {
               protected Projectile getProjectile(Level p_123501_, Position p_123502_, ItemStack p_123503_) {
                  return Util.make(new ThrownPotion(p_123501_, p_123502_.x(), p_123502_.y(), p_123502_.z()), (p_123499_) -> {
                     p_123499_.setItem(p_123503_);
                  });
               }

               protected float getUncertainty() {
                  return super.getUncertainty() * 0.5F;
               }

               protected float getPower() {
                  return super.getPower() * 1.25F;
               }
            }).dispense(p_123491_, p_123492_);
         }
      });
      DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenseItemBehavior() {
         public ItemStack dispense(BlockSource p_123507_, ItemStack p_123508_) {
            return (new AbstractProjectileDispenseBehavior() {
               protected Projectile getProjectile(Level p_123517_, Position p_123518_, ItemStack p_123519_) {
                  return Util.make(new ThrownPotion(p_123517_, p_123518_.x(), p_123518_.y(), p_123518_.z()), (p_123515_) -> {
                     p_123515_.setItem(p_123519_);
                  });
               }

               protected float getUncertainty() {
                  return super.getUncertainty() * 0.5F;
               }

               protected float getPower() {
                  return super.getPower() * 1.25F;
               }
            }).dispense(p_123507_, p_123508_);
         }
      });
      DefaultDispenseItemBehavior defaultdispenseitembehavior = new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource p_123523_, ItemStack p_123524_) {
            Direction direction = p_123523_.getBlockState().getValue(DispenserBlock.FACING);
            EntityType<?> entitytype = ((SpawnEggItem)p_123524_.getItem()).getType(p_123524_.getTag());

            try {
               entitytype.spawn(p_123523_.getLevel(), p_123524_, (Player)null, p_123523_.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
            } catch (Exception exception) {
               LOGGER.error("Error while dispensing spawn egg from dispenser at {}", p_123523_.getPos(), exception);
               return ItemStack.EMPTY;
            }

            p_123524_.shrink(1);
            p_123523_.getLevel().gameEvent(GameEvent.ENTITY_PLACE, p_123523_.getPos());
            return p_123524_;
         }
      };

      for(SpawnEggItem spawneggitem : SpawnEggItem.eggs()) {
         DispenserBlock.registerBehavior(spawneggitem, defaultdispenseitembehavior);
      }

      DispenserBlock.registerBehavior(Items.ARMOR_STAND, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource p_123461_, ItemStack p_123462_) {
            Direction direction = p_123461_.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = p_123461_.getPos().relative(direction);
            Level level = p_123461_.getLevel();
            ArmorStand armorstand = new ArmorStand(level, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
            EntityType.updateCustomEntityTag(level, (Player)null, armorstand, p_123462_.getTag());
            armorstand.setYRot(direction.toYRot());
            level.addFreshEntity(armorstand);
            p_123462_.shrink(1);
            return p_123462_;
         }
      });
      DispenserBlock.registerBehavior(Items.SADDLE, new OptionalDispenseItemBehavior() {
         public ItemStack execute(BlockSource p_123529_, ItemStack p_123530_) {
            BlockPos blockpos = p_123529_.getPos().relative(p_123529_.getBlockState().getValue(DispenserBlock.FACING));
            List<LivingEntity> list = p_123529_.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), (p_123527_) -> {
               if (!(p_123527_ instanceof Saddleable)) {
                  return false;
               } else {
                  Saddleable saddleable = (Saddleable)p_123527_;
                  return !saddleable.isSaddled() && saddleable.isSaddleable();
               }
            });
            if (!list.isEmpty()) {
               ((Saddleable)list.get(0)).equipSaddle(SoundSource.BLOCKS);
               p_123530_.shrink(1);
               this.setSuccess(true);
               return p_123530_;
            } else {
               return super.execute(p_123529_, p_123530_);
            }
         }
      });
      DefaultDispenseItemBehavior defaultdispenseitembehavior1 = new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource p_123535_, ItemStack p_123536_) {
            BlockPos blockpos = p_123535_.getPos().relative(p_123535_.getBlockState().getValue(DispenserBlock.FACING));

            for(AbstractHorse abstracthorse : p_123535_.getLevel().getEntitiesOfClass(AbstractHorse.class, new AABB(blockpos), (p_123533_) -> {
               return p_123533_.isAlive() && p_123533_.canWearArmor();
            })) {
               if (abstracthorse.isArmor(p_123536_) && !abstracthorse.isWearingArmor() && abstracthorse.isTamed()) {
                  abstracthorse.getSlot(401).set(p_123536_.split(1));
                  this.setSuccess(true);
                  return p_123536_;
               }
            }

            return super.execute(p_123535_, p_123536_);
         }
      };
      DispenserBlock.registerBehavior(Items.LEATHER_HORSE_ARMOR, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.IRON_HORSE_ARMOR, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.GOLDEN_HORSE_ARMOR, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.DIAMOND_HORSE_ARMOR, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.WHITE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.ORANGE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.CYAN_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.BLUE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.BROWN_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.BLACK_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.GRAY_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.GREEN_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.LIGHT_BLUE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.LIGHT_GRAY_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.LIME_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.MAGENTA_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.PINK_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.PURPLE_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.RED_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.YELLOW_CARPET, defaultdispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.CHEST, new OptionalDispenseItemBehavior() {
         public ItemStack execute(BlockSource p_123541_, ItemStack p_123542_) {
            BlockPos blockpos = p_123541_.getPos().relative(p_123541_.getBlockState().getValue(DispenserBlock.FACING));

            for(AbstractChestedHorse abstractchestedhorse : p_123541_.getLevel().getEntitiesOfClass(AbstractChestedHorse.class, new AABB(blockpos), (p_123539_) -> {
               return p_123539_.isAlive() && !p_123539_.hasChest();
            })) {
               if (abstractchestedhorse.isTamed() && abstractchestedhorse.getSlot(499).set(p_123542_)) {
                  p_123542_.shrink(1);
                  this.setSuccess(true);
                  return p_123542_;
               }
            }

            return super.execute(p_123541_, p_123542_);
         }
      });
      DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource p_123547_, ItemStack p_123548_) {
            Direction direction = p_123547_.getBlockState().getValue(DispenserBlock.FACING);
            FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(p_123547_.getLevel(), p_123548_, p_123547_.x(), p_123547_.y(), p_123547_.x(), true);
            DispenseItemBehavior.setEntityPokingOutOfBlock(p_123547_, fireworkrocketentity, direction);
            fireworkrocketentity.shoot((double)direction.getStepX(), (double)direction.getStepY(), (double)direction.getStepZ(), 0.5F, 1.0F);
            p_123547_.getLevel().addFreshEntity(fireworkrocketentity);
            p_123548_.shrink(1);
            return p_123548_;
         }

         protected void playSound(BlockSource p_123545_) {
            p_123545_.getLevel().levelEvent(1004, p_123545_.getPos(), 0);
         }
      });
      DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource p_123556_, ItemStack p_123557_) {
            Direction direction = p_123556_.getBlockState().getValue(DispenserBlock.FACING);
            Position position = DispenserBlock.getDispensePosition(p_123556_);
            double d0 = position.x() + (double)((float)direction.getStepX() * 0.3F);
            double d1 = position.y() + (double)((float)direction.getStepY() * 0.3F);
            double d2 = position.z() + (double)((float)direction.getStepZ() * 0.3F);
            Level level = p_123556_.getLevel();
            Random random = level.random;
            double d3 = random.nextGaussian() * 0.05D + (double)direction.getStepX();
            double d4 = random.nextGaussian() * 0.05D + (double)direction.getStepY();
            double d5 = random.nextGaussian() * 0.05D + (double)direction.getStepZ();
            SmallFireball smallfireball = new SmallFireball(level, d0, d1, d2, d3, d4, d5);
            level.addFreshEntity(Util.make(smallfireball, (p_123552_) -> {
               p_123552_.setItem(p_123557_);
            }));
            p_123557_.shrink(1);
            return p_123557_;
         }

         protected void playSound(BlockSource p_123554_) {
            p_123554_.getLevel().levelEvent(1018, p_123554_.getPos(), 0);
         }
      });
      DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK));
      DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE));
      DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH));
      DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE));
      DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK));
      DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA));
      DispenseItemBehavior dispenseitembehavior1 = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(BlockSource p_123561_, ItemStack p_123562_) {
            DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)p_123562_.getItem();
            BlockPos blockpos = p_123561_.getPos().relative(p_123561_.getBlockState().getValue(DispenserBlock.FACING));
            Level level = p_123561_.getLevel();
            if (dispensiblecontaineritem.emptyContents((Player)null, level, blockpos, (BlockHitResult)null, p_123562_)) {
               dispensiblecontaineritem.checkExtraContent((Player)null, level, p_123562_, blockpos);
               return new ItemStack(Items.BUCKET);
            } else {
               return this.defaultDispenseItemBehavior.dispense(p_123561_, p_123562_);
            }
         }
      };
      DispenserBlock.registerBehavior(Items.LAVA_BUCKET, dispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.WATER_BUCKET, dispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.POWDER_SNOW_BUCKET, dispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.SALMON_BUCKET, dispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.COD_BUCKET, dispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, dispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, dispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.AXOLOTL_BUCKET, dispenseitembehavior1);
      DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(BlockSource p_123566_, ItemStack p_123567_) {
            LevelAccessor levelaccessor = p_123566_.getLevel();
            BlockPos blockpos = p_123566_.getPos().relative(p_123566_.getBlockState().getValue(DispenserBlock.FACING));
            BlockState blockstate = levelaccessor.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (block instanceof BucketPickup) {
               ItemStack itemstack = ((BucketPickup)block).pickupBlock(levelaccessor, blockpos, blockstate);
               if (itemstack.isEmpty()) {
                  return super.execute(p_123566_, p_123567_);
               } else {
                  levelaccessor.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, blockpos);
                  Item item = itemstack.getItem();
                  p_123567_.shrink(1);
                  if (p_123567_.isEmpty()) {
                     return new ItemStack(item);
                  } else {
                     if (p_123566_.<DispenserBlockEntity>getEntity().addItem(new ItemStack(item)) < 0) {
                        this.defaultDispenseItemBehavior.dispense(p_123566_, new ItemStack(item));
                     }

                     return p_123567_;
                  }
               }
            } else {
               return super.execute(p_123566_, p_123567_);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource p_123412_, ItemStack p_123413_) {
            Level level = p_123412_.getLevel();
            this.setSuccess(true);
            Direction direction = p_123412_.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = p_123412_.getPos().relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            if (BaseFireBlock.canBePlacedAt(level, blockpos, direction)) {
               level.setBlockAndUpdate(blockpos, BaseFireBlock.getState(level, blockpos));
               level.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockpos);
            } else if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
               if (blockstate.isFlammable(level, blockpos, p_123412_.getBlockState().getValue(DispenserBlock.FACING).getOpposite())) {
                  blockstate.onCaughtFire(level, blockpos, p_123412_.getBlockState().getValue(DispenserBlock.FACING).getOpposite(), null);
                  if (blockstate.getBlock() instanceof TntBlock)
                     level.removeBlock(blockpos, false);
               } else {
                  this.setSuccess(false);
               }
            } else {
               level.setBlockAndUpdate(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)));
               level.gameEvent((Entity)null, GameEvent.BLOCK_CHANGE, blockpos);
            }

            if (this.isSuccess() && p_123413_.hurt(1, level.random, (ServerPlayer)null)) {
               p_123413_.setCount(0);
            }

            return p_123413_;
         }
      });
      DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource p_123416_, ItemStack p_123417_) {
            this.setSuccess(true);
            Level level = p_123416_.getLevel();
            BlockPos blockpos = p_123416_.getPos().relative(p_123416_.getBlockState().getValue(DispenserBlock.FACING));
            if (!BoneMealItem.growCrop(p_123417_, level, blockpos) && !BoneMealItem.growWaterPlant(p_123417_, level, blockpos, (Direction)null)) {
               this.setSuccess(false);
            } else if (!level.isClientSide) {
               level.levelEvent(1505, blockpos, 0);
            }

            return p_123417_;
         }
      });
      DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
         protected ItemStack execute(BlockSource p_123425_, ItemStack p_123426_) {
            Level level = p_123425_.getLevel();
            BlockPos blockpos = p_123425_.getPos().relative(p_123425_.getBlockState().getValue(DispenserBlock.FACING));
            PrimedTnt primedtnt = new PrimedTnt(level, (double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D, (LivingEntity)null);
            level.addFreshEntity(primedtnt);
            level.playSound((Player)null, primedtnt.getX(), primedtnt.getY(), primedtnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent((Entity)null, GameEvent.ENTITY_PLACE, blockpos);
            p_123426_.shrink(1);
            return p_123426_;
         }
      });
      DispenseItemBehavior dispenseitembehavior = new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource p_123429_, ItemStack p_123430_) {
            this.setSuccess(ArmorItem.dispenseArmor(p_123429_, p_123430_));
            return p_123430_;
         }
      };
      DispenserBlock.registerBehavior(Items.CREEPER_HEAD, dispenseitembehavior);
      DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, dispenseitembehavior);
      DispenserBlock.registerBehavior(Items.DRAGON_HEAD, dispenseitembehavior);
      DispenserBlock.registerBehavior(Items.SKELETON_SKULL, dispenseitembehavior);
      DispenserBlock.registerBehavior(Items.PLAYER_HEAD, dispenseitembehavior);
      DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource p_123433_, ItemStack p_123434_) {
            Level level = p_123433_.getLevel();
            Direction direction = p_123433_.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = p_123433_.getPos().relative(direction);
            if (level.isEmptyBlock(blockpos) && WitherSkullBlock.canSpawnMob(level, blockpos, p_123434_)) {
               level.setBlock(blockpos, Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, Integer.valueOf(direction.getAxis() == Direction.Axis.Y ? 0 : direction.getOpposite().get2DDataValue() * 4)), 3);
               level.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockpos);
               BlockEntity blockentity = level.getBlockEntity(blockpos);
               if (blockentity instanceof SkullBlockEntity) {
                  WitherSkullBlock.checkSpawn(level, blockpos, (SkullBlockEntity)blockentity);
               }

               p_123434_.shrink(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(ArmorItem.dispenseArmor(p_123433_, p_123434_));
            }

            return p_123434_;
         }
      });
      DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource p_123437_, ItemStack p_123438_) {
            Level level = p_123437_.getLevel();
            BlockPos blockpos = p_123437_.getPos().relative(p_123437_.getBlockState().getValue(DispenserBlock.FACING));
            CarvedPumpkinBlock carvedpumpkinblock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
            if (level.isEmptyBlock(blockpos) && carvedpumpkinblock.canSpawnGolem(level, blockpos)) {
               if (!level.isClientSide) {
                  level.setBlock(blockpos, carvedpumpkinblock.defaultBlockState(), 3);
                  level.gameEvent((Entity)null, GameEvent.BLOCK_PLACE, blockpos);
               }

               p_123438_.shrink(1);
               this.setSuccess(true);
            } else {
               this.setSuccess(ArmorItem.dispenseArmor(p_123437_, p_123438_));
            }

            return p_123438_;
         }
      });
      DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());

      for(DyeColor dyecolor : DyeColor.values()) {
         DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor(dyecolor).asItem(), new ShulkerBoxDispenseBehavior());
      }

      DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new OptionalDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         private ItemStack takeLiquid(BlockSource p_123447_, ItemStack p_123448_, ItemStack p_123449_) {
            p_123448_.shrink(1);
            if (p_123448_.isEmpty()) {
               p_123447_.getLevel().gameEvent((Entity)null, GameEvent.FLUID_PICKUP, p_123447_.getPos());
               return p_123449_.copy();
            } else {
               if (p_123447_.<DispenserBlockEntity>getEntity().addItem(p_123449_.copy()) < 0) {
                  this.defaultDispenseItemBehavior.dispense(p_123447_, p_123449_.copy());
               }

               return p_123448_;
            }
         }

         public ItemStack execute(BlockSource p_123444_, ItemStack p_123445_) {
            this.setSuccess(false);
            ServerLevel serverlevel = p_123444_.getLevel();
            BlockPos blockpos = p_123444_.getPos().relative(p_123444_.getBlockState().getValue(DispenserBlock.FACING));
            BlockState blockstate = serverlevel.getBlockState(blockpos);
            if (blockstate.is(BlockTags.BEEHIVES, (p_123442_) -> {
               return p_123442_.hasProperty(BeehiveBlock.HONEY_LEVEL) && p_123442_.getBlock() instanceof BeehiveBlock;
            }) && blockstate.getValue(BeehiveBlock.HONEY_LEVEL) >= 5) {
               ((BeehiveBlock)blockstate.getBlock()).releaseBeesAndResetHoneyLevel(serverlevel, blockstate, blockpos, (Player)null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
               this.setSuccess(true);
               return this.takeLiquid(p_123444_, p_123445_, new ItemStack(Items.HONEY_BOTTLE));
            } else if (serverlevel.getFluidState(blockpos).is(FluidTags.WATER)) {
               this.setSuccess(true);
               return this.takeLiquid(p_123444_, p_123445_, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER));
            } else {
               return super.execute(p_123444_, p_123445_);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.GLOWSTONE, new OptionalDispenseItemBehavior() {
         public ItemStack execute(BlockSource p_123452_, ItemStack p_123453_) {
            Direction direction = p_123452_.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockpos = p_123452_.getPos().relative(direction);
            Level level = p_123452_.getLevel();
            BlockState blockstate = level.getBlockState(blockpos);
            this.setSuccess(true);
            if (blockstate.is(Blocks.RESPAWN_ANCHOR)) {
               if (blockstate.getValue(RespawnAnchorBlock.CHARGE) != 4) {
                  RespawnAnchorBlock.charge(level, blockpos, blockstate);
                  p_123453_.shrink(1);
               } else {
                  this.setSuccess(false);
               }

               return p_123453_;
            } else {
               return super.execute(p_123452_, p_123453_);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenseItemBehavior());
      DispenserBlock.registerBehavior(Items.HONEYCOMB, new OptionalDispenseItemBehavior() {
         public ItemStack execute(BlockSource p_175747_, ItemStack p_175748_) {
            BlockPos blockpos = p_175747_.getPos().relative(p_175747_.getBlockState().getValue(DispenserBlock.FACING));
            Level level = p_175747_.getLevel();
            BlockState blockstate = level.getBlockState(blockpos);
            Optional<BlockState> optional = HoneycombItem.getWaxed(blockstate);
            if (optional.isPresent()) {
               level.setBlockAndUpdate(blockpos, optional.get());
               level.levelEvent(3003, blockpos, 0);
               p_175748_.shrink(1);
               this.setSuccess(true);
               return p_175748_;
            } else {
               return super.execute(p_175747_, p_175748_);
            }
         }
      });
   }

   static void setEntityPokingOutOfBlock(BlockSource p_123396_, Entity p_123397_, Direction p_123398_) {
      p_123397_.setPos(p_123396_.x() + (double)p_123398_.getStepX() * (0.5000099999997474D - (double)p_123397_.getBbWidth() / 2.0D), p_123396_.y() + (double)p_123398_.getStepY() * (0.5000099999997474D - (double)p_123397_.getBbHeight() / 2.0D) - (double)p_123397_.getBbHeight() / 2.0D, p_123396_.z() + (double)p_123398_.getStepZ() * (0.5000099999997474D - (double)p_123397_.getBbWidth() / 2.0D));
   }
}

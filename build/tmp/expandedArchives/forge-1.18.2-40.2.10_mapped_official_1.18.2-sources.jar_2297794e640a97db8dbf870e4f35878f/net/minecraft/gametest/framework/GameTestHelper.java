package net.minecraft.gametest.framework;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GameTestHelper {
   private final GameTestInfo testInfo;
   private boolean finalCheckAdded;

   public GameTestHelper(GameTestInfo p_127597_) {
      this.testInfo = p_127597_;
   }

   public ServerLevel getLevel() {
      return this.testInfo.getLevel();
   }

   public BlockState getBlockState(BlockPos p_177233_) {
      return this.getLevel().getBlockState(this.absolutePos(p_177233_));
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos p_177348_) {
      return this.getLevel().getBlockEntity(this.absolutePos(p_177348_));
   }

   public void killAllEntities() {
      AABB aabb = this.getBounds();
      List<Entity> list = this.getLevel().getEntitiesOfClass(Entity.class, aabb.inflate(1.0D), (p_177131_) -> {
         return !(p_177131_ instanceof Player);
      });
      list.forEach(Entity::kill);
   }

   public ItemEntity spawnItem(Item p_177190_, float p_177191_, float p_177192_, float p_177193_) {
      ServerLevel serverlevel = this.getLevel();
      Vec3 vec3 = this.absoluteVec(new Vec3((double)p_177191_, (double)p_177192_, (double)p_177193_));
      ItemEntity itementity = new ItemEntity(serverlevel, vec3.x, vec3.y, vec3.z, new ItemStack(p_177190_, 1));
      itementity.setDeltaMovement(0.0D, 0.0D, 0.0D);
      serverlevel.addFreshEntity(itementity);
      return itementity;
   }

   public <E extends Entity> E spawn(EntityType<E> p_177177_, BlockPos p_177178_) {
      return this.spawn(p_177177_, Vec3.atBottomCenterOf(p_177178_));
   }

   public <E extends Entity> E spawn(EntityType<E> p_177174_, Vec3 p_177175_) {
      ServerLevel serverlevel = this.getLevel();
      E e = p_177174_.create(serverlevel);
      if (e instanceof Mob) {
         ((Mob)e).setPersistenceRequired();
      }

      Vec3 vec3 = this.absoluteVec(p_177175_);
      e.moveTo(vec3.x, vec3.y, vec3.z, e.getYRot(), e.getXRot());
      serverlevel.addFreshEntity(e);
      return e;
   }

   public <E extends Entity> E spawn(EntityType<E> p_177169_, int p_177170_, int p_177171_, int p_177172_) {
      return this.spawn(p_177169_, new BlockPos(p_177170_, p_177171_, p_177172_));
   }

   public <E extends Entity> E spawn(EntityType<E> p_177164_, float p_177165_, float p_177166_, float p_177167_) {
      return this.spawn(p_177164_, new Vec3((double)p_177165_, (double)p_177166_, (double)p_177167_));
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> p_177330_, BlockPos p_177331_) {
      E e = this.spawn(p_177330_, p_177331_);
      e.removeFreeWill();
      return e;
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> p_177322_, int p_177323_, int p_177324_, int p_177325_) {
      return this.spawnWithNoFreeWill(p_177322_, new BlockPos(p_177323_, p_177324_, p_177325_));
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> p_177327_, Vec3 p_177328_) {
      E e = this.spawn(p_177327_, p_177328_);
      e.removeFreeWill();
      return e;
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> p_177317_, float p_177318_, float p_177319_, float p_177320_) {
      return this.spawnWithNoFreeWill(p_177317_, new Vec3((double)p_177318_, (double)p_177319_, (double)p_177320_));
   }

   public GameTestSequence walkTo(Mob p_177186_, BlockPos p_177187_, float p_177188_) {
      return this.startSequence().thenExecuteAfter(2, () -> {
         Path path = p_177186_.getNavigation().createPath(this.absolutePos(p_177187_), 0);
         p_177186_.getNavigation().moveTo(path, (double)p_177188_);
      });
   }

   public void pressButton(int p_177104_, int p_177105_, int p_177106_) {
      this.pressButton(new BlockPos(p_177104_, p_177105_, p_177106_));
   }

   public void pressButton(BlockPos p_177386_) {
      this.assertBlockState(p_177386_, (p_177212_) -> {
         return p_177212_.is(BlockTags.BUTTONS);
      }, () -> {
         return "Expected button";
      });
      BlockPos blockpos = this.absolutePos(p_177386_);
      BlockState blockstate = this.getLevel().getBlockState(blockpos);
      ButtonBlock buttonblock = (ButtonBlock)blockstate.getBlock();
      buttonblock.press(blockstate, this.getLevel(), blockpos);
   }

   public void useBlock(BlockPos p_177409_) {
      BlockPos blockpos = this.absolutePos(p_177409_);
      BlockState blockstate = this.getLevel().getBlockState(blockpos);
      blockstate.use(this.getLevel(), this.makeMockPlayer(), InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(blockpos), Direction.NORTH, blockpos, true));
   }

   public LivingEntity makeAboutToDrown(LivingEntity p_177184_) {
      p_177184_.setAirSupply(0);
      p_177184_.setHealth(0.25F);
      return p_177184_;
   }

   public Player makeMockPlayer() {
      return new Player(this.getLevel(), BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
         public boolean isSpectator() {
            return false;
         }

         public boolean isCreative() {
            return true;
         }
      };
   }

   public void pullLever(int p_177303_, int p_177304_, int p_177305_) {
      this.pullLever(new BlockPos(p_177303_, p_177304_, p_177305_));
   }

   public void pullLever(BlockPos p_177422_) {
      this.assertBlockPresent(Blocks.LEVER, p_177422_);
      BlockPos blockpos = this.absolutePos(p_177422_);
      BlockState blockstate = this.getLevel().getBlockState(blockpos);
      LeverBlock leverblock = (LeverBlock)blockstate.getBlock();
      leverblock.pull(blockstate, this.getLevel(), blockpos);
   }

   public void pulseRedstone(BlockPos p_177235_, long p_177236_) {
      this.setBlock(p_177235_, Blocks.REDSTONE_BLOCK);
      this.runAfterDelay(p_177236_, () -> {
         this.setBlock(p_177235_, Blocks.AIR);
      });
   }

   public void destroyBlock(BlockPos p_177435_) {
      this.getLevel().destroyBlock(this.absolutePos(p_177435_), false, (Entity)null);
   }

   public void setBlock(int p_177108_, int p_177109_, int p_177110_, Block p_177111_) {
      this.setBlock(new BlockPos(p_177108_, p_177109_, p_177110_), p_177111_);
   }

   public void setBlock(int p_177113_, int p_177114_, int p_177115_, BlockState p_177116_) {
      this.setBlock(new BlockPos(p_177113_, p_177114_, p_177115_), p_177116_);
   }

   public void setBlock(BlockPos p_177246_, Block p_177247_) {
      this.setBlock(p_177246_, p_177247_.defaultBlockState());
   }

   public void setBlock(BlockPos p_177253_, BlockState p_177254_) {
      this.getLevel().setBlock(this.absolutePos(p_177253_), p_177254_, 3);
   }

   public void setNight() {
      this.setDayTime(13000);
   }

   public void setDayTime(int p_177102_) {
      this.getLevel().setDayTime((long)p_177102_);
   }

   public void assertBlockPresent(Block p_177204_, int p_177205_, int p_177206_, int p_177207_) {
      this.assertBlockPresent(p_177204_, new BlockPos(p_177205_, p_177206_, p_177207_));
   }

   public void assertBlockPresent(Block p_177209_, BlockPos p_177210_) {
      BlockState blockstate = this.getBlockState(p_177210_);
      this.assertBlock(p_177210_, (p_177216_) -> {
         return blockstate.is(p_177209_);
      }, "Expected " + p_177209_.getName().getString() + ", got " + blockstate.getBlock().getName().getString());
   }

   public void assertBlockNotPresent(Block p_177337_, int p_177338_, int p_177339_, int p_177340_) {
      this.assertBlockNotPresent(p_177337_, new BlockPos(p_177338_, p_177339_, p_177340_));
   }

   public void assertBlockNotPresent(Block p_177342_, BlockPos p_177343_) {
      this.assertBlock(p_177343_, (p_177251_) -> {
         return !this.getBlockState(p_177343_).is(p_177342_);
      }, "Did not expect " + p_177342_.getName().getString());
   }

   public void succeedWhenBlockPresent(Block p_177378_, int p_177379_, int p_177380_, int p_177381_) {
      this.succeedWhenBlockPresent(p_177378_, new BlockPos(p_177379_, p_177380_, p_177381_));
   }

   public void succeedWhenBlockPresent(Block p_177383_, BlockPos p_177384_) {
      this.succeedWhen(() -> {
         this.assertBlockPresent(p_177383_, p_177384_);
      });
   }

   public void assertBlock(BlockPos p_177272_, Predicate<Block> p_177273_, String p_177274_) {
      this.assertBlock(p_177272_, p_177273_, () -> {
         return p_177274_;
      });
   }

   public void assertBlock(BlockPos p_177276_, Predicate<Block> p_177277_, Supplier<String> p_177278_) {
      this.assertBlockState(p_177276_, (p_177296_) -> {
         return p_177277_.test(p_177296_.getBlock());
      }, p_177278_);
   }

   public <T extends Comparable<T>> void assertBlockProperty(BlockPos p_177256_, Property<T> p_177257_, T p_177258_) {
      this.assertBlockState(p_177256_, (p_177223_) -> {
         return p_177223_.hasProperty(p_177257_) && p_177223_.<T>getValue(p_177257_).equals(p_177258_);
      }, () -> {
         return "Expected property " + p_177257_.getName() + " to be " + p_177258_;
      });
   }

   public <T extends Comparable<T>> void assertBlockProperty(BlockPos p_177260_, Property<T> p_177261_, Predicate<T> p_177262_, String p_177263_) {
      this.assertBlockState(p_177260_, (p_177300_) -> {
         return p_177262_.test(p_177300_.getValue(p_177261_));
      }, () -> {
         return p_177263_;
      });
   }

   public void assertBlockState(BlockPos p_177358_, Predicate<BlockState> p_177359_, Supplier<String> p_177360_) {
      BlockState blockstate = this.getBlockState(p_177358_);
      if (!p_177359_.test(blockstate)) {
         throw new GameTestAssertPosException(p_177360_.get(), this.absolutePos(p_177358_), p_177358_, this.testInfo.getTick());
      }
   }

   public void assertEntityPresent(EntityType<?> p_177157_) {
      List<? extends Entity> list = this.getLevel().getEntities(p_177157_, this.getBounds(), Entity::isAlive);
      if (list.isEmpty()) {
         throw new GameTestAssertException("Expected " + p_177157_.toShortString() + " to exist");
      }
   }

   public void assertEntityPresent(EntityType<?> p_177370_, int p_177371_, int p_177372_, int p_177373_) {
      this.assertEntityPresent(p_177370_, new BlockPos(p_177371_, p_177372_, p_177373_));
   }

   public void assertEntityPresent(EntityType<?> p_177375_, BlockPos p_177376_) {
      BlockPos blockpos = this.absolutePos(p_177376_);
      List<? extends Entity> list = this.getLevel().getEntities(p_177375_, new AABB(blockpos), Entity::isAlive);
      if (list.isEmpty()) {
         throw new GameTestAssertPosException("Expected " + p_177375_.toShortString(), blockpos, p_177376_, this.testInfo.getTick());
      }
   }

   public void assertEntityPresent(EntityType<?> p_177180_, BlockPos p_177181_, double p_177182_) {
      BlockPos blockpos = this.absolutePos(p_177181_);
      List<? extends Entity> list = this.getLevel().getEntities(p_177180_, (new AABB(blockpos)).inflate(p_177182_), Entity::isAlive);
      if (list.isEmpty()) {
         throw new GameTestAssertPosException("Expected " + p_177180_.toShortString(), blockpos, p_177181_, this.testInfo.getTick());
      }
   }

   public void assertEntityInstancePresent(Entity p_177133_, int p_177134_, int p_177135_, int p_177136_) {
      this.assertEntityInstancePresent(p_177133_, new BlockPos(p_177134_, p_177135_, p_177136_));
   }

   public void assertEntityInstancePresent(Entity p_177141_, BlockPos p_177142_) {
      BlockPos blockpos = this.absolutePos(p_177142_);
      List<? extends Entity> list = this.getLevel().getEntities(p_177141_.getType(), new AABB(blockpos), Entity::isAlive);
      list.stream().filter((p_177139_) -> {
         return p_177139_ == p_177141_;
      }).findFirst().orElseThrow(() -> {
         return new GameTestAssertPosException("Expected " + p_177141_.getType().toShortString(), blockpos, p_177142_, this.testInfo.getTick());
      });
   }

   public void assertItemEntityCountIs(Item p_177199_, BlockPos p_177200_, double p_177201_, int p_177202_) {
      BlockPos blockpos = this.absolutePos(p_177200_);
      List<ItemEntity> list = this.getLevel().getEntities(EntityType.ITEM, (new AABB(blockpos)).inflate(p_177201_), Entity::isAlive);
      int i = 0;

      for(Entity entity : list) {
         ItemEntity itementity = (ItemEntity)entity;
         if (itementity.getItem().getItem().equals(p_177199_)) {
            i += itementity.getItem().getCount();
         }
      }

      if (i != p_177202_) {
         throw new GameTestAssertPosException("Expected " + p_177202_ + " " + p_177199_.getDescription().getString() + " items to exist (found " + i + ")", blockpos, p_177200_, this.testInfo.getTick());
      }
   }

   public void assertItemEntityPresent(Item p_177195_, BlockPos p_177196_, double p_177197_) {
      BlockPos blockpos = this.absolutePos(p_177196_);

      for(Entity entity : this.getLevel().getEntities(EntityType.ITEM, (new AABB(blockpos)).inflate(p_177197_), Entity::isAlive)) {
         ItemEntity itementity = (ItemEntity)entity;
         if (itementity.getItem().getItem().equals(p_177195_)) {
            return;
         }
      }

      throw new GameTestAssertPosException("Expected " + p_177195_.getDescription().getString() + " item", blockpos, p_177196_, this.testInfo.getTick());
   }

   public void assertEntityNotPresent(EntityType<?> p_177310_) {
      List<? extends Entity> list = this.getLevel().getEntities(p_177310_, this.getBounds(), Entity::isAlive);
      if (!list.isEmpty()) {
         throw new GameTestAssertException("Did not expect " + p_177310_.toShortString() + " to exist");
      }
   }

   public void assertEntityNotPresent(EntityType<?> p_177398_, int p_177399_, int p_177400_, int p_177401_) {
      this.assertEntityNotPresent(p_177398_, new BlockPos(p_177399_, p_177400_, p_177401_));
   }

   public void assertEntityNotPresent(EntityType<?> p_177403_, BlockPos p_177404_) {
      BlockPos blockpos = this.absolutePos(p_177404_);
      List<? extends Entity> list = this.getLevel().getEntities(p_177403_, new AABB(blockpos), Entity::isAlive);
      if (!list.isEmpty()) {
         throw new GameTestAssertPosException("Did not expect " + p_177403_.toShortString(), blockpos, p_177404_, this.testInfo.getTick());
      }
   }

   public void assertEntityTouching(EntityType<?> p_177159_, double p_177160_, double p_177161_, double p_177162_) {
      Vec3 vec3 = new Vec3(p_177160_, p_177161_, p_177162_);
      Vec3 vec31 = this.absoluteVec(vec3);
      Predicate<? super Entity> predicate = (p_177346_) -> {
         return p_177346_.getBoundingBox().intersects(vec31, vec31);
      };
      List<? extends Entity> list = this.getLevel().getEntities(p_177159_, this.getBounds(), predicate);
      if (list.isEmpty()) {
         throw new GameTestAssertException("Expected " + p_177159_.toShortString() + " to touch " + vec31 + " (relative " + vec3 + ")");
      }
   }

   public void assertEntityNotTouching(EntityType<?> p_177312_, double p_177313_, double p_177314_, double p_177315_) {
      Vec3 vec3 = new Vec3(p_177313_, p_177314_, p_177315_);
      Vec3 vec31 = this.absoluteVec(vec3);
      Predicate<? super Entity> predicate = (p_177231_) -> {
         return !p_177231_.getBoundingBox().intersects(vec31, vec31);
      };
      List<? extends Entity> list = this.getLevel().getEntities(p_177312_, this.getBounds(), predicate);
      if (list.isEmpty()) {
         throw new GameTestAssertException("Did not expect " + p_177312_.toShortString() + " to touch " + vec31 + " (relative " + vec3 + ")");
      }
   }

   public <E extends Entity, T> void assertEntityData(BlockPos p_177238_, EntityType<E> p_177239_, Function<? super E, T> p_177240_, @Nullable T p_177241_) {
      BlockPos blockpos = this.absolutePos(p_177238_);
      List<E> list = this.getLevel().getEntities(p_177239_, new AABB(blockpos), Entity::isAlive);
      if (list.isEmpty()) {
         throw new GameTestAssertPosException("Expected " + p_177239_.toShortString(), blockpos, p_177238_, this.testInfo.getTick());
      } else {
         for(E e : list) {
            T t = p_177240_.apply(e);
            if (t == null) {
               if (p_177241_ != null) {
                  throw new GameTestAssertException("Expected entity data to be: " + p_177241_ + ", but was: " + t);
               }
            } else if (!t.equals(p_177241_)) {
               throw new GameTestAssertException("Expected entity data to be: " + p_177241_ + ", but was: " + t);
            }
         }

      }
   }

   public void assertContainerEmpty(BlockPos p_177441_) {
      BlockPos blockpos = this.absolutePos(p_177441_);
      BlockEntity blockentity = this.getLevel().getBlockEntity(blockpos);
      if (blockentity instanceof BaseContainerBlockEntity && !((BaseContainerBlockEntity)blockentity).isEmpty()) {
         throw new GameTestAssertException("Container should be empty");
      }
   }

   public void assertContainerContains(BlockPos p_177243_, Item p_177244_) {
      BlockPos blockpos = this.absolutePos(p_177243_);
      BlockEntity blockentity = this.getLevel().getBlockEntity(blockpos);
      if (blockentity instanceof BaseContainerBlockEntity && ((BaseContainerBlockEntity)blockentity).countItem(p_177244_) != 1) {
         throw new GameTestAssertException("Container should contain: " + p_177244_);
      }
   }

   public void assertSameBlockStates(BoundingBox p_177225_, BlockPos p_177226_) {
      BlockPos.betweenClosedStream(p_177225_).forEach((p_177267_) -> {
         BlockPos blockpos = p_177226_.offset(p_177267_.getX() - p_177225_.minX(), p_177267_.getY() - p_177225_.minY(), p_177267_.getZ() - p_177225_.minZ());
         this.assertSameBlockState(p_177267_, blockpos);
      });
   }

   public void assertSameBlockState(BlockPos p_177269_, BlockPos p_177270_) {
      BlockState blockstate = this.getBlockState(p_177269_);
      BlockState blockstate1 = this.getBlockState(p_177270_);
      if (blockstate != blockstate1) {
         this.fail("Incorrect state. Expected " + blockstate1 + ", got " + blockstate, p_177269_);
      }

   }

   public void assertAtTickTimeContainerContains(long p_177124_, BlockPos p_177125_, Item p_177126_) {
      this.runAtTickTime(p_177124_, () -> {
         this.assertContainerContains(p_177125_, p_177126_);
      });
   }

   public void assertAtTickTimeContainerEmpty(long p_177121_, BlockPos p_177122_) {
      this.runAtTickTime(p_177121_, () -> {
         this.assertContainerEmpty(p_177122_);
      });
   }

   public <E extends Entity, T> void succeedWhenEntityData(BlockPos p_177350_, EntityType<E> p_177351_, Function<E, T> p_177352_, T p_177353_) {
      this.succeedWhen(() -> {
         this.assertEntityData(p_177350_, p_177351_, p_177352_, p_177353_);
      });
   }

   public <E extends Entity> void assertEntityProperty(E p_177153_, Predicate<E> p_177154_, String p_177155_) {
      if (!p_177154_.test(p_177153_)) {
         throw new GameTestAssertException("Entity " + p_177153_ + " failed " + p_177155_ + " test");
      }
   }

   public <E extends Entity, T> void assertEntityProperty(E p_177148_, Function<E, T> p_177149_, String p_177150_, T p_177151_) {
      T t = p_177149_.apply(p_177148_);
      if (!t.equals(p_177151_)) {
         throw new GameTestAssertException("Entity " + p_177148_ + " value " + p_177150_ + "=" + t + " is not equal to expected " + p_177151_);
      }
   }

   public void succeedWhenEntityPresent(EntityType<?> p_177414_, int p_177415_, int p_177416_, int p_177417_) {
      this.succeedWhenEntityPresent(p_177414_, new BlockPos(p_177415_, p_177416_, p_177417_));
   }

   public void succeedWhenEntityPresent(EntityType<?> p_177419_, BlockPos p_177420_) {
      this.succeedWhen(() -> {
         this.assertEntityPresent(p_177419_, p_177420_);
      });
   }

   public void succeedWhenEntityNotPresent(EntityType<?> p_177427_, int p_177428_, int p_177429_, int p_177430_) {
      this.succeedWhenEntityNotPresent(p_177427_, new BlockPos(p_177428_, p_177429_, p_177430_));
   }

   public void succeedWhenEntityNotPresent(EntityType<?> p_177432_, BlockPos p_177433_) {
      this.succeedWhen(() -> {
         this.assertEntityNotPresent(p_177432_, p_177433_);
      });
   }

   public void succeed() {
      this.testInfo.succeed();
   }

   private void ensureSingleFinalCheck() {
      if (this.finalCheckAdded) {
         throw new IllegalStateException("This test already has final clause");
      } else {
         this.finalCheckAdded = true;
      }
   }

   public void succeedIf(Runnable p_177280_) {
      this.ensureSingleFinalCheck();
      this.testInfo.createSequence().thenWaitUntil(0L, p_177280_).thenSucceed();
   }

   public void succeedWhen(Runnable p_177362_) {
      this.ensureSingleFinalCheck();
      this.testInfo.createSequence().thenWaitUntil(p_177362_).thenSucceed();
   }

   public void succeedOnTickWhen(int p_177118_, Runnable p_177119_) {
      this.ensureSingleFinalCheck();
      this.testInfo.createSequence().thenWaitUntil((long)p_177118_, p_177119_).thenSucceed();
   }

   public void runAtTickTime(long p_177128_, Runnable p_177129_) {
      this.testInfo.setRunAtTickTime(p_177128_, p_177129_);
   }

   public void runAfterDelay(long p_177307_, Runnable p_177308_) {
      this.runAtTickTime(this.testInfo.getTick() + p_177307_, p_177308_);
   }

   public void randomTick(BlockPos p_177447_) {
      BlockPos blockpos = this.absolutePos(p_177447_);
      ServerLevel serverlevel = this.getLevel();
      serverlevel.getBlockState(blockpos).randomTick(serverlevel, blockpos, serverlevel.random);
   }

   public void fail(String p_177290_, BlockPos p_177291_) {
      throw new GameTestAssertPosException(p_177290_, this.absolutePos(p_177291_), p_177291_, this.getTick());
   }

   public void fail(String p_177287_, Entity p_177288_) {
      throw new GameTestAssertPosException(p_177287_, p_177288_.blockPosition(), this.relativePos(p_177288_.blockPosition()), this.getTick());
   }

   public void fail(String p_177285_) {
      throw new GameTestAssertException(p_177285_);
   }

   public void failIf(Runnable p_177393_) {
      this.testInfo.createSequence().thenWaitUntil(p_177393_).thenFail(() -> {
         return new GameTestAssertException("Fail conditions met");
      });
   }

   public void failIfEver(Runnable p_177411_) {
      LongStream.range(this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach((p_177365_) -> {
         this.testInfo.setRunAtTickTime(p_177365_, p_177411_::run);
      });
   }

   public GameTestSequence startSequence() {
      return this.testInfo.createSequence();
   }

   public BlockPos absolutePos(BlockPos p_177450_) {
      BlockPos blockpos = this.testInfo.getStructureBlockPos();
      BlockPos blockpos1 = blockpos.offset(p_177450_);
      return StructureTemplate.transform(blockpos1, Mirror.NONE, this.testInfo.getRotation(), blockpos);
   }

   public BlockPos relativePos(BlockPos p_177453_) {
      BlockPos blockpos = this.testInfo.getStructureBlockPos();
      Rotation rotation = this.testInfo.getRotation().getRotated(Rotation.CLOCKWISE_180);
      BlockPos blockpos1 = StructureTemplate.transform(p_177453_, Mirror.NONE, rotation, blockpos);
      return blockpos1.subtract(blockpos);
   }

   public Vec3 absoluteVec(Vec3 p_177228_) {
      Vec3 vec3 = Vec3.atLowerCornerOf(this.testInfo.getStructureBlockPos());
      return StructureTemplate.transform(vec3.add(p_177228_), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getStructureBlockPos());
   }

   public long getTick() {
      return this.testInfo.getTick();
   }

   private AABB getBounds() {
      return this.testInfo.getStructureBounds();
   }

   private AABB getRelativeBounds() {
      AABB aabb = this.testInfo.getStructureBounds();
      return aabb.move(BlockPos.ZERO.subtract(this.absolutePos(BlockPos.ZERO)));
   }

   public void forEveryBlockInStructure(Consumer<BlockPos> p_177293_) {
      AABB aabb = this.getRelativeBounds();
      BlockPos.MutableBlockPos.betweenClosedStream(aabb.move(0.0D, 1.0D, 0.0D)).forEach(p_177293_);
   }

   public void onEachTick(Runnable p_177424_) {
      LongStream.range(this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach((p_177283_) -> {
         this.testInfo.setRunAtTickTime(p_177283_, p_177424_::run);
      });
   }
}
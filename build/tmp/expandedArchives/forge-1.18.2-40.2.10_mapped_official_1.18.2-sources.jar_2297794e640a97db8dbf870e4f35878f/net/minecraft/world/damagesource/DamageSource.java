package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class DamageSource {
   public static final DamageSource IN_FIRE = (new DamageSource("inFire")).bypassArmor().setIsFire();
   public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
   public static final DamageSource ON_FIRE = (new DamageSource("onFire")).bypassArmor().setIsFire();
   public static final DamageSource LAVA = (new DamageSource("lava")).setIsFire();
   public static final DamageSource HOT_FLOOR = (new DamageSource("hotFloor")).setIsFire();
   public static final DamageSource IN_WALL = (new DamageSource("inWall")).bypassArmor();
   public static final DamageSource CRAMMING = (new DamageSource("cramming")).bypassArmor();
   public static final DamageSource DROWN = (new DamageSource("drown")).bypassArmor();
   public static final DamageSource STARVE = (new DamageSource("starve")).bypassArmor().bypassMagic();
   public static final DamageSource CACTUS = new DamageSource("cactus");
   public static final DamageSource FALL = (new DamageSource("fall")).bypassArmor().setIsFall();
   public static final DamageSource FLY_INTO_WALL = (new DamageSource("flyIntoWall")).bypassArmor();
   public static final DamageSource OUT_OF_WORLD = (new DamageSource("outOfWorld")).bypassArmor().bypassInvul();
   public static final DamageSource GENERIC = (new DamageSource("generic")).bypassArmor();
   public static final DamageSource MAGIC = (new DamageSource("magic")).bypassArmor().setMagic();
   public static final DamageSource WITHER = (new DamageSource("wither")).bypassArmor();
   public static final DamageSource ANVIL = (new DamageSource("anvil")).damageHelmet();
   public static final DamageSource FALLING_BLOCK = (new DamageSource("fallingBlock")).damageHelmet();
   public static final DamageSource DRAGON_BREATH = (new DamageSource("dragonBreath")).bypassArmor();
   public static final DamageSource DRY_OUT = new DamageSource("dryout");
   public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
   public static final DamageSource FREEZE = (new DamageSource("freeze")).bypassArmor();
   public static final DamageSource FALLING_STALACTITE = (new DamageSource("fallingStalactite")).damageHelmet();
   public static final DamageSource STALAGMITE = (new DamageSource("stalagmite")).bypassArmor().setIsFall();
   private boolean damageHelmet;
   private boolean bypassArmor;
   private boolean bypassInvul;
   private boolean bypassMagic;
   private float exhaustion = 0.1F;
   private boolean isFireSource;
   private boolean isProjectile;
   private boolean scalesWithDifficulty;
   private boolean isMagic;
   private boolean isExplosion;
   private boolean isFall;
   private boolean noAggro;
   public final String msgId;

   public static DamageSource sting(LivingEntity p_19365_) {
      return new EntityDamageSource("sting", p_19365_);
   }

   public static DamageSource mobAttack(LivingEntity p_19371_) {
      return new EntityDamageSource("mob", p_19371_);
   }

   public static DamageSource indirectMobAttack(Entity p_19341_, @Nullable LivingEntity p_19342_) {
      return new IndirectEntityDamageSource("mob", p_19341_, p_19342_);
   }

   public static DamageSource playerAttack(Player p_19345_) {
      return new EntityDamageSource("player", p_19345_);
   }

   public static DamageSource arrow(AbstractArrow p_19347_, @Nullable Entity p_19348_) {
      return (new IndirectEntityDamageSource("arrow", p_19347_, p_19348_)).setProjectile();
   }

   public static DamageSource trident(Entity p_19338_, @Nullable Entity p_19339_) {
      return (new IndirectEntityDamageSource("trident", p_19338_, p_19339_)).setProjectile();
   }

   public static DamageSource fireworks(FireworkRocketEntity p_19353_, @Nullable Entity p_19354_) {
      return (new IndirectEntityDamageSource("fireworks", p_19353_, p_19354_)).setExplosion();
   }

   public static DamageSource fireball(Fireball p_19350_, @Nullable Entity p_19351_) {
      return p_19351_ == null ? (new IndirectEntityDamageSource("onFire", p_19350_, p_19350_)).setIsFire().setProjectile() : (new IndirectEntityDamageSource("fireball", p_19350_, p_19351_)).setIsFire().setProjectile();
   }

   public static DamageSource witherSkull(WitherSkull p_19356_, Entity p_19357_) {
      return (new IndirectEntityDamageSource("witherSkull", p_19356_, p_19357_)).setProjectile();
   }

   public static DamageSource thrown(Entity p_19362_, @Nullable Entity p_19363_) {
      return (new IndirectEntityDamageSource("thrown", p_19362_, p_19363_)).setProjectile();
   }

   public static DamageSource indirectMagic(Entity p_19368_, @Nullable Entity p_19369_) {
      return (new IndirectEntityDamageSource("indirectMagic", p_19368_, p_19369_)).bypassArmor().setMagic();
   }

   public static DamageSource thorns(Entity p_19336_) {
      return (new EntityDamageSource("thorns", p_19336_)).setThorns().setMagic();
   }

   public static DamageSource explosion(@Nullable Explosion p_19359_) {
      return explosion(p_19359_ != null ? p_19359_.getSourceMob() : null);
   }

   public static DamageSource explosion(@Nullable LivingEntity p_19374_) {
      return p_19374_ != null ? (new EntityDamageSource("explosion.player", p_19374_)).setScalesWithDifficulty().setExplosion() : (new DamageSource("explosion")).setScalesWithDifficulty().setExplosion();
   }

   public static DamageSource badRespawnPointExplosion() {
      return new BadRespawnPointDamage();
   }

   public String toString() {
      return "DamageSource (" + this.msgId + ")";
   }

   public boolean isProjectile() {
      return this.isProjectile;
   }

   public DamageSource setProjectile() {
      this.isProjectile = true;
      return this;
   }

   public boolean isExplosion() {
      return this.isExplosion;
   }

   public DamageSource setExplosion() {
      this.isExplosion = true;
      return this;
   }

   public boolean isBypassArmor() {
      return this.bypassArmor;
   }

   public boolean isDamageHelmet() {
      return this.damageHelmet;
   }

   public float getFoodExhaustion() {
      return this.exhaustion;
   }

   public boolean isBypassInvul() {
      return this.bypassInvul;
   }

   public boolean isBypassMagic() {
      return this.bypassMagic;
   }

   public DamageSource(String p_19333_) {
      this.msgId = p_19333_;
   }

   @Nullable
   public Entity getDirectEntity() {
      return this.getEntity();
   }

   @Nullable
   public Entity getEntity() {
      return null;
   }

   public DamageSource bypassArmor() {
      this.bypassArmor = true;
      this.exhaustion = 0.0F;
      return this;
   }

   public DamageSource damageHelmet() {
      this.damageHelmet = true;
      return this;
   }

   public DamageSource bypassInvul() {
      this.bypassInvul = true;
      return this;
   }

   public DamageSource bypassMagic() {
      this.bypassMagic = true;
      this.exhaustion = 0.0F;
      return this;
   }

   public DamageSource setIsFire() {
      this.isFireSource = true;
      return this;
   }

   public DamageSource setNoAggro() {
      this.noAggro = true;
      return this;
   }

   public Component getLocalizedDeathMessage(LivingEntity p_19343_) {
      LivingEntity livingentity = p_19343_.getKillCredit();
      String s = "death.attack." + this.msgId;
      String s1 = s + ".player";
      return livingentity != null ? new TranslatableComponent(s1, p_19343_.getDisplayName(), livingentity.getDisplayName()) : new TranslatableComponent(s, p_19343_.getDisplayName());
   }

   public boolean isFire() {
      return this.isFireSource;
   }

   public boolean isNoAggro() {
      return this.noAggro;
   }

   public String getMsgId() {
      return this.msgId;
   }

   public DamageSource setScalesWithDifficulty() {
      this.scalesWithDifficulty = true;
      return this;
   }

   public boolean scalesWithDifficulty() {
      return this.scalesWithDifficulty;
   }

   public boolean isMagic() {
      return this.isMagic;
   }

   public DamageSource setMagic() {
      this.isMagic = true;
      return this;
   }

   public boolean isFall() {
      return this.isFall;
   }

   public DamageSource setIsFall() {
      this.isFall = true;
      return this;
   }

   public boolean isCreativePlayer() {
      Entity entity = this.getEntity();
      return entity instanceof Player && ((Player)entity).getAbilities().instabuild;
   }

   @Nullable
   public Vec3 getSourcePosition() {
      return null;
   }
}
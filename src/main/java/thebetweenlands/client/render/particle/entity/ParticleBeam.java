package thebetweenlands.client.render.particle.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.common.block.IParticleCollidable;

import java.util.List;

public class ParticleBeam extends Particle implements ParticleTextureStitcher.IParticleSpriteReceiver {

    private double preMoveY = posY;
    private double preMoveX = posX;
    private double preMoveZ = posZ;
    private boolean colliding = false;

    private ParticleBeam(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i46347_8_, double p_i46347_10_, double p_i46347_12_)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, p_i46347_8_, p_i46347_10_, p_i46347_12_, 1.0F);
    }

    protected ParticleBeam(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i46348_8_, double p_i46348_10_, double p_i46348_12_, float p_i46348_14_) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.motionX = p_i46348_8_;
        this.motionY = p_i46348_10_;
        this.motionZ = p_i46348_12_;
        this.particleScale = 1.5F;
        this.particleMaxAge = 25;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }

        this.motionY += (motionY > 0 ? 0.004D: 0);
        this.move(this.motionX, this.motionY, this.motionZ);

        if (this.posY == this.prevPosY)
        {
            this.motionX *= 1.0D;
            this.motionZ *= 1.0D;
        }

        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    @Override
    public void move(double x, double y, double z) {
        double d0 = y;
        double origX = x;
        double origZ = z;

        if (colliding) {
            setExpired();
            return;
        }

        if (this.canCollide)
        {
            List<AxisAlignedBB> list = this.world.getCollisionBoxes((Entity)null, this.getBoundingBox().expand(x, y, z));

            for (AxisAlignedBB axisalignedbb : list)
            {
                y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
                if (y == 0 && preMoveY != posY) {
                    colliding = true;
                    Vec3d p = axisalignedbb.getCenter();
                    BlockPos pos = new BlockPos(Math.floor(p.x), Math.floor(p.y), Math.floor(p.z));
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof IParticleCollidable) {
                        RayTraceResult ray = axisalignedbb.calculateIntercept(axisalignedbb.getCenter(), getBoundingBox().getCenter());
                        ((IParticleCollidable) state.getBlock()).onParticleCollidedWithBlock(world, pos, state, ray.sideHit, this);
                    }
                }
            }

            this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));

            for (AxisAlignedBB axisalignedbb1 : list)
            {
                x = axisalignedbb1.calculateXOffset(this.getBoundingBox(), x);
                if (x != origX && preMoveX != posX) {
                    colliding = true;
                    Vec3d p = axisalignedbb1.getCenter();
                    BlockPos pos = new BlockPos(Math.floor(p.x), Math.floor(p.y), Math.floor(p.z));
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof IParticleCollidable) {
                        RayTraceResult ray = axisalignedbb1.calculateIntercept(axisalignedbb1.getCenter(), getBoundingBox().getCenter());
                        ((IParticleCollidable) state.getBlock()).onParticleCollidedWithBlock(world, pos, state, ray.sideHit, this);
                    }
                }
            }

            this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));

            for (AxisAlignedBB axisalignedbb2 : list)
            {
                z = axisalignedbb2.calculateZOffset(this.getBoundingBox(), z);
                if (z != origZ && preMoveZ != posZ) {
                    colliding = true;
                    Vec3d p = axisalignedbb2.getCenter();
                    BlockPos pos = new BlockPos(Math.floor(p.x), Math.floor(p.y), Math.floor(p.z));
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof IParticleCollidable) {
                        RayTraceResult ray = axisalignedbb2.calculateIntercept(axisalignedbb2.getCenter(), getBoundingBox().getCenter());
                        ((IParticleCollidable) state.getBlock()).onParticleCollidedWithBlock(world, pos, state, ray.sideHit, this);
                    }
                }
            }

            this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));
        }
        else
        {
            this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
        }

        preMoveX = posX;
        preMoveY = posY;
        preMoveZ = posZ;
        this.resetPositionToBB();
        this.onGround = d0 != y && d0 < 0.0D;

        if (origX != x)
        {
            this.motionX = 0.0D;
        }

        if (origZ != z)
        {
            this.motionZ = 0.0D;
        }
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    public static final class Factory extends ParticleFactory<Factory, ParticleBeam> {
        public Factory() {
            super(ParticleBeam.class, ParticleTextureStitcher.create(ParticleBeam.class, new ResourceLocation("thebetweenlands:particle/beam")));
        }

        @Override
        protected ParticleBeam createParticle(ImmutableParticleArgs args) {
            return new ParticleBeam(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ);
        }
    }
}

package thebetweenlands.client.model;

import net.minecraft.util.MathHelper;

import java.util.Random;

/**
 * This is a timer that can be used to easily animate models with intermittent poses. You have to
 * set the number of ticks between poses, a number of ticks that represents the interval of the pose
 * change, increase or decrease the timer, and get the percentage using a specific function.
 *
 * @author RafaMv
 */

public class IntermittentAnimation
{
    /**
     * It is the random used to randomize the movement.
     */
    Random rand = new Random();
    /**
     * It is the timer used to animate.
     */
    private double timer;
    /**
     * It is the limit time, the maximum value that the timer can be. I
     * represents the duration of the animation.
     */
    private double duration;
    /**
     * It is a boolean that shows if the animation is already in the new pose.
     */
    private boolean runInterval;
    /**
     * It is an inverter for the timer.
     */
    private int inverter;
    /**
     * It is the timer used for the interval.
     */
    private double timerInterval;
    /**
     * It is the interval to return to the first animation.
     */
    private double intervalDuration;
    /**
     * It is the chance to go to the new animation.
     */
    private int goChance;

    public IntermittentAnimation(int d, int i, int g)
    {
        timer = 0;
        duration = (double) d;
        intervalDuration = (double) i;
        runInterval = true;
        goChance = g;
        inverter = -1;
    }

    /**
     * Sets the duration of the animation in ticks. Try values around 50.
     *
     * @param d is the maximum number of ticks that the timer can reach.
     */
    public void setDuration(int d)
    {
        timer = 0;
        duration = (double) d;
    }

    /**
     * Returns the timer of this animation. Useful to save the progress of the animation.
     */
    public double getTimer()
    {
        return timer;
    }

    /**
     * Sets the timer to a specific value.
     *
     * @param time is the number of ticks to be set.
     */
    public void setTimer(int time)
    {
        timer = (double) time;

        if (timer > duration)
        {
            timer = duration;
        }
        else if (timer < 0)
        {
            timer = 0;
        }
    }

    /**
     * Sets the timer to 0.
     */
    public void resetTimer()
    {
        timer = 0;
    }

    /**
     * Increases the timer by 1.
     */
    public void runAnimation()
    {
        if (!runInterval)
        {
            if (timer < duration && timer > 0.0D)
            {
                timer += inverter;
            }
            else
            {
                if (timer >= duration)
                {
                    timer = duration;
                }
                else if (timer <= 0.0D)
                {
                    timer = 0.0D;
                }
                timerInterval = 0.0D;
                runInterval = true;
            }
        }
        else
        {
            if (timerInterval < intervalDuration)
            {
                timerInterval++;
            }
            else
            {
                if (rand.nextInt(goChance) == 0)
                {
                    if (inverter > 0)
                    {
                        inverter = -1;
                    }
                    else
                    {
                        inverter = 1;
                    }
                    timer += inverter;
                    runInterval = false;
                }
            }
        }
    }

    /**
     * Decreases the timer by 1.
     */
    public void stopAnimation()
    {
        if (timer > 0.0D)
        {
            timer--;
        }
        else
        {
            timer = 0.0D;
            runInterval = true;
            timerInterval = 0.0D;
            inverter = 1;
        }
    }

    /**
     * Decreases the timer by a specific value.
     *
     * @param time is the number of ticks to be decreased in the timer
     */
    public void stopAnimation(int time)
    {
        if (timer - time > 0.0D)
        {
            timer -= time;
        }
        else
        {
            timer = 0.0D;
            runInterval = false;
            timerInterval = 0.0D;
            inverter = 1;
        }
    }

    /**
     * Returns a float that represents a fraction of the animation, a value between 0.0F and 1.0F.
     */
    public float getAnimationFraction()
    {
        return (float) (timer / duration);
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using 1/(1 + e^(4-8*x)). It
     * is quite uniform but slow, and needs if statements.
     */
    public float getAnimationProgressSmooth()
    {
        if (timer > 0.0D)
        {
            if (timer < duration)
            {
                return (float) (1.0D / (1.0D + Math.exp(4.0D - 8.0D * (timer / duration))));
            }
            else
            {
                return 1.0F;
            }
        }
        return 0.0F;
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using 1/(1 + e^(6-12*x)). It
     * is quite uniform, but fast.
     */
    public float getAnimationProgressSteep()
    {
        return (float) (1.0D / (1.0D + Math.exp(6.0D - 12.0D * (timer / duration))));
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using a sine function. It is
     * fast in the beginning and slow in the end.
     */
    public float getAnimationProgressSin()
    {
        return MathHelper.sin(1.57079632679F * (float) (timer / duration));
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using a sine function
     * squared. It is very smooth.
     */
    public float getAnimationProgressSinSqrt()
    {
        float result = MathHelper.sin(1.57079632679F * (float) (timer / duration));
        return result * result;
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using a sine function to the
     * power of ten. It is slow in the beginning and fast in the end.
     */
    public float getAnimationProgressSinToTen()
    {
        return (float) Math.pow((double) MathHelper.sin(1.57079632679F * (float) (timer / duration)), 10);
    }

    public float getAnimationProgressSinToTenWithoutReturn()
    {
        if (inverter == -1)
            return MathHelper.sin(1.57079632679F * (float) (timer / duration)) * MathHelper.sin(1.57079632679F * (float) (timer / duration));
        return (float) Math.pow((double) MathHelper.sin(1.57079632679F * (float) (timer / duration)), 10);
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using a sine function to a
     * specific power "i."
     *
     * @param i is the power of the sine function.
     */
    public float getAnimationProgressSinPowerOf(int i)
    {
        return (float) Math.pow((double) MathHelper.sin(1.57079632679F * (float) (timer / duration)), i);
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using x^2 / (x^2 + (1-x)^2).
     * It is smooth.
     */
    public float getAnimationProgressPoly2()
    {
        float x = (float) (timer / duration);
        float x2 = x * x;
        return x2 / (x2 + (1 - x) * (1 - x));
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using x^3 / (x^3 + (1-x)^3).
     * It is steep.
     */
    public float getAnimationProgressPoly3()
    {
        float x = (float) (timer / duration);
        float x3 = x * x * x;
        return x3 / (x3 + (1 - x) * (1 - x) * (1 - x));
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using x^n / (x^n + (1-x)^n).
     * It is steeper when n increases.
     *
     * @param n is the power of the polynomial function.
     */

    public float getAnimationProgressPolyN(int n)
    {
        double x = timer / duration;
        double xi = Math.pow(x, (double) n);
        return (float) (xi / (xi + Math.pow((1.0D - x), (double) n)));
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. It reaches 1.0F using 0.5 + arctan(PI * (x -
     * 0.5)) / 2.00776964. It is super smooth.
     */
    public float getAnimationProgressArcTan()
    {
        return (float) (0.5F + 0.49806510671F * Math.atan(3.14159265359D * (timer / duration - 0.5D)));
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration of the animation.
     * This value starts at 1.0F and ends at 1.0F.
     * The equation used is 0.5 - 0.5 * cos(2 * PI * x + sin(2 * PI * x)). It is smooth.
     */
    public float getAnimationProgressTemporary()
    {
        float x = 6.28318530718F * (float) (timer / duration);
        return 0.5F - 0.5F * MathHelper.cos(x + MathHelper.sin(x));
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration
     * of the animation. This value starts at 0.0F and ends at 0.0F.
     * The equation used is sin(x * PI + sin(x * PI)). It is fast in the beginning and slow in the end.
     */
    public float getAnimationProgressTemporaryFS()
    {
        float x = 3.14159265359F * (float) (timer / duration);
        return MathHelper.sin(x + MathHelper.sin(x));
    }

    /**
     * Returns a value between 0.0F and 1.0F depending on the timer and duration of the animation.
     * This value starts at 1.0F and ends at 1.0F.
     * The equation used is 0.5 + 0.5 * cos(2 PI * x + sin(2 * PI * x)). It is smooth.
     */
    public float getAnimationProgressTemporaryInvesed()
    {
        float x = 6.28318530718F * (float) (timer / duration);
        return 0.5F + 0.5F * MathHelper.cos(x + MathHelper.sin(x));
    }
}

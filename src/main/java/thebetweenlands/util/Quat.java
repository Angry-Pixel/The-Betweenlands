package thebetweenlands.util;

public class Quat {
    private final static double EPS = 1e-12;

    public double x;

    public double y;

    public double z;

    public double w;

    public Quat(Quat q) {
        this(q.x, q.y, q.z, q.w);
    }

    public Quat(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void normalize() {
        double mag = 1 / Math.sqrt(x * x + y * y + z * z + w * w);
        x *= mag;
        y *= mag;
        z *= mag;
        w *= mag;
    }

    public void interpolate(Quat q, double t) {
        // From "Advanced Animation and Rendering Techniques"
        // by Watt and Watt pg. 364, function as implemented appeared to be
        // incorrect. Fails to choose the same quaternion for the double
        // covering. Resulting in change of direction for rotations.
        // Fixed function to negate the first quaternion in the case that the
        // dot product of q1 and this is negative. Second case was not needed.
        double dot, s1, s2, om, sinom;
        dot = x * q.x + y * q.y + z * q.z + w * q.w;
        if (dot < 0) {
            q.x = -q.x;
            q.y = -q.y;
            q.z = -q.z;
            q.w = -q.w;
            dot = -dot;
        }
        if (1 - dot > EPS) {
            om = Math.acos(dot);
            sinom = Math.sin(om);
            s1 = Math.sin((1 - t) * om) / sinom;
            s2 = Math.sin(t * om) / sinom;
        } else {
            s1 = 1 - t;
            s2 = t;
        }
        w = s1 * w + s2 * q.w;
        x = s1 * x + s2 * q.x;
        y = s1 * y + s2 * q.y;
        z = s1 * z + s2 * q.z;
    }

    public static Quat fromAxisAngle(double x, double y, double z, double angle) {
        double sin = Math.sin(angle / 2);
        return new Quat(x * sin, y * sin, z * sin, Math.cos(angle / 2));
    }
}

/*
 * Copyright 1997-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */

package thebetweenlands.utils.vectormath;

public abstract class Tuple3f {
	public float x;

	public float y;

	public float z;

	public Tuple3f() {
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}

	public Tuple3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Tuple3f(Tuple3f t1) {
		x = t1.x;
		y = t1.y;
		z = t1.z;
	}

	public boolean equals(Tuple3f t1) {
		try {
			return x == t1.x && y == t1.y && z == t1.z;
		} catch (NullPointerException e2) {
			return false;
		}
	}

	public final void interpolate(Tuple3f t1, float alpha) {
		interpolate(t1, alpha, false);
	}

	public final void interpolate(Tuple3f t1, float alpha, boolean angle) {
		if (angle) {
			x = lerpa(x, t1.x, alpha);
			y = lerpa(y, t1.y, alpha);
			z = lerpa(z, t1.z, alpha);
		} else {
			x = (1 - alpha) * x + alpha * t1.x;
			y = (1 - alpha) * y + alpha * t1.y;
			z = (1 - alpha) * z + alpha * t1.z;
		}
	}

	private final float lerpa(float start, float end, float amount) {
		float difference = Math.abs(end - start);
		if (difference > Math.PI) {
			if (end > start) {
				start += 2 * Math.PI;
			} else {
				end += 2 * Math.PI;
			}
		}

		float value = start + (end - start) * amount;

		float rangeZero = (float) (2 * Math.PI);

		if (value >= 0 && value <= 2 * Math.PI) {
			return value;
		}

		return value % rangeZero;
	}

	public final void sub(Tuple3f t1) {
		x -= t1.x;
		y -= t1.y;
		z -= t1.z;
	}
}

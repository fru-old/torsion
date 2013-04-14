package com.github.fru.torsion.main;

@SuppressWarnings("unused")
public class A {

	public Object t;
	
	public Object[] test(Object[] o){
		this.test(null);
		int i = 0;
		Object obj = String.CASE_INSENSITIVE_ORDER;
		return null;
	}

	public int first(int i) {
		
		A o = this;
		int x = i;
		int y = 45;
		
		long z = 456L;
		
		String test = "teststring";

		while (true) {
			if (y > 0) {
				if (x > 0) {
					x++;
				}
				y++;
			}
			if (y >= 1) {
				y++;
				if (x > 0) break;
				return y;
			}

			x++;
		}
		y++;
		return y;
	}

	public static void second(long i) {
		// System.out.println("test");
		// Math.abs(345);

		while (i > 45)
			if (56 > i) {
				int j = 0;
				i = j;
			}

	}
	
}

package com.github.fru.torsion.example;

public class A {
	
	public Object t;
	
	public int first(){
		int x = 0;
		int y = 45;
		
		while(true){
			if(y > 0){
				if(x > 0){
					x++;
				}
				y++;
			}
			if(y>=1){
				y++;
				if(x > 0)break;
				return y;
			}
			
			x++;
		}
		y++;
		return y;
	}
	
	public static void second(long i){
		//System.out.println("test");
		//Math.abs(345);
		/*
		while(i > 45)
		if(56>i){
			int j = 0;
			i = j;
		}*/
		
	}
	
}

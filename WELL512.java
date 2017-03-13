package com.gmail.s.birchyboy.AngryChickens;

import java.util.Random;

public class WELL512 extends Random{
	private static final long serialVersionUID = 1L;
	private long[] state;
	private int index;
	
	public WELL512() {
		state = new long[16];
		index = 0;
		
		Random r = new Random();
		
		seed(r.nextInt());
	}
	
	public WELL512(int seed) {
		state = new long[16];
		index = 0;
		
		seed(seed);
	}
	
	private void seed(int seed) {
		if(seed == 0){
			seed--;
		}
		
		for(int i = 0; i < 16; i++) {
			state[i] = (seed + 1) * ((seed + 1) << 2) * i;
		}
	}
	
	@Override
	protected int next(int nbits) {
		long a, b, c, d; 
		a = state[index]; 
		c = state[(index+13)&15]; 
		b = a^c^(a<<16)^(c<<15); 
		c = state[(index+9)&15]; 
		c ^= (c>>11); 
		a = state[index] = b^c; 
		d = a^((a<<5)&0xDA442D24L); 
		index = (index + 15)&15; 
		a = state[index]; 
		state[index] = a^b^d^(a<<2)^(b<<18)^(c<<28); 
		return (int) state[index]; 
	}
	
}

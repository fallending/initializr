package io.spring.initializr.bench;

import java.io.IOException;

public class BinderAllocations {

	public static void main(String[] args) throws IOException {
		while (true) {
			long start = System.currentTimeMillis();
			PropertiesBenchmarkIT.MainState state = new PropertiesBenchmarkIT.MainState();
			state.setup();
			for (int i = 0; i < 5; i++) {
				state.run();
			}
			long end = System.currentTimeMillis();
			System.out.println("Duration: " + (end - start));
		}
	}

}
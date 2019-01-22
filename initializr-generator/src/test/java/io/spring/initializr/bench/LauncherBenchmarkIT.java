/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.initializr.bench;

import java.io.IOException;

import io.spring.initializr.metadata.InitializrProperties;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import jmh.mbr.junit5.Microbenchmark;

/**
 * @author Dave Syer
 *
 */
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 1, time = 1)
@Fork(value = 2, warmups = 0)
@BenchmarkMode(Mode.AverageTime)
@Microbenchmark
public class LauncherBenchmarkIT {

	@Benchmark
	public void auto(MainState state) throws Exception {
		state.isolated();
	}

	@State(Scope.Thread)
	public static class MainState extends LauncherState {

		public MainState() {
			super(Application.class);
		}

		public static enum Profile {

			medium("test-default"), small("test-ssl"), large("large");

			private String profile;

			private Profile(String profile) {
				this.profile = profile;
			}

			@Override
			public String toString() {
				return profile;
			}
		}

		@Param
		private Profile prof = Profile.large;

		@Setup
		public void setup() throws Exception {
			addProperties("spring.profiles.active=" + prof, "spring.main.bannerMode=OFF",
					"spring.main.web-application-type=none");
			super.start();
		}

		@Override
		@TearDown
		public void close() throws IOException {
			super.close();
		}
	}

	@SpringBootConfiguration
	@EnableConfigurationProperties(InitializrProperties.class)
	public static class Application {
	}

}

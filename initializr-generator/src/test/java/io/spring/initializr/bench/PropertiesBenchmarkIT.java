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

import java.util.Properties;

import io.spring.initializr.metadata.InitializrProperties;

import org.openjdk.jmh.annotations.AuxCounters;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

import jmh.mbr.junit5.Microbenchmark;

/**
 * @author Dave Syer
 *
 */
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 1, time = 1)
@Fork(value = 2, warmups = 0)
@Microbenchmark
public class PropertiesBenchmarkIT {

	@Benchmark
	public void auto(MainState state) throws Exception {
		state.run();
	}

	@State(Scope.Thread)
	@AuxCounters
	public static class MainState {

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

		public int size;

		private Properties props;

		@Setup
		public void setup() {
			if (props == null) {
				props = loadProperties(
						new ClassPathResource("application-" + prof + ".yml"));
			}
		}

		public void run() {
			size = props.size();
			InitializrProperties config = load(props);
			assertThat(config).isNotNull();
		}
	}

	private static InitializrProperties load(Properties resource) {
		ConfigurationPropertySource source = new MapConfigurationPropertySource(resource);
		Binder binder = new Binder(source);
		return binder.bind("initializr", InitializrProperties.class).get();
	}

	private static Properties loadProperties(Resource resource) {
		YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
		yamlFactory.setResources(resource);
		yamlFactory.afterPropertiesSet();
		return yamlFactory.getObject();
	}

}

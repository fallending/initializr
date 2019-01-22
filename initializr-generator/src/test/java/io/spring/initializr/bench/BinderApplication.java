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

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Dave Syer
 *
 */
public class BinderApplication {

	public static void main(String[] args) {
		Properties config = loadProperties(
				new ClassPathResource("application-large.yml"));
		long start = System.nanoTime();
		InitializrProperties properties = bind(config);
		long duration = System.nanoTime() - start;
		System.out
				.println("Bound " + properties + " in " + (duration / 1000000.0) + "ms");
	}

	private static InitializrProperties bind(Properties resource) {
		ConfigurationPropertySource source = new MapConfigurationPropertySource(resource);
		Binder binder = new Binder(source);
		return binder.bind("initializr", InitializrProperties.class).get();
	}

	private static Properties loadProperties(Resource resource) {
		YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
		yamlFactory.setResources(resource);
		yamlFactory.afterPropertiesSet();
		Properties properties = yamlFactory.getObject();
		System.out.println(
				"Loaded " + properties.keySet().size() + " properties from " + resource);
		return properties;
	}

}

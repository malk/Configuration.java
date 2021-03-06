package org.zameth.configuration;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;


public class ConfigurationTest {
	@Test
	public void testEmpty() {
		assertThat(
			Configuration.get("PropertyThatDoesNotExistsAtAll"))
			.isEmpty();
	}
	
	@Test
	public void testSys() {
		System.setProperty("test.property", "value");
		assertThat(
			Configuration.get("test.property"))
			.isEqualTo("value");
	}
	
	@Test
	public void testImmutable() {
		System.setProperty("test.property", "value");
		assertThat(
			System.getProperty("test.property"))
			.isEqualTo("value");
		assertThat(
			Configuration.get("test.property"))
			.isEqualTo("value");

		System.setProperty("test.property", "other");
		assertThat(
			System.getProperty("test.property"))
			.isEqualTo("other");
		assertThat(
			Configuration.get("test.property"))
			.isEqualTo("value");
	}
	
	@Test
	public void testFromResourceFile() {
		assertThat(
			Configuration.get("property.in.a.ressource.file"))
			.isEqualTo("12344");
	}
}

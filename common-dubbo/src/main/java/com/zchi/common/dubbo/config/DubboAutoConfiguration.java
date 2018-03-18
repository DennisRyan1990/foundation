package com.zchi.common.dubbo.config;

import com.alibaba.dubbo.common.serialize.support.hessian.Hessian2SerializerFactory;
import com.alibaba.dubbo.config.*;
import com.zchi.common.dubbo.serialize.JdkSerializerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DubboAutoConfiguration
 *
 * <p>
 * dubbo服务自动配置 application.yml 示例如下：
 * *********************************************************
 * dubbo:
 *   application:
 *     name: order-provider
 *     owner: fanksy
 *   registry: （单个注册中心）
 *     address: 172.16.2.1:2185
 *   protocol:
 *     port: 20888
 *   provider:
 *     retries: 2
 *     loadbalance: random
 * ********************************************************
 */
@Configuration
@ConditionalOnProperty(prefix = "dubbo", name = "active", havingValue = "true")
@ConditionalOnClass(ApplicationConfig.class)
public class DubboAutoConfiguration {

	private static final String DEFAULT_REGISTRY_PROTOCOL = "zookeeper";
	private static final String DEFAULT_REGISTRY_CLIENT = "curator";

	@Bean
	@ConfigurationProperties("dubbo.application")
	@ConditionalOnProperty(prefix = "dubbo.application", name = "name")
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		// 解决Hessian不能序列化java.time.LocalDate的问题
		String pattern = "java\\.time.*";
		Hessian2SerializerFactory.SERIALIZER_FACTORY.addFactory(new JdkSerializerFactory(pattern));
		return applicationConfig;
	}

	@Bean
	@ConfigurationProperties("dubbo.protocol")
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName("dubbo");
		return protocolConfig;
	}

	@Bean
	@ConfigurationProperties("dubbo.module")
	@ConditionalOnProperty(prefix = "dubbo.module", name = "name")
	public ModuleConfig moduleConfig(ApplicationConfig applicationConfig) {
		ModuleConfig moduleConfig = new ModuleConfig();
		return moduleConfig;
	}

	@Bean
	@ConfigurationProperties("dubbo.registry")
	@ConditionalOnProperty(prefix = "dubbo.registry", name = "address")
	public RegistryConfig registryConfig() {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setProtocol(DEFAULT_REGISTRY_PROTOCOL);
		registryConfig.setClient(DEFAULT_REGISTRY_CLIENT);
		return registryConfig;
	}

	@Bean
	@ConfigurationProperties("dubbo.provider")
	public ProviderConfig providerConfig() {
		ProviderConfig providerConfig = new ProviderConfig();
		return providerConfig;
	}

	@Bean
	@ConfigurationProperties("dubbo.monitor")
	public MonitorConfig monitorConfig() {
		MonitorConfig monitorConfig = new MonitorConfig();
		return monitorConfig;
	}

	@Bean
	@ConfigurationProperties("dubbo.consumer")
	public ConsumerConfig consumerConfig() {
		ConsumerConfig consumerConfig = new ConsumerConfig();
		return consumerConfig;
	}
}

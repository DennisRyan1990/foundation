package com.zchi.common.conf.mybatis.annotation;

import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MapperScannerRegistrar
 */
public class MapperScannerRegistrar
    implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {

    private static final String MYBATIS_MAPPERBASEPACKAGE_PROPERTY =
        "spring.mybatis.mapperBasePackage";

    private Environment environment;

    private ResourceLoader resourceLoader;

    @Override public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
        BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes
            .fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        // this check is needed in Spring 3.1
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
        if (!Annotation.class.equals(annotationClass)) {
            scanner.setAnnotationClass(annotationClass);
        }

        Class<?> markerInterface = annoAttrs.getClass("markerInterface");
        if (!Class.class.equals(markerInterface)) {
            scanner.setMarkerInterface(markerInterface);
        }

        Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
        if (!BeanNameGenerator.class.equals(generatorClass)) {
            scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
        }

        Class<? extends MapperFactoryBean> mapperFactoryBeanClass =
            annoAttrs.getClass("factoryBean");
        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
            scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
        }

        scanner.setSqlSessionTemplateBeanName(annoAttrs.getString("sqlSessionTemplateRef"));
        scanner.setSqlSessionFactoryBeanName(annoAttrs.getString("sqlSessionFactoryRef"));

        List<String> basePackages = new ArrayList<String>();
        for (String pkg : annoAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        scanner.registerFilters();
        try {
            String mapperBasePackage = environment.getProperty(MYBATIS_MAPPERBASEPACKAGE_PROPERTY);
            if(StringUtils.hasLength(mapperBasePackage)) {
                basePackages.addAll(Arrays.asList(StringUtils
                    .tokenizeToStringArray(mapperBasePackage,
                        ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS)));
            }
        } catch (Throwable e) {

        }
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }
    @Override public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}

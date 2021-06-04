package com.setge.dddpost.commons.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }


  @Bean
  public Docker api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.setge.dddpost"))
//                .paths(PathSelectors.ant("/api/**"))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo());
  }

  @Bean
  public LinkDiscoverers discoverer() {
    List<LinkDiscoverer> plugin = new ArrayList<>();
    plugin.add(new CollectionJsonLinkDiscoverer());
    return new LinkDiscoverers(SimplePluginRegistry.create(plugin));
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("주문시스템 Rest Api")
        .description("API 상세소개 및 사용법")
        .version("2.9.2")
        .build();
  }

}

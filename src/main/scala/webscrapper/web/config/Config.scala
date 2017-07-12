package webscrapper.web.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = Array("webscrapper.web"))
class Config extends WebMvcConfigurerAdapter {
  override def addResourceHandlers(registry: ResourceHandlerRegistry) = {
    registry.addResourceHandler("/css/**").addResourceLocations("/css/")
    registry.addResourceHandler("/img/**").addResourceLocations("/img/")
    registry.addResourceHandler("/js/**").addResourceLocations("/js/")
    registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/")
    registry.addResourceHandler("/plugins/**").addResourceLocations("/plugins/")
    registry.addResourceHandler("/font-awesome/**").addResourceLocations("/font-awesome/")
  }

  @Bean
  def freemarkerViewResolver = {
    val resolver = new FreeMarkerViewResolver()
    resolver.setCache(true);
    resolver.setPrefix("");
    resolver.setSuffix(".ftl");
    resolver.setRequestContextAttribute("rc");
    resolver.setContentType("text/html;charset=UTF-8");
    resolver
  }

  @Bean
  def freemarkerConfig = {
    val freeMarkerConfigurer = new FreeMarkerConfigurer
    freeMarkerConfigurer.setTemplateLoaderPath("/WEB-INF/pages/")
    freeMarkerConfigurer.setDefaultEncoding("UTF-8")
    freeMarkerConfigurer
  }
}

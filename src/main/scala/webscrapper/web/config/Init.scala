package webscrapper.web.config

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

class Init extends WebApplicationInitializer {

  override def onStartup(container: ServletContext) {
    val ctx = new AnnotationConfigWebApplicationContext
    ctx.register(classOf[Config])
    ctx.setServletContext(container)
    val servlet = container.addServlet("dispatcher", new DispatcherServlet(ctx))
    servlet.setLoadOnStartup(1)
    servlet.addMapping("/")
  }
}
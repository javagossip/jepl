package io.github.jepl;

import io.github.jepl.annotation.Extension;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class ExtensionLoader implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> extensions = applicationContext.getBeansWithAnnotation(Extension.class);
        if (CollectionUtils.isEmpty(extensions)) {
            return;
        }
        extensions.values().forEach(ExtensionRegistry::registerExtension);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

package de.serdioa.spring.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;


public final class PropertiesUtil {

    private PropertiesUtil() {
        // A private constructor prevents instantiation of this class.
        // Only static methods are publicly available.
    }


    public static Map<String, String> sub(Properties props, String prefix) {
        return new SubProperties(props, prefix);
    }


    public static Map<String, String> sub(Map<String, String> map, String prefix) {
        return new SubMap(map, prefix);
    }


    public static Map<String, String> sub(Iterable<PropertySource<?>> props, String prefix) {
        List<Map<String, String>> subProps = new ArrayList<>();

        for (PropertySource prop : props) {
            if (prop instanceof EnumerablePropertySource) {
                Map<String, String> subProp = new SubEnumerablePropertySource((EnumerablePropertySource) prop, prefix);
                subProps.add(subProp);
            }
        }

        return new CompositeProperties(subProps);
    }


    public static Map<String, String> sub(ApplicationContext ctx, String prefix) {
        Environment env = ctx.getEnvironment();
        if (env instanceof ConfigurableEnvironment) {
            Iterable<PropertySource<?>> propertySources = ((ConfigurableEnvironment) env).getPropertySources();
            return sub(propertySources, prefix);
        } else {
            return Collections.emptyMap();
        }
    }
}

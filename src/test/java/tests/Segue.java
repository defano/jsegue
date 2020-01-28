package tests;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.defano.jsegue.AnimatedSegue;

public class Segue {

	public static Set<Class<? extends AnimatedSegue>> classes() {
		Reflections reflections = new Reflections("com.defano.jsegue.renderers");
		return reflections.getSubTypesOf(AnimatedSegue.class).stream()
				.filter(c -> !Modifier.isAbstract(c.getModifiers())).collect(Collectors.toSet());
	}

	public static List<String> names() {
		return classes().stream().filter(c -> !Modifier.isAbstract(c.getModifiers())).map(Class::getSimpleName)
				.collect(Collectors.toList());
	}

	public static Class<? extends AnimatedSegue> classNamed(String name) {
		return classes().stream().filter(c -> c.getSimpleName().equalsIgnoreCase(name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No such renderer: " + name));
	}
}

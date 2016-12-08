
package com.jfixby.redreporter.glassfish;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class CustomClassLoader extends ClassLoader {
	private final ChildClassLoader childClassLoader;

	public CustomClassLoader (final List<URL> classpath) {
		super(Thread.currentThread().getContextClassLoader());
		final URL[] urls = classpath.toArray(new URL[classpath.size()]);
		this.childClassLoader = new ChildClassLoader(urls, new DetectClass(this.getParent()));
	}

	@Override
	protected synchronized Class<?> loadClass (final String name, final boolean resolve) throws ClassNotFoundException {
		try {
			return this.childClassLoader.findClass(name);
		} catch (final ClassNotFoundException e) {
			return super.loadClass(name, resolve);
		}
	}

	private static class ChildClassLoader extends URLClassLoader {
		private final DetectClass realParent;

		public ChildClassLoader (final URL[] urls, final DetectClass realParent) {
			super(urls, null);
			this.realParent = realParent;
		}

		@Override
		public Class<?> findClass (final String name) throws ClassNotFoundException {
			try {
				final Class<?> loaded = super.findLoadedClass(name);
				if (loaded != null) {
					return loaded;
				}
				return super.findClass(name);
			} catch (final ClassNotFoundException e) {
				return this.realParent.loadClass(name);
			}
		}
	}

	private static class DetectClass extends ClassLoader {
		public DetectClass (final ClassLoader parent) {
			super(parent);
		}

		@Override
		public Class<?> findClass (final String name) throws ClassNotFoundException {
			return super.findClass(name);
		}
	}
}

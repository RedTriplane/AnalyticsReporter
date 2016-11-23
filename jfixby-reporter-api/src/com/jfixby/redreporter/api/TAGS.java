
package com.jfixby.redreporter.api;

public class TAGS {

	public static class System {
		public static final String OS_NAME = "os.name";
	}

	public static class Java {
		public static final String Version = "java.version";

	}

	public static class Desktop {
		public static class Screen {
			public static final String WIDTH = "desktop.screen.width";
			public static final String HEIGHT = "desktop.screen.height";
		}
	}

	public static class Android {

		public static final String Brand = "android.brand";
		public static final String Model = "android.model";
		public static final String Host = "android.host";
		public static final String Release = "android.release";

		public static class Display {
			public static final String WIDTH = "android.display.width";
			public static final String HEIGHT = "android.display.height";
		}

	}

}

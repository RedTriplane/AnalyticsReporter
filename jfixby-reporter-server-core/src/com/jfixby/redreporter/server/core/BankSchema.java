
package com.jfixby.redreporter.server.core;

public class BankSchema {

	public static class INSTALLS {
		public static final String TableName = "installs";

		public static final String id = "id";
		public static final String token = "token";
		public static final String timestamp = "timestamp";

	}

	public static class SYSTEM_INFO {
		public static final String TableName = "system_info";

		public static final String install_id = "install_id";
		public static final String parameter_name = "parameter_name";
		public static final String parameter_value = "parameter_value";

	}

}

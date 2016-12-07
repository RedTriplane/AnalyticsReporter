
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

	public static class SERIALIZED_REPORTS {
		public static final String TableName = "srlzd_reports";

		public static final String install_id = "install_id";
		public static final String report_id = "report_id";
		public static final String received_timestamp = "received_timestamp";
		public static final String written_timestamp = "written_timestamp";
		public static final String sent_timestamp = "sent_timestamp";
		public static final String report_version = "report_version";
		public static final String file_id = "file_id";
		public static final String file_path = "file_path";

	}

}

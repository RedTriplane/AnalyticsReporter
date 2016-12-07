
package com.jfixby.redreporter.server.core.run;

import java.io.IOException;

import com.jfixby.amazon.aws.s3.AWSS3FileSystem;
import com.jfixby.amazon.aws.s3.AWSS3FileSystemConfig;
import com.jfixby.cmns.api.log.L;
import com.jfixby.red.desktop.DesktopSetup;
import com.jfixby.redreporter.server.credentials.CONFIG;

public class TestS3Bucket {

	public static void main (final String[] args) throws IOException {
		DesktopSetup.deploy();

		final AWSS3FileSystemConfig specs = new AWSS3FileSystemConfig();
		specs.setAccessKeyID(CONFIG.S3_RR1_ACCESS_KEY);
		specs.setSecretKeyID(CONFIG.S3_RR1_SECRET_KEY);
		specs.setBucketName(CONFIG.S3_BUCKET_NAME);//
		final AWSS3FileSystem S3 = new AWSS3FileSystem(specs);
		try {
			S3.ROOT().listAllChildren().print("remote");
		} catch (final Throwable e) {
			L.e("FAIL");
			e.printStackTrace();
		}
	}

}

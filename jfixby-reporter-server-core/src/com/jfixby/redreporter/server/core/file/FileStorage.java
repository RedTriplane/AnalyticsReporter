
package com.jfixby.redreporter.server.core.file;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfixby.amazon.aws.s3.AWSS3FileSystem;
import com.jfixby.amazon.aws.s3.AWSS3FileSystemConfig;
import com.jfixby.amazon.aws.s3.S3CredentialsProvider;
import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.redreporter.server.api.ReportStoreArguments;

public class FileStorage {

	private final S3CredentialsProvider credentialsProvider;
	private final String bucketName;
	private File root;

	public FileStorage (final FileStorageConfig fsConfig) {
		this.credentialsProvider = fsConfig.getS3CredentialsProvider();
		this.bucketName = Debug.checkNull("getBucketName()", fsConfig.getBucketName());
	}

	boolean deploy () throws IOException {
		final String accessKeyID = this.credentialsProvider.getAccessKeyID();
		final String secretKeyID = this.credentialsProvider.getSecretKeyID();
		if (accessKeyID == null || secretKeyID == null) {
			return false;
		}

		final AWSS3FileSystemConfig specs = new AWSS3FileSystemConfig();
		specs.setAccessKeyID(accessKeyID);
		specs.setSecretKeyID(secretKeyID);
		specs.setBucketName(this.bucketName);//
		final AWSS3FileSystem S3 = new AWSS3FileSystem(specs);

		S3.ROOT().listDirectChildren();
		this.root = S3.ROOT();
		return true;

	}

	public File storeReport (final ReportStoreArguments store_args) throws IOException {
		if (this.root == null) {
			this.deploy();
		}
		final Long installID = store_args.getInstallID();
		final File installFolder = this.root.child("id-" + installID);
		final Long received = store_args.getReceivedTimeStamp();

		final Date date = new Date(received);
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// //2013/10/15 16:16:39
		final String dateText = dateFormat.format(date);
		final File dayFolder = installFolder.child(dateText);

		final SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH-mm-ss");
		final String logFileName = dateFormat2.format(date) + "-" + store_args.getFileID();
		final File logFile = dayFolder.child(logFileName);
		L.d("writing report", logFile);

		final byte[] bytes = store_args.getReportData();
		logFile.writeBytes(bytes);

		return logFile;

	}

}

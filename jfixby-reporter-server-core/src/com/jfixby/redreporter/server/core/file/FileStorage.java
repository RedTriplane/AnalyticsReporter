
package com.jfixby.redreporter.server.core.file;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfixby.cmns.api.debug.Debug;
import com.jfixby.cmns.api.file.File;
import com.jfixby.cmns.api.log.L;
import com.jfixby.cmns.aws.api.AWS;
import com.jfixby.cmns.aws.api.S3;
import com.jfixby.cmns.aws.api.S3FileSystem;
import com.jfixby.cmns.aws.api.S3FileSystemConfig;
import com.jfixby.cmns.aws.api.s3.S3CredentialsProvider;
import com.jfixby.redreporter.server.api.ReportStoreArguments;
import com.jfixby.redreporter.server.api.STORAGE_STATE;

public class FileStorage {

	private final S3CredentialsProvider credentialsProvider;
	private final String bucketName;
	private File root;

	public FileStorage (final FileStorageConfig fsConfig) {
		this.credentialsProvider = fsConfig.getS3CredentialsProvider();
		this.bucketName = Debug.checkNull("getBucketName()", fsConfig.getBucketName());
	}

	void deploy () throws IOException {
		final String accessKeyID = this.credentialsProvider.getAccessKeyID();
		final String secretKeyID = this.credentialsProvider.getSecretKeyID();
		if (accessKeyID == null || secretKeyID == null) {
			throw new IOException("Missing accessKeyID.secretKeyID for S3 Bucket");
		}

		final S3 S3 = AWS.getS3();
		final S3FileSystemConfig aws_specs = S3.newFileSystemConfig();
		aws_specs.setAccessKeyID(accessKeyID);
		aws_specs.setSecretKeyID(secretKeyID);
		aws_specs.setBucketName(this.bucketName);//
		final S3FileSystem fileSystem = AWS.getS3().newFileSystem(aws_specs);
		try {
			fileSystem.ROOT().listDirectChildren();
		} catch (final Throwable e) {
			throw new IOException(e);
		}
		this.root = fileSystem.ROOT();
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
// dayFolder.makeFolder();
		final SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH-mm-ss");
		final String logFileName = dateFormat2.format(date) + "-" + store_args.getFileID();
		final File logFile = dayFolder.child(logFileName);
		L.d("writing report", logFile);

		final byte[] bytes = store_args.getReportData();
		logFile.writeBytes(bytes);

		return logFile;

	}

	public STORAGE_STATE getState () {

		try {
			this.deploy();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		if (this.root == null) {
			return STORAGE_STATE.ERROR;
		}
		return STORAGE_STATE.OK;
	}

}

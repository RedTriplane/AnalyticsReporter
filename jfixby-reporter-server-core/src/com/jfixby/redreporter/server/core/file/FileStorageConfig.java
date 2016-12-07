
package com.jfixby.redreporter.server.core.file;

import com.jfixby.amazon.aws.s3.S3CredentialsProvider;

public class FileStorageConfig {
	S3CredentialsProvider s3CredentialsProvider;
	private String bucketName;

	public void setS3CredentialsProvider (final S3CredentialsProvider s3CredentialsProvider) {
		this.s3CredentialsProvider = s3CredentialsProvider;
	}

	public S3CredentialsProvider getS3CredentialsProvider () {
		return this.s3CredentialsProvider;
	}

	public String getBucketName () {
		return this.bucketName;
	}

	public void setBucketName (final String bucketName) {
		this.bucketName = bucketName;
	}
}

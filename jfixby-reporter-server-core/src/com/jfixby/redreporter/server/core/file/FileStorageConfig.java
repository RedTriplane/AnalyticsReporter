
package com.jfixby.redreporter.server.core.file;

import com.jfixby.scarabei.aws.api.AWSCredentialsProvider;

public class FileStorageConfig {
	AWSCredentialsProvider AWSCredentialsProvider;
	private String bucketName;

	public void setAWSCredentialsProvider (final AWSCredentialsProvider AWSCredentialsProvider) {
		this.AWSCredentialsProvider = AWSCredentialsProvider;
	}

	public AWSCredentialsProvider getAWSCredentialsProvider () {
		return this.AWSCredentialsProvider;
	}

	public String getBucketName () {
		return this.bucketName;
	}

	public void setBucketName (final String bucketName) {
		this.bucketName = bucketName;
	}
}

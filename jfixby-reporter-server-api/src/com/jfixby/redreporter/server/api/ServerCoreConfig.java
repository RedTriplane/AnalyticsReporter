
package com.jfixby.redreporter.server.api;

import com.jfixby.scarabei.db.api.DataBase;

public interface ServerCoreConfig {

	void setDataBase (DataBase mySQL);

	void setS3BucketName (String bucketName);

	DataBase getDataBase ();

	String getBucketName ();

}

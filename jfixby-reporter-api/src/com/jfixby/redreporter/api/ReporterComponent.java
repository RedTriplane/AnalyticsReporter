
package com.jfixby.redreporter.api;

public interface ReporterComponent {

	ReporterLogger getLogger ();

	ReporterErrorComponent getErr ();

}

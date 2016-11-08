package fr.inria.diverse.torgen.inspectorguidget.xp;

import java.util.Collections;
import java.util.List;

public class BasexLauncher extends XPLauncher {

	public static void main(String args[]) {
		// git clone git@github.com:BaseXdb/basex.git
		// git checkout 8.5.3
		new BasexLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Collections.singletonList("/media/data/dev/repoAnalysisBlob/basex/basex-core/src/main/java");
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/home/foo/.m2/repository/xml-resolver/xml-resolver/1.2/xml-resolver-1.2.jar",
		"/home/foo/.m2/repository/jline/jline/2.13/jline-2.13.jar",
		"/home/foo/.m2/repository/org/fusesource/jansi/jansi/1.11/jansi-1.11.jar",
		"/home/foo/.m2/repository/org/apache/lucene-stemmers/3.4.0/lucene-stemmers-3.4.0.jar",
		"/home/foo/.m2/repository/com/thaiopensource/jing/20091111/jing-20091111.jar",
		"/home/foo/.m2/repository/org/ccil/cowan/tagsoup/tagsoup/1.2.1/tagsoup-1.2.1.jar",
		"/home/foo/.m2/repository/jp/sourceforge/igo/igo/0.4.3/igo-0.4.3.jar",
		"/home/foo/.m2/repository/commons-codec/commons-codec/1.4/commons-codec-1.4.jar",
		"/home/foo/.m2/repository/javax/xml/xquery/xqj-api/1.0/xqj-api-1.0.jar",
		"/home/foo/.m2/repository/org/xmldb/xmldb-api/1.0/xmldb-api-1.0.jar",
		"/home/foo/.m2/repository/commons-io/commons-io/1.4/commons-io-1.4.jar",
		"/home/foo/.m2/repository/com/xqj2/xqj2/0.2.0/xqj2-0.2.0.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/orbit/javax.servlet/3.0.0.v201112011016/javax.servlet-3.0.0.v201112011016.jar",
		"/home/foo/.m2/repository/com/ettrema/milton-api/1.8.1.4/milton-api-1.8.1.4.jar",
		"/home/foo/.m2/repository/commons-fileupload/commons-fileupload/1.3.1/commons-fileupload-1.3.1.jar",
		"/home/foo/.m2/repository/com/vividsolutions/jts/1.13/jts-1.13.jar",
		"/home/foo/.m2/repository/org/slf4j/slf4j-simple/1.7.13/slf4j-simple-1.7.13.jar",
		"/home/foo/.m2/repository/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-server/8.1.18.v20150929/jetty-server-8.1.18.v20150929.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-http/8.1.18.v20150929/jetty-http-8.1.18.v20150929.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-continuation/8.1.18.v20150929/jetty-continuation-8.1.18.v20150929.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-servlet/8.1.18.v20150929/jetty-servlet-8.1.18.v20150929.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-io/8.1.18.v20150929/jetty-io-8.1.18.v20150929.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-webapp/8.1.18.v20150929/jetty-webapp-8.1.18.v20150929.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-security/8.1.18.v20150929/jetty-security-8.1.18.v20150929.jar",
		"/home/foo/.m2/repository/org/jdom/jdom/1.1/jdom-1.1.jar",
		"/home/foo/.m2/repository/javax/xml/xquery/xqj-api/1.0/xqj-api-1.0.jar",
		"/home/foo/.m2/repository/net/xqj/basex-xqj/1.7.0/basex-xqj-1.7.0.jar",
		"/home/foo/.m2/repository/org/basex/basex/8.5.3/basex-8.5.3.jar",
		"/home/foo/.m2/repository/eu/medsea/mimeutil/mime-util/2.1.3/mime-util-2.1.3.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-util/8.1.18.v20150929/jetty-util-8.1.18.v20150929.jar",
		"/home/foo/.m2/repository/org/eclipse/jetty/jetty-xml/8.1.18.v20150929/jetty-xml-8.1.18.v20150929.jar"
		};
	}

	@Override
	protected int getCompilianceLevel() {
		return 7;
	}

	@Override
	protected boolean usingLambda() {
		return false;
	}

	@Override
	protected String getOutputFolder() {
		return "/media/data/dev/refactor/basex-refactored/src";
	}
}

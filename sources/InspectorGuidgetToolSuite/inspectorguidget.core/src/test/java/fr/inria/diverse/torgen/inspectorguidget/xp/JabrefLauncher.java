package fr.inria.diverse.torgen.inspectorguidget.xp;

import fr.inria.diverse.torgen.inspectorguidget.analyser.Command;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class JabrefLauncher extends XPLauncher {

	public static void main(String args[]) {
		// git clone ...
		// git checkout v3.6
		new JabrefLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Arrays.asList("/media/data/dev/repoAnalysisBlob/jabref/src/main/java",
			"/media/data/dev/repoAnalysisBlob/jabref/src/main/gen");
	}


	@Override
	protected @NotNull List<Command> filterBlobsToRefactor() {
		return blobAnalyser.getBlobs().entrySet().stream().
			filter(e ->
				(e.getKey().getSimpleName().equals("valueChanged") && e.getKey().getPosition().getLine()==329) ||
					(e.getKey().getSimpleName().equals("actionPerformed") && e.getKey().getPosition().getLine()==361) ||
					(e.getKey().getSimpleName().equals("actionPerformed") && e.getKey().getPosition().getLine()==321) ||
					(e.getKey().getSimpleName().equals("actionPerformed") && e.getKey().getPosition().getLine()==130)
			).map(e -> e.getValue()).flatMap(s -> s.stream()).collect(Collectors.toList());
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/home/foo/.gradle/caches/modules-2/files-2.1/com.jgoodies/jgoodies-common/1.8.1/dffc159cf71bde5dcbb65916305684f6b43d45b1/jgoodies-common-1.8.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.jgoodies/jgoodies-forms/1.9.0/eda960be2f88d47d14a81b742772b07684abc7d0/jgoodies-forms-1.9.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.jgoodies/jgoodies-looks/2.7.0/7679705b2d036267407138983611a4dd3ec9b72c/jgoodies-looks-2.7.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.swinglabs/swingx/1.6.1/a4abf05f5f1d3b020ec4099495dcd1452b615e26/swingx-1.6.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.pdfbox/pdfbox/1.8.12/5491c1dc61748ee106237b9a8b81ca3aa8ef81bf/pdfbox-1.8.12.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.pdfbox/fontbox/1.8.12/272ab4b5d0fd99dce8d03c8b5befd393385d79c2/fontbox-1.8.12.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.pdfbox/jempbox/1.8.12/426450c573c19f6f2c751a7a52c11931b712c9f6/jempbox-1.8.12.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.bouncycastle/bcprov-jdk15on/1.55/935f2e57a00ec2c489cbd2ad830d4a399708f979/bcprov-jdk15on-1.55.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/commons-cli/commons-cli/1.3.1/1303efbc4b181e5a58bf2e967dc156a3132b97c0/commons-cli-1.3.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.openoffice/juh/4.1.2/bbd1b7b49879b5ca34b50053a5929755bcab5063/juh-4.1.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.openoffice/jurt/4.1.2/49fc3e3808c2a6c97831824794d3b1313c12e406/jurt-4.1.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.openoffice/ridl/4.1.2/2b3f48a310c7bdc1526146c0e2bd97c4ce3b8b85/ridl-4.1.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.openoffice/unoil/4.1.2/48921ffdfdf14f47d91d90c5e7c9ec6138941340/unoil-4.1.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.googlecode.java-diff-utils/diffutils/1.3.0/7e060dd5b19431e6d198e91ff670644372f60fbd/diffutils-1.3.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.antlr/antlr-runtime/3.5.2/cd9cd41361c155f3af0f653009dcecb08d8b4afd/antlr-runtime-3.5.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.antlr/antlr4-runtime/4.5.3/2609e36f18f7e8d593cc1cddfb2ac776dc96b8e0/antlr4-runtime-4.5.3.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/mysql/mysql-connector-java/5.1.39/4617fe8dc8f1969ec450984b0b9203bc8b7c8ad5/mysql-connector-java-5.1.39.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.postgresql/postgresql/9.4.1209/7f25826357976ed495184938b6a464acdf0ea3f8/postgresql-9.4.1209.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/net.java.dev.glazedlists/glazedlists_java15/1.9.1/3ec96aff6b7addc9a2f0f82eca7147613142e45/glazedlists_java15-1.9.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.google.guava/guava/19.0/6ce200f6b23222af3d8abb6b6459e6c44f4bb0e9/guava-19.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/commons-logging/commons-logging/1.2/4bfc12adfe4842bf07b657f0369c4cb522955686/commons-logging-1.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-lang3/3.4/5fe28b9518e58819180a43a850fbc0dd24b7c050/commons-lang3-3.4.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.jsoup/jsoup/1.9.2/5e3bda828a80c7a21dfbe2308d1755759c2fd7b4/jsoup-1.9.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.mashape.unirest/unirest-java/1.4.9/778cffcba803dc7d43932266aef4c91f5b6b4dd0/unirest-java-1.4.9.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/info.debatty/java-string-similarity/0.16/ca004007c458e0dafe962965891909ae23c5d37/java-string-similarity-0.16.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-jcl/2.6.2/b7a667640d88e7c4d9f43d0d5445f16de057e215/log4j-jcl-2.6.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-api/2.6.2/bd1b74a5d170686362091c7cf596bbc3adf5c09b/log4j-api-2.6.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.logging.log4j/log4j-core/2.6.2/a91369f655eb1639c6aece5c5eb5108db18306/log4j-core-2.6.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/junit/junit/4.12/2973d150c0dc1fefe998f834810d68f278ea58ec/junit-4.12.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.mockito/mockito-core/1.10.19/e8546f5bef4e061d8dd73895b4e8f40e3fe6effe/mockito-core-1.10.19.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.github.tomakehurst/wiremock/2.1.11/4c77d0741c5ed0a86385bd8f3e3731a84f0253f6/wiremock-2.1.11.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.assertj/assertj-swing-junit/3.4.0/c6e337b6de54d99961ef528a684705a066fd41ef/assertj-swing-junit-3.4.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.jhlabs/filters/2.0.235/af6a2dfefef70f1ab2d7a8d1f8173f67e276b3f4/filters-2.0.235.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.swinglabs/swing-worker/1.1/2392206f318ef3af02f8e8a30b2963c253a70390/swing-worker-1.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpclient/4.5.2/733db77aa8d9b2d68015189df76ab06304406e50/httpclient-4.5.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpasyncclient/4.1.1/dd624f5a0ff43eb5cdf828d9739d3177ee00a5a9/httpasyncclient-4.1.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpmime/4.5.2/22b4c53dd9b6761024258de8f9240c3dce6ea368/httpmime-4.5.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.json/json/20160212/a742e3f85161835b95877478c5dd5b405cefaab9/json-20160212.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/net.jcip/jcip-annotations/1.0/afba4942caaeaf46aab0b976afd57cc7c181467e/jcip-annotations-1.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.hamcrest/hamcrest-core/1.3/42a25dc3219429f0e5d060061f71acb49bf010a0/hamcrest-core-1.3.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.objenesis/objenesis/2.1/87c0ea803b69252868d09308b4618f766f135a96/objenesis-2.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-server/9.2.13.v20150730/5be7d1da0a7abffd142de3091d160717c120b6ab/jetty-server-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-servlet/9.2.13.v20150730/5ad6e38015a97ae9a60b6c2ad744ccfa9cf93a50/jetty-servlet-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-servlets/9.2.13.v20150730/23eb48f1d889d45902e400750460d4cd94d74663/jetty-servlets-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-webapp/9.2.13.v20150730/716b5cdea1e818cd0e36dfea791f620d49bd2d2a/jetty-webapp-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.6.1/892d15011456ea3563319b27bdd612dbc89bb776/jackson-core-2.6.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.6.1/f9661ddd2456d523b9428651c61e34b4ebf79f4e/jackson-annotations-2.6.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.6.1/45c37a03be19f3e0db825fd7814d0bbec40b9e0/jackson-databind-2.6.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.xmlunit/xmlunit-core/2.1.1/94840bd83168c7de36f3779e2514d0bf4ed8c9bc/xmlunit-core-2.1.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.xmlunit/xmlunit-legacy/2.1.1/e4d45154e0cef8334ccb7f3e0b8ebaf2596eb477/xmlunit-legacy-2.1.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.jayway.jsonpath/json-path/2.0.0/26b8555596b3fb9652c1ffe193fa9123945b32cc/json-path-2.0.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.12/8e20852d05222dc286bf1c71d78d0531e177c317/slf4j-api-1.7.12.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/net.sf.jopt-simple/jopt-simple/4.9/ee9e9eaa0a35360dcfeac129ff4923215fd65904/jopt-simple-4.9.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/com.flipkart.zjsonpatch/zjsonpatch/0.2.1/f3f67d52dbf2ca6edc2ae0b3ae53488110e848c9/zjsonpatch-0.2.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.assertj/assertj-swing/3.4.0/9465757e914b66ba466e8b7f54630558fb401f40/assertj-swing-3.4.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.easytesting/fest-reflect/1.4.1/2b92d5275e92a49e16c7ce6bd7e46b9080db0530/fest-reflect-1.4.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpcore/4.4.4/b31526a230871fbe285fbcbe2813f9c0839ae9b0/httpcore-4.4.4.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.httpcomponents/httpcore-nio/4.4.4/16badfc2d99db264c486ba8c57ae577301a58bd9/httpcore-nio-4.4.4.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/javax.servlet/javax.servlet-api/3.1.0/3cd63d075497751784b2fa84be59432f4905bf7c/javax.servlet-api-3.1.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-http/9.2.13.v20150730/23a745d9177ef67ef53cc46b9b70c5870082efc2/jetty-http-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-io/9.2.13.v20150730/7a351e6a1b63dfd56b6632623f7ca2793ffb67ad/jetty-io-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-security/9.2.13.v20150730/cc7c7f27ec4cc279253be1675d9e47e58b995943/jetty-security-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-continuation/9.2.13.v20150730/f6bd4e6871ecd0a5e7a5e5addcea160cd73f81bb/jetty-continuation-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-util/9.2.13.v20150730/c101476360a7cdd0670462de04053507d5e70c97/jetty-util-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.eclipse.jetty/jetty-xml/9.2.13.v20150730/9e17bdfb8c25d0cd377960326b79379df3181776/jetty-xml-9.2.13.v20150730.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/net.minidev/json-smart/2.1.1/922d12fb1f394e2b6999ae0f7936ab13f4dffb81/json-smart-2.1.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-collections4/4.0/da217367fd25e88df52ba79e47658d4cf928b0d1/commons-collections4-4.0.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.assertj/assertj-core/3.5.1/5d1b52563000e5f1ce3b83edf0592f9346f2d67d/assertj-core-3.5.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.easytesting/fest-util/1.2.5/c4a8d7305b23b8d043be12c979813b096df11f44/fest-util-1.2.5.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/net.minidev/asm/1.0.2/63900a15f524db0b8c4fb2d9e24c0cb179842ea5/asm-1.0.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/asm/asm/3.3.1/1d5f20b4ea675e6fab6ab79f1cd60ec268ddc015/asm-3.3.1.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/commons-codec/commons-codec/1.9/9ce04e34240f674bc72680f8b843b1457383161a/commons-codec-1.9.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.openjdk.jmh/jmh-core/1.11.3/128e449951e3337dccf2e355fe396481f56d081e/jmh-core-1.11.3.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.openjdk.jmh/jmh-generator-bytecode/1.11.3/72fa292baf63bcd96486cf80ec2aa965b1fbab43/jmh-generator-bytecode-1.11.3.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/net.sf.jopt-simple/jopt-simple/4.6/306816fb57cf94f108a43c95731b08934dcae15c/jopt-simple-4.6.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-math3/3.2/ec2544ab27e110d2d431bdad7d538ed509b21e62/commons-math3-3.2.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.openjdk.jmh/jmh-generator-reflection/1.11.3/29ff9d44b8f508eaa51ffcf0d1b7259594593abf/jmh-generator-reflection-1.11.3.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.openjdk.jmh/jmh-generator-asm/1.11.3/6248171c8586d7d8addf592c9c0d93f44a4571b7/jmh-generator-asm-1.11.3.jar",
			"/home/foo/.gradle/caches/modules-2/files-2.1/org.ow2.asm/asm/5.0.3/dcc2193db20e19e1feca8b1240dbbc4e190824fa/asm-5.0.3.jar",
			"/media/data/dev/repoAnalysisBlob/jabref/lib/spin.jar",
			"/media/data/dev/repoAnalysisBlob/jabref/lib/microba.jar",
			"/media/data/dev/repoAnalysisBlob/jabref/lib/AppleJavaExtensions.jar"
		};
	}

	@Override
	protected int getCompilianceLevel() {
		return 8;
	}

	@Override
	protected boolean usingLambda() {
		return true;
	}

	@Override
	protected String getOutputFolder() {
		return "/media/data/dev/refactor/jabref-refactored/src";
	}
}

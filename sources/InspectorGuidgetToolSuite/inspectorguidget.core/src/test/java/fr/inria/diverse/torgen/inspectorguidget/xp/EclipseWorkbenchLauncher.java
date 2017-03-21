package fr.inria.diverse.torgen.inspectorguidget.xp;

import java.util.Collections;
import java.util.List;

public class EclipseWorkbenchLauncher extends XPLauncher {

	/*
	git checkout f1ad167b9b70942f14378e8ed97f5b3cc9821f47
	 */

	public static void main(String args[]) {
		new EclipseWorkbenchLauncher().run();
	}

	@Override
	protected List<String> getInputResoures() {
		return Collections.singletonList("/media/data/dev/repoAnalysisBlob/eclipse/git/eclipse.platform.ui/bundles/org.eclipse.ui.workbench/");
	}

	@Override
	protected String getProjectName() {
		return "org.eclipse.ui.workbench";
	}

	@Override
	protected String[] getSourceClassPath() {
		return new String[]{"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.osgi/3.12.0.v20170302-2050/org.eclipse.osgi-3.12.0.v20170302-2050.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.apache.batik.css/1.8.0.v20170214-1941/org.apache.batik.css-1.8.0.v20170214-1941.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.apache.batik.util/1.8.0.v20170214-1941/org.apache.batik.util-1.8.0.v20170214-1941.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.w3c.css.sac/1.3.1.v200903091627/org.w3c.css.sac-1.3.1.v200903091627.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.osgi.services/3.6.0.v20170228-1906/org.eclipse.osgi.services-3.6.0.v20170228-1906.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.apache.felix.scr/2.0.8.v20170123-2104/org.apache.felix.scr-2.0.8.v20170123-2104.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.osgi.util/3.4.0.v20170111-1608/org.eclipse.osgi.util-3.4.0.v20170111-1608.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.core.commands/3.9.0.v20170210-0856/org.eclipse.core.commands-3.9.0.v20170210-0856.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.equinox.common/3.9.0.v20170207-1454/org.eclipse.equinox.common-3.9.0.v20170207-1454.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.core.contenttype/3.6.0.v20170207-1037/org.eclipse.core.contenttype-3.6.0.v20170207-1037.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.equinox.preferences/3.7.0.v20170126-2132/org.eclipse.equinox.preferences-3.7.0.v20170126-2132.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.equinox.registry/3.7.0.v20170222-1344/org.eclipse.equinox.registry-3.7.0.v20170222-1344.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.core.databinding/1.6.0.v20170210-0856/org.eclipse.core.databinding-1.6.0.v20170210-0856.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.core.databinding.observable/1.6.0.v20170210-0856/org.eclipse.core.databinding.observable-1.6.0.v20170210-0856.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.core.databinding.property/1.6.0.v20170210-0856/org.eclipse.core.databinding.property-1.6.0.v20170210-0856.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/com.ibm.icu/58.2.0.v20170208-1743/com.ibm.icu-58.2.0.v20170208-1743.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.core.expressions/3.6.0.v20170207-1037/org.eclipse.core.expressions-3.6.0.v20170207-1037.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.core.runtime/3.13.0.v20170207-1030/org.eclipse.core.runtime-3.13.0.v20170207-1030.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.core.jobs/3.9.0.v20170223-1702/org.eclipse.core.jobs-3.9.0.v20170223-1702.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.equinox.app/1.3.400.v20150715-1528/org.eclipse.equinox.app-1.3.400.v20150715-1528.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.core.commands/0.12.0.v20160919-1453/org.eclipse.e4.core.commands-0.12.0.v20160919-1453.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.core.di/1.6.100.v20170228-1124/org.eclipse.e4.core.di-1.6.100.v20170228-1124.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/javax.inject/1.0.0.v20091030/javax.inject-1.0.0.v20091030.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.core.contexts/1.5.0.v20170314-1932/org.eclipse.e4.core.contexts-1.5.0.v20170314-1932.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.core.services/2.1.0.v20170307-2027/org.eclipse.e4.core.services-2.1.0.v20170307-2027.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.core.di.annotations/1.6.0.v20170119-2002/org.eclipse.e4.core.di.annotations-1.6.0.v20170119-2002.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.core.di.extensions/0.15.0.v20170228-1728/org.eclipse.e4.core.di.extensions-0.15.0.v20170228-1728.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.equinox.ds/1.5.0.v20170307-1429/org.eclipse.equinox.ds-1.5.0.v20170307-1429.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.core.di.extensions.supplier/0.15.0.v20170313-2055/org.eclipse.e4.core.di.extensions.supplier-0.15.0.v20170313-2055.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.emf.xpath/0.2.0.v20160630-0728/org.eclipse.e4.emf.xpath-0.2.0.v20160630-0728.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.apache.commons.jxpath/1.3.0.v200911051830/org.apache.commons.jxpath-1.3.0.v200911051830.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.emf.ecore/2.13.0.v20170123-0427/org.eclipse.emf.ecore-2.13.0.v20170123-0427.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.swt/3.106.0.v20170315-1905/org.eclipse.swt-3.106.0.v20170315-1905.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.jface/3.13.0.v20170315-2013/org.eclipse.jface-3.13.0.v20170315-2013.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.bindings/0.12.0.v20170312-2302/org.eclipse.e4.ui.bindings-0.12.0.v20170312-2302.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.css.core/0.12.100.v20170313-0809/org.eclipse.e4.ui.css.core-0.12.100.v20170313-0809.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.css.swt/0.13.0.v20170312-2302/org.eclipse.e4.ui.css.swt-0.13.0.v20170312-2302.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.css.swt.theme/0.11.0.v20170312-2302/org.eclipse.e4.ui.css.swt.theme-0.11.0.v20170312-2302.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.di/1.2.100.v20170307-2032/org.eclipse.e4.ui.di-1.2.100.v20170307-2032.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.model.workbench/2.0.0.v20170228-1842/org.eclipse.e4.ui.model.workbench-2.0.0.v20170228-1842.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.services/1.3.0.v20170307-2032/org.eclipse.e4.ui.services-1.3.0.v20170307-2032.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.equinox.event/1.4.0.v20170105-1446/org.eclipse.equinox.event-1.4.0.v20170105-1446.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.widgets/1.2.0.v20160630-0736/org.eclipse.e4.ui.widgets-1.2.0.v20160630-0736.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.emf.ecore.xmi/2.13.0.v20170123-0427/org.eclipse.emf.ecore.xmi-2.13.0.v20170123-0427.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.emf.ecore.change/2.11.0.v20170123-0427/org.eclipse.emf.ecore.change-2.11.0.v20170123-0427.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.workbench/1.5.0.v20170312-2302/org.eclipse.e4.ui.workbench-1.5.0.v20170312-2302.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.workbench.addons.swt/1.3.1.v20170210-0857/org.eclipse.e4.ui.workbench.addons.swt-1.3.1.v20170210-0857.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.workbench.renderers.swt/0.14.100.v20170312-2302/org.eclipse.e4.ui.workbench.renderers.swt-0.14.100.v20170312-2302.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.workbench.swt/0.14.100.v20170312-2302/org.eclipse.e4.ui.workbench.swt-0.14.100.v20170312-2302.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.jface.databinding/1.8.100.v20170210-0857/org.eclipse.jface.databinding-1.8.100.v20170210-0857.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.e4.ui.workbench3/0.14.0.v20160630-0740/org.eclipse.e4.ui.workbench3-0.14.0.v20160630-0740.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.emf.common/2.13.0.v20170123-0427/org.eclipse.emf.common-2.13.0.v20170123-0427.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.help/3.8.0.v20160823-1530/org.eclipse.help-3.8.0.v20160823-1530.jar",
			"/home/foo/.m2/repository/p2/osgi/bundle/org.eclipse.swt.gtk.linux.x86_64/3.106.0.v20170315-1905/org.eclipse.swt.gtk.linux.x86_64-3.106.0.v20170315-1905.jar"
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
		return "/media/data/dev/refactor/eclipseRefactored";
	}
}

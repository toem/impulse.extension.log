package de.toem.impulse.extension.log;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import de.toem.toolkits.utils.bundles.Activator;


	public class ImpulseLogExtension extends Activator {

	    public static final String BUNDLE_ID = "de.toem.impulse.extension.log";

	    public ImpulseLogExtension() {
	        super(BUNDLE_ID);
	        instance = this;
	    }

	    // ========================================================================================================================
	    // Static interface
	    // ========================================================================================================================

	    public static ImpulseLogExtension instance;

	    public static ImpulseLogExtension getInstance() {
	        return instance;
	    }

	    public static BundleContext getContext() {
	        return instance != null ? instance.context : null;
	    }

	    public static Bundle getBundle() {
	        return instance != null && instance.context != null ? instance.context.getBundle() : null;
	    }

	    public static float getVersion() {
	        Version v = getBundle() != null ? getBundle().getVersion() : null;
	        return instance != null ? instance.extractVersion() : 0;
	    }
	}
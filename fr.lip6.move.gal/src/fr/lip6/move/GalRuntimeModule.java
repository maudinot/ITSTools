/*
 * generated by Xtext
 */
package fr.lip6.move;

import org.eclipse.xtext.debug.IStratumBreakpointSupport;
import fr.lip6.move.debug.GalStratumBreakpointSupport;

/**
 * Use this class to register components to be used at runtime / without the
 * Equinox extension registry.
 */
public class GalRuntimeModule extends fr.lip6.move.AbstractGalRuntimeModule {
	@Override
	public Class<? extends IStratumBreakpointSupport> bindIStratumBreakpointSupport() {
		return GalStratumBreakpointSupport.class;
	}
}

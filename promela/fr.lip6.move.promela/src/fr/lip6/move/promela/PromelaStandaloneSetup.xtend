/*
 * generated by Xtext 2.11.0
 */
package fr.lip6.move.promela


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class PromelaStandaloneSetup extends PromelaStandaloneSetupGenerated {

	def static void doSetup() {
		new PromelaStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}

package fr.lip6.move.validation;
 

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.validation.Check;

import fr.lip6.move.gal.ArrayPrefix;
import fr.lip6.move.gal.Call;
import fr.lip6.move.gal.GalPackage;
import fr.lip6.move.gal.List;
import fr.lip6.move.gal.System;
import fr.lip6.move.gal.Transition;
import fr.lip6.move.gal.Variable;
 

/** 
 * Validation class for grammar elements
 */
public class GalJavaValidator extends AbstractGalJavaValidator {


	/*
	 * Static fields used in quickfix class in user interface
	 */
	// Error codes
	public static final String GAL_ERROR_NAME_EXISTS      = "101" ; 
	public static final String GAL_ERROR_MISSING_ELEMENTS = "102";
	public static final String GAL_ERROR_EXCESS_ITEMS     = "103";
	public static final String GAL_ERROR_CIRCULAR_CALLS     = "104";
	/** 
	 * List of the names of all Gal elements 
	 */ 
	public static final HashMap<String, EObject> galElementsName = new HashMap<String, EObject>() ;
	/** 
	 * Contains, for each array, the number of elements missing (for quickfixes)
	 */
	public static final HashMap<String, Integer> arrayMissingValues = new HashMap<String, Integer>();
	
	private System system;

	@Check
	/**
	 * Check uniqueness between all Gal element name
	 */
	public void checkNameUnicity(EObject e)
	{
		// Systeme
		if(e instanceof System) 
			system = (System) e ;
		// Variables
		else if(e instanceof Variable)
			checkExistsInHashMap(e, ((Variable)e).getName(), GalPackage.Literals.VARIABLE__NAME );
		// ArrayPrefix
		else if(e instanceof ArrayPrefix)
			checkExistsInHashMap(e, ((ArrayPrefix)e).getName(), GalPackage.Literals.ARRAY_PREFIX__NAME );
		// Listes
		else if(e instanceof List)
			checkExistsInHashMap(e, ((List)e).getName(), GalPackage.Literals.LIST__NAME );
		// Transitions
		else if(e instanceof Transition)
			checkExistsInHashMap(e, ((Transition)e).getName(), GalPackage.Literals.TRANSITION__NAME);
		
	}
	
	private void checkExistsInHashMap(EObject objectToCheck, String name, EAttribute galLiteral) 
	{
		if(galElementsName.containsKey(name) 
				&& galElementsName.get(name) != objectToCheck
				&& existInGalSystem(galElementsName.get(name))
		  )
		{
			error("This name is already used", /* Error Message */ 
					objectToCheck,             /* Object Source of Error */ 
					galLiteral,                /* wrong Feature */
					GAL_ERROR_NAME_EXISTS      /* Error Code. @see GalJavaValidator.GAL_ERROR_*  */
					);
			
		}
		else
		{
			galElementsName.put(name, objectToCheck);
		}
	}
	
	/**
	 * Test if an object is in the system
	 * @param obj:
	 * 			object to find
	 * @return :
	 * 			true if object is present
	 */
	private boolean existInGalSystem(EObject obj) 
	{
		if(system == null) return false ;
		
		EList<Variable> variables = system.getVariables();
		
		// Variables
		for(Variable v : variables)
		{
			if(v == obj)
				return true;
		}
		
		// Transitions
		EList<Transition> transitions = system.getTransitions();
		for(Transition t : transitions)
		{
			if(t == obj)
				return true ;
		}
		
		// Lists
		EList<List> listes = system.getLists();
		for(List l : listes)
		{
			if(l == obj)
				return true ;
		}
		
		// Arrays
		EList<ArrayPrefix> aps = system.getArrays();
		for(ArrayPrefix arr : aps)
		{
			if(arr == obj)
				return true ;
		}
		
		return false;
	}

	
	@Check
	/**
	 * Verify that there are no circular references to labels, i.e. call graphs form a strict DAG.
	 * @param s the full system
	 */
	public void checkNoCircularCalls (System s) {

		// First scan transitions to build a map "label" to set of transitions bearing it.
		Map<String,Set<Transition>> labMap = buildLabMap(s);

		// Now search for call instances in the system
		for (Transition t : s.getTransitions()) {
			for (Iterator<EObject> it = t.eAllContents() ; it.hasNext() ; /*NOP*/) {
				EObject cont = it.next();
				if (cont instanceof Call) {
					Call call = (Call) cont;
					Set<Transition> tosee = new HashSet<Transition>();
					getDependencies(labMap, call.getLabel().getName(), tosee);
					if (tosee.contains(t)) {
						error("There are circular calls between actions of your system", /* Error Message */ 
								call,             /* Object Source of Error */ 
								GalPackage.Literals.CALL__LABEL,                /* wrong Feature */
								GAL_ERROR_CIRCULAR_CALLS      /* Error Code. @see GalJavaValidator.GAL_ERROR_*  */
								);
					}
				}
			}
		}		
		
	}
	
	private Map<String, Set<Transition>> buildLabMap(System s) {
		Map<String,Set<Transition>> labMap = new HashMap<String, Set<Transition>>();
		for (Transition t : s.getTransitions()) {
			if (t.getLabel()!=null) {
				String lab =t.getLabel().getName();
				Set<Transition> toadd = labMap.get(lab);
				if (toadd == null) {
					toadd = new HashSet<Transition>();
				}
				toadd.add(t);
				labMap.put(lab, toadd);
			}
		}
		return labMap;
	}

	/**
	 * Constructs (adds to) in "seen" the list of transitions that are necessary to compute "label"'s effect.
	 * @param labMap maps labels to the set of transitions that bear this label
	 * @param label the current target label
	 * @param seen all transitions we know we already know we depend upon
	 */
	public void getDependencies (Map<String,Set<Transition>> labMap, String label, Set<Transition> seen) {

		for (Transition t : labMap.get(label)) {
				if (! seen.contains(t)) {
					// time to add it !
					seen.add(t);
					// compute its own transitive dependencies
					for (Iterator<EObject> it = t.eAllContents() ; it.hasNext() ; /*NOP*/) {
						EObject cont = it.next();
						if (cont instanceof Call) {
							Call call = (Call) cont;
							// recurse
							getDependencies(labMap, call.getLabel().getName(), seen);
						}
					}
				}
			}
		
	}
	
	
	@Check
	/**
	 * Checks if the declared size of the array is equal to the number of 
	 * items of the declared integer list 
	 */
	public void checkArrayNumber(ArrayPrefix array)
	{
		int size = array.getSize() ; 
	
		if(array.getValues() == null && size != 0)
		{
			String plurielElements = size > 1 ? " elements" : " element" ; 
			arrayMissingValues.put(array.getName(), size);
			
			error("This array should be initialized (with " + array.getSize() + plurielElements + ")",
					array,					
					GalPackage.Literals.ARRAY_PREFIX__NAME /* wrong Feature */,
					GAL_ERROR_MISSING_ELEMENTS
					
					);
			return ; 
		}
		
		int nbDeclared = array.getValues().getValues().size() ; 
		int diff = size - nbDeclared ; 
		
		if(diff == 0) return ; 
		
		if(diff>0)
		{
			// Add in the missing element list to help quickfix 
			arrayMissingValues.put(array.getName(), diff);
			
			error("You need to add "+diff+" more values at initialization",
					array,                               /* Object Source of Error */ 
					GalPackage.Literals.ARRAY_PREFIX__NAME, /* wrong Feature */
					GAL_ERROR_MISSING_ELEMENTS               /* Error Code. @see GalJavaValidator.GAL_ERROR_*  */
					);
			
			
		}
		else if(diff<0)
		{
			arrayMissingValues.put(array.getName(), diff);
			
			error("There are too much items. You need to remove "+(-diff)+" values at initialization",
					array,
					GalPackage.Literals.ARRAY_PREFIX__NAME /* wrong Feature */,
					GAL_ERROR_EXCESS_ITEMS); 
		}
	}
}
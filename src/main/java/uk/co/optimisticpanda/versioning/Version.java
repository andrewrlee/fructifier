package uk.co.optimisticpanda.versioning;

/**
 * Has to implement hashcode equals.
 * Implementors have to cast arguments. 
 */
public interface Version extends Comparable<Version> {

	public boolean isContinuationOfRange(Version thisNumber);

}

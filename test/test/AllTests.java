package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DiffAnalyzerArtificialTests.class,
	DiffAnalyzerDebugTests.class,
	/*
	 * Busybox excluded due to constant changes in the way model files
	 * are named and used; further, recent changes enforce the definition
	 * of build and model information as comments in code file, which will
	 * be extracted by a script --> breaks all assumptions on file diffs.
	 */
	//DiffAnalyzerBusyboxTests.class,
	DiffAnalyzerCorebootTests.class,
	DiffAnalyzerLinuxTests.class
})

/**
 * This class summarizes all tests into a single test suite.
 * 
 * @author Christian Kröher
 *
 */
public class AllTests {

}

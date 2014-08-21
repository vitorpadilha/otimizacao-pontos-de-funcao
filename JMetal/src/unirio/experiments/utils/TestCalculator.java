package unirio.experiments.utils;

import junit.framework.TestCase;

public class TestCalculator extends TestCase
{
	public void testMannWhitney()
	{
		double[] v1 = {3.42, 2.71, 2.84, 1.85, 3.22, 3.48, 2.68, 4.30, 2.49, 1.54, 1.54}; 
		double[] v2 = {3.44, 4.97, 4.76, 4.96, 4.10, 3.05, 4.09, 3.69, 4.21, 4.40, 3.49};
		assertFalse(new Calculator().calculateMannWhitney(v1, v2));
		assertTrue(new Calculator().calculateMannWhitney(v1, v1));
		assertTrue(new Calculator().calculateMannWhitney(v2, v2));

		double[] v3 = {4.42, 3.71, 3.84, 2.85, 4.22, 4.48, 3.68, 5.30, 3.49, 2.54, 2.54};
		assertFalse(new Calculator().calculateMannWhitney(v1, v3));

		double[] v4 = {4.42, 3.71, 3.84, 2.85, 4.22, 4.48, 3.68, 5.30, 3.49, 2.54};
		assertFalse(new Calculator().calculateMannWhitney(v1, v4));
	}

	public void testMannWhitneyLong()
	{
		double[] v1 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2}; 
		double[] v2 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		assertTrue(new Calculator().calculateMannWhitney(v1, v2));
	}

	public void testMannWhitneyLong2()
	{
		double[] v1 = {5.2, 5.3, 5.4, 5.6, 6.2, 6.3, 6.8, 7.7, 8.0, 8.1};
		double[] v2 = {4.6, 4.7, 4.9, 5.1, 5.2, 5.5, 5.8, 6.1, 6.5, 6.5, 7.2};
		assertTrue(new Calculator().calculateMannWhitney(v1, v2));
	}

	public void testMannWhitneyLong3()
	{
		double[] v1 = {13, 12, 12, 10, 10, 10, 10, 9, 8, 8, 7, 7, 7, 7, 7, 6};
		double[] v2 = {17, 16, 15, 15, 15, 14, 14, 14, 13, 13, 13, 12, 12, 12, 12, 11, 11, 10, 10, 10, 8, 8, 6};
		assertTrue(new Calculator().calculateMannWhitney(v1, v1));
		assertTrue(new Calculator().calculateMannWhitney(v2, v2));
		assertFalse(new Calculator().calculateMannWhitney(v1, v2));
	}

	public void testMannWhitneyLong4()
	{
		double[] v1 = {3.42, 2.71, 2.84, 1.85, 3.22, 3.48, 2.68, 4.30, 2.49, 1.54, 1.54, 3.42, 2.71, 2.84, 1.85, 3.22, 3.48, 2.68, 4.30, 2.49, 1.54, 1.54}; 
		double[] v2 = {3.44, 4.97, 4.76, 4.96, 4.10, 3.05, 4.09, 3.69, 4.21, 4.40, 3.49, 3.44, 4.97, 4.76, 4.96, 4.10, 3.05, 4.09, 3.69, 4.21, 4.40, 3.49};
		assertFalse(new Calculator().calculateMannWhitney(v1, v2));
		assertTrue(new Calculator().calculateMannWhitney(v1, v1));
		assertTrue(new Calculator().calculateMannWhitney(v2, v2));
	}
}
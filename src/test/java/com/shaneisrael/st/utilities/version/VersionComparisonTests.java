package com.shaneisrael.st.utilities.version;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VersionComparisonTests
{
    @Test
    public void testVersionComparisonWithValidVersions()
    {
        new VersionCompareTest("1.0.0", "1.0.0", 0).test();
        new VersionCompareTest("1.0.1", "1.0.0", 1).test();
        new VersionCompareTest("1.1.0", "1.0.5", 1).test();
        new VersionCompareTest("1.5.0", "1.8.0", -1).test();
        new VersionCompareTest("3.5.9", "2.5.0", 1).test();
        new VersionCompareTest("7.3.4", "7.3.4", 0).test();
        new VersionCompareTest("5.5.9", "5.5.15", -1).test();
        new VersionCompareTest("13.50.09", "13.50.9", 0).test();
        new VersionCompareTest(" 3. 4. 9 ", "3.4.9", 0).test();
        new VersionCompareTest(" 6.2.2", "6.2.3", -1).test();
        new VersionCompareTest("6.2.2", "6.3.0", -1).test();
        new VersionCompareTest(" 6.2.2", "7.0.0", -1).test();
        new VersionCompareTest("5.18.24", "5.18.7", 1).test();
    }

    @Test
    public void testVersionComparisonWithInvalidVersions()
    {
        new VersionCompareTest("1.0.0", "123z0", 1).test();
        new VersionCompareTest("1.0.1", "1vc0", 1).test();
        new VersionCompareTest("1.0.0", null, 1).test();
    }

    class VersionCompareTest
    {
        public Version versionA;
        public Version versionB;
        public int expectedResult;

        public VersionCompareTest(String versionA, String versionB, int expectedResult)
        {
            try
            {
                this.versionA = Version.fromString(versionA);
            } catch (IllegalArgumentException ex)
            {
                this.versionA = null;
            }

            try
            {
                this.versionB = Version.fromString(versionB);
            } catch (IllegalArgumentException ex)
            {
                this.versionB = null;
            }

            this.expectedResult = expectedResult;
        }

        public void test()
        {
            String message = String.format("Comparing %s with %s should yield %d.",
                versionA == null ? "null" : versionA.getVersionString(),
                versionB == null ? "null" : versionB.getVersionString(),
                expectedResult);
            assertEquals(message, expectedResult, versionA.compareTo(versionB));
        }
    }
}

package com.syntepro.sueldazo;

import com.syntepro.sueldazo.utils.UploadData;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void upload() {
        UploadData.Companion.addLoyaltyPlans();
    }
}
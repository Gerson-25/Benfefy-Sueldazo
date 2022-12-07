package com.appbenefy.sueldazo;

import com.appbenefy.sueldazo.utils.UploadData;

import org.junit.Test;

import static org.junit.Assert.*;

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
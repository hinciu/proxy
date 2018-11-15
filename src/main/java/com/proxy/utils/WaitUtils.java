package com.proxy.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;


public class WaitUtils {
    public static boolean waitUntilCondition(BooleanSupplier supplier, boolean expectedCondition, long timeoutInSeconds) {
        boolean actualCondition = !expectedCondition;
        LocalDateTime startOfExecution = LocalDateTime.from(LocalDateTime.now());

        while (startOfExecution.until(LocalDateTime.now(), ChronoUnit.SECONDS) < timeoutInSeconds && actualCondition != expectedCondition) {
            actualCondition = supplier.getAsBoolean();
            if (actualCondition == expectedCondition) {
                return true;
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
        }
        return false;
    }
}

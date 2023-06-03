package com.example.CodeGeneratieRestAPI.helpers;

import com.example.CodeGeneratieRestAPI.helpers.IBANGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IBANGeneratorTest {

    @Test
    //  This test tests the generateIban method from IBANGenerator helper class
    //  This test makes sure that whenever the generateIban method is invoked, it returns an IBAN with the correct format
    void testGenerateIban() {
        String iban = IBANGenerator.generateIban();

        // Check if the generated IBAN has the correct format
        Assertions.assertEquals(22, iban.length());
        Assertions.assertEquals("NL", iban.substring(0, 2));
        Assertions.assertEquals("MRBA", iban.substring(5, 9));

        // Print the generated IBAN to the console
        System.out.println("Generated IBAN: " + iban);
    }
    @Test
    //  This test is not necessary, but it is a good example of how to test private methods
    //  What this test does is it tests the calculateChecksum method from IBANGenerator helper class
    //  This method is private, so it cannot be tested directly
    //  To test it, we use reflection to make the method accessible and then invoke it
    //  This is not a good practice, but it is a good example of how to test private methods
    //  If you want to test a private method, you should probably make it public
    //  This test makes sure that whenever the calculateChecksum method is invoked with the digits "5876385641",
    //  it returns the correct checksum, which is 20
    void testCalculateChecksum() {
        try {
            Method calculateChecksumMethod = IBANGenerator.class.getDeclaredMethod("calculateChecksum", String.class);
            calculateChecksumMethod.setAccessible(true); // Set the method accessible

            String ibanDigits = "5876385641";
            int expectedChecksum = 20;

            // Invoke the private method
            int calculatedChecksum = (int) calculateChecksumMethod.invoke(null, ibanDigits);

            Assertions.assertEquals(expectedChecksum, calculatedChecksum);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

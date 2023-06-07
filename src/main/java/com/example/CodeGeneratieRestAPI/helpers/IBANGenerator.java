package com.example.CodeGeneratieRestAPI.helpers;

import com.example.CodeGeneratieRestAPI.exceptions.IBANGenerationException;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;

import java.math.BigInteger;
import java.util.Random;

public class IBANGenerator {
    private static final BigInteger MODULUS = BigInteger.valueOf(97);
    private static final int IBAN_LENGTH = 18; //   18 is the standard length of an IBAN in the Netherlands + all the dashes is 22
    private static final String COUNTRY_CODE = "NL";
    private static final int CHECKSUM_LENGTH = 2;
    private static final String BANK_CODE = "MRBA";

    public static String getUniqueIban() {
        do {
            String iban = generateIban();
            if (ServiceHelper.checkIfObjectExistsByIdentifier(iban, AccountRepository.class)) {
                return iban;
            }
        } while (true);
    }

    public static String generateIban() {
        try {
            StringBuilder ibanBuilder = new StringBuilder();
            Random random = new Random();

            // Generate a string of random numbers
            for (int i = 0; i < IBAN_LENGTH - COUNTRY_CODE.length() - BANK_CODE.length() - CHECKSUM_LENGTH; i++) {
                ibanBuilder.append(random.nextInt(10));
            }

            // Calculate the two-digit checksum
            String ibanDigits = ibanBuilder.toString();
            int checksum = calculateChecksum(ibanDigits);

            // Format the IBAN with the checksum and components
            StringBuilder formattedIban = new StringBuilder(COUNTRY_CODE);
            formattedIban.append(String.format("%02d", checksum)).append(BANK_CODE).append(ibanDigits);

            // Insert dashes every 4 characters for readability
            for (int i = 4; i < formattedIban.length(); i += 5) {
                formattedIban.insert(i, "-");
            }

            // Return the formatted IBAN
            // IMPORTANT! This could return an already existing IBAN,
            // so make sure to check if the IBAN already exists in the database before saving it
            // OR use the getUniqueIban method
            return formattedIban.toString().toUpperCase();
        } catch (Exception exception) {
            throw new IBANGenerationException(exception.getMessage(), exception);
        }

    }

    // Calculate the two-digit checksum using modulo-97 (this is the ISO 7064 mod 97-10 algorithm)
    private static int calculateChecksum(String ibanDigits) {
        ibanDigits += "00"; // Append the country code and checksum (initially 00)
        BigInteger number = new BigInteger(ibanDigits);
        BigInteger mod97 = number.mod(new BigInteger(MODULUS.toByteArray()));
        return 98 - mod97.intValue(); // Subtract the remainder from 98
    }
}

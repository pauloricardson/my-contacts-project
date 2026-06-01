package br.capacita.contatos.util;

import java.util.regex.Pattern;

public class PhoneValidator {

    private static final Pattern PHONE_BR = Pattern.compile("^\\(?\\d{2}\\)?\\s?(9?\\d{4})-?\\d{4}$");

    public static boolean phoneValidator(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            return false;
        }
        return PHONE_BR.matcher(telephone).matches();
    }
}

package com.carlosnazario.carteira_de_acoes.utils;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    /**
     * Metodo que verifica se o email inforamdo possui um formato valido
     *
     * @param email Email que sera verificado
     * @return Retorna verdadeiro caso o email seja valido, e falso caso seja invalido
     */
    public static boolean isValidEmail(String email) {
        return pattern.matcher(email).matches();
    }
}

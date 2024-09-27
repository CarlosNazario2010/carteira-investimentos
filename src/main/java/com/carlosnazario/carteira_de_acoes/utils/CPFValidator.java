package com.carlosnazario.carteira_de_acoes.utils;

public class CPFValidator {

    /**
     * Metodo que verifica se o número de cpf informado e valido
     *
     * @param cpf Cpf que sera verificado
     * @return Retorna verdadeiro caso o cpf seja valido, e falso caso seja invalido
     */
    public static boolean isValidCPF(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        // Verifica se o CPF tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Todos os dígitos são iguais?
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        char digito1, digito2;
        int sm, i, r, num, peso;

        // Calcula o primeiro dígito verificador
        sm = 0;
        peso = 10;
        for (i = 0; i < 9; i++) {
            num = Integer.parseInt(cpf.substring(i, i + 1));
            sm += num * peso;
            peso--;
        }
        r = 11 - (sm % 11);
        if (r == 10 || r == 11) {
            digito1 = '0';
        } else {
            digito1 = (char) (r + 48); // converte int para char
        }

        // Calcula o segundo dígito verificador
        sm = 0;
        peso = 11;
        for (i = 0; i < 10; i++) {
            num = Integer.parseInt(cpf.substring(i, i + 1));
            sm += num * peso;
            peso--;
        }
        r = 11 - (sm % 11);
        if (r == 10 || r == 11) {
            digito2 = '0';
        } else {
            digito2 = (char) (r + 48);
        }

        // Verifica se os dígitos calculados conferem com os dígitos informados
        String digitos = cpf.substring(9, 11);
        return digitos.equals(Character.toString(digito1) + Character.toString(digito2));
    }
}
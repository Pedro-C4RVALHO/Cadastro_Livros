import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import java.text.DecimalFormat;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String fileName = "data.txt";
        String nome, cpf, cep = "0", endereco = "0";
        double salarioBruto, descontoInss;
        int dependentes;

        List<String[]> employees = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] employee = line.split(", ");
                employees.add(employee);
            }
        } catch (IOException e) {
            // Handle exception
        }

        System.out.print("Digite seu nome: ");
        nome = scanner.nextLine();

        System.out.print("Digite seu salário bruto: ");
        salarioBruto = scanner.nextDouble();

        System.out.print("Digite o desconto de INSS: ");
        descontoInss = scanner.nextDouble();

        scanner.nextLine();

        System.out.print("Digite o número de dependentes: ");
        dependentes = scanner.nextInt();

        scanner.nextLine();

        double valorDependentes = dependentes * 189.59;
        double baseIRRF = salarioBruto - descontoInss - valorDependentes;
        double valorIRRF;
        double salarioLiquido;

        if (baseIRRF <= 2259.20) {
            valorIRRF = 0;
        } else if (baseIRRF >= 2259.21 && baseIRRF <= 2826.65) {
            valorIRRF = baseIRRF * 0.075 - 169.44;
        } else if (baseIRRF >= 2826.66 && baseIRRF <= 3751.05) {
            valorIRRF = baseIRRF * 0.15 - 381.44;
        } else if (baseIRRF >= 3751.06 && baseIRRF <= 4664.68) {
            valorIRRF = baseIRRF * 0.225 - 662.77;
        } else {
            valorIRRF = baseIRRF * 0.275 - 896.00;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#,##00.00");

        if (valorIRRF == 0) {
            salarioLiquido = salarioBruto - descontoInss;
            System.out.println("\nSalario Líquido: " + decimalFormat.format(salarioLiquido) + "\nIsento de imposto de renda!\n");
        } else {
            salarioLiquido = salarioBruto - descontoInss - valorIRRF;
            System.out.println("\nSalario Líquido: " + decimalFormat.format(salarioLiquido) + "\nImposto de renda: " + decimalFormat.format(valorIRRF) + "\n");
        }

        System.out.print("Digite seu CPF: ");
        cpf = scanner.nextLine();

        if (cpf.length() != 11) {
            do {
                System.out.print("CPF inválido, digite novamente: ");
                cpf = scanner.nextLine();
            } while (cpf.length() != 11);
        }

        System.out.print("Digite o CEP: ");
        boolean cepValido = false;

        while (!cepValido) {
            cep = scanner.nextLine();

            try {
                URL url = new URL("https://viacep.com.br/ws/" + cep + "/json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String responseLine;
                StringBuilder responseBuilder = new StringBuilder();

                while ((responseLine = responseReader.readLine()) != null) {
                    responseBuilder.append(responseLine);
                }
                responseReader.close();

                JSONObject jsonResponse = new JSONObject(responseBuilder.toString());

                if (jsonResponse.has("erro")) {
                    System.out.print("CEP inválido, digite novamente: ");
                } else {
                    String logradouro = jsonResponse.getString("logradouro");
                    String bairro = jsonResponse.getString("bairro");
                    String cidade = jsonResponse.getString("localidade");
                    String estado = jsonResponse.getString("uf");

                    endereco = logradouro + " - " + bairro + " " + cidade + " - " + estado;
                    System.out.println("Endereço: " + endereco);
                    cepValido = true;
                }

            } catch (Exception e) {
                System.out.print("CEP inválido, digite novamente: ");
            }
        }

        String salarioBrutoString = decimalFormat.format(salarioBruto);
        String salarioLiquidoString = decimalFormat.format(salarioLiquido);
        String dependentesString = String.valueOf(dependentes);

        boolean cpfExistente = false;

        for (String[] employee : employees) {
            if (employee.length >= 2 && employee[1].equals(cpf)) {
                employee[0] = nome;
                employee[2] = salarioLiquidoString;
                employee[3] = dependentesString;
                employee[4] = salarioBrutoString;
                employee[5] = cep;
                employee[6] = endereco;
                cpfExistente = true;
                break;
            }
        }

        if (!cpfExistente) {
            employees.add(new String[]{nome, cpf, salarioLiquidoString, dependentesString, salarioBrutoString, cep, endereco});
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            for (String[] employee : employees) {
                for (int i = 0; i < employee.length; i++) {
                    writer.write(employee[i]);
                    if (i < employee.length - 1) {
                        writer.write(", ");
                    } else {
                        writer.write("\n");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ocorreu um erro");
        }

        scanner.close();
    }
}

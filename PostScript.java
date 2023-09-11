import java.util.Stack;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;

public class PostScript {
    private static Stack<Double> pila = new Stack<>();
    private static FileWriter archivoBitacora;

    public static void main(String[] args) {
        try {
            archivoBitacora = new FileWriter("bitacora_postscript.log");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            String entrada = JOptionPane.showInputDialog("Ingrese una expresión PostScript (o 'salir' para salir):\nEjemplo: 5 3 + (para sumar 5 y 3)");

            if (entrada == null || entrada.equalsIgnoreCase("salir")) {
                break;
            }

            try {
                evaluarExpresionPostScript(entrada);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                registrarError(e.getMessage());
            }
        }

        try {
            archivoBitacora.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, "Saliendo del programa.");
    }

    private static void evaluarExpresionPostScript(String expresion) {
        String[] tokens = expresion.split("\\s+");

        for (String token : tokens) {
            if (token.matches("-?\\d+(\\.\\d+)?")) {
                pila.push(Double.parseDouble(token));
            } else if (esOperador(token)) {
                realizarOperacion(token);
            } else if (token.startsWith("/")) {
                // Definición de símbolos (ejemplo: /pi 3.141592653 def)
                String simbolo = token.substring(1);
                pila.push(Double.parseDouble(simbolo));
            } else {
                throw new IllegalArgumentException("Operador no reconocido: " + token);
            }
        }

        if (pila.size() == 1) {
            JOptionPane.showMessageDialog(null, "Resultado: " + pila.pop());
        } else {
            throw new IllegalArgumentException("La expresión es inválida o incompleta.");
        }
    }

    private static boolean esOperador(String token) {
        return "+".equals(token) || "-".equals(token) || "*".equals(token) || "/".equals(token);
    }

    private static void realizarOperacion(String operador) {
        if (pila.size() < 2) {
            throw new IllegalArgumentException("No hay suficientes operandos en la pila para " + operador);
        }

        double b = pila.pop();
        double a = pila.pop();

        switch (operador) {
            case "+":
                pila.push(a + b);
                break;
            case "-":
                pila.push(a - b);
                break;
            case "*":
                pila.push(a * b);
                break;
            case "/":
                if (b == 0) {
                    throw new IllegalArgumentException("No se puede dividir por cero.");
                }
                pila.push(a / b);
                break;
        }
    }

    private static void registrarError(String mensajeError) {
        try {
            archivoBitacora.write("Error: " + mensajeError + "\n");
            archivoBitacora.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

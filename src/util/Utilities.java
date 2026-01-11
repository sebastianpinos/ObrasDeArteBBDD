package util;

import javax.swing.*;
import java.awt.*;

public class Utilities {

    public static void showInfoAlert(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Información",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showErrorAlert(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showWarningAlert(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Advertencia",
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static int confirmMessage(String message, String title) {
        return JOptionPane.showConfirmDialog(
            null,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
    }

    public static int confirmMessage(String message) {
        return JOptionPane.showConfirmDialog(
            null,
            message,
            "Confirmar",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasUpperCase && hasLowerCase && hasDigit) {
                return true;
            }
        }

        return hasUpperCase && hasLowerCase && hasDigit;
    }

    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public static void displayCard(CardLayout cardLayout, Container container, String cardName) {
        cardLayout.show(container, cardName);
    }

    public static void manageBtn(JButton button, boolean enabled) {
        button.setEnabled(enabled);
    }

    public static void centerWindow(Window window) {
        window.setLocationRelativeTo(null);
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isPositive(double number) {
        return number > 0;
    }

    public static boolean isNonNegative(double number) {
        return number >= 0;
    }

    public static void clearTextFields(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof JTextArea) {
                ((JTextArea) component).setText("");
            } else if (component instanceof JPasswordField) {
                ((JPasswordField) component).setText("");
            } else if (component instanceof Container) {
                clearTextFields((Container) component);
            }
        }
    }

    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String formatCurrency(double amount) {
        return String.format("%.2f €", amount);
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String showInputDialog(String message, String title) {
        return JOptionPane.showInputDialog(
            null,
            message,
            title,
            JOptionPane.QUESTION_MESSAGE
        );
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static boolean isAlphabetic(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    public static void showSuccessMessage(String message) {
        JOptionPane pane = new JOptionPane(
            message,
            JOptionPane.INFORMATION_MESSAGE
        );
        JDialog dialog = pane.createDialog("Éxito");
        dialog.setVisible(true);
    }
}

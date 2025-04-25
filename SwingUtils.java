package utils;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

/**
 * Classe utilitaire pour l'interface Swing
 */
public class SwingUtils {
    
    // Couleurs du thème moderne bleu et blanc
    public static final Color PRIMARY_COLOR = new Color(24, 90, 189);       // Bleu principal plus foncé
    public static final Color SECONDARY_COLOR = new Color(66, 133, 244);    // Bleu secondaire
    public static final Color LIGHT_COLOR = new Color(232, 240, 254);       // Bleu très clair
    public static final Color WHITE_COLOR = Color.WHITE;                    // Blanc
    public static final Color TEXT_COLOR = new Color(33, 33, 33);           // Texte foncé
    public static final Color ACCENT_COLOR = new Color(251, 184, 41);       // Jaune doré accent
    public static final Color BACKGROUND_COLOR = new Color(245, 247, 250);  // Fond gris très clair
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);       // Vert succès
    public static final Color ERROR_COLOR = new Color(244, 67, 54);         // Rouge erreur
    
    // Polices modernes
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Formatage des nombres
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("0.00 €");
    
    /**
     * Crée un bouton stylisé moderne avec coins arrondis
     * @param text le texte du bouton
     * @param primary true pour un bouton principal, false pour un bouton secondaire
     * @return le bouton stylisé
     */
    public static JButton createButton(String text, boolean primary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(primary ? PRIMARY_COLOR.darker() : LIGHT_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(primary ? SECONDARY_COLOR : LIGHT_COLOR);
                } else {
                    g2.setColor(primary ? PRIMARY_COLOR : WHITE_COLOR);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                if (!primary) {
                    g2.setColor(PRIMARY_COLOR);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        if (primary) {
            button.setForeground(WHITE_COLOR);
        } else {
            button.setForeground(PRIMARY_COLOR);
        }
        
        // Ajouter une marge intérieure au bouton
        button.setMargin(new Insets(8, 15, 8, 15));
        
        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    /**
     * Crée un panel avec une bordure et un titre moderne
     * @param title le titre du panel
     * @return le panel stylisé
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE_COLOR);
        
        // Créer un panneau pour le titre avec couleur de fond
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(WHITE_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        titlePanel.add(titleLabel);
        
        // Créer un panneau pour le contenu avec bordure
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(WHITE_COLOR);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Ajouter les panneaux
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée un champ de texte stylisé moderne
     * @param placeholder le texte de placeholder
     * @param columns le nombre de colonnes
     * @return le champ de texte stylisé
     */
    public static JTextField createTextField(String placeholder, int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(REGULAR_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Implementer un placeholder simple
        if (placeholder != null && !placeholder.isEmpty()) {
            textField.setText(placeholder);
            textField.setForeground(Color.GRAY);
            
            textField.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (textField.getText().equals(placeholder)) {
                        textField.setText("");
                        textField.setForeground(TEXT_COLOR);
                    }
                }
                
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (textField.getText().isEmpty()) {
                        textField.setText(placeholder);
                        textField.setForeground(Color.GRAY);
                    }
                }
            });
        }
        
        return textField;
    }
    
    /**
     * Crée un ComboBox stylisé moderne
     * @param items les éléments du ComboBox
     * @return le ComboBox stylisé
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(REGULAR_FONT);
        comboBox.setBackground(WHITE_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(SECONDARY_COLOR);
                    c.setForeground(WHITE_COLOR);
                } else {
                    c.setBackground(WHITE_COLOR);
                    c.setForeground(TEXT_COLOR);
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });
        
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        return comboBox;
    }
    
    /**
     * Crée une table stylisée moderne
     * @param model le modèle de données
     * @return la table stylisée
     */
    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(REGULAR_FONT);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setGridColor(LIGHT_COLOR);
        table.setSelectionBackground(LIGHT_COLOR);
        table.setSelectionForeground(PRIMARY_COLOR);
        table.setFocusable(false);
        
        // Style alternance des lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    comp.setBackground(LIGHT_COLOR);
                    comp.setForeground(PRIMARY_COLOR);
                    comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                } else {
                    if (row % 2 == 0) {
                        comp.setBackground(WHITE_COLOR);
                    } else {
                        comp.setBackground(BACKGROUND_COLOR);
                    }
                    comp.setForeground(TEXT_COLOR);
                    comp.setFont(REGULAR_FONT);
                }
                
                // Ajouter une marge aux cellules
                ((JLabel) comp).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                
                return comp;
            }
        });
        
        // Styliser l'en-tête et toujours afficher les titres (sans survol)
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(WHITE_COLOR);
        header.setFont(SUBTITLE_FONT);
        header.setBorder(null);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(PRIMARY_COLOR);
                comp.setForeground(WHITE_COLOR);
                comp.setFont(SUBTITLE_FONT);
                ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                return comp;
            }
        });
        
        // Désactiver le redimensionnement des colonnes
        table.getTableHeader().setReorderingAllowed(false);
        
        return table;
    }
    
    /**
     * Affiche un message d'erreur stylisé
     * @param parent le composant parent
     * @param message le message d'erreur
     */
    public static void showError(Component parent, String message) {
        UIManager.put("OptionPane.background", WHITE_COLOR);
        UIManager.put("Panel.background", WHITE_COLOR);
        UIManager.put("OptionPane.messageForeground", ERROR_COLOR);
        UIManager.put("OptionPane.messageFont", SUBTITLE_FONT);
        UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
        
        JOptionPane optionPane = new JOptionPane(
            message,
            JOptionPane.ERROR_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[] { "OK" }
        );
        
        JDialog dialog = optionPane.createDialog(parent, "Erreur");
        dialog.setBackground(WHITE_COLOR);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Affiche un message d'information stylisé
     * @param parent le composant parent
     * @param message le message d'information
     */
    public static void showInfo(Component parent, String message) {
        UIManager.put("OptionPane.background", WHITE_COLOR);
        UIManager.put("Panel.background", WHITE_COLOR);
        UIManager.put("OptionPane.messageForeground", PRIMARY_COLOR);
        UIManager.put("OptionPane.messageFont", SUBTITLE_FONT);
        UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
        
        JOptionPane optionPane = new JOptionPane(
            message,
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[] { "OK" }
        );
        
        JDialog dialog = optionPane.createDialog(parent, "Information");
        dialog.setBackground(WHITE_COLOR);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Affiche un message de succès stylisé (pour les remises)
     * @param parent le composant parent
     * @param message le message de succès
     */
    public static void showSuccess(Component parent, String message) {
        UIManager.put("OptionPane.background", WHITE_COLOR);
        UIManager.put("Panel.background", WHITE_COLOR);
        UIManager.put("OptionPane.messageForeground", SUCCESS_COLOR);
        UIManager.put("OptionPane.messageFont", SUBTITLE_FONT);
        UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
        
        // Créer un panneau personnalisé avec une bordure verte
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SUCCESS_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Ajouter une icône de succès (checkmark)
        JLabel iconLabel = new JLabel("\u2713"); // Caractère Unicode pour une coche
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        iconLabel.setForeground(SUCCESS_COLOR);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Message de succès
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(SUBTITLE_FONT);
        messageLabel.setForeground(SUCCESS_COLOR);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        
        JOptionPane optionPane = new JOptionPane(
            panel,
            JOptionPane.PLAIN_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[] { "Super !" }
        );
        
        JDialog dialog = optionPane.createDialog(parent, "Remise Appliquée !");
        dialog.setBackground(WHITE_COLOR);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Affiche une boîte de dialogue de confirmation stylisée
     * @param parent le composant parent
     * @param message le message de confirmation
     * @return true si l'utilisateur confirme
     */
    public static boolean showConfirm(Component parent, String message) {
        UIManager.put("OptionPane.background", WHITE_COLOR);
        UIManager.put("Panel.background", WHITE_COLOR);
        UIManager.put("OptionPane.messageForeground", PRIMARY_COLOR);
        UIManager.put("OptionPane.messageFont", SUBTITLE_FONT);
        UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
        
        JOptionPane optionPane = new JOptionPane(
            message,
            JOptionPane.QUESTION_MESSAGE,
            JOptionPane.YES_NO_OPTION,
            null,
            new Object[] { "Oui", "Non" }
        );
        
        JDialog dialog = optionPane.createDialog(parent, "Confirmation");
        dialog.setBackground(WHITE_COLOR);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        Object selectedValue = optionPane.getValue();
        return selectedValue != null && "Oui".equals(selectedValue);
    }
}
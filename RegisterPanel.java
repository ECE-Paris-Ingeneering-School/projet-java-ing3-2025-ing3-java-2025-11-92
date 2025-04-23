package view;

import controller.UtilisateurController;
import model.Client;
import utils.SwingUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau d'inscription
 */
public class RegisterPanel extends JPanel {
    
    private MainFrame mainFrame;
    
    private JTextField nomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;
    
    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        
        // Configurer le panneau
        setLayout(new BorderLayout());
        setBackground(SwingUtils.WHITE_COLOR);
        
        // Créer les composants
        initializeComponents();
    }
    
    /**
     * Initialise les composants du panneau
     */
    private void initializeComponents() {
        // Panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        // Panneau du logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(SwingUtils.PRIMARY_COLOR);
        
        JLabel titleLabel = new JLabel("Shopping App");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(SwingUtils.WHITE_COLOR);
        logoPanel.add(titleLabel);
        
        // Panneau de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(SwingUtils.WHITE_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        
        // Titre
        JLabel registerTitle = new JLabel("Inscription");
        registerTitle.setFont(SwingUtils.TITLE_FONT);
        registerTitle.setForeground(SwingUtils.PRIMARY_COLOR);
        registerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Champ nom
        JPanel nomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nomPanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setFont(SwingUtils.REGULAR_FONT);
        nomField = SwingUtils.createTextField("Entrez votre nom", 20);
        nomPanel.add(nomLabel);
        nomPanel.add(nomField);
        
        // Champ email
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emailPanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(SwingUtils.REGULAR_FONT);
        emailField = SwingUtils.createTextField("Entrez votre email", 20);
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        
        // Champ mot de passe
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setFont(SwingUtils.REGULAR_FONT);
        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SwingUtils.SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        
        // Champ confirmation mot de passe
        JPanel confirmPasswordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        confirmPasswordPanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel confirmPasswordLabel = new JLabel("Confirmer:");
        confirmPasswordLabel.setFont(SwingUtils.REGULAR_FONT);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SwingUtils.SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        confirmPasswordPanel.add(confirmPasswordLabel);
        confirmPasswordPanel.add(confirmPasswordField);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        registerButton = SwingUtils.createButton("S'inscrire", true);
        cancelButton = SwingUtils.createButton("Annuler", false);
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        // Ajouter les composants au panneau de formulaire
        formPanel.add(Box.createVerticalGlue());
        formPanel.add(registerTitle);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(nomPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(emailPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(confirmPasswordPanel);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalGlue());
        
        // Ajouter les actions des boutons
        registerButton.addActionListener(e -> inscrire());
        cancelButton.addActionListener(e -> mainFrame.afficherConnexion());
        
        // Ajouter les panneaux au panneau principal
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Ajouter le panneau principal à ce panneau
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Gère l'inscription
     */
    private void inscrire() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String motDePasse = new String(passwordField.getPassword());
        String confirmMotDePasse = new String(confirmPasswordField.getPassword());
        
        // Validation des champs
        if (nom.isEmpty() || email.isEmpty() || motDePasse.isEmpty() || confirmMotDePasse.isEmpty()) {
            SwingUtils.showError(this, "Veuillez remplir tous les champs");
            return;
        }
        
        if (!motDePasse.equals(confirmMotDePasse)) {
            SwingUtils.showError(this, "Les mots de passe ne correspondent pas");
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            SwingUtils.showError(this, "Email invalide");
            return;
        }
        
        if (motDePasse.length() < 6) {
            SwingUtils.showError(this, "Le mot de passe doit contenir au moins 6 caractères");
            return;
        }
        
        // Inscrire le client
        UtilisateurController utilisateurController = mainFrame.getUtilisateurController();
        Client client = utilisateurController.inscrireClient(nom, email, motDePasse);
        
        if (client != null) {
            // Vider les champs
            nomField.setText("");
            emailField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            
            // Connecter le client
            mainFrame.connecter(client);
        } else {
            SwingUtils.showError(this, "Erreur lors de l'inscription. L'email est peut-être déjà utilisé.");
        }
    }
}
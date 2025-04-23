package view;

import controller.UtilisateurController;
import model.Utilisateur;
import utils.SwingUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Panneau de connexion
 */
public class LoginPanel extends JPanel {
    
    private MainFrame mainFrame;
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    public LoginPanel(MainFrame mainFrame) {
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
        mainPanel.setBackground(SwingUtils.BACKGROUND_COLOR);
        
        // Panneau du logo - Header stylisé
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(SwingUtils.PRIMARY_COLOR);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Icône shopping cart (Unicode)
        JLabel iconLabel = new JLabel("\uD83D\uDED2"); // Unicode pour icône shopping cart
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setForeground(SwingUtils.WHITE_COLOR);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Shopping App");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(SwingUtils.WHITE_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Votre boutique en ligne");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitleLabel.setForeground(SwingUtils.WHITE_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoPanel.add(Box.createVerticalStrut(20));
        logoPanel.add(iconLabel);
        logoPanel.add(Box.createVerticalStrut(10));
        logoPanel.add(titleLabel);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(subtitleLabel);
        logoPanel.add(Box.createVerticalStrut(20));
        
        // Panneau de formulaire
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(SwingUtils.BACKGROUND_COLOR);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(SwingUtils.WHITE_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SwingUtils.LIGHT_COLOR, 1),
            BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        
        // Effet d'ombre pour la carte (panel)
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SwingUtils.LIGHT_COLOR, 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
            )
        ));
        
        // Titre
        JLabel loginTitle = new JLabel("Connexion");
        loginTitle.setFont(SwingUtils.TITLE_FONT);
        loginTitle.setForeground(SwingUtils.PRIMARY_COLOR);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Ligne de séparation sous le titre
        JSeparator separator = new JSeparator();
        separator.setForeground(SwingUtils.SECONDARY_COLOR);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        separator.setMaximumSize(new Dimension(250, 2));
        
        // Champ email - Style amélioré
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(SwingUtils.WHITE_COLOR);
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailPanel.setMaximumSize(new Dimension(400, 70));
        
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(SwingUtils.REGULAR_FONT);
        emailLabel.setForeground(SwingUtils.PRIMARY_COLOR);
        
        emailField = SwingUtils.createTextField("Entrez votre email", 20);
        emailField.setMaximumSize(new Dimension(400, 35));
        
        emailPanel.add(emailLabel);
        emailPanel.add(Box.createVerticalStrut(5));
        emailPanel.add(emailField);
        
        // Champ mot de passe - Style amélioré
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBackground(SwingUtils.WHITE_COLOR);
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordPanel.setMaximumSize(new Dimension(400, 70));
        
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(SwingUtils.REGULAR_FONT);
        passwordLabel.setForeground(SwingUtils.PRIMARY_COLOR);
        
        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, SwingUtils.SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setMaximumSize(new Dimension(400, 35));
        
        passwordPanel.add(passwordLabel);
        passwordPanel.add(Box.createVerticalStrut(5));
        passwordPanel.add(passwordField);
        
        // Boutons - Style amélioré
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(SwingUtils.WHITE_COLOR);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginButton = SwingUtils.createButton("Se connecter", true);
        registerButton = SwingUtils.createButton("S'inscrire", false);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        // Ajouter les composants au panneau de formulaire
        formPanel.add(loginTitle);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(separator);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(emailPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(buttonPanel);
        
        // Ajouter le formulaire au conteneur
        formContainer.add(formPanel);
        
        // Ajouter les actions des boutons
        loginButton.addActionListener(e -> connecter());
        registerButton.addActionListener(e -> mainFrame.afficherInscription());
        
        // Ajouter les entrées par touche entrée
        emailField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> connecter());
        
        // Ajouter les panneaux au panneau principal
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(formContainer, BorderLayout.CENTER);
        
        // Ajouter le panneau principal à ce panneau
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Gère la connexion
     */
    private void connecter() {
        String email = emailField.getText();
        String motDePasse = new String(passwordField.getPassword());
        
        if (email.isEmpty() || motDePasse.isEmpty()) {
            SwingUtils.showError(this, "Veuillez remplir tous les champs");
            return;
        }
        
        UtilisateurController utilisateurController = mainFrame.getUtilisateurController();
        Utilisateur utilisateur = utilisateurController.authentifier(email, motDePasse);
        
        if (utilisateur != null) {
            // Vider les champs
            emailField.setText("");
            passwordField.setText("");
            
            // Connecter l'utilisateur
            mainFrame.connecter(utilisateur);
        } else {
            SwingUtils.showError(this, "Email ou mot de passe incorrect");
        }
    }
}
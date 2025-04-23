package view;

import controller.UtilisateurController;
import model.Administrateur;
import model.Client;
import model.Utilisateur;
import utils.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Fenêtre principale de l'application
 */
public class MainFrame extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private ClientMainPanel clientMainPanel;
    private AdminMainPanel adminMainPanel;
    
    private JMenuBar menuBar;
    private JMenuItem logoutMenuItem;
    
    private UtilisateurController utilisateurController;
    
    public MainFrame() {
        super("Shopping App");
        
        // Initialiser le contrôleur
        utilisateurController = new UtilisateurController();
        
        // Configurer la fenêtre
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Gérer les événements de fermeture
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (UtilisateurController.getUtilisateurConnecte() != null) {
                    utilisateurController.deconnecter();
                }
            }
        });
        
        // Initialiser le layout principal
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Créer les différents panels
        initializePanels();
        
        // Créer la barre de menu
        createMenuBar();
        
        // Afficher la vue de connexion par défaut
        cardLayout.show(mainPanel, "login");
        
        setContentPane(mainPanel);
    }
    
    /**
     * Initialise les différents panels de l'application
     */
    private void initializePanels() {
        // Panneau de connexion
        loginPanel = new LoginPanel(this);
        mainPanel.add(loginPanel, "login");
        
        // Panneau d'inscription
        registerPanel = new RegisterPanel(this);
        mainPanel.add(registerPanel, "register");
        
        // Panneau principal client (créé dynamiquement à la connexion)
        
        // Panneau principal admin (créé dynamiquement à la connexion)
    }
    
    /**
     * Crée la barre de menu
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(SwingUtils.PRIMARY_COLOR);
        
        // Menu Application
        JMenu appMenu = new JMenu("Application");
        appMenu.setForeground(SwingUtils.WHITE_COLOR);
        
        // Option de déconnexion
        logoutMenuItem = new JMenuItem("Déconnexion");
        logoutMenuItem.addActionListener(e -> {
            if (SwingUtils.showConfirm(this, "Voulez-vous vraiment vous déconnecter ?")) {
                deconnecter();
            }
        });
        
        // Option de quitter
        JMenuItem exitMenuItem = new JMenuItem("Quitter");
        exitMenuItem.addActionListener(e -> {
            if (SwingUtils.showConfirm(this, "Voulez-vous vraiment quitter l'application ?")) {
                if (UtilisateurController.getUtilisateurConnecte() != null) {
                    utilisateurController.deconnecter();
                }
                System.exit(0);
            }
        });
        
        appMenu.add(logoutMenuItem);
        appMenu.addSeparator();
        appMenu.add(exitMenuItem);
        
        menuBar.add(appMenu);
        
        // Ajouter la barre de menu à la fenêtre
        setJMenuBar(menuBar);
        
        // Cacher l'option de déconnexion par défaut
        logoutMenuItem.setVisible(false);
    }
    
    /**
     * Gère la connexion d'un utilisateur
     * @param utilisateur l'utilisateur connecté
     */
    public void connecter(Utilisateur utilisateur) {
        if (utilisateur instanceof Client) {
            // Créer le panneau client s'il n'existe pas
            if (clientMainPanel == null) {
                clientMainPanel = new ClientMainPanel(this, (Client) utilisateur);
                mainPanel.add(clientMainPanel, "client");
            } else {
                clientMainPanel.setClient((Client) utilisateur);
            }
            
            // Afficher le panneau client
            cardLayout.show(mainPanel, "client");
        } else if (utilisateur instanceof Administrateur) {
            // Créer le panneau admin s'il n'existe pas
            if (adminMainPanel == null) {
                adminMainPanel = new AdminMainPanel(this, (Administrateur) utilisateur);
                mainPanel.add(adminMainPanel, "admin");
            } else {
                adminMainPanel.setAdmin((Administrateur) utilisateur);
            }
            
            // Afficher le panneau admin
            cardLayout.show(mainPanel, "admin");
        }
        
        // Afficher l'option de déconnexion
        logoutMenuItem.setVisible(true);
    }
    
    /**
     * Gère la déconnexion de l'utilisateur
     */
    public void deconnecter() {
        utilisateurController.deconnecter();
        
        // Cacher l'option de déconnexion
        logoutMenuItem.setVisible(false);
        
        // Retourner à l'écran de connexion
        cardLayout.show(mainPanel, "login");
    }
    
    /**
     * Affiche le panneau d'inscription
     */
    public void afficherInscription() {
        cardLayout.show(mainPanel, "register");
    }
    
    /**
     * Affiche le panneau de connexion
     */
    public void afficherConnexion() {
        cardLayout.show(mainPanel, "login");
    }
    
    /**
     * Retourne le contrôleur des utilisateurs
     * @return le contrôleur des utilisateurs
     */
    public UtilisateurController getUtilisateurController() {
        return utilisateurController;
    }
}
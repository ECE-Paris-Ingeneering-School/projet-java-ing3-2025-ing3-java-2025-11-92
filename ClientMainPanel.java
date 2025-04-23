package view;

import controller.ArticleController;
import controller.CommandeController;
import controller.HistoriqueController;
import controller.PanierController;
import model.Article;
import model.Client;
import model.Commande;
import model.HistoriqueAction;
import utils.SwingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panneau principal pour les clients
 */
public class ClientMainPanel extends JPanel {
    
    private MainFrame mainFrame;
    private Client client;
    
    private ArticleController articleController;
    private PanierController panierController;
    private CommandeController commandeController;
    private HistoriqueController historiqueController;
    
    private JTabbedPane tabbedPane;
    private JPanel cataloguePanel;
    private JPanel panierPanel;
    private JPanel commandesPanel;
    private JPanel historiquePanel;
    
    private JComboBox<String> marqueComboBox;
    private JTable articleTable;
    private DefaultTableModel articleModel;
    private JSpinner quantiteSpinner;
    private JButton ajouterPanierButton;
    
    private JTable panierTable;
    private DefaultTableModel panierModel;
    private JLabel totalLabel;
    private JButton validerCommandeButton;
    private JButton viderPanierButton;
    
    private JTable commandesTable;
    private DefaultTableModel commandesModel;
    private JTextArea detailsCommandeArea;
    
    private JTable historiqueTable;
    private DefaultTableModel historiqueModel;
    
    public ClientMainPanel(MainFrame mainFrame, Client client) {
        this.mainFrame = mainFrame;
        this.client = client;
        
        // Initialiser les contrôleurs
        this.articleController = new ArticleController();
        this.panierController = new PanierController(client);
        this.commandeController = new CommandeController();
        this.historiqueController = new HistoriqueController();
        
        // Configurer le panneau
        setLayout(new BorderLayout());
        
        // Créer les composants
        initializeComponents();
        
        // Charger les données
        chargerDonnees();
    }
    
    /**
     * Initialise les composants du panneau
     */
    private void initializeComponents() {
        // Panneau d'en-tête
        JPanel headerPanel = createHeaderPanel();
        
        // Panneau à onglets
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(SwingUtils.SUBTITLE_FONT);
        tabbedPane.setBackground(SwingUtils.WHITE_COLOR);
        tabbedPane.setForeground(SwingUtils.PRIMARY_COLOR);
        
        // Onglet Catalogue
        cataloguePanel = createCataloguePanel();
        tabbedPane.addTab("Catalogue", new ImageIcon(), cataloguePanel);
        
        // Onglet Panier
        panierPanel = createPanierPanel();
        tabbedPane.addTab("Panier", new ImageIcon(), panierPanel);
        
        // Onglet Commandes
        commandesPanel = createCommandesPanel();
        tabbedPane.addTab("Mes commandes", new ImageIcon(), commandesPanel);
        
        // Onglet Historique
        historiquePanel = createHistoriquePanel();
        tabbedPane.addTab("Historique", new ImageIcon(), historiquePanel);
        
        // Ajouter les panneaux
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Crée le panneau d'en-tête
     * @return le panneau d'en-tête
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SwingUtils.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Panel gauche avec message de bienvenue
        JLabel welcomeLabel = new JLabel("Bienvenue, " + client.getNom());
        welcomeLabel.setFont(SwingUtils.TITLE_FONT);
        welcomeLabel.setForeground(SwingUtils.WHITE_COLOR);
        
        // Panel droit avec bouton de déconnexion
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false); // Pour garder le fond du header
        
        // Bouton de déconnexion stylisé
        JButton logoutButton = new JButton("Déconnexion") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(SwingUtils.ERROR_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(SwingUtils.ERROR_COLOR);
                } else {
                    g2.setColor(SwingUtils.ERROR_COLOR);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        logoutButton.setFont(SwingUtils.BUTTON_FONT);
        logoutButton.setForeground(SwingUtils.WHITE_COLOR);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setOpaque(false);
        logoutButton.setMargin(new Insets(5, 15, 5, 15));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Action du bouton de déconnexion
        logoutButton.addActionListener(e -> {
            if (SwingUtils.showConfirm(this, "Voulez-vous vraiment vous déconnecter ?")) {
                mainFrame.deconnecter();
            }
        });
        
        rightPanel.add(logoutButton);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Crée le panneau du catalogue
     * @return le panneau du catalogue
     */
    private JPanel createCataloguePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panneau de filtres
        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtrePanel.setBackground(SwingUtils.WHITE_COLOR);
        
        JLabel marqueLabel = new JLabel("Filtrer par marque:");
        marqueLabel.setFont(SwingUtils.SUBTITLE_FONT);
        
        marqueComboBox = new JComboBox<>();
        marqueComboBox.addItem("Toutes les marques");
        marqueComboBox.setFont(SwingUtils.REGULAR_FONT);
        marqueComboBox.addActionListener(e -> filtrerArticles());
        
        filtrePanel.add(marqueLabel);
        filtrePanel.add(marqueComboBox);
        
        // Tableau des articles
        String[] columns = {"ID", "Nom", "Marque", "Prix unitaire", "Prix gros", "Seuil gros", "Stock"};
        articleModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        articleTable = SwingUtils.createTable(articleModel);
        JScrollPane tableScrollPane = new JScrollPane(articleTable);
        
        // Panneau d'actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        JLabel quantiteLabel = new JLabel("Quantité:");
        quantiteLabel.setFont(SwingUtils.REGULAR_FONT);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        quantiteSpinner = new JSpinner(spinnerModel);
        quantiteSpinner.setFont(SwingUtils.REGULAR_FONT);
        
        ajouterPanierButton = SwingUtils.createButton("Ajouter au panier", true);
        ajouterPanierButton.addActionListener(e -> ajouterAuPanier());
        
        actionPanel.add(quantiteLabel);
        actionPanel.add(quantiteSpinner);
        actionPanel.add(ajouterPanierButton);
        
        // Ajouter les composants au panneau
        panel.add(filtrePanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crée le panneau du panier
     * @return le panneau du panier
     */
    private JPanel createPanierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tableau du panier
        String[] columns = {"Article", "Marque", "Quantité", "Prix unitaire", "Prix total"};
        panierModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        panierTable = SwingUtils.createTable(panierModel);
        JScrollPane tableScrollPane = new JScrollPane(panierTable);
        
        // Panneau de résumé
        JPanel resumePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        resumePanel.setBackground(SwingUtils.WHITE_COLOR);
        
        totalLabel = new JLabel("Total: 0.00 €");
        totalLabel.setFont(SwingUtils.SUBTITLE_FONT);
        totalLabel.setForeground(SwingUtils.PRIMARY_COLOR);
        
        resumePanel.add(totalLabel);
        
        // Panneau d'actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        viderPanierButton = SwingUtils.createButton("Vider le panier", false);
        viderPanierButton.addActionListener(e -> viderPanier());
        
        validerCommandeButton = SwingUtils.createButton("Valider la commande", true);
        validerCommandeButton.addActionListener(e -> validerCommande());
        
        actionPanel.add(viderPanierButton);
        actionPanel.add(validerCommandeButton);
        
        // Panneau du bas
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SwingUtils.WHITE_COLOR);
        bottomPanel.add(resumePanel, BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Ajouter les composants au panneau
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crée le panneau des commandes
     * @return le panneau des commandes
     */
    private JPanel createCommandesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tableau des commandes
        String[] columns = {"N° Commande", "Date", "Nombre d'articles", "Montant"};
        commandesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        commandesTable = SwingUtils.createTable(commandesModel);
        JScrollPane tableScrollPane = new JScrollPane(commandesTable);
        
        // Sélection d'une commande
        commandesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && commandesTable.getSelectedRow() != -1) {
                afficherDetailsCommande();
            }
        });
        
        // Panneau de détails
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(SwingUtils.WHITE_COLOR);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails de la commande"));
        
        detailsCommandeArea = new JTextArea(10, 40);
        detailsCommandeArea.setFont(SwingUtils.REGULAR_FONT);
        detailsCommandeArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(detailsCommandeArea);
        
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Séparation des panneaux
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailsPanel);
        splitPane.setDividerLocation(300);
        
        // Ajouter les composants au panneau
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée le panneau de l'historique
     * @return le panneau de l'historique
     */
    private JPanel createHistoriquePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tableau de l'historique
        String[] columns = {"Date", "Action"};
        historiqueModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historiqueTable = SwingUtils.createTable(historiqueModel);
        JScrollPane tableScrollPane = new JScrollPane(historiqueTable);
        
        // Ajouter les composants au panneau
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Charge les données initiales
     */
    private void chargerDonnees() {
        // Charger les marques
        List<String> marques = articleController.getAllMarques();
        for (String marque : marques) {
            marqueComboBox.addItem(marque);
        }
        
        // Charger tous les articles
        chargerArticles();
        
        // Charger le panier
        mettreAJourPanier();
        
        // Charger les commandes
        chargerCommandes();
        
        // Charger l'historique
        chargerHistorique();
    }
    
    /**
     * Charge les articles dans le tableau
     */
    private void chargerArticles() {
        // Vider le tableau
        articleModel.setRowCount(0);
        
        // Récupérer les articles
        List<Article> articles;
        String marqueSelectionnee = (String) marqueComboBox.getSelectedItem();
        
        if (marqueSelectionnee == null || "Toutes les marques".equals(marqueSelectionnee)) {
            articles = articleController.getAllArticles();
        } else {
            articles = articleController.getArticlesByMarque(marqueSelectionnee);
        }
        
        // Ajouter les articles au tableau
        for (Article article : articles) {
            Object[] row = {
                article.getId(),
                article.getNom(),
                article.getMarque(),
                SwingUtils.PRICE_FORMAT.format(article.getPrixUnitaire()),
                SwingUtils.PRICE_FORMAT.format(article.getPrixGros()),
                article.getSeuilGros(),
                article.getStock()
            };
            articleModel.addRow(row);
        }
    }
    
    /**
     * Filtre les articles selon la marque sélectionnée
     */
    private void filtrerArticles() {
        chargerArticles();
    }
    
    /**
     * Ajoute un article au panier
     */
    private void ajouterAuPanier() {
        int selectedRow = articleTable.getSelectedRow();
        
        if (selectedRow == -1) {
            SwingUtils.showError(this, "Veuillez sélectionner un article");
            return;
        }
        
        int articleId = (int) articleModel.getValueAt(selectedRow, 0);
        int quantite = (int) quantiteSpinner.getValue();
        
        boolean resultat = panierController.ajouterArticle(articleId, quantite, this);
        
        if (resultat) {
            // Le message de succès est déjà affiché si une remise est appliquée
            // Mais on affiche quand même un message simple si pas de remise
            if (quantite < articleController.getArticleById(articleId).getSeuilGros()) {
                SwingUtils.showInfo(this, "Article ajouté au panier");
            }
            mettreAJourPanier();
        } else {
            SwingUtils.showError(this, "Impossible d'ajouter l'article au panier");
        }
    }
    
    /**
     * Met à jour l'affichage du panier
     */
    private void mettreAJourPanier() {
        // Vider le tableau
        panierModel.setRowCount(0);
        
        // Récupérer le panier
        model.Panier panier = panierController.getPanier();
        
        // Ajouter les lignes au tableau
        for (model.LigneCommande ligne : panier.getLignes()) {
            model.Article article = ligne.getArticle();
            
            Object[] row = {
                article.getNom(),
                article.getMarque(),
                ligne.getQuantite(),
                SwingUtils.PRICE_FORMAT.format(article.getPrixUnitaire()),
                SwingUtils.PRICE_FORMAT.format(ligne.calculerPrix())
            };
            panierModel.addRow(row);
        }
        
        // Mettre à jour le total
        totalLabel.setText("Total: " + SwingUtils.PRICE_FORMAT.format(panier.calculerTotal()));
        
        // Activer/désactiver les boutons
        boolean panierVide = panier.estVide();
        validerCommandeButton.setEnabled(!panierVide);
        viderPanierButton.setEnabled(!panierVide);
    }
    
    /**
     * Vide le panier
     */
    private void viderPanier() {
        if (SwingUtils.showConfirm(this, "Voulez-vous vraiment vider votre panier ?")) {
            panierController.viderPanier();
            mettreAJourPanier();
            SwingUtils.showInfo(this, "Panier vidé");
        }
    }
    
    /**
     * Valide la commande
     */
    private void validerCommande() {
        if (SwingUtils.showConfirm(this, "Voulez-vous valider votre commande ?")) {
            Commande commande = commandeController.validerCommande(panierController.getPanier());
            
            if (commande != null) {
                SwingUtils.showInfo(this, "Commande validée avec succès");
                panierController.viderPanier();
                mettreAJourPanier();
                chargerCommandes();
                chargerHistorique();
                
                // Afficher la facture
                afficherFacture(commande);
            } else {
                SwingUtils.showError(this, "Erreur lors de la validation de la commande");
            }
        }
    }
    
    /**
     * Affiche la facture d'une commande
     * @param commande la commande
     */
    private void afficherFacture(Commande commande) {
        JDialog factureDialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Facture",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        factureDialog.setSize(500, 600);
        factureDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        JLabel titleLabel = new JLabel("FACTURE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(SwingUtils.PRIMARY_COLOR);
        
        JLabel infoLabel = new JLabel("<html>Shopping App<br>Commande #" + commande.getId() + 
                                      "<br>Date: " + commande.getDateCommande() + 
                                      "<br>Client: " + client.getNom() + "</html>");
        infoLabel.setFont(SwingUtils.REGULAR_FONT);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(infoLabel, BorderLayout.CENTER);
        
        // Tableau des articles
        String[] columns = {"Article", "Quantité", "Prix unitaire", "Prix total"};
        DefaultTableModel factureModel = new DefaultTableModel(columns, 0);
        JTable factureTable = SwingUtils.createTable(factureModel);
        JScrollPane tableScrollPane = new JScrollPane(factureTable);
        
        // Ajouter les lignes de commande
        double total = 0;
        for (model.LigneCommande ligne : commande.getLignesCommande()) {
            model.Article article = ligne.getArticle();
            double prixLigne = ligne.calculerPrix();
            total += prixLigne;
            
            Object[] row = {
                article.getNom() + " (" + article.getMarque() + ")",
                ligne.getQuantite(),
                SwingUtils.PRICE_FORMAT.format(article.getPrixUnitaire()),
                SwingUtils.PRICE_FORMAT.format(prixLigne)
            };
            factureModel.addRow(row);
        }
        
        // Pied de page
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        JLabel totalLabel = new JLabel("Total: " + SwingUtils.PRICE_FORMAT.format(total));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(SwingUtils.PRIMARY_COLOR);
        
        JButton fermerButton = SwingUtils.createButton("Fermer", true);
        fermerButton.addActionListener(e -> factureDialog.dispose());
        
        footerPanel.add(totalLabel, BorderLayout.NORTH);
        footerPanel.add(fermerButton, BorderLayout.EAST);
        
        // Ajouter les composants au panneau
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        factureDialog.setContentPane(panel);
        factureDialog.setVisible(true);
    }
    
    /**
     * Charge les commandes dans le tableau
     */
    private void chargerCommandes() {
        // Vider le tableau
        commandesModel.setRowCount(0);
        
        // Récupérer les commandes
        List<Commande> commandes = commandeController.getCommandesByClient(client.getId());
        
        // Ajouter les commandes au tableau
        for (Commande commande : commandes) {
            int nbArticles = 0;
            double montant = 0;
            
            for (model.LigneCommande ligne : commande.getLignesCommande()) {
                nbArticles += ligne.getQuantite();
                montant += ligne.calculerPrix();
            }
            
            Object[] row = {
                commande.getId(),
                commande.getDateCommande(),
                nbArticles,
                SwingUtils.PRICE_FORMAT.format(montant)
            };
            commandesModel.addRow(row);
        }
    }
    
    /**
     * Affiche les détails d'une commande
     */
    private void afficherDetailsCommande() {
        int selectedRow = commandesTable.getSelectedRow();
        
        if (selectedRow == -1) {
            return;
        }
        
        int commandeId = (int) commandesModel.getValueAt(selectedRow, 0);
        Commande commande = commandeController.getCommandeById(commandeId);
        
        if (commande == null) {
            return;
        }
        
        // Construire le texte des détails
        StringBuilder details = new StringBuilder();
        details.append("Commande #").append(commande.getId()).append("\n");
        details.append("Date: ").append(commande.getDateCommande()).append("\n\n");
        details.append("Articles commandés:\n");
        details.append("--------------------------------------------------\n");
        
        double total = 0;
        for (model.LigneCommande ligne : commande.getLignesCommande()) {
            model.Article article = ligne.getArticle();
            double prixLigne = ligne.calculerPrix();
            total += prixLigne;
            
            details.append(article.getNom()).append(" (").append(article.getMarque()).append(")\n");
            details.append("Quantité: ").append(ligne.getQuantite()).append("\n");
            details.append("Prix unitaire: ").append(SwingUtils.PRICE_FORMAT.format(article.getPrixUnitaire())).append("\n");
            
            if (ligne.getQuantite() >= article.getSeuilGros()) {
                int qteGros = ligne.getQuantite() / article.getSeuilGros();
                int qteNormale = ligne.getQuantite() % article.getSeuilGros();
                
                details.append("Remise gros: ").append(qteGros * article.getSeuilGros()).append(" article(s) à ");
                details.append(SwingUtils.PRICE_FORMAT.format(article.getPrixGros())).append(" l'unité\n");
                
                if (qteNormale > 0) {
                    details.append("Articles au tarif normal: ").append(qteNormale).append(" article(s) à ");
                    details.append(SwingUtils.PRICE_FORMAT.format(article.getPrixUnitaire())).append(" l'unité\n");
                }
            }
            
            details.append("Sous-total: ").append(SwingUtils.PRICE_FORMAT.format(prixLigne)).append("\n");
            details.append("--------------------------------------------------\n");
        }
        
        details.append("\nTotal: ").append(SwingUtils.PRICE_FORMAT.format(total));
        
        // Afficher les détails
        detailsCommandeArea.setText(details.toString());
    }
    
    /**
     * Charge l'historique dans le tableau
     */
    private void chargerHistorique() {
        // Vider le tableau
        historiqueModel.setRowCount(0);
        
        // Récupérer l'historique
        List<HistoriqueAction> actions = historiqueController.getActionsByUtilisateur(client.getId());
        
        // Ajouter les actions au tableau
        for (HistoriqueAction action : actions) {
            Object[] row = {
                action.getDateHeure(),
                action.getAction()
            };
            historiqueModel.addRow(row);
        }
    }
    
    /**
     * Met à jour le client
     * @param client le nouveau client
     */
    public void setClient(Client client) {
        this.client = client;
        
        // Mettre à jour le panier
        this.panierController = new PanierController(client);
        
        // Recharger les données
        chargerDonnees();
    }
}
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

// panneau principal du client
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
        
        // init des controllers
        this.articleController = new ArticleController();
        this.panierController = new PanierController(client);
        this.commandeController = new CommandeController();
        this.historiqueController = new HistoriqueController();
        
        // setup du panneau
        setLayout(new BorderLayout());
        
        // créer ts les composants
        initializeComponents();
        
        // on charge les données au démarrage
        chargerDonnees();
    }
    
    // méthode pour initialiser les composants
    private void initializeComponents() {
        // header panel
        JPanel headerPanel = createHeaderPanel();
        
        // création des tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(SwingUtils.SUBTITLE_FONT);
        tabbedPane.setBackground(SwingUtils.WHITE_COLOR);
        tabbedPane.setForeground(SwingUtils.PRIMARY_COLOR);
        
        // catalogue onglet
        cataloguePanel = createCataloguePanel();
        tabbedPane.addTab("Catalogue", new ImageIcon(), cataloguePanel);
        
        // panier onglet
        panierPanel = createPanierPanel();
        tabbedPane.addTab("Panier", new ImageIcon(), panierPanel);
        
        // commandes onglet
        commandesPanel = createCommandesPanel();
        tabbedPane.addTab("Mes commandes", new ImageIcon(), commandesPanel);
        
        // historique onglet
        historiquePanel = createHistoriquePanel();
        tabbedPane.addTab("Historique", new ImageIcon(), historiquePanel);
        
        // on ajoute tout au panneau
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    // crée le header (welcome + déconnexion)
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SwingUtils.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // partie gauche -> msg de bienvenue
        JLabel welcomeLabel = new JLabel("Bienvenue, " + client.getNom());
        welcomeLabel.setFont(SwingUtils.TITLE_FONT);
        welcomeLabel.setForeground(SwingUtils.WHITE_COLOR);
        
        // partie droite -> bouton deco
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false); // transparent
        
        // bouton deco stylé
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
        
        // action du bouton deco
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
    
    // crée panneau catalogue pr afficher articles
    private JPanel createCataloguePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // zone des filtres
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
        
        // table articles
        String[] columns = {"ID", "Nom", "Marque", "Prix unitaire", "Prix gros", "Seuil gros", "Stock"};
        articleModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // pas d'édition
            }
        };
        articleTable = SwingUtils.createTable(articleModel);
        JScrollPane tableScrollPane = new JScrollPane(articleTable);
        
        // panel actions (ajouter au panier)
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
        
        // assemble tout ça
        panel.add(filtrePanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // crée panneau pr voir/modifier le panier
    private JPanel createPanierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // table panier
        String[] columns = {"Article", "Marque", "Quantité", "Prix unitaire", "Prix total"};
        panierModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // on bloque l'édition
            }
        };
        panierTable = SwingUtils.createTable(panierModel);
        JScrollPane tableScrollPane = new JScrollPane(panierTable);
        
        // panel résumé (prix total)
        JPanel resumePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        resumePanel.setBackground(SwingUtils.WHITE_COLOR);
        
        totalLabel = new JLabel("Total: 0.00 €");
        totalLabel.setFont(SwingUtils.SUBTITLE_FONT);
        totalLabel.setForeground(SwingUtils.PRIMARY_COLOR);
        
        resumePanel.add(totalLabel);
        
        // panel boutons action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        viderPanierButton = SwingUtils.createButton("Vider le panier", false);
        viderPanierButton.addActionListener(e -> viderPanier());
        
        validerCommandeButton = SwingUtils.createButton("Valider la commande", true);
        validerCommandeButton.addActionListener(e -> validerCommande());
        
        actionPanel.add(viderPanierButton);
        actionPanel.add(validerCommandeButton);
        
        // bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(SwingUtils.WHITE_COLOR);
        bottomPanel.add(resumePanel, BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // tout mettre ensemble
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}
       
    
    // crée le panneau des commandes
private JPanel createCommandesPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(SwingUtils.WHITE_COLOR);
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // tableau des commandes
    String[] columns = {"N° Commande", "Date", "Nombre d'articles", "Montant"};
    commandesModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    commandesTable = SwingUtils.createTable(commandesModel);
    JScrollPane tableScrollPane = new JScrollPane(commandesTable);

    // sélection d'une commande => on affiche ses détails
    commandesTable.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() && commandesTable.getSelectedRow() != -1) {
            afficherDetailsCommande();
        }
    });

    // panneau pr afficher les détails d'une commande
    JPanel detailsPanel = new JPanel(new BorderLayout());
    detailsPanel.setBackground(SwingUtils.WHITE_COLOR);
    detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails de la commande"));

    detailsCommandeArea = new JTextArea(10, 40);
    detailsCommandeArea.setFont(SwingUtils.REGULAR_FONT);
    detailsCommandeArea.setEditable(false);
    JScrollPane detailsScrollPane = new JScrollPane(detailsCommandeArea);

    detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

    // on sépare table et détails
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailsPanel);
    splitPane.setDividerLocation(300);

    // on ajoute tout au panel
    panel.add(splitPane, BorderLayout.CENTER);

    return panel;
}

// crée le panneau historique
private JPanel createHistoriquePanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(SwingUtils.WHITE_COLOR);
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // tableau pr l'historique
    String[] columns = {"Date", "Action"};
    historiqueModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    historiqueTable = SwingUtils.createTable(historiqueModel);
    JScrollPane tableScrollPane = new JScrollPane(historiqueTable);

    panel.add(tableScrollPane, BorderLayout.CENTER);

    return panel;
}

// charge toutes les données nécessaires au lancement
private void chargerDonnees() {
    List<String> marques = articleController.getAllMarques();
    for (String marque : marques) {
        marqueComboBox.addItem(marque);
    }

    // On charge tout
    chargerArticles();
    mettreAJourPanier();
    chargerCommandes();
    chargerHistorique();
}

// On recharge les articles affichés
private void chargerArticles() {
    articleModel.setRowCount(0);

    List<Article> articles;
    String marqueSelectionnee = (String) marqueComboBox.getSelectedItem();

    if (marqueSelectionnee == null || "Toutes les marques".equals(marqueSelectionnee)) {
        articles = articleController.getAllArticles();
    } else {
        articles = articleController.getArticlesByMarque(marqueSelectionnee);
    }

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

// Filtre les articles en fonction de la marque
private void filtrerArticles() {
    chargerArticles();
}

// ajoute l'article sélectionné au panier
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
        if (quantite < articleController.getArticleById(articleId).getSeuilGros()) {
            SwingUtils.showInfo(this, "Article ajouté au panier");
        }
        mettreAJourPanier();
    } else {
        SwingUtils.showError(this, "Impossible d'ajouter l'article au panier");
    }
}

// maj de l'affichage du panier (total, liste, boutons...)
private void mettreAJourPanier() {
    panierModel.setRowCount(0);

    model.Panier panier = panierController.getPanier();

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

    totalLabel.setText("Total: " + SwingUtils.PRICE_FORMAT.format(panier.calculerTotal()));

    boolean panierVide = panier.estVide();
    validerCommandeButton.setEnabled(!panierVide);
    viderPanierButton.setEnabled(!panierVide);
}

// vide totalement le panier
private void viderPanier() {
    if (SwingUtils.showConfirm(this, "Voulez-vous vraiment vider votre panier ?")) {
        panierController.viderPanier();
        mettreAJourPanier();
        SwingUtils.showInfo(this, "Panier vidé");
    }
}

// On valide la commande (création + vidage panier)
private void validerCommande() {
    if (SwingUtils.showConfirm(this, "Voulez-vous valider votre commande ?")) {
        Commande commande = commandeController.validerCommande(panierController.getPanier());

        if (commande != null) {
            SwingUtils.showInfo(this, "Commande validée avec succès");
            panierController.viderPanier();
            mettreAJourPanier();
            chargerCommandes();
            chargerHistorique();
            afficherFacture(commande);
        } else {
            SwingUtils.showError(this, "Erreur lors de la validation de la commande");
        }
    }
}

// affiche une facture pour une commande
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

    String[] columns = {"Article", "Quantité", "Prix unitaire", "Prix total"};
    DefaultTableModel factureModel = new DefaultTableModel(columns, 0);
    JTable factureTable = SwingUtils.createTable(factureModel);
    JScrollPane tableScrollPane = new JScrollPane(factureTable);

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

    JPanel footerPanel = new JPanel(new BorderLayout());
    footerPanel.setBackground(SwingUtils.WHITE_COLOR);

    JLabel totalLabel = new JLabel("Total: " + SwingUtils.PRICE_FORMAT.format(total));
    totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    totalLabel.setForeground(SwingUtils.PRIMARY_COLOR);

    JButton fermerButton = SwingUtils.createButton("Fermer", true);
    fermerButton.addActionListener(e -> factureDialog.dispose());

    footerPanel.add(totalLabel, BorderLayout.NORTH);
    footerPanel.add(fermerButton, BorderLayout.EAST);

    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(tableScrollPane, BorderLayout.CENTER);
    panel.add(footerPanel, BorderLayout.SOUTH);

    factureDialog.setContentPane(panel);
    factureDialog.setVisible(true);
}

// recharge les commandes dans le tableau
private void chargerCommandes() {
    commandesModel.setRowCount(0);

    List<Commande> commandes = commandeController.getCommandesByClient(client.getId());

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

// affiche les détails de la commande sélectionnée
private void afficherDetailsCommande() {
    int selectedRow = commandesTable.getSelectedRow();

    if (selectedRow == -1) return;

    int commandeId = (int) commandesModel.getValueAt(selectedRow, 0);
    Commande commande = commandeController.getCommandeById(commandeId);

    if (commande == null) return;

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
    detailsCommandeArea.setText(details.toString());
}

// recharge l'historique utilisateur
private void chargerHistorique() {
    historiqueModel.setRowCount(0);

    List<HistoriqueAction> actions = historiqueController.getActionsByUtilisateur(client.getId());

    for (HistoriqueAction action : actions) {
        Object[] row = {
            action.getDateHeure(),
            action.getAction()
        };
        historiqueModel.addRow(row);
    }
}

// maj du client et reload des données
public void setClient(Client client) {
    this.client = client;
    this.panierController = new PanierController(client);
    chargerDonnees();
}

package view;

import controller.ArticleController;
import controller.CommandeController;
import controller.HistoriqueController;
import controller.UtilisateurController;
import model.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import utils.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Panneau principal pour les administrateurs
 */
public class AdminMainPanel extends JPanel {
    
    private MainFrame mainFrame;
    private Administrateur admin;
    private ArticleController articleController;
    private CommandeController commandeController;
    private UtilisateurController utilisateurController;
    private HistoriqueController historiqueController;
    private JTabbedPane tabbedPane;
    private JPanel articlesPanel;
    private JPanel clientsPanel;
    private JPanel commandesPanel;
    private JPanel statistiquesPanel;
    private JPanel historiquePanel;
    private JTable articlesTable;
    private DefaultTableModel articlesModel;
    private JTable clientsTable;
    private DefaultTableModel clientsModel;
    private JTable commandesTable;
    private DefaultTableModel commandesModel;
    private JTextArea detailsCommandeArea;
    private JTable historiqueTable;
    private DefaultTableModel historiqueModel;
    public AdminMainPanel(MainFrame mainFrame, Administrateur admin) {
        this.mainFrame = mainFrame;
        this.admin = admin;
        
        this.articleController = new ArticleController(); // Initialise les controllers
        this.commandeController = new CommandeController();
        this.utilisateurController = new UtilisateurController();
        this.historiqueController = new HistoriqueController();
        
        
        setLayout(new BorderLayout()); // Configure le panneau
        initializeComponents(); // Crée les composants
        chargerDonnees();// Charge les données
    }
    
    private void initializeComponents() {
        
        JPanel headerPanel = createHeaderPanel(); //Haut de page 
        
       
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(SwingUtils.SUBTITLE_FONT);  // onglets
        tabbedPane.setBackground(SwingUtils.WHITE_COLOR);
        tabbedPane.setForeground(SwingUtils.PRIMARY_COLOR);
        
        // Onglet Articles
        articlesPanel = createArticlesPanel();
        tabbedPane.addTab("Gestion des articles", new ImageIcon(), articlesPanel);
        // Onglet Clients
        clientsPanel = createClientsPanel();
        tabbedPane.addTab("Clients", new ImageIcon(), clientsPanel);
        // Onglet Commandes
        commandesPanel = createCommandesPanel();
        tabbedPane.addTab("Commandes", new ImageIcon(), commandesPanel);
        
        // Onglet Statistiques
        statistiquesPanel = createStatistiquesPanel();
        tabbedPane.addTab("Statistiques", new ImageIcon(), statistiquesPanel);
        // Onglet Historique
        historiquePanel = createHistoriquePanel();
        tabbedPane.addTab("Historique", new ImageIcon(), historiquePanel);
        // Ajouter les panneaux
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SwingUtils.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        // Panel gauche avec titre d'administration
        JLabel welcomeLabel = new JLabel("Administration - " + admin.getNom());
        welcomeLabel.setFont(SwingUtils.TITLE_FONT);
        welcomeLabel.setForeground(SwingUtils.WHITE_COLOR);
        // Panel droit avec bouton de déconnexion
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false); // Pour garder le fond du header
        
        // Bouton de déconnexion stylisé
        JButton logoutButton = new JButton("Déconnexion") { // Bouton déconnexion
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
    private JPanel createArticlesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tableau des articles
        String[] columns = {"ID", "Nom", "Marque", "Prix unitaire", "Prix gros", "Seuil gros", "Stock", "Image"};
        articlesModel = new DefaultTableModel(columns, 0) {  // Tableau des articles
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        articlesTable = SwingUtils.createTable(articlesModel);
        JScrollPane tableScrollPane = new JScrollPane(articlesTable);
        
        // Panneau d'actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        JButton ajouterButton = SwingUtils.createButton("Ajouter un article", true);
        ajouterButton.addActionListener(e -> afficherFormulaireArticle(null));
        
        JButton modifierButton = SwingUtils.createButton("Modifier", true);
        modifierButton.addActionListener(e -> {
            int selectedRow = articlesTable.getSelectedRow();
            if (selectedRow == -1) {
                SwingUtils.showError(this, "Veuillez sélectionner un article");
                return;
            }
            
            int articleId = (int) articlesModel.getValueAt(selectedRow, 0);
            Article article = articleController.getArticleById(articleId);
            
            if (article != null) {
                afficherFormulaireArticle(article);
            }
        });
        
        JButton supprimerButton = SwingUtils.createButton("Supprimer", false);
        supprimerButton.addActionListener(e -> {
            int selectedRow = articlesTable.getSelectedRow();
            if (selectedRow == -1) {
                SwingUtils.showError(this, "Veuillez sélectionner un article");
                return;
            }
            
            int articleId = (int) articlesModel.getValueAt(selectedRow, 0);
            
            if (SwingUtils.showConfirm(this, "Voulez-vous vraiment supprimer cet article ?")) {
                boolean resultat = articleController.supprimerArticle(articleId);
                
                if (resultat) {
                    SwingUtils.showInfo(this, "Article supprimé avec succès");
                    chargerArticles();
                } else {
                    SwingUtils.showError(this, "Erreur lors de la suppression de l'article");
                }
            }
        });
        
        actionPanel.add(ajouterButton);
        actionPanel.add(modifierButton);
        actionPanel.add(supprimerButton);
        panel.add(tableScrollPane, BorderLayout.CENTER); //Ajout composant
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = {"ID", "Nom", "Email", "Client ancien"}; //Tableau vu client
        clientsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientsTable = SwingUtils.createTable(clientsModel);
        JScrollPane tableScrollPane = new JScrollPane(clientsTable);
        // Ajouter les composants au panneau
        panel.add(tableScrollPane, BorderLayout.CENTER);
        return panel;
    }
    private JPanel createCommandesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tableau des commandes passées
        String[] columns = {"N° Commande", "Client", "Date", "Nombre d'articles", "Montant"};
        commandesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        commandesTable = SwingUtils.createTable(commandesModel);
        JScrollPane tableScrollPane = new JScrollPane(commandesTable);
        commandesTable.getSelectionModel().addListSelectionListener(e -> { //Selection commande
            if (!e.getValueIsAdjusting() && commandesTable.getSelectedRow() != -1) {
                afficherDetailsCommande();
            }
        });//details sur commande 
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(SwingUtils.WHITE_COLOR);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails de la commande"));
        detailsCommandeArea = new JTextArea(10, 40);
        detailsCommandeArea.setFont(SwingUtils.REGULAR_FONT);
        detailsCommandeArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(detailsCommandeArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailsPanel); //Separation des panneaux
        splitPane.setDividerLocation(300);
        
        // Ajoute les composants au panneau
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatistiquesPanel() { //Panneau stats
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panneau des graphiques
        JPanel graphiquesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        graphiquesPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        JPanel panelCamembert = SwingUtils.createTitledPanel("Ventes par article");
        
        JPanel panelHistogramme = SwingUtils.createTitledPanel("Top clients");
        
        graphiquesPanel.add(panelCamembert);
        graphiquesPanel.add(panelHistogramme);
        
        panel.add(graphiquesPanel, BorderLayout.CENTER);
        return panel;
    }
 
    private JPanel createHistoriquePanel() { //Historique Panel 
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = {"Date", "Utilisateur", "Action"}; //tableau
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
    
    private void chargerDonnees() {
        // Charger les articles
        chargerArticles();
        
        // Charger les clients
        chargerClients();
        
        // Charger les commandes
        chargerCommandes();
        
        // Charger les statistiques
        chargerStatistiques();
        
        // Charger l'historique
        chargerHistorique();
    }
    private void chargerArticles() {
        // Vider le tableau
        articlesModel.setRowCount(0);
        
        // Récupérer les articles
        List<Article> articles = articleController.getAllArticles();
        
        // Ajouter les articles au tableau
        for (Article article : articles) {
            String imageStatus = (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) ? "Oui" : "Non";
            Object[] row = {
                article.getId(),
                article.getNom(),
                article.getMarque(),
                SwingUtils.PRICE_FORMAT.format(article.getPrixUnitaire()),
                SwingUtils.PRICE_FORMAT.format(article.getPrixGros()),
                article.getSeuilGros(),
                article.getStock(),
                imageStatus
            };
            articlesModel.addRow(row);
        }
    }
    private void chargerClients() {
        // Vider le tableau
        clientsModel.setRowCount(0);
        
        // Récupérer les clients
        List<Client> clients = utilisateurController.getAllClients();
        
        // Ajouter les clients au tableau
        for (Client client : clients) {
            Object[] row = {
                client.getId(),
                client.getNom(),
                client.getEmail(),
                client.isEstAncienClient() ? "Oui" : "Non"
            };
            clientsModel.addRow(row);
        }
    }
    private void chargerCommandes() {
        // Vider le tableau
        commandesModel.setRowCount(0);
        
        // Récupérer les commandes
        List<Commande> commandes = commandeController.getAllCommandes();
        
        // Ajouter les commandes au tableau
        for (Commande commande : commandes) {
            int nbArticles = 0;
            double montant = 0;
            
            for (LigneCommande ligne : commande.getLignesCommande()) {
                nbArticles += ligne.getQuantite();
                montant += ligne.calculerPrix();
            }
            
            Client client = utilisateurController.getClientById(commande.getClientId());
            String nomClient = (client != null) ? client.getNom() : "Client " + commande.getClientId();
            
            Object[] row = {
                commande.getId(),
                nomClient,
                commande.getDateCommande(),
                nbArticles,
                SwingUtils.PRICE_FORMAT.format(montant)
            };
            commandesModel.addRow(row);
        }
    }
    private void chargerStatistiques() {
        // Graphique camembert des ventes par article
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        
        List<Object[]> ventesArticles = articleController.getVentesParArticle();
        for (Object[] vente : ventesArticles) {
            String nomArticle = (String) vente[0];
            int quantite = (int) vente[1];
            pieDataset.setValue(nomArticle, quantite);
        }
        
        JFreeChart pieChart = ChartFactory.createPieChart(
            "Ventes par article",
            pieDataset,
            true,
            true,
            false
        );
        
        PiePlot piePlot = (PiePlot) pieChart.getPlot();
        piePlot.setBackgroundPaint(SwingUtils.WHITE_COLOR);
        
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setPreferredSize(new Dimension(400, 300)); //Meilleur client graphique
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        
        List<Object[]> meilleursClients = commandeController.getMeilleursClients(10);
        for (Object[] client : meilleursClients) {
            String nomClient = (String) client[0];
            int nbCommandes = (int) client[1];
            int nbArticles = (int) client[2];
            
            barDataset.addValue(nbCommandes, "Commandes", nomClient);
            barDataset.addValue(nbArticles, "Articles", nomClient);
        }
        
        JFreeChart barChart = ChartFactory.createBarChart(
            "Top clients",
            "Client",
            "Nombre",
            barDataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        ChartPanel barChartPanel = new ChartPanel(barChart);
        barChartPanel.setPreferredSize(new Dimension(400, 300));
        
        // Ajouter les graphiques aux panneaux
        Component[] components = ((Container) statistiquesPanel.getComponent(0)).getComponents();
        ((JPanel) components[0]).add(pieChartPanel, BorderLayout.CENTER);
        ((JPanel) components[1]).add(barChartPanel, BorderLayout.CENTER);
    }
    private void chargerHistorique() {
        // Vider le tableau
        historiqueModel.setRowCount(0);
        
        // Récupérer l'historique
        List<HistoriqueAction> actions = historiqueController.getActionsAdministrateurs();
        
        // Ajouter les actions au tableau
        for (HistoriqueAction action : actions) {
            Object[] row = {
                action.getDateHeure(),
                action.getNomUtilisateur(),
                action.getAction()
            };
            historiqueModel.addRow(row);
        }
    }
    private void afficherFormulaireArticle(Article article) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                article == null ? "Ajouter un article" : "Modifier un article",
                Dialog.ModalityType.APPLICATION_MODAL //Formulaire artcile
        );

        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(SwingUtils.WHITE_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Champs du formulaire
        JTextField nomField = SwingUtils.createTextField("", 20);
        JTextField marqueField = SwingUtils.createTextField("", 20);
        JTextField prixUnitaireField = SwingUtils.createTextField("", 20);
        JTextField prixGrosField = SwingUtils.createTextField("", 20);
        JSpinner seuilGrosSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        
        // Champ pour l'image
        JTextField imageUrlField = SwingUtils.createTextField("", 20);
        JLabel imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(150, 150));
        imagePreview.setBorder(BorderFactory.createLineBorder(SwingUtils.LIGHT_COLOR));
        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setText("Aucune image");
        
        // Si on modifie un article existant, remplir les champs
        if (article != null) {
            nomField.setText(article.getNom());
            marqueField.setText(article.getMarque());
            prixUnitaireField.setText(String.valueOf(article.getPrixUnitaire()));
            prixGrosField.setText(String.valueOf(article.getPrixGros()));
            seuilGrosSpinner.setValue(article.getSeuilGros());
            stockSpinner.setValue(article.getStock());
            
            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                imageUrlField.setText(article.getImageUrl());
                try {
                    File imageFile = new File(article.getImageUrl());
                    if (imageFile.exists()) {
                        ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                        Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                        imagePreview.setIcon(new ImageIcon(image));
                        imagePreview.setText("");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                }
            }
        }
        
        // Création des panneaux pour chaque champ
        JPanel nomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nomPanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setPreferredSize(new Dimension(100, 20));
        nomPanel.add(nomLabel);
        nomPanel.add(nomField);
        
        JPanel marquePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        marquePanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel marqueLabel = new JLabel("Marque:");
        marqueLabel.setPreferredSize(new Dimension(100, 20));
        marquePanel.add(marqueLabel);
        marquePanel.add(marqueField);
        
        JPanel prixUnitairePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prixUnitairePanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel prixUnitaireLabel = new JLabel("Prix unitaire:");
        prixUnitaireLabel.setPreferredSize(new Dimension(100, 20));
        prixUnitairePanel.add(prixUnitaireLabel);
        prixUnitairePanel.add(prixUnitaireField);
        
        JPanel prixGrosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prixGrosPanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel prixGrosLabel = new JLabel("Prix gros:");
        prixGrosLabel.setPreferredSize(new Dimension(100, 20));
        prixGrosPanel.add(prixGrosLabel);
        prixGrosPanel.add(prixGrosField);
        
        JPanel seuilGrosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        seuilGrosPanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel seuilGrosLabel = new JLabel("Seuil gros:");
        seuilGrosLabel.setPreferredSize(new Dimension(100, 20));
        seuilGrosPanel.add(seuilGrosLabel);
        seuilGrosPanel.add(seuilGrosSpinner);
        
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockPanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel stockLabel = new JLabel("Stock:");
        stockLabel.setPreferredSize(new Dimension(100, 20));
        stockPanel.add(stockLabel);
        stockPanel.add(stockSpinner);
        
        // Panneau pour l'image
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.setBackground(SwingUtils.WHITE_COLOR);
        JLabel imageLabel = new JLabel("Image URL:");
        imageLabel.setPreferredSize(new Dimension(100, 20));
        imagePanel.add(imageLabel);
        imagePanel.add(imageUrlField);
        
        // Bouton de sélection d'image
        JButton selectImageButton = SwingUtils.createButton("Parcourir...", false);
        selectImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
            
            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String fileName = selectedFile.getName();
                String destinationPath = "/mnt/c/Users/erwan/IdeaProjects/ProjetShopping/resources/images/articles/" + fileName;
                
                // Copier le fichier dans le dossier de l'application
                try {
                    Files.copy(selectedFile.toPath(), new File(destinationPath).toPath(), 
                              StandardCopyOption.REPLACE_EXISTING);
                    
                    imageUrlField.setText(destinationPath);
                    
                    // Mettre à jour l'aperçu
                    ImageIcon icon = new ImageIcon(destinationPath);
                    Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imagePreview.setIcon(new ImageIcon(image));
                    imagePreview.setText("");
                } catch (IOException ex) {
                    System.err.println("Erreur lors de la copie de l'image: " + ex.getMessage());
                    SwingUtils.showError(dialog, "Erreur lors de la copie de l'image");
                }
            }
        });
        imagePanel.add(selectImageButton);
        
        // Panneau pour l'aperçu de l'image
        JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        previewPanel.setBackground(SwingUtils.WHITE_COLOR);
        previewPanel.add(imagePreview);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(SwingUtils.WHITE_COLOR);
        
        JButton cancelButton = SwingUtils.createButton("Annuler", false);
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = SwingUtils.createButton("Enregistrer", true);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validation des champs
                String nom = nomField.getText().trim();
                String marque = marqueField.getText().trim();
                String prixUnitaireStr = prixUnitaireField.getText().trim();
                String prixGrosStr = prixGrosField.getText().trim();
                int seuilGros = (int) seuilGrosSpinner.getValue();
                int stock = (int) stockSpinner.getValue();
                
                if (nom.isEmpty() || marque.isEmpty() || prixUnitaireStr.isEmpty() || prixGrosStr.isEmpty()) {
                    SwingUtils.showError(dialog, "Veuillez remplir tous les champs");
                    return;
                }
                double prixUnitaire;
                double prixGros;
                try {
                    prixUnitaire = Double.parseDouble(prixUnitaireStr);
                    prixGros = Double.parseDouble(prixGrosStr);
                } catch (NumberFormatException ex) {
                    SwingUtils.showError(dialog, "Les prix doivent être des nombres valides");
                    return;
                }
                
                if (prixUnitaire <= 0 || prixGros <= 0) {
                    SwingUtils.showError(dialog, "Les prix doivent être positifs");
                    return;
                }
                if (prixGros >= prixUnitaire) {
                    SwingUtils.showError(dialog, "Le prix de gros doit être inférieur au prix unitaire");
                    return;
                }
                // Créer ou mettre à jour l'article
                if (article == null) {
                    // Nouvel article
                    Article nouvelArticle = new Article();
                    nouvelArticle.setNom(nom);
                    nouvelArticle.setMarque(marque);
                    nouvelArticle.setPrixUnitaire(prixUnitaire);
                    nouvelArticle.setPrixGros(prixGros);
                    nouvelArticle.setSeuilGros(seuilGros);
                    nouvelArticle.setStock(stock);
                    
                    // Récupérer l'URL de l'image
                    String imageUrl = imageUrlField.getText().trim();
                    nouvelArticle.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);
                    
                    boolean resultat = articleController.ajouterArticle(nouvelArticle);
                    
                    if (resultat) {
                        SwingUtils.showInfo(dialog, "Article ajouté avec succès");
                        dialog.dispose();
                        chargerArticles();
                    } else {
                        SwingUtils.showError(dialog, "Erreur lors de l'ajout de l'article");
                    }
                } else {
                    // Mise à jour de l'article
                    article.setNom(nom);
                    article.setMarque(marque);
                    article.setPrixUnitaire(prixUnitaire);
                    article.setPrixGros(prixGros);
                    article.setSeuilGros(seuilGros);
                    article.setStock(stock);
                    
                    // Récupérer l'URL de l'image
                    String imageUrl = imageUrlField.getText().trim();
                    article.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);
                    
                    boolean resultat = articleController.mettreAJourArticle(article);
                    
                    if (resultat) {
                        SwingUtils.showInfo(dialog, "Article mis à jour avec succès");
                        dialog.dispose();
                        chargerArticles();
                    } else {
                        SwingUtils.showError(dialog, "Erreur lors de la mise à jour de l'article");
                    }
                }
            }
        });
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        // Ajouter les composants au panneau
        panel.add(nomPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(marquePanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(prixUnitairePanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(prixGrosPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(seuilGrosPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(stockPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(imagePanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(previewPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(buttonPanel);
        
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
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
        details.append("Date: ").append(commande.getDateCommande()).append("\n");
        
        Client client = utilisateurController.getClientById(commande.getClientId());
        details.append("Client: ").append(client != null ? client.getNom() : "Client " + commande.getClientId()).append("\n\n");
        
        details.append("Articles commandés:\n");
        details.append("--------------------------------------------------\n");//Affiche détails d une commande
        
        double total = 0;
        for (LigneCommande ligne : commande.getLignesCommande()) {
            Article article = ligne.getArticle();
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
    public void setAdmin(Administrateur admin) {
        this.admin = admin;
        // Recharger les données
        chargerDonnees();
    }
}

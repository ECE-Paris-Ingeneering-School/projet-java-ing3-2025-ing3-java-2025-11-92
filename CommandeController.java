package controller;

import dao.CommandeDAO;
import dao.HistoriqueActionDAO;
import model.*;

import java.util.List;

// Controller gérant les actions liées aux commandes
public class CommandeController {
    private CommandeDAO commandeDAO;
    private HistoriqueActionDAO historiqueActionDAO;
    private ArticleController articleController;

    public CommandeController() {
        this.commandeDAO = new CommandeDAO();
        this.historiqueActionDAO = new HistoriqueActionDAO();
        this.articleController = new ArticleController();
    }

    // Valide et enregistre une commande à partir d'un panier

    public Commande validerCommande(Panier panier) {
        // Vérifier que le panier n'est pas vide
        if (panier.estVide()) {
            return null;
        }

        // Vérifier les stocks pour chaque article
        for (LigneCommande ligne : panier.getLignes()) {
            if (!articleController.verifierStock(ligne.getArticleId(), ligne.getQuantite())) {
                return null; // Cas pour stock insuffisant
            }
        }

        // Créer la commande à partir du panier
        Commande commande = panier.convertirEnCommande();

        // Enregistre la commande en BDD
        boolean resultat = commandeDAO.ajouterCommande(commande);

        if (resultat) {
            // Enregistrer l'action dans l'historique en la stockant
            Utilisateur utilisateur = UtilisateurController.getUtilisateurConnecte();
            if (utilisateur != null) {
                HistoriqueAction action = new HistoriqueAction(
                        utilisateur.getId(),
                        "Création de la commande #" + commande.getId() + " avec " + panier.getNombreArticles() + " articles"
                );
                historiqueActionDAO.ajouterAction(action);
            }

            return commande;
        }

        return null;
    }

    // Récupère les commandes d'un client
    public List<Commande> getCommandesByClient(int clientId) {
        return commandeDAO.getCommandesByClient(clientId);
    }

    // Récupère toutes les commandes
    public List<Commande> getAllCommandes() {
        return commandeDAO.getAllCommandes();
    }

    // Récupère les détails d'une commande
    public Commande getCommandeById(int commandeId) {
        return commandeDAO.getCommandeById(commandeId);
    }

    // Récupère les données pour le graphique des meilleurs clients
    public List<Object[]> getMeilleursClients(int limit) {
        return commandeDAO.getMeilleursClients(limit);
    }
}

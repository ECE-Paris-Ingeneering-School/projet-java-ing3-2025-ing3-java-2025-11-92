package model;

/**
 * Classe représentant un client du système
 * Hérite des propriétés de base d'un utilisateur avec des attributs spécifiques
 */
public class Client extends Utilisateur {
    // Indique si le client a déjà effectué des achats précédemment
    private boolean estAncienClient;

    // Constructeur par défaut
    public Client() {
        super();
    }

    // Constructeur avec tous les paramètres, utilisant le constructeur parent
    public Client(int id, String nom, String email, String motDePasse, boolean estAncienClient) {
        super(id, nom, email, motDePasse);
        this.estAncienClient = estAncienClient;
    }

    // Accesseur pour vérifier si c'est un ancien client
    public boolean isEstAncienClient() {
        return estAncienClient;
    }

    // Mutateur pour modifier le statut d'ancien client
    public void setEstAncienClient(boolean estAncienClient) {
        this.estAncienClient = estAncienClient;
    }

    // Redéfinition de la méthode toString pour afficher les infos du client
    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +       // Utilise le getter de la classe parent
                ", nom='" + getNom() + '\'' +    // Utilise le getter de la classe parent
                ", email='" + getEmail() + '\'' +    // Utilise le getter de la classe parent
                ", estAncienClient=" + estAncienClient +   // Attribut spécifique à Client
                '}';
    }
}

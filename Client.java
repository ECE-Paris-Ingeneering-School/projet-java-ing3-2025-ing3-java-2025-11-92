package model;

/**
 * Classe représentant un client du système
 */
public class Client extends Utilisateur {
    private boolean estAncienClient;

    public Client() {
        super();
    }

    public Client(int id, String nom, String email, String motDePasse, boolean estAncienClient) {
        super(id, nom, email, motDePasse);
        this.estAncienClient = estAncienClient;
    }

    public boolean isEstAncienClient() {
        return estAncienClient;
    }

    public void setEstAncienClient(boolean estAncienClient) {
        this.estAncienClient = estAncienClient;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", estAncienClient=" + estAncienClient +
                '}';
    }
}
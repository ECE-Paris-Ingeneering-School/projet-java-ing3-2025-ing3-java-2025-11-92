package model;

/**
 * Classe représentant un administrateur du système
 */
public class Administrateur extends Utilisateur {

    public Administrateur() {
        super();
    }

    public Administrateur(int id, String nom, String email, String motDePasse) {
        super(id, nom, email, motDePasse);
    }

    @Override
    public String toString() {
        return "Administrateur{" +
                "id=" + getId() +
                ", nom='" + getNom() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}

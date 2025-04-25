// RendezVousApp.java
import java.util.*;

public class RendezVousApp {
    public static void main(String[] args) {
        Application app = new Application();
        app.run();
    }
}

class Application {
    private List<Patient> patients = new ArrayList<>();
    private List<Specialiste> specialistes = new ArrayList<>();
    private List<RendezVous> rendezVousList = new ArrayList<>();

    public void run() {
        // Menu très simplifié
        System.out.println("Bienvenue dans l'application de rendez-vous spécialiste !");
        // Tu pourras ici ajouter : inscription, connexion, réservation, etc.
    }
}

class Patient {
    String id;
    String motDePasse;
    boolean estAncien;

    public Patient(String id, String motDePasse, boolean estAncien) {
        this.id = id;
        this.motDePasse = motDePasse;
        this.estAncien = estAncien;
    }
}

class Specialiste {
    String nom;
    String specialisation;
    List<String> lieux;

    public Specialiste(String nom, String specialisation, List<String> lieux) {
        this.nom = nom;
        this.specialisation = specialisation;
        this.lieux = lieux;
    }
}

class RendezVous {
    Patient patient;
    Specialiste specialiste;
    String dateHeure;
    String lieu;

    public RendezVous(Patient patient, Specialiste specialiste, String dateHeure, String lieu) {
        this.patient = patient;
        this.specialiste = specialiste;
        this.dateHeure = dateHeure;
        this.lieu = lieu;
    }
}

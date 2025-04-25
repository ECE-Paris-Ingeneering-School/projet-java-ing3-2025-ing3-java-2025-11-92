import java.util.*;

public class RendezVousSimple {
    static Scanner scanner = new Scanner(System.in);
    static Map<String, String> comptes = new HashMap<>();
    static Map<String, List<String>> rdvs = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("=== Application Rendez-vous Spécialiste ===");
        while (true) {
            System.out.println("\n1. Inscription\n2. Connexion\n3. Quitter");
            String choix = scanner.nextLine();

            if (choix.equals("1")) inscription();
            else if (choix.equals("2")) connexion();
            else if (choix.equals("3")) break;
            else System.out.println("Choix invalide.");
        }
    }

    static void inscription() {
        System.out.print("Identifiant : ");
        String id = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String mdp = scanner.nextLine();

        if (comptes.containsKey(id)) {
            System.out.println("Ce compte existe déjà.");
        } else {
            comptes.put(id, mdp);
            System.out.println("Inscription réussie !");
        }
    }

    static void connexion() {
        System.out.print("Identifiant : ");
        String id = scanner.nextLine();
        System.out.print("Mot de passe : ");
        String mdp = scanner.nextLine();

        if (mdp.equals(comptes.get(id))) {
            System.out.println("Connexion réussie !");
            menuPatient(id);
        } else {
            System.out.println("Identifiants incorrects.");
        }
    }

    static void menuPatient(String id) {
        while (true) {
            System.out.println("\n--- Menu Patient ---");
            System.out.println("1. Prendre rendez-vous\n2. Voir mes rendez-vous\n3. Déconnexion");
            String choix = scanner.nextLine();

            if (choix.equals("1")) prendreRdv(id);
            else if (choix.equals("2")) voirRdvs(id);
            else if (choix.equals("3")) break;
            else System.out.println("Choix invalide.");
        }
    }

    static void prendreRdv(String id) {
        System.out.print("Nom du spécialiste : ");
        String specialiste = scanner.nextLine();
        System.out.print("Date et heure (ex: 12/05 à 15h) : ");
        String dateHeure = scanner.nextLine();

        String rdv = specialiste + " - " + dateHeure;
        rdvs.computeIfAbsent(id, k -> new ArrayList<>()).add(rdv);

        System.out.println("Rendez-vous pris avec " + rdv);
    }

    static void voirRdvs(String id) {
        List<String> mesRdvs = rdvs.getOrDefault(id, new ArrayList<>());
        if (mesRdvs.isEmpty()) {
            System.out.println("Aucun rendez-vous trouvé.");
        } else {
            System.out.println("Vos rendez-vous :");
            for (String rdv : mesRdvs) {
                System.out.println("• " + rdv);
            }
        }
    }
}

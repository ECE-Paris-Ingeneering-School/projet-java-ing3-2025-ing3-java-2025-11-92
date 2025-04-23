-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mer. 23 avr. 2025 à 18:24
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `shopping_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `administrateur`
--

CREATE TABLE `administrateur` (
  `utilisateur_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `administrateur`
--

INSERT INTO `administrateur` (`utilisateur_id`) VALUES
(1);

-- --------------------------------------------------------

--
-- Structure de la table `article`
--

CREATE TABLE `article` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `marque` varchar(50) NOT NULL,
  `prix_unitaire` decimal(10,2) NOT NULL,
  `prix_gros` decimal(10,2) NOT NULL,
  `seuil_gros` int(11) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `image_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `article`
--

INSERT INTO `article` (`id`, `nom`, `marque`, `prix_unitaire`, `prix_gros`, `seuil_gros`, `stock`, `image_url`) VALUES
(1, 'Stylo à bille', 'Bic', 1.99, 1.50, 10, 100, 'C:\\xampp\\htdocs\\Shopping\\Stylo.jpeg'),
(2, 'Cahier spirale A4', 'Oxford', 4.99, 3.99, 5, 80, 'C:\\xampp\\htdocs\\Shopping\\Cahier.jpeg'),
(3, 'Classeur rigide', 'Exacompta', 7.50, 6.00, 5, 45, 'C:\\xampp\\htdocs\\Shopping\\Classeur.jpg'),
(4, 'Ordinateur portable', 'Dell', 799.99, 699.99, 3, 15, 'C:\\xampp\\htdocs\\Shopping\\Ordinateur.jpeg'),
(5, 'Écouteurs sans fil', 'Samsung', 129.99, 99.99, 3, 30, 'C:\\xampp\\htdocs\\Shopping\\Ecouteurs.jpeg'),
(6, 'Souris optique', 'Logitech', 24.99, 19.99, 5, 50, 'C:\\xampp\\htdocs\\Shopping\\Souris.jpeg'),
(7, 'Clavier mécanique', 'Corsair', 89.99, 79.99, 3, 25, 'C:\\xampp\\htdocs\\Shopping\\Clavier.jpeg'),
(8, 'Écran 24 pouces', 'Asus', 159.99, 149.99, 2, 20, 'C:\\xampp\\htdocs\\Shopping\\Ecran.jpeg'),
(9, 'Imprimante laser', 'HP', 199.99, 179.99, 2, 10, 'C:\\xampp\\htdocs\\Shopping\\Imprimante.jpeg'),
(10, 'Tablette 10 pouces', 'Apple', 329.99, 299.99, 2, 12, 'C:\\xampp\\htdocs\\Shopping\\Tablette.jpeg'),
(11, 'Chaussures de sport', 'Nike', 89.99, 79.99, 3, 40, 'C:\\xampp\\htdocs\\Shopping\\Nike.jpeg'),
(12, 'Baskets casual', 'Adidas', 79.99, 69.99, 3, 35, 'C:\\xampp\\htdocs\\Shopping\\Adidas.jpeg'),
(13, 'T-shirt coton', 'H&M', 14.99, 12.99, 5, 100, 'C:\\xampp\\htdocs\\Shopping\\Tshirt.jpg'),
(14, 'Jeans slim', 'Levi\'s', 99.99, 89.99, 3, 45, 'C:\\xampp\\htdocs\\Shopping\\Jean.jpg'),
(15, 'Veste légère', 'Zara', 49.99, 39.99, 3, 30, 'C:\\xampp\\htdocs\\Shopping\\Veste.jpeg');

-- --------------------------------------------------------

--
-- Structure de la table `client`
--

CREATE TABLE `client` (
  `utilisateur_id` int(11) NOT NULL,
  `est_ancien_client` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `client`
--

INSERT INTO `client` (`utilisateur_id`, `est_ancien_client`) VALUES
(2, 1),
(3, 0),
(4, 1),
(5, 1),
(6, 0);

-- --------------------------------------------------------

--
-- Structure de la table `commande`
--

CREATE TABLE `commande` (
  `id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `date_commande` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `commande`
--

INSERT INTO `commande` (`id`, `client_id`, `date_commande`) VALUES
(1, 2, '2025-01-15 09:30:00'),
(2, 3, '2025-01-20 13:45:00'),
(3, 4, '2025-02-01 08:15:00'),
(4, 5, '2025-02-10 15:20:00'),
(5, 2, '2025-02-15 10:00:00'),
(6, 4, '2025-02-28 12:30:00');

-- --------------------------------------------------------

--
-- Structure de la table `historique_action`
--

CREATE TABLE `historique_action` (
  `id` int(11) NOT NULL,
  `utilisateur_id` int(11) NOT NULL,
  `action` varchar(255) NOT NULL,
  `date_heure` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `historique_action`
--

INSERT INTO `historique_action` (`id`, `utilisateur_id`, `action`, `date_heure`) VALUES
(1, 1, 'Connexion au système', '2025-01-10 07:30:00'),
(2, 1, 'Ajout de l\'article \"Écouteurs sans fil\"', '2025-01-10 08:15:00'),
(3, 1, 'Modification du stock de l\'article \"Ordinateur portable\"', '2025-01-10 09:30:00'),
(4, 2, 'Connexion au système', '2025-01-15 09:00:00'),
(5, 2, 'Création de la commande #1', '2025-01-15 09:30:00'),
(6, 3, 'Inscription au système', '2025-01-20 13:30:00'),
(7, 3, 'Création de la commande #2', '2025-01-20 13:45:00'),
(8, 4, 'Connexion au système', '2025-02-01 08:00:00'),
(9, 4, 'Création de la commande #3', '2025-02-01 08:15:00'),
(10, 5, 'Connexion au système', '2025-02-10 15:00:00'),
(11, 5, 'Création de la commande #4', '2025-02-10 15:20:00'),
(12, 2, 'Connexion au système', '2025-02-15 09:45:00'),
(13, 2, 'Création de la commande #5', '2025-02-15 10:00:00'),
(14, 1, 'Connexion au système', '2025-02-20 07:30:00'),
(15, 1, 'Ajout de l\'article \"Baskets casual\"', '2025-02-20 08:00:00'),
(16, 4, 'Connexion au système', '2025-02-28 12:15:00'),
(17, 4, 'Création de la commande #6', '2025-02-28 12:30:00'),
(18, 1, 'Connexion au système', '2025-03-05 07:30:00'),
(19, 1, 'Consultation des statistiques de ventes', '2025-03-05 08:00:00'),
(20, 1, 'Connexion au système', '2025-04-23 14:03:47'),
(21, 1, 'Mise à jour de l\'article Baskets casual (Adidas)', '2025-04-23 14:06:54'),
(22, 1, 'Déconnexion du système', '2025-04-23 14:07:03'),
(23, 2, 'Connexion au système', '2025-04-23 14:07:25'),
(24, 2, 'Déconnexion du système', '2025-04-23 14:08:04'),
(25, 1, 'Connexion au système', '2025-04-23 14:08:41'),
(26, 1, 'Mise à jour de l\'article Tablette 10 pouces (Apple)', '2025-04-23 14:17:56'),
(27, 1, 'Mise à jour de l\'article Écran 24 pouces (Asus)', '2025-04-23 14:18:16'),
(28, 1, 'Mise à jour de l\'article Stylo à bille (Bic)', '2025-04-23 14:18:56'),
(29, 1, 'Mise à jour de l\'article Clavier mécanique (Corsair)', '2025-04-23 14:19:13'),
(30, 1, 'Mise à jour de l\'article Ordinateur portable (Dell)', '2025-04-23 14:19:25'),
(31, 1, 'Mise à jour de l\'article Classeur rigide (Exacompta)', '2025-04-23 14:19:35'),
(32, 1, 'Mise à jour de l\'article T-shirt coton (H&M)', '2025-04-23 14:19:53'),
(33, 1, 'Mise à jour de l\'article Imprimante laser (HP)', '2025-04-23 14:20:20'),
(34, 1, 'Mise à jour de l\'article Jeans slim (Levi\'s)', '2025-04-23 14:20:29'),
(35, 1, 'Mise à jour de l\'article Souris optique (Logitech)', '2025-04-23 14:20:45'),
(36, 1, 'Mise à jour de l\'article Chaussures de sport (Nike)', '2025-04-23 14:20:57'),
(37, 1, 'Mise à jour de l\'article Cahier spirale A4 (Oxford)', '2025-04-23 14:21:06'),
(38, 1, 'Mise à jour de l\'article Écouteurs sans fil (Samsung)', '2025-04-23 14:21:22'),
(39, 1, 'Mise à jour de l\'article Veste légère (Zara)', '2025-04-23 14:21:32'),
(40, 1, 'Déconnexion du système', '2025-04-23 14:22:47'),
(41, 2, 'Connexion au système', '2025-04-23 14:23:14'),
(42, 2, 'Déconnexion du système', '2025-04-23 14:23:30');

-- --------------------------------------------------------

--
-- Structure de la table `ligne_commande`
--

CREATE TABLE `ligne_commande` (
  `id` int(11) NOT NULL,
  `commande_id` int(11) NOT NULL,
  `article_id` int(11) NOT NULL,
  `quantite` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `ligne_commande`
--

INSERT INTO `ligne_commande` (`id`, `commande_id`, `article_id`, `quantite`) VALUES
(1, 1, 1, 15),
(2, 1, 2, 3),
(3, 2, 4, 1),
(4, 2, 5, 2),
(5, 3, 11, 2),
(6, 3, 13, 5),
(7, 4, 7, 1),
(8, 4, 8, 1),
(9, 5, 3, 10),
(10, 5, 1, 20),
(11, 6, 10, 1),
(12, 6, 6, 2);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `mot_de_passe` varchar(100) NOT NULL,
  `date_creation` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `nom`, `email`, `mot_de_passe`, `date_creation`) VALUES
(1, 'Admin Test', 'admin@example.com', 'admin123', '2025-04-23 16:00:19'),
(2, 'Client Test', 'client@example.com', '123456', '2025-04-23 16:00:19'),
(3, 'Marie Dupont', 'marie@example.com', 'marie123', '2025-04-23 16:00:19'),
(4, 'Pierre Martin', 'pierre@example.com', 'pierre123', '2025-04-23 16:00:19'),
(5, 'Sophie Bernard', 'sophie@example.com', 'sophie123', '2025-04-23 16:00:19'),
(6, 'Thomas Durand', 'thomas@example.com', 'thomas123', '2025-04-23 16:00:19');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `administrateur`
--
ALTER TABLE `administrateur`
  ADD PRIMARY KEY (`utilisateur_id`);

--
-- Index pour la table `article`
--
ALTER TABLE `article`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`utilisateur_id`);

--
-- Index pour la table `commande`
--
ALTER TABLE `commande`
  ADD PRIMARY KEY (`id`),
  ADD KEY `client_id` (`client_id`);

--
-- Index pour la table `historique_action`
--
ALTER TABLE `historique_action`
  ADD PRIMARY KEY (`id`),
  ADD KEY `utilisateur_id` (`utilisateur_id`);

--
-- Index pour la table `ligne_commande`
--
ALTER TABLE `ligne_commande`
  ADD PRIMARY KEY (`id`),
  ADD KEY `commande_id` (`commande_id`),
  ADD KEY `article_id` (`article_id`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `article`
--
ALTER TABLE `article`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT pour la table `commande`
--
ALTER TABLE `commande`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `historique_action`
--
ALTER TABLE `historique_action`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- AUTO_INCREMENT pour la table `ligne_commande`
--
ALTER TABLE `ligne_commande`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `administrateur`
--
ALTER TABLE `administrateur`
  ADD CONSTRAINT `administrateur_ibfk_1` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `client`
--
ALTER TABLE `client`
  ADD CONSTRAINT `client_ibfk_1` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `commande`
--
ALTER TABLE `commande`
  ADD CONSTRAINT `commande_ibfk_1` FOREIGN KEY (`client_id`) REFERENCES `utilisateur` (`id`);

--
-- Contraintes pour la table `historique_action`
--
ALTER TABLE `historique_action`
  ADD CONSTRAINT `historique_action_ibfk_1` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`);

--
-- Contraintes pour la table `ligne_commande`
--
ALTER TABLE `ligne_commande`
  ADD CONSTRAINT `ligne_commande_ibfk_1` FOREIGN KEY (`commande_id`) REFERENCES `commande` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `ligne_commande_ibfk_2` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

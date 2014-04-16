Projet de Ogier Bouvier 212480 & Tristan Overney 217280

========================================
Serveur Pub/Sub
========================================
/!\ Logger.java: dans notre main (ligne 9), vous avez la possibilité de desactiver/activer le « mode debug », c’est à dire d’activer les logs de notre serveur sur stderr, ce grâce à Logger.getLogger().setDebug(…) en choisissant TRUE ou FALSE /!\

========================================

Nous avons décelé trois points critiques d’un point de vue concurrentiel dans notre implémentation de ce serveur pub/sub; le MessageBuffer, l’accès à notre liste de sujets// contenu de chacun de ces sujets (SubscriptionHandler) & l’utilisation des outputstreams de chaque clients (dans la classe Client).


MessageBuffer:

La gestion de la pile de message à traiter (le tampon) pose clairement un problème de concurrence, en effet, nous avons plusieurs CommandHandler qui vont accéder à notre tampon pour récupérer les commandes à effectuer et en plus nos TCPReader vont accéder au tampon pour y ajouter des commandes.

Ainsi dans ce cas là nous avons choisis les Moniteurs de java, typiquement un cas de producteur - consommateur. De plus, nous n’avons jamais besoin d’avoir plus d’un thread en section critique, en mettant donc nos méthodes put & get en « synchronized », aucun des threads ayant besoins d’un de ces deux fonction bloquera tout autre thread arrivant dessus par la suite. 

- - - - - - - - - - - - - - - - - - - - -

SubscriptionHandler:

Pour gérer nos sujets d’abonnements, nous avons choisi d’utiliser une HashMap (data) avec les sujet comme clés pour le set de leur clients. Afin de protéger l’accès à notre HashMap, nous avons un ReentrantLock (mapModification) ce verrou agis aussi pour la deuxième HashMap que nous avons mis en place, cette seconde HashMap (locks) contient un ReentrantLock par sujet d’abonnement présent dans notre première HashMap.

Quand un commandHandler souhaite ajouter un client, si ce client est le premier pour le sujet désiré, nous devons bloquer le verrou mapModification, ajouter le sujet à la HashMap (data) & son verrou à la HashMap (lock). Ensuite nous pouvons bloquer le verrou du sujet fraichement créé et ajouter notre client à la liste des abonnés de notre sujet.

Pour désinscrire un client, le procédé est proche de celui de l’inscription (ajout d’un client) mais dans l’ordre inversé; on commence par verrouiller le sujet, on enlève le client du sujet, puis, si le client était le dernier abonné, on verrouille la HashMap (data) et on enlève le sujet en question.

Si un commandHandler reçoit une commande de publication sur un sujet, elle demande le set des clients à notre SubscriptionHandler via startPublish(sujet). Cette méthode se charge de verrouiller le verrou du sujet en question et renvoie le set des clients abonnés au sujet. Une fois que notre commandHandler à envoyé le message à tout les abonnés il appelle endPublish(sujet) qui se charge de déverrouiller le sujet.

- - - - - - - - - - - - - - - - - - - - -

Client (gestion des outputstreams):

Notre classe client gère elle même l’outputstream de son client ainsi, toute autre classe de notre projet qui veux écrire un message à un client, utilise une des deux méthodes d’envois de notre Classe client, méthodes qui sont les deux « synchronized » et ainsi nous pouvons être sur qu’il n’y aura jamais deux write() concurrent sur le même outputstream.

========================================
Description de nos tests
========================================



MultiTest:
Ce fichier fait tourner notre serveur à l’infini avec 15 demandes de clients qui envoient aléatoirement des messages d’abonnement, désabonnement & publication et ce aléatoirement sur trois sujets. (ce n’est donc pas un test qui a une fin)

- - - - - - - - - - - - - - - - - - - - -

/!\ Pour ces autres tests, il faut que votre configuration d’ant inclue la bibliothèque ant-junit4.jar (dans ANT_HOME) /!\

- - - - - - - - - - - - - - - - - - - - -

CommandHandlerTest (JUnit) :

Teste notre CommandHandler;
	insertRemoveTest: On testes si notre CommandHandler se comporte bien s’il y a un message à traiter dans le tampon et s’il y en a aucun.
	allInTest: On met 3 messages à la fois dans notre tampon et on vérifie que le CommandHandler les traites bien, sans s’arrêter.

- - - - - - - - - - - - - - - - - - - - -

MessageHandlerTest (JUnit) :

Teste notre MessageHandler:
	blockingWhenEmptyTest: on vérifie que si le tampon est vide le thread bloque pour attendre un message.
	putThenGetTest: on vérifie que la taille de notre tampon corresponde au nombre de messages mis dedans et que le message récupéré est égal au dernier message mis dans le tampon.
	multiplePutThenGetTest: même chose que putThenGetTest mais nous mettons plusieurs messages dans le tampon avant de récupérer nos messages.
	maximumSizePutTest: on vérifie que la taille maximale de notre tampon est respectée.
	oneReaderOneWriterTest: on crée un reader et on vérifie que ce dernier ne s’arrête pas après avoir lu le seul message du tampon.

- - - - - - - - - - - - - - - - - - - - -

SubscriptionManagerTest (JUnit) :

Teste notre SubscriptionManager:
	subscribeThenRemoveAllTest: on inscrit un client à tout les sujet puis on imite une fin de client pour voir si notre fonction removeFromAll(client) fonctionne comme désiré.
	duSubscribeTest: on vérifie qu’inscrire des clients à des sujets fonctionne correctement.
	unsubscribeTest: on vérifie que désinscrire plusieurs fois un client de sujets qui n’existent pas ne fait rien.
	concurrentSubUnsubTest: lance deux thread en parallèle qui abonne et désabonne des clients au sujet 0 (epfl). Nous vérifions si le nombres d’abonnés à epfl est égal à la somme des abonnés de chaque thread.
	



























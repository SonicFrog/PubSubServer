Projet de Ogier Bouvier 212480 & Tristan Overney 217280
========================================
Serveur Pub/Sub
========================================

Nous avons décelé trois points critiques d’un point de vue concurrentiel dans notre implémentation de ce serveur pub/sub; le MessageBuffer, l’accès à notre liste de sujets// contenu de chacun de ces sujets (SubscriptionHandler) & l’utilisation des outputstreams de chaque clients (dans la classe Client).


MessageBuffer:
La gestion de la pile de message à traiter (le tampon) pose clairement un problème de concurrence, en effet, nous avons plusieurs CommandHandler qui vont accéder à notre tampon pour récupérer les commandes à effectuer et en plus nos TCPReader vont accéder au tampon pour y ajouter des commandes.

Ainsi dans ce cas là nous avons choisis les Moniteurs de java avec une exclusion mutuelle, en effet nous n’avons jamais besoin d’avoir plus d’un thread en section critique, en mettant donc nos méthodes put & get en « synchronized ». Aucun des threads ayant besoins d’un de ces deux fonction bloquera tout autre thread arrivant dessus par la suite. 

- - - - - - - - - - - - - - - - - - - - -

SubscriptionHandler:


- - - - - - - - - - - - - - - - - - - - -

Client (gestion des outputstreams):
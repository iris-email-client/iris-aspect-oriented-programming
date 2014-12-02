# IRIS E-mail Client

This is the AOP version of IRIS e-mail client. The following features 
are implemented in this version:

* Send and Receive e-mail messages;
* Multiple folders (though we are not able to create new folders);
* Address book;
* Relational database.

## Import into Eclipse IDE

By following the steps below in order you shouldn't get in trouble:

1. Install Java SDK 8;
2. Download Eclipse Kepler SR2;
3. Install both "Java 8 support for Eclipse Kepler" and "Java 8 support for m2e for Eclipse Kepler SR2" (see: [http://www.eclipse.org/downloads/java8/](http://www.eclipse.org/downloads/java8/));
4. Install "AJDT: AspectJ Development Tools" (add the update site: [http://download.eclipse.org/tools/ajdt/43/dev/update](http://download.eclipse.org/tools/ajdt/43/dev/update));
5. Clone this repository and import the project in Eclipse as an existing Maven project;
6. Install whatever Eclipse suggests;
7. Have fun! :-)


## Known issues

(a) building a product with relational persistence requires a review of the hibernate.cfg.xml, because some of
the domain classes (such as addres book entry / tag) might not be available in a given product.

(b) it is necessary to clear the ~/.iris/lucene_idx folder, otherwise the unit test
*testFind(br.unb.cic.iris.persistence.lucene.TestAddressBookDAO)* fails.

(c) there duplicated folders in LUCENE implementation. I suppose that unit tests
are populating the same lucene database as the *production* version.

(d) changing a folder (e.g. INBOX or OUTBOX) in LUCENE is not working. This might occur
because the index is different. We should change to a folder based on the name, not the
id. 
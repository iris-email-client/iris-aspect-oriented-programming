package br.unb.cic.iris.persistence.lucene;

import org.apache.lucene.document.Document;

public abstract class LuceneDoc<T> {
	
	protected abstract Document toLuceneDoc(T obj);
	
	protected abstract T fromLuceneDoc(Document doc);

}

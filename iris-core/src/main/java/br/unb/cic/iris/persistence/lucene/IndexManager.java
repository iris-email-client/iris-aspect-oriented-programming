package br.unb.cic.iris.persistence.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class IndexManager {

	/* Indexed, tokenized, stored. */
	public static final FieldType TYPE_STORED = new FieldType();

	static {
		TYPE_STORED.setIndexed(true);
		TYPE_STORED.setTokenized(true);
		TYPE_STORED.setStored(true);
		TYPE_STORED.setStoreTermVectors(true);
		TYPE_STORED.setStoreTermVectorPositions(true);
		TYPE_STORED.freeze();
	}
	
	private static Directory index;
	private static IndexWriter writer;
	private static IndexReader reader;
	
	private IndexManager() {}
	
	public static Directory createIndex(String filepath) throws IOException {
		File path = null;
		if (filepath != null && !filepath.isEmpty()) {
			path = new File(filepath);
		}
		
		if (path == null || !path.exists())
			index = new RAMDirectory();
		else
			index = FSDirectory.open(path);
		
		return index;
	}
	
	public static void setIndex(String filepath) throws IOException {
		File path = null;
		if (filepath != null && !filepath.isEmpty()) {
			path = new File(filepath);
		}
			
		if (path == null || !path.exists())
			throw new IOException("Couldn't open the specified Lucene index.");
		
		index = FSDirectory.open(path);	
	}
	
	public static Directory getIndex() throws IOException {
		if (index == null)
			index = createIndex(null); // Index in RAM
		return index;
	}
	
	public static void closeIndex() throws IOException {
		index.close();
		index = null;
	}
	
	public static IndexWriter getWriter() throws IOException {
		if (writer == null) {
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
			writer = new IndexWriter(getIndex(), config);
		}
		return writer;
	}
	
	public static IndexReader getReader() throws IOException {
		if (reader == null)
			reader = DirectoryReader.open(getIndex());
		
		// Line below ensures we get an updated view of the index.
		return DirectoryReader.openIfChanged((DirectoryReader) reader);
	}

}

package br.unb.cic.iris.persistence.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class IndexManager {

	private static Directory index;

	private static IndexWriter writer;
	
	private static IndexReader reader;

	private IndexManager() {
	}

	public static Directory createIndex(String filepath) throws IOException {
		File path = null;
		if (filepath != null && !filepath.isEmpty()) {
			path = new File(filepath);
		}

		if (path == null || !path.exists() || !path.isDirectory()) {
			index = new RAMDirectory();
		} else {
			// Clears the directory...
			for (File f : path.listFiles())
				f.delete();
			
			index = FSDirectory.open(path);
		}
		
		// Initializes the index with an empty document.
		// *This is ABSOLUTELY NECESSARY!*
		getWriter().addDocument(new Document());
		getWriter().commit();

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
		writer.close();
		index.close();
		writer = null;
		reader = null;
		index = null;
	}

	public static IndexWriter getWriter() throws IOException {
		if (writer == null) {
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,
					analyzer);
			writer = new IndexWriter(getIndex(), config);
		}
		return writer;
	}

	private static IndexReader getReader() throws IOException {
		if (reader == null) {
			reader = DirectoryReader.open(getIndex());
		} else {
			// The method below ensures we get an updated view of the index. It
			// returns a new reader if the index has changed after we opened the
			// previous reader. Otherwise, it returns 'null'.
			IndexReader r = DirectoryReader.openIfChanged((DirectoryReader) reader);
			if (r != null) reader = r;
		}
		// Line below 
		return reader;
	}

	public static IndexSearcher getSearcher() throws IOException {
		// According to the documentation, creating a searcher from an existing
		// reader is cheap.
		return new IndexSearcher(getReader());
	}

}

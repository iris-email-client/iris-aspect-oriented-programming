package br.unb.cic.iris.search.persistence.lucene;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import br.unb.cic.iris.core.exception.DBException;
import br.unb.cic.iris.core.exception.EmailException;
import br.unb.cic.iris.core.model.EmailMessage;
import br.unb.cic.iris.persistence.lucene.EmailDAO;
import br.unb.cic.iris.persistence.lucene.IndexManager;
import br.unb.cic.iris.search.Searcher;

public aspect SearchFeature {
	declare parents: br.unb.cic.iris.core.SystemFacade implements Searcher;

	public List<EmailMessage> br.unb.cic.iris.core.SystemFacade.search(
			String query) throws EmailException {
		EmailDAO dao = EmailDAO.instance();
		return dao.search(query);
	}

	declare parents: br.unb.cic.iris.persistence.lucene.EmailDAO implements Searcher;

	public List<EmailMessage> br.unb.cic.iris.persistence.lucene.EmailDAO.search(
			String query) throws EmailException {
		List<EmailMessage> result = new ArrayList<EmailMessage>();
		try {
			Query typeQuery = new TermQuery(new Term("type", "email"));

			Query expressionQuery = new QueryParser("<default field>",
					new StandardAnalyzer()).parse(query);

			BooleanQuery q = new BooleanQuery();
			q.add(new BooleanClause(typeQuery, Occur.MUST));
			q.add(expressionQuery, Occur.MUST);

			IndexSearcher searcher = IndexManager.getSearcher();
			searcher.search(q, new EmailCollector(searcher, result));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DBException(
					"Error ocurred while retrieving messages from folder.", e);
		}
		return result;
	}
}
